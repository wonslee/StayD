CREATE OR REPLACE PROCEDURE get_reservation_status_by_day(
    p_cafe_id IN NUMBER,
    p_day_of_week IN VARCHAR2,
    p_reservations OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_reservations FOR
        SELECT usage_started_at, usage_ended_at, day_of_week
        FROM reservation
        WHERE cafe_id = p_cafe_id
          AND day_of_week = p_day_of_week
          AND is_canceled IS NULL;
END get_reservation_status_by_day;
/
