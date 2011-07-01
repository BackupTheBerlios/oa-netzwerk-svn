Delete from  "RawData";
Delete from "WorkflowDB";
Delete from "Object";

SELECT setval('"Object_object_id_seq"', max(object_id)) FROM "Object";
SELECT setval('"RawData_object_id_seq"', max(object_id)) FROM "RawData";
SELECT setval('"WorkflowDB_workflow_id_seq"', max(workflow_id)) FROM "WorkflowDB";