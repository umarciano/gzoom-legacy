delete
from job_sandbox js0
where exists (
	select js.*
	from job_sandbox js
	inner join (
		select job_name, status_id, count(*) as count_job, max(job_id) as max_job_id
		from job_sandbox
		group by job_name, status_id
		having count(*) > 1
	) js2 on js2.job_name=js.job_name and js2.status_id=js.status_id
	where js.job_id <> js2.max_job_id and js.status_id in ('SERVICE_PENDING','SERVICE_RUNNING')
		and js0.job_id=js.job_id
);
