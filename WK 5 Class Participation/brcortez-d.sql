drop procedure if exists redgreenSuppliers;
Delimiter $$
Create procedure redgreenSuppliers()
Begin
	select distinct(s.sid), s.sname
		from suppliersparts.suppliers s, suppliersparts.catalog c1, suppliersparts.parts p1,
			suppliersparts.catalog c2, suppliersparts.parts p2
		where s.sid = c1.sid and c1.pid = p1.pid and s.sid = c2.sid and c2.pid = p2.pid
		and p1.color = "red" and p2.color = "green";
end;$$
Delimiter ;


call redgreenSuppliers();