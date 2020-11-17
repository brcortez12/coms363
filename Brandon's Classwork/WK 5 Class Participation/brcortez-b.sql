drop procedure if exists suppliersWithoutParts;
Delimiter $$
Create procedure suppliersWithoutParts()
Begin
	Select s.sid, sname 
	From suppliersparts.suppliers s, suppliersparts.catalog c
	Where Not exists( select * from suppliersparts.catalog where c.sid = s.sid);
end;$$
Delimiter ;

call suppliersWithoutParts
