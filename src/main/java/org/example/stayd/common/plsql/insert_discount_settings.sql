-- 할인 설정을 위한 프로시저
CREATE OR REPLACE PROCEDURE insert_discount_settings(
    p_user_id IN NUMBER,
    p_day_of_week IN VARCHAR2,
    p_discount_start IN NUMBER,
    p_discount_end IN NUMBER,
    p_discount_rate IN NUMBER
) AS
    v_cafe_id NUMBER;
BEGIN
    -- 유저의 카페 ID를 가져오기
    SELECT cafe_id
    INTO v_cafe_id
    FROM cafe
    WHERE owner_id = p_user_id;

    -- discount_hours 테이블에 할인 설정 정보 삽입
    INSERT INTO discount_hours (cafe_id, day_of_week, discount_start, discount_end, discount_rate)
    VALUES (v_cafe_id, p_day_of_week, p_discount_start, p_discount_end, p_discount_rate);

    COMMIT;  -- 커밋
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('유효하지 않은 사용자 ID입니다.');
    WHEN OTHERS THEN
        RAISE;  -- 기타 예외 발생 시
END insert_discount_settings;
/
