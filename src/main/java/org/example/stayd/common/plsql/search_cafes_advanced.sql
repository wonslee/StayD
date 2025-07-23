-- 카페 검색 및 정렬 함수
CREATE OR REPLACE FUNCTION search_cafes_advanced(
    p_keyword IN VARCHAR2 DEFAULT NULL,         -- 검색 키워드
    p_sort_type IN VARCHAR2 DEFAULT 'LATEST',   -- LATEST, RATING
    p_page_num IN NUMBER DEFAULT 1,             -- 페이지 번호
    p_page_size IN NUMBER DEFAULT 8             -- 페이지 크기
) RETURN SYS_REFCURSOR AS
    v_cursor SYS_REFCURSOR;
    v_sql VARCHAR2(4000);
    v_where_clause VARCHAR2(500) := '';
    v_order_clause VARCHAR2(200) := '';
BEGIN
    DBMS_OUTPUT.PUT_LINE('PL/SQL 카페 검색 시작...');
    DBMS_OUTPUT.PUT_LINE('   키워드: ' || NVL(p_keyword, '전체'));
    DBMS_OUTPUT.PUT_LINE('   정렬: ' || p_sort_type);
    DBMS_OUTPUT.PUT_LINE('   페이지: ' || p_page_num || ' (크기: ' || p_page_size || ')');

    -- 기본 쿼리 구성 (review_count를 COUNT로 집계)
    v_sql := 'SELECT * FROM (
        SELECT c.cafe_id, c.name, c.address, c.price_per_hour,
               c.description, c.phone_number, c.image_url,
               COALESCE(AVG(r.rating), 0.0) as avg_rating,
               COUNT(CASE WHEN r.rating IS NOT NULL THEN 1 END) as review_count,
               ROW_NUMBER() OVER (';

    -- 동적 정렬 조건 설정
CASE UPPER(p_sort_type)
        WHEN 'RATING' THEN
            v_order_clause := 'ORDER BY COALESCE(AVG(r.rating), 0.0) DESC, COUNT(CASE WHEN r.rating IS NOT NULL THEN 1 END) DESC, c.cafe_id DESC';
            DBMS_OUTPUT.PUT_LINE('정렬 방식: 평점순');
ELSE
            v_order_clause := 'ORDER BY c.cafe_id DESC';
            DBMS_OUTPUT.PUT_LINE('정렬 방식: 최신순');
END CASE;

    v_sql := v_sql || v_order_clause || ') as rn
        FROM cafe c
        LEFT JOIN reservation r ON c.cafe_id = r.cafe_id AND r.rating IS NOT NULL';

    -- 동적 WHERE 조건 추가
    IF p_keyword IS NOT NULL AND LENGTH(TRIM(p_keyword)) > 0 THEN
        v_where_clause := ' WHERE UPPER(c.name) LIKE UPPER(''%' || p_keyword || '%'')';
        v_sql := v_sql || v_where_clause;
        DBMS_OUTPUT.PUT_LINE('검색 조건: 카페명에 "' || p_keyword || '" 포함');
ELSE
        DBMS_OUTPUT.PUT_LINE('검색 조건: 전체 조회');
END IF;

    -- GROUP BY 추가 (집계 함수 사용을 위해 필요)
    v_sql := v_sql || ' GROUP BY c.cafe_id, c.name, c.address, c.price_per_hour,
                                c.description, c.phone_number, c.image_url';

    -- 페이징 조건 추가
    v_sql := v_sql || ') WHERE rn BETWEEN ' || ((p_page_num-1) * p_page_size + 1)
                   || ' AND ' || (p_page_num * p_page_size);

    DBMS_OUTPUT.PUT_LINE('페이징: ' || ((p_page_num-1) * p_page_size + 1) || ' ~ ' || (p_page_num * p_page_size));

    -- 디버깅용 쿼리 출력
    DBMS_OUTPUT.PUT_LINE('실행 쿼리: ' || SUBSTR(v_sql, 1, 500) || '...');

    -- 커서 열기
OPEN v_cursor FOR v_sql;

DBMS_OUTPUT.PUT_LINE('PL/SQL 검색 함수 실행 완료');

RETURN v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('PL/SQL 검색 함수 실행 실패: ' || SQLERRM);
        RAISE;
END search_cafes_advanced;