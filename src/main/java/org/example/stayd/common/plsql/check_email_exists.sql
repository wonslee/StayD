CREATE OR REPLACE PROCEDURE check_email_exists(
    p_email IN VARCHAR2,
    p_exists OUT NUMBER
) AS
BEGIN
    SELECT COUNT(*) INTO p_exists
    FROM users
    WHERE email = p_email;
END check_email_exists;
/
