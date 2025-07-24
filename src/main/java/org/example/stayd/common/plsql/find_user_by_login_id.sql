CREATE OR REPLACE PROCEDURE find_user_by_login_id(
    p_in_login_id IN VARCHAR2,
    p_user_id OUT NUMBER,
    p_login_id OUT VARCHAR2,
    p_email OUT VARCHAR2,
    p_password OUT VARCHAR2,
    p_role OUT VARCHAR2
) AS
BEGIN
    SELECT user_id, login_id, email, password, role
    INTO p_user_id, p_login_id, p_email, p_password, p_role
    FROM users
    WHERE login_id = p_in_login_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_user_id := NULL;
        p_login_id := NULL;
        p_email := NULL;
        p_password := NULL;
        p_role := NULL;
END find_user_by_login_id;
/
