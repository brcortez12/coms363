-- 1. Show eids of all employees who work on all departments in the dept relation. Show the eid and the number of
-- departments the employee works for in descending order of eid.

SELECT w.eid, count(w.did) as cnt
FROM works w
group by w.eid
having cnt = (select count(*) from dept)
order by w.eid desc;

-- 2. List the values of eid and ename of all employees who have not worked in any 
-- department in three different ways. 
select e.eid, e.ename
from emp e
left join works w on e.eid=w.eid 
where w.eid is null;

-- right join
select e.eid, e.ename
from works w right join emp e 
on w.eid = e.eid
where w.eid is null;

select eid, e.ename
from emp e
where not exists 
(select *
 from works w where w.eid=e.eid);

select e.eid, e.ename
from emp e
where e.eid not in
(select eid
from works);

-- 3. Find eids and names of the managers who manage the department with the largest budget.

select e.eid, e.ename
from dept d, emp e
where d.managerid=e.eid 
and d.budget = 
(select max(budget)
 from dept);

-- 4.	For each department, print department ID, department name, and the highest salary -- of the employee who works in that department. List only departments with the highest 
-- salary paid within the department over 4 million dollars.
select w.did, d.dname, max(salary) as max_salary
from emp e, works w, dept d
where e.eid = w.eid and w.did = d.did
group by w.did
having max_salary > 4000000;

-- 5. List employees who work in exactly three departments. 
-- Show the eid and ename values. It does not matter which three departments.
SELECT w.eid, e.ename
FROM works w, emp e 
where w.eid=e.eid
group by w.eid
having count(distinct w.did)=3;

-- 6. Find employees who work in Accounting, Human Resource, and Maintenance departments. 
-- These employees may work in additional departments. For each of these employees, 
-- show the name and the percent work time for the three departments combined.
select e.ename, w1.pct_time+w2.pct_time+w3.pct_time as combined_pct_time
from works w1, dept d1, works w2, dept d2, works w3, dept d3, emp e
where w1.did=d1.did and d1.dname="Accounting" 
and d2.dname="Maintenance" and w2.did=d2.did
and d3.dname="Human Resource" and w3.did=d3.did
and w1.eid = w2.eid and w1.eid=w3.eid
and w1.eid=e.eid;

