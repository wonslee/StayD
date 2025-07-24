CREATE OR REPLACE PROCEDURE get_reservation_status_by_date(
    p_cafe_id IN NUMBER,
    p_selected_date IN DATE,
    p_reservations OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_reservations FOR
        SELECT usage_started_at, usage_ended_at, reservation_date
        FROM reservation
        WHERE cafe_id = p_cafe_id
          AND reservation_date = p_selected_date
          AND is_canceled IS NULL;
END get_reservation_status_by_date;
/
