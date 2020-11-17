DELIMITER &&
CREATE PROCEDURE my_total_sales(IN given_month INT, OUT total_amount double)
BEGIN
SELECT sum(p.amount) INTO total_amount
 FROM payment p
 INNER JOIN rental AS r ON p.rental_id = r.rental_id
 WHERE month(r.rental_date)=given_month;
END &&
DELIMITER ;