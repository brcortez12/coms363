drop procedure if exists whosupplycolor;
delimiter //
create procedure whosupplycolor(IN colorarray VARCHAR(100))
begin
	select distinct s.sid, s.sname
	from suppliersparts.suppliers s, suppliersparts.catalog c, suppliersparts.parts p 
	where s.sid=c.sid and c.pid=p.pid 
	and find_in_set(p.color, colorarray);
end;//
delimiter ;