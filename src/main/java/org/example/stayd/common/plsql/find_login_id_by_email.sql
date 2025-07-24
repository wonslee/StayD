CREATE OR REPLACE PROCEDURE find_login_id_by_email(
    p_email IN VARCHAR2,         -- 이메일 입력 파라미터
    p_login_id OUT VARCHAR2      -- 반환될 로그인 아이디
) AS
BEGIN
    SELECT login_id
    INTO p_login_id
    FROM users
    WHERE email = p_email;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_login_id := NULL; -- 이메일이 없으면 NULL 반환
    WHEN OTHERS THEN
        RAISE; -- 다른 오류는 그대로 전파
END find_login_id_by_email;
/
