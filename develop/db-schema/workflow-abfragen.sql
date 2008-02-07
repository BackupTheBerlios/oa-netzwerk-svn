# 2

SELECT w1.workflow_id, w1.object_id , w1.time FROM dbo.WorkflowDB w1 JOIN dbo.WorkflowDB w2 ON (w1.object_id = w2.object_id and w1.time > w2.time) WHERE w1.service_id = 1
UNION
SELECT w1.workflow_id, w1.object_id, w1.time FROM dbo.WorkflowDB w1 WHERE w1.service_id = 1 and w1.object_id IN (SELECT w2.object_id FROM dbo.WorkflowDB w2 GROUP BY w2.object_id HAVING count(w2.object_id) = 1)

# 1

SELECT w1.workflow_id, w1.object_id , w1.time FROM dbo.WorkflowDB w1 JOIN dbo.WorkflowDB w2 ON (w1.object_id = w2.object_id and w1.time > w2.time) WHERE w1.service_id = 1 and w1.time = (SELECT max(time) FROM dbo.WorkflowDB where object_id = w1.object_id)
UNION
SELECT w1.workflow_id, w1.object_id, w1.time FROM dbo.WorkflowDB w1 WHERE w1.service_id = 1 and w1.object_id IN (SELECT w2.object_id FROM dbo.WorkflowDB w2 GROUP BY w2.object_id HAVING count(w2.object_id) = 1)


# Abfrage der zu bearbeitenden Service-ID
SELECT w1.workflow_id, w1.service_id, so.predecessor_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id and so.service_id = 2

# vollstaendige Abfrage, evtl. muss noch optimiert werden, damit die Subqueries nicht immer fuer jeden 
# Datensatz erfolgen muessen (und jeweils doppelt aufgrund des OR in der WHERE-Klausel;
# die einzige effektive Einschraenkung ist immer die Service_id;
# alternativ sollte man die Abfrage eventuell so gestalten, dass immer das ältestes Object zurückgegeben wird, ist eventuell billiger, da die Menge an Daten so kleiner wird

SELECT w1.object_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id and so.service_id = 2 
where (w1.time > (select MAX(time) from dbo.WorkflowDB where object_id = w1.object_id and service_id= so.service_id)
or w1.object_id not in (select object_id from dbo.WorkflowDB where object_id = w1.object_id and service_id = so.service_id)) group by object_id
