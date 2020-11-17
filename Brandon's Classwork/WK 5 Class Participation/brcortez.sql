-- 2. List the values of eid and ename of all employees who have not worked in any 
-- department in three different ways. 

select e.eid, e.ename
from emp e
where e.eid not in
(select eid
from works);

-- 4.	For each department, print department ID, department name, and the highest salary -- of the employee who works in that department. List only departments with the highest 
-- salary paid within the department over 4 million dollars.
select w.did, d.dname, max(salary) as max_sal
from emp e, works w, dept d
where e.eid = w.eid and w.did = d.did
group by w.did
having max_sal > 4000000;

-- 6. Find employees who work in Accounting, Human Resource, and Maintenance departments. 
-- These employees may work in additional departments. For each of these employees, 
-- show the name and the percent work time for the three departments combined.
select e.ename, w1.pct_time+w2.pct_time+w3.pct_time as comb_pct_time
from works w1, dept d1, works w2, dept d2, works w3, dept d3, emp e
where w1.did=d1.did and d1.dname="Accounting"
and w2.did=d2.did and d2.dname="Maintenance"
and w3.did=d3.did and d3.dname="Human Resource"
and w1.eid = w2.eid and w1.eid=w3.eid
and w1.eid=e.eid;

