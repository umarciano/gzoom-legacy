delete js
from JOB_SANDBOX js
inner join (
	select JOB_NAME, STATUS_ID, count(*) count_JOB, max(JOB_ID) MAX_JOB_ID
	from JOB_SANDBOX
	group by JOB_NAME, STATUS_ID
	having count(*) > 1
) js2 on js2.JOB_NAME=js.JOB_NAME and js2.STATUS_ID=js.STATUS_ID
where js.JOB_ID <> js2.MAX_JOB_ID and js.STATUS_ID in ('SERVICE_PENDING','SERVICE_RUNNING')
