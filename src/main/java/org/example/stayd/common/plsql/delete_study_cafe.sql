-- 작성자 : 최영준
-- 스터디 카페 삭제 프로시저 (수정 버전)
CREATE OR REPLACE PROCEDURE delete_study_cafe(
    p_cafe_id IN NUMBER,
    p_owner_id IN NUMBER,
    p_result OUT NUMBER  -- 0:성공, 1:권한없음, 2:실패, 3:카페없음
) AS
    v_owner_count NUMBER;
    v_reservations_deleted NUMBER;
    v_seats_deleted NUMBER;
    v_hours_deleted NUMBER;
    v_discount_deleted NUMBER;
    v_cafe_deleted NUMBER;
BEGIN
    -- 디버깅 로그
    DBMS_OUTPUT.PUT_LINE(' PL/SQL 카페 삭제 시작...');
    DBMS_OUTPUT.PUT_LINE('   카페 ID: ' || p_cafe_id);
    DBMS_OUTPUT.PUT_LINE('   소유자 ID: ' || p_owner_id);

    -- 트랜잭션 시작점 설정
SAVEPOINT delete_cafe_start;

BEGIN
        -- 1. 카페 존재 여부 및 소유자 확인
SELECT COUNT(*) INTO v_owner_count
FROM cafe
WHERE cafe_id = p_cafe_id AND owner_id = p_owner_id;

IF v_owner_count = 0 THEN
            -- 카페가 없거나 소유자가 아님
SELECT COUNT(*) INTO v_owner_count
FROM cafe
WHERE cafe_id = p_cafe_id;

IF v_owner_count = 0 THEN
                p_result := 3; -- 카페 없음
                DBMS_OUTPUT.PUT_LINE(' 존재하지 않는 카페입니다.');
ELSE
                p_result := 1; -- 권한 없음
                DBMS_OUTPUT.PUT_LINE(' 소유자만 카페를 삭제할 수 있습니다.');
END IF;
            RETURN;
END IF;

        DBMS_OUTPUT.PUT_LINE(' 소유자 확인 완료');

        -- 2. 연관 데이터 삭제 (외래키 순서대로)

        -- 2-1. 예약 삭제 (가장 먼저 삭제해야 함)
DELETE FROM reservation WHERE cafe_id = p_cafe_id;
v_reservations_deleted := SQL%ROWCOUNT;
        DBMS_OUTPUT.PUT_LINE(' 예약 ' || v_reservations_deleted || '개 삭제 완료');

        -- 2-2. 좌석 삭제
DELETE FROM seat WHERE cafe_id = p_cafe_id;
v_seats_deleted := SQL%ROWCOUNT;
        DBMS_OUTPUT.PUT_LINE(' 좌석 ' || v_seats_deleted || '개 삭제 완료');

        -- 2-3. 할인시간 삭제 (있다면)
BEGIN
DELETE FROM discount_hours WHERE cafe_id = p_cafe_id;
v_discount_deleted := SQL%ROWCOUNT;
            DBMS_OUTPUT.PUT_LINE(' 할인시간 ' || v_discount_deleted || '개 삭제 완료');
EXCEPTION
            WHEN OTHERS THEN
                -- 할인시간 테이블이 없거나 오류가 있어도 계속 진행
                v_discount_deleted := 0;
                DBMS_OUTPUT.PUT_LINE(' 할인시간 테이블 처리 스킵');
END;

        -- 2-4. 운영시간 삭제
DELETE FROM operation_hours WHERE cafe_id = p_cafe_id;
v_hours_deleted := SQL%ROWCOUNT;
        DBMS_OUTPUT.PUT_LINE(' 운영시간 ' || v_hours_deleted || '개 삭제 완료');

        -- 3. 메인 카페 삭제
DELETE FROM cafe WHERE cafe_id = p_cafe_id;
v_cafe_deleted := SQL%ROWCOUNT;

        IF v_cafe_deleted = 1 THEN
            -- 성공 시 커밋
            COMMIT;
            p_result := 0; -- 성공

            DBMS_OUTPUT.PUT_LINE(' 카페 삭제 완료!');
            DBMS_OUTPUT.PUT_LINE('   카페 ID: ' || p_cafe_id);
            DBMS_OUTPUT.PUT_LINE('   삭제된 예약: ' || v_reservations_deleted || '개');
            DBMS_OUTPUT.PUT_LINE('   삭제된 좌석: ' || v_seats_deleted || '개');
            DBMS_OUTPUT.PUT_LINE('   삭제된 할인시간: ' || v_discount_deleted || '개');
            DBMS_OUTPUT.PUT_LINE('   삭제된 운영시간: ' || v_hours_deleted || '개');
ELSE
            -- 카페 삭제 실패
            ROLLBACK TO delete_cafe_start;
            p_result := 2; -- 실패
            DBMS_OUTPUT.PUT_LINE(' 카페 삭제에 실패했습니다.');
END IF;

EXCEPTION
        WHEN OTHERS THEN
            -- 실패 시 롤백
            ROLLBACK TO delete_cafe_start;
            p_result := 2; -- 실패

            -- 에러 로그
            DBMS_OUTPUT.PUT_LINE(' PL/SQL 카페 삭제 실패: ' || SQLERRM);
            RAISE;
END;
END delete_study_cafe;