CREATE OR REPLACE PROCEDURE check_id_exists(
    p_login_id IN VARCHAR2,
    p_exists OUT NUMBER
) AS
BEGIN
    SELECT COUNT(*) INTO p_exists
    FROM users
    WHERE login_id = p_login_id;
END check_id_exists;
/
