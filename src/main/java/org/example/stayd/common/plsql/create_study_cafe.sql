-- 기존 타입과 프로시저 삭제
DROP PROCEDURE create_study_cafe;
DROP TYPE operating_days_array;
-- 스터디 카페 생성 프로시저
CREATE OR REPLACE PROCEDURE create_study_cafe(
    p_owner_id IN NUMBER,
    p_name IN VARCHAR2,
    p_address IN VARCHAR2,
    p_price_per_hour IN NUMBER,
    p_description IN VARCHAR2,
    p_phone_number IN VARCHAR2,
    p_image_url IN VARCHAR2,
    p_operating_days IN VARCHAR2,  -- "월,화,수,목,금" 형태의 문자열
    p_start_hour IN NUMBER,
    p_end_hour IN NUMBER,
    p_cafe_id OUT NUMBER,
    p_result OUT NUMBER  -- 0:성공, 1:실패
) AS
    v_current_time TIMESTAMP := SYSTIMESTAMP;
    v_day VARCHAR2(10);
    v_start_pos NUMBER := 1;
    v_comma_pos NUMBER;
    v_days_processed NUMBER := 0;
BEGIN
    -- 입력 검증
    IF p_operating_days IS NULL OR LENGTH(TRIM(p_operating_days)) = 0 THEN
        p_result := 1;
        RETURN;
END IF;

    -- 트랜잭션 시작점 설정
SAVEPOINT create_cafe_start;

BEGIN
        DBMS_OUTPUT.PUT_LINE('🔄 PL/SQL 카페 생성 시작...');

        -- 1. 카페 생성 및 ID 반환
INSERT INTO cafe (
    owner_id, name, address, price_per_hour,
    description, phone_number, image_url, created_at
) VALUES (
             p_owner_id, p_name, p_address, p_price_per_hour,
             p_description, p_phone_number, p_image_url, v_current_time
         ) RETURNING cafe_id INTO p_cafe_id;

DBMS_OUTPUT.PUT_LINE('✅ 카페 생성 완료 - ID: ' || p_cafe_id);

        -- 2. 운영시간 파싱 및 생성
        -- "월,화,수,목,금" 문자열을 파싱하여 각각 INSERT
        WHILE v_start_pos <= LENGTH(p_operating_days) LOOP
            v_comma_pos := INSTR(p_operating_days, ',', v_start_pos);

            IF v_comma_pos = 0 THEN
                -- 마지막 요일
                v_day := TRIM(SUBSTR(p_operating_days, v_start_pos));
                v_start_pos := LENGTH(p_operating_days) + 1;
ELSE
                -- 중간 요일
                v_day := TRIM(SUBSTR(p_operating_days, v_start_pos, v_comma_pos - v_start_pos));
                v_start_pos := v_comma_pos + 1;
END IF;

            -- 요일이 비어있지 않으면 INSERT
            IF LENGTH(v_day) > 0 THEN
                INSERT INTO operation_hours (
                    cafe_id, day_of_week, operation_start,
                    operation_end, created_at
                ) VALUES (
                    p_cafe_id, v_day, p_start_hour,
                    p_end_hour, v_current_time
                );

                v_days_processed := v_days_processed + 1;
                DBMS_OUTPUT.PUT_LINE('✅ 운영시간 추가: ' || v_day || ' ' || p_start_hour || ':00-' || p_end_hour || ':00');
END IF;
END LOOP;

        -- 3. 좌석 20개 배치 생성 (1~20번)
FOR i IN 1..20 LOOP
            INSERT INTO seat (
                cafe_id, seat_number, is_available, created_at
            ) VALUES (
                p_cafe_id, TO_CHAR(i), 'Y', v_current_time
            );
END LOOP;

        -- 성공 시 커밋
COMMIT;
p_result := 0; -- 성공

        -- 최종 성공 로그
        DBMS_OUTPUT.PUT_LINE('🎉 카페 생성 완료!');
        DBMS_OUTPUT.PUT_LINE('   카페 ID: ' || p_cafe_id);
        DBMS_OUTPUT.PUT_LINE('   운영일 수: ' || v_days_processed || '개');
        DBMS_OUTPUT.PUT_LINE('   좌석 수: 20개');

EXCEPTION
        WHEN OTHERS THEN
            -- 실패 시 롤백
            ROLLBACK TO create_cafe_start;
            p_result := 1; -- 실패
            p_cafe_id := NULL;

            -- 에러 로그
            DBMS_OUTPUT.PUT_LINE('❌ PL/SQL 카페 생성 실패: ' || SQLERRM);
            RAISE;
END;
END create_study_cafe;