/*
Navicat PGSQL Data Transfer

Source Server         : pg-local
Source Server Version : 80407
Source Host           : localhost:5432
Source Database       : oanetdb
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 80407
File Encoding         : 65001

Date: 2011-04-18 14:00:00
*/



-- ----------------------------
-- Table structure for "public"."DDC_Categories"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DDC_Categories";
CREATE TABLE "public"."DDC_Categories" (
"DDC_Categorie" varchar(10) NOT NULL CONSTRAINT ddc_id PRIMARY KEY,
"name" varchar(256) NOT NULL,
"name_en" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."DINI_Set_Categories"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DINI_Set_Categories";
CREATE TABLE "public"."DINI_Set_Categories" (
"DINI_set_id" bigserial CONSTRAINT dini_id PRIMARY KEY,
"name" varchar(256) NOT NULL,
"setNameEng" varchar(256) NOT NULL,
"setNameDeu" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."DNB_Categories"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DNB_Categories";
CREATE TABLE "public"."DNB_Categories" (
"DNB_Categorie" varchar(10) NOT NULL CONSTRAINT dnb_id PRIMARY KEY,
"name" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."DuplicatePossibilities"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DuplicatePossibilities";
CREATE TABLE "public"."DuplicatePossibilities" (
"object_id" bigserial,
"duplicate_id" bigserial,
"percentage" numeric(7,3) NOT NULL,
"reverse_percentage" numeric(7,3) NOT NULL,
CONSTRAINT dp_pk PRIMARY KEY("object_id", "duplicate_id")
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Interpolated_DDC_Categories"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Interpolated_DDC_Categories";
CREATE TABLE "public"."Interpolated_DDC_Categories" (
"Interpolated_DDC_Categorie" varchar(10) NOT NULL CONSTRAINT idc_pk PRIMARY KEY,
"name" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Interpolated_DDC_Classification"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Interpolated_DDC_Classification";
CREATE TABLE "public"."Interpolated_DDC_Classification" (
"object_id" bigserial,
"Interpolated_DDC_Categorie" varchar(10) NOT NULL,
"percentage" numeric(7,3) NOT NULL,
CONSTRAINT idc2_pk PRIMARY KEY("object_id", "Interpolated_DDC_Categorie")
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Iso639Language"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Iso639Language";
CREATE TABLE "public"."Iso639Language" (
"language_id" bigserial CONSTRAINT language_id PRIMARY KEY,
"iso639language" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Keywords"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Keywords";
CREATE TABLE "public"."Keywords" (
"keyword_id" bigserial CONSTRAINT keyword_id PRIMARY KEY,
"keyword" varchar(256) NOT NULL,
"lang" varchar(3)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Language"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Language";
CREATE TABLE "public"."Language" (
"language_id" bigserial CONSTRAINT lang_id PRIMARY KEY,
"language" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."LoginData"
-- ----------------------------

DROP TABLE IF EXISTS "public"."LoginData";
CREATE TABLE "public"."LoginData" (
"name" varchar(50) NOT NULL,
"email" varchar(100) NOT NULL,
"password" varchar(256) NOT NULL,
"superuser" boolean NOT NULL DEFAULT FALSE
)
WITH (OIDS=FALSE);



-- ----------------------------
-- Table structure for "public"."Person"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Person";
CREATE TABLE "public"."Person" (
"person_id" bigserial CONSTRAINT pk_personid PRIMARY KEY,
"firstname" varchar(100),
"lastname" varchar(100) NOT NULL,
"title" varchar(100),
"institution" varchar(256),
"email" varchar(150)
)
WITH (OIDS=FALSE);



-- ----------------------------
-- Table structure for "public"."Repositories"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Repositories";
CREATE TABLE "public"."Repositories" (
"repository_id" bigserial CONSTRAINT repository_id PRIMARY KEY,
"name" varchar(256) NOT NULL,
"url" varchar(256) NOT NULL,
"oai_url" varchar(256) NOT NULL,
"test_data" boolean NOT NULL DEFAULT true,
"harvest_amount" int NOT NULL DEFAULT 10,
"harvest_pause" int NOT NULL DEFAULT 5000,
"last_full_harvest_begin" timestamp,
"listrecords" boolean NOT NULL DEFAULT true,
"last_markereraser_begin" timestamp, 
"active" boolean NOT NULL DEFAULT false
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Services"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Services";
CREATE TABLE "public"."Services" (
"service_id" bigserial CONSTRAINT service_id PRIMARY KEY,
"name" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."UsageData_Metrics"
-- ----------------------------

DROP TABLE IF EXISTS "public"."UsageData_Metrics";
CREATE TABLE "public"."UsageData_Metrics" (
"metrics_id" bigserial CONSTRAINT metrics_id PRIMARY KEY,
"name" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);



-- ----------------------------
-- Table structure for "public"."Object"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Object";
CREATE TABLE "public"."Object" (
"object_id" bigserial CONSTRAINT object_id PRIMARY KEY,
"repository_id" bigserial CONSTRAINT repo_id REFERENCES "Repositories"(repository_id),
"harvested" timestamp NOT NULL,
"repository_datestamp" timestamp NOT NULL, 
"repository_identifier" varchar(256) NOT NULL,
"testdata" boolean NOT NULL DEFAULT true,
"failure_counter" int NOT NULL DEFAULT 0,
"peculiar" boolean NOT NULL DEFAULT false,
"outdated" boolean NOT NULL DEFAULT false,
"peculiar_counter" int NOT NULL DEFAULT 0
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."AggregatorMetadata"
-- ----------------------------

DROP TABLE IF EXISTS "public"."AggregatorMetadata";
CREATE TABLE "public"."AggregatorMetadata" (
"object_id" bigserial CONSTRAINT am_oid REFERENCES "Object"(object_id),
"harvested" timestamp NOT NULL,
CONSTRAINT pk_amd PRIMARY KEY(object_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."DateValues"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DateValues";
CREATE TABLE "public"."DateValues" (
"object_id" bigserial CONSTRAINT dv_oid REFERENCES "Object"(object_id),
"number" int NOT NULL,
"value" timestamp,
"originalValue" varchar(50) NOT NULL,
CONSTRAINT pk_dv PRIMARY KEY(object_id, number)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."DDC_Browsing_Help"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DDC_Browsing_Help";
CREATE TABLE "public"."DDC_Browsing_Help" (
"DDC_Categorie" varchar(10) NOT NULL CONSTRAINT dv_ddc REFERENCES "DDC_Categories"("DDC_Categorie"),
"name_deu" varchar(256) NOT NULL,
"name_eng" varchar(256) NOT NULL,
"direct_count" int NOT NULL,
"sub_count" int NOT NULL,
"parent_DDC" varchar(10),
CONSTRAINT pk_dbh PRIMARY KEY("DDC_Categorie")
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."DDC_Classification"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DDC_Classification";
CREATE TABLE "public"."DDC_Classification" (
"object_id" bigserial CONSTRAINT ddcbh_oid REFERENCES "Object"(object_id),
"DDC_Categorie" varchar(10) NOT NULL CONSTRAINT ddcbh_ddc REFERENCES "DDC_Categories"("DDC_Categorie"),
"generated" bool DEFAULT 'f',
CONSTRAINT pk_ddcclass PRIMARY KEY(object_id, "DDC_Categorie")
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Description"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Description";
CREATE TABLE "public"."Description" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"number" int NOT NULL,
"abstract" text NOT NULL,
"lang" varchar(3),
CONSTRAINT pk_desc PRIMARY KEY(object_id, number)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."DINI_Set_Classification"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DINI_Set_Classification";
CREATE TABLE "public"."DINI_Set_Classification" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"DINI_set_id" bigserial CONSTRAINT fk_dini_set_id REFERENCES "DINI_Set_Categories"("DINI_set_id"),
CONSTRAINT pk_dinisetclass PRIMARY KEY(object_id, "DINI_set_id")
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."DNB_Classification"
-- ----------------------------

DROP TABLE IF EXISTS "public"."DNB_Classification";
CREATE TABLE "public"."DNB_Classification" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"DNB_Categorie" varchar(10) NOT NULL CONSTRAINT fk_dnb_categorie REFERENCES "DNB_Categories"("DNB_Categorie"),
CONSTRAINT pk_dnbclass PRIMARY KEY(object_id, "DNB_Categorie")
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Format"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Format";
CREATE TABLE "public"."Format" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"number" int NOT NULL,
"schema_f" varchar(256),
CONSTRAINT pk_format PRIMARY KEY(object_id, number)
)
WITH (OIDS=FALSE);



-- ----------------------------
-- Table structure for "public"."FullTextLinks"
-- ----------------------------

DROP TABLE IF EXISTS "public"."FullTextLinks";
CREATE TABLE "public"."FullTextLinks" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"mimeformat" varchar(256) NOT NULL,
"link" varchar(256),
CONSTRAINT pk_ftl PRIMARY KEY(object_id, mimeformat)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Identifier"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Identifier";
CREATE TABLE "public"."Identifier" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"number" int NOT NULL,
"identifier" varchar(256) NOT NULL,
CONSTRAINT pk_identif PRIMARY KEY(object_id, number)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."OAIExportCache"
-- ----------------------------

DROP TABLE IF EXISTS "public"."OAIExportCache";
CREATE TABLE "public"."OAIExportCache" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"create_date" timestamp NOT NULL,
"xml" text NOT NULL,
CONSTRAINT pk_oaiexport PRIMARY KEY(object_id, create_date)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Object2Author"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Object2Author";
CREATE TABLE "public"."Object2Author" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"person_id" bigserial CONSTRAINT fk_personid REFERENCES "Person"(person_id),
"number" int NOT NULL,
CONSTRAINT pk_obj2auth PRIMARY KEY(object_id, person_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Object2Contributor"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Object2Contributor";
CREATE TABLE "public"."Object2Contributor" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"person_id" bigserial CONSTRAINT fk_personid REFERENCES "Person"(person_id),
"number" int NOT NULL,
CONSTRAINT pk_obj2cont PRIMARY KEY(object_id, person_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Object2Editor"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Object2Editor";
CREATE TABLE "public"."Object2Editor" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"person_id" bigserial CONSTRAINT fk_personid REFERENCES "Person"(person_id),
"number" int NOT NULL,
CONSTRAINT pk_obj2edit PRIMARY KEY(object_id, person_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Object2Iso639Language"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Object2Iso639Language";
CREATE TABLE "public"."Object2Iso639Language" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"language_id" bigserial CONSTRAINT fk_languageid REFERENCES "Iso639Language"(language_id),
"number" int NOT NULL,
CONSTRAINT pk_obj2iso PRIMARY KEY(object_id, language_id)
)
WITH (OIDS=FALSE);



-- ----------------------------
-- Table structure for "public"."Object2Keywords"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Object2Keywords";
CREATE TABLE "public"."Object2Keywords" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"keyword_id" bigserial CONSTRAINT fk_keywordid REFERENCES "Keywords"(keyword_id),
CONSTRAINT pk_obj2keyw PRIMARY KEY(object_id, keyword_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Object2Language"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Object2Language";
CREATE TABLE "public"."Object2Language" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"language_id" bigserial CONSTRAINT fk_languageid REFERENCES "Language"(language_id),
"number" int NOT NULL,
CONSTRAINT pk_obj2lang PRIMARY KEY(object_id, language_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Other_Categories"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Other_Categories";
CREATE TABLE "public"."Other_Categories" (
"other_id" bigserial CONSTRAINT pk_otherid PRIMARY KEY,
"name" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Other_Classification"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Other_Classification";
CREATE TABLE "public"."Other_Classification" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"other_id" bigserial CONSTRAINT fk_otherid REFERENCES "Other_Categories"(other_id),
CONSTRAINT pk_otherclass PRIMARY KEY(object_id, other_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Publisher"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Publisher";
CREATE TABLE "public"."Publisher" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"number" int NOT NULL,
"name" varchar(256) NOT NULL,
CONSTRAINT pk_publisher PRIMARY KEY(object_id, number)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."RawData"
-- ----------------------------

DROP TABLE IF EXISTS "public"."RawData";
CREATE TABLE "public"."RawData" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"repository_timestamp" timestamp NOT NULL,
"MetaDataFormat" varchar(256) NOT NULL,
"data" text NOT NULL,
"precleaned_data" text,
CONSTRAINT pk_rawdata PRIMARY KEY(object_id, repository_timestamp, "MetaDataFormat")
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Repository_Sets"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Repository_Sets";
CREATE TABLE "public"."Repository_Sets" (
"name" varchar(30) NOT NULL CONSTRAINT pk_reponame PRIMARY KEY,
"repository_id" bigserial CONSTRAINT fk_repoid REFERENCES "Repositories"(repository_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."ServiceNotify"
-- ----------------------------

DROP TABLE IF EXISTS "public"."ServiceNotify";
CREATE TABLE "public"."ServiceNotify" (
"service_id" bigserial CONSTRAINT fk_serviceid REFERENCES "Services"(service_id),
"inserttime" timestamp NOT NULL,
"finishtime" timestamp,
"urgent" boolean NOT NULL,
"complete" boolean NOT NULL,
CONSTRAINT pk_serviceid PRIMARY KEY(service_id, inserttime)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."ServicesOrder"
-- ----------------------------

DROP TABLE IF EXISTS "public"."ServicesOrder";
CREATE TABLE "public"."ServicesOrder" (
"service_id" bigserial CONSTRAINT fk_servid REFERENCES "Services"(service_id),
"predecessor_id" int8 CONSTRAINT fk_servid2 REFERENCES "Services"(service_id),
CONSTRAINT pk_servid PRIMARY KEY(service_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Titles"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Titles";
CREATE TABLE "public"."Titles" (
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"qualifier" varchar(50) NOT NULL,
"title" varchar(256) NOT NULL,
"lang" varchar(3),
CONSTRAINT pk_titles PRIMARY KEY(object_id, qualifier)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."TypeValue"
-- ----------------------------

DROP TABLE IF EXISTS "public"."TypeValue";
CREATE TABLE "public"."TypeValue" (
"type_id" bigserial CONSTRAINT pk_typeid PRIMARY KEY,
"object_id" bigserial CONSTRAINT fk_oid REFERENCES "Object"(object_id),
"value" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."UsageData_Months"
-- ----------------------------

DROP TABLE IF EXISTS "public"."UsageData_Months";
CREATE TABLE "public"."UsageData_Months" (
"object_id" bigserial,
"metrics_id" bigserial CONSTRAINT fk_metricsid REFERENCES "UsageData_Metrics"(metrics_id),
"counter" bigserial,
"counted_for_date" timestamp NOT NULL,
CONSTRAINT pk_usagedatamonths PRIMARY KEY(object_id, metrics_id, counted_for_date)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."UsageData_Overall"
-- ----------------------------

DROP TABLE IF EXISTS "public"."UsageData_Overall";
CREATE TABLE "public"."UsageData_Overall" (
"object_id" bigserial,
"metrics_id" bigserial CONSTRAINT fk_metricsid REFERENCES "UsageData_Metrics"(metrics_id),
"counter" bigserial,
"last_update" timestamp NOT NULL,
CONSTRAINT pk_usagedataoverall PRIMARY KEY(object_id, metrics_id)
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."WorkflowDB"
-- ----------------------------

DROP TABLE IF EXISTS "public"."WorkflowDB";
CREATE TABLE "public"."WorkflowDB" (
"workflow_id" bigserial CONSTRAINT pk_workflowid PRIMARY KEY,
"object_id" bigserial CONSTRAINT fk_objectid REFERENCES "Object"(object_id),
"service_id" bigserial CONSTRAINT fk_serviceid REFERENCES "Services"(service_id),
"time" timestamp NOT NULL
)
WITH (OIDS=FALSE);


-- ----------------------------
-- Table structure for "public"."Worklist"
-- ----------------------------

DROP TABLE IF EXISTS "public"."Worklist";
CREATE TABLE "public"."Worklist" (
"worklist_id" bigserial CONSTRAINT pk_worklistid PRIMARY KEY,
"object_id" bigserial CONSTRAINT fk_objectid REFERENCES "Object"(object_id),
"service_id" bigserial CONSTRAINT fk_serviceid REFERENCES "Services"(service_id),
"time" timestamp NOT NULL
)
WITH (OIDS=FALSE);

-- ----------------------------
-- Table structure for "public"."ServicesScheduling"
-- ----------------------------

DROP TABLE IF EXISTS "public"."ServicesScheduling";
CREATE TABLE "public"."ServicesScheduling" (
	   job_id serial CONSTRAINT servicesscheduling_job_id PRIMARY KEY,
       service_id bigserial CONSTRAINT servicesscheduling_repo_id REFERENCES "Repositories"(repository_id),
       name varchar(256) NOT NULL,
       status varchar(100) NOT NULL,
       info varchar(512),
       periodic bool NOT NULL,
       nonperiodic_date timestamp,
       periodic_interval_type varchar(30),
       periodic_interval_days smallint
) WITH (OIDS=FALSE);



-- ----------------------------
-- CREATE INDEXES
-- ----------------------------



