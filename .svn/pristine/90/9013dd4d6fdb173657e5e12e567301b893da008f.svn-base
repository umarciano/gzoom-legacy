-- Duplicated Jobs
delete
from job_sandbox 
where status_id in ('SERVICE_PENDING','SERVICE_RUNNING') and job_id not in 
(
    select max(job_id) max_job_id
    from job_sandbox
    where status_id in ('SERVICE_PENDING','SERVICE_RUNNING')
    group by job_name, status_id
    having count(*) >= 1  
)