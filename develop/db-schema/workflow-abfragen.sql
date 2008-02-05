# 2

SELECT w1.workflow_id, w1.object_id , w1.time FROM dbo.WorkflowDB w1 JOIN dbo.WorkflowDB w2 ON (w1.object_id = w2.object_id and w1.time > w2.time) WHERE w1.service_id = 1
UNION
SELECT w1.workflow_id, w1.object_id, w1.time FROM dbo.WorkflowDB w1 WHERE w1.service_id = 1 and w1.object_id IN (SELECT w2.object_id FROM dbo.WorkflowDB w2 GROUP BY w2.object_id HAVING count(w2.object_id) = 1)

# 1

SELECT w1.workflow_id, w1.object_id , w1.time FROM dbo.WorkflowDB w1 JOIN dbo.WorkflowDB w2 ON (w1.object_id = w2.object_id and w1.time > w2.time) WHERE w1.service_id = 1 and w1.time = (SELECT max(time) FROM dbo.WorkflowDB where object_id = w1.object_id)
UNION
SELECT w1.workflow_id, w1.object_id, w1.time FROM dbo.WorkflowDB w1 WHERE w1.service_id = 1 and w1.object_id IN (SELECT w2.object_id FROM dbo.WorkflowDB w2 GROUP BY w2.object_id HAVING count(w2.object_id) = 1)


# Abfrage der zu bearbeitenden Service-ID

SELECT w1.workflow_id, w1.service_id, so1.predecessor_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so1 ON w1.service_id = so1.predecessor_id and so1.service_id = 2