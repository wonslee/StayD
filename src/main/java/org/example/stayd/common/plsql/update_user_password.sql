CREATE OR REPLACE PROCEDURE update_user_password(
    p_login_id IN VARCHAR2,
    p_new_password IN VARCHAR2
) AS
BEGIN
    UPDATE users
    SET password = p_new_password
    WHERE login_id = p_login_id;
END update_user_password;
/
