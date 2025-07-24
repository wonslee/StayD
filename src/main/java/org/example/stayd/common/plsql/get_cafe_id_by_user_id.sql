CREATE OR REPLACE PROCEDURE get_cafe_id_by_user_id(
    p_user_id IN NUMBER,
    p_cafe_id OUT NUMBER
) AS
BEGIN
    SELECT cafe_id INTO p_cafe_id
    FROM cafe
    WHERE owner_id = p_user_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_cafe_id := -1;  -- 카페를 찾지 못한 경우 -1 반환
END get_cafe_id_by_user_id;
/
