CREATE OR REPLACE PROCEDURE register_user (
  p_login_id   IN VARCHAR2,
  p_email      IN VARCHAR2,
  p_password   IN VARCHAR2,
  p_role       IN VARCHAR2 DEFAULT NULL,  -- 생략 가능
  p_status     OUT NUMBER                 -- 0:성공,1:ID중복,2:Email중복,3:역할오류,-1:기타오류
) AS
  v_role VARCHAR2(20);
BEGIN
  -- 1) 역할 기본값 설정 & 유효성 검사
  IF p_role IS NULL OR TRIM(p_role) = '' THEN
    v_role := 'USER';
  ELSE
    v_role := UPPER(TRIM(p_role));
  END IF;

  IF v_role NOT IN ('USER','CAFE_OWNER','ADMIN') THEN
    p_status := 3;  -- 역할 오류
    RETURN;
  END IF;

  -- 2) 아이디 중복 체크
  IF (SELECT COUNT(*) FROM users WHERE login_id = p_login_id) > 0 THEN
    p_status := 1; RETURN;
  END IF;

  -- 3) 이메일 중복 체크
  IF (SELECT COUNT(*) FROM users WHERE email = p_email) > 0 THEN
    p_status := 2; RETURN;
  END IF;

  -- 4) 사용자 삽입
  INSERT INTO users (user_id, login_id, email, password, role)
  VALUES (user_seq.NEXTVAL, p_login_id, p_email, p_password, v_role);

  p_status := 0;  -- 성공
EXCEPTION
  WHEN OTHERS THEN
    p_status := -1;  -- 기타 오류
END;
/
