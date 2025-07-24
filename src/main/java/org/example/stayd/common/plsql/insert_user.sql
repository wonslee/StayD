CREATE OR REPLACE PROCEDURE insert_user(
    p_login_id IN VARCHAR2,
    p_email IN VARCHAR2,
    p_password IN VARCHAR2,
    p_role IN VARCHAR2
) AS
BEGIN
    INSERT INTO users (login_id, email, password, role)
    VALUES (p_login_id, p_email, p_password, p_role);
END insert_user;
/
