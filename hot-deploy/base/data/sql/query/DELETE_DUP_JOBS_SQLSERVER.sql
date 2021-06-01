-- Duplicated Jobs
delete js
from job_sandbox js
inner join (
	select job_name, count(*) count_job, max(job_id) max_job_id
	from job_sandbox
	group by job_name
	having count(*) > 1
) js2 on js2.job_name=js.job_name
where js.job_id <> js2.max_job_id and js.status_id in ('SERVICE_PENDING','SERVICE_RUNNING')