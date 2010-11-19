CREATE TABLE dbo.LoginData (
	name UNIVARCHAR(50) NOT NULL UNIQUE,
	email UNIVARCHAR(100) NOT NULL,
	password UNIVARCHAR(255) NOT NULL,
	superuser BIT DEFAULT 0
);

CREATE TABLE dbo.ServiceNotify (
       service_id NUMERIC(38) NOT NULL
	 , inserttime DATETIME NOT NULL
	 , finishtime DATETIME NULL
     , urgent BIT NOT NULL
	 , complete BIT DEFAULT 0
     , PRIMARY KEY (service_id, inserttime)
);


CREATE TABLE dbo.AggregatorMetadata (
       object_id NUMERIC(38) NOT NULL
     , harvested DATETIME NOT NULL
     , PRIMARY KEY (object_id)
);


CREATE TABLE dbo.Person (
       person_id NUMERIC(38) IDENTITY
     , firstname UNIVARCHAR(100) NULL
     , lastname UNIVARCHAR(100)
     , title UNIVARCHAR(100) NULL
     , institution UNIVARCHAR(255) NULL
     , email UNIVARCHAR(150) NULL
     , PRIMARY KEY (person_id)
);

CREATE TABLE dbo.Language (
       language_id NUMERIC(38) IDENTITY
     , language UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (language_id)
);

CREATE TABLE dbo.Iso639Language (
       language_id NUMERIC(38) IDENTITY
     , iso639language UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (language_id)
);


CREATE TABLE dbo.Keywords (
       keyword_id NUMERIC(38) IDENTITY
     , keyword UNIVARCHAR(255) NOT NULL
     , lang UNICHAR(3) NULL
     , PRIMARY KEY (keyword_id)
);

CREATE TABLE dbo.DDC_Categories (
       DDC_Categorie UNIVARCHAR(10) NOT NULL
     , name UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (DDC_Categorie)
);

CREATE TABLE dbo.DNB_Categories (
       DNB_Categorie UNIVARCHAR(10) NOT NULL
     , name UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (DNB_Categorie)
);

CREATE TABLE dbo.DINI_Set_Categories (
       DINI_set_id NUMERIC(38) IDENTITY
     , name UNIVARCHAR(255) NOT NULL
	 , setNameEng UNIVARCHAR(255) NULL
	 , setNameDeu UNIVARCHAR(255) NULL
     , PRIMARY KEY (DINI_set_id)
);

CREATE TABLE dbo.Other_Categories (
       other_id NUMERIC(38) IDENTITY
     , name UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (other_id)
);

CREATE TABLE dbo.Services (
       service_id NUMERIC(38) IDENTITY
     , name UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (service_id)
);

CREATE TABLE dbo.ServicesOrder (
       service_id NUMERIC(38) NOT NULL
     , predecessor_id NUMERIC(38) NULL
     , PRIMARY KEY (service_id)
);

CREATE TABLE dbo.Repositories (
       repository_id NUMERIC(38) IDENTITY
     , name UNIVARCHAR(255) NOT NULL
     , url UNIVARCHAR(255)
     , oai_url UNIVARCHAR(255)
	 , test_data BIT default 1
	 , harvest_amount INT default 10
	 , harvest_pause INT default 5000
     , last_full_harvest_begin DATETIME NULL
	 , listrecords BIT default 0
     , PRIMARY KEY (repository_id)
);

CREATE TABLE dbo.Object (
       object_id NUMERIC(38) IDENTITY
     , repository_id NUMERIC(38) NOT NULL
     , harvested DATETIME NOT NULL
     , repository_datestamp DATETIME NOT NULL
     , repository_identifier UNIVARCHAR(255) NOT NULL
     , testdata BIT DEFAULT 0
     , failure_counter INTEGER DEFAULT 0
	 , peculiar BIT DEFAULT 0
	 , outdated BIT DEFAULT 0
     , PRIMARY KEY (object_id)
);

CREATE TABLE dbo.Titles (
       object_id NUMERIC(38) NOT NULL
     , title UNIVARCHAR(255) NOT NULL
     , qualifier UNIVARCHAR(50) NOT NULL
     , lang UNIVARCHAR(3) NULL
     , PRIMARY KEY (object_id, qualifier)
);


CREATE TABLE dbo.Object2Author (
       object_id NUMERIC(38) NOT NULL
     , person_id NUMERIC(38) NOT NULL
     , number INTEGER
     , PRIMARY KEY (object_id, person_id)
);

CREATE TABLE dbo.Object2Contributor (
       object_id NUMERIC(38) NOT NULL
     , person_id NUMERIC(38) NOT NULL
     , number INTEGER
     , PRIMARY KEY (object_id, person_id)
);

CREATE TABLE dbo.Object2Editor (
       object_id NUMERIC(38) NOT NULL
     , person_id NUMERIC(38) NOT NULL
     , number INTEGER
     , PRIMARY KEY (object_id, person_id)
);


CREATE TABLE dbo.Object2Keywords (
       object_id NUMERIC(38) NOT NULL
     , keyword_id NUMERIC(38) NOT NULL
     , PRIMARY KEY (object_id, keyword_id)
);

CREATE TABLE dbo.Object2Language (
       object_id NUMERIC(38) NOT NULL
     , language_id NUMERIC(38) NOT NULL
     , number INTEGER
     , PRIMARY KEY (object_id, language_id)
);

CREATE TABLE dbo.Object2Iso639Language (
       object_id NUMERIC(38) NOT NULL
     , language_id NUMERIC(38) NOT NULL
     , number INTEGER
     , PRIMARY KEY (object_id, language_id)
);

CREATE TABLE dbo.DDC_Classification (
       object_id NUMERIC(38) NOT NULL
     , DDC_Categorie UNIVARCHAR(10) NOT NULL
     , PRIMARY KEY (object_id, DDC_Categorie)
);

CREATE TABLE dbo.DNB_Classification (
       object_id NUMERIC(38) NOT NULL
     , DNB_Categorie UNIVARCHAR(10) NOT NULL
     , PRIMARY KEY (object_id, DNB_Categorie)
);

CREATE TABLE dbo.DINI_Set_Classification (
       object_id NUMERIC(38) NOT NULL
     , DINI_set_id NUMERIC(38) NOT NULL
     , PRIMARY KEY (object_id, DINI_set_id)
);

CREATE TABLE dbo.Other_Classification (
       object_id NUMERIC(38) NOT NULL
     , other_id NUMERIC(38) NOT NULL
     , PRIMARY KEY (object_id, other_id)
);

CREATE TABLE dbo.DateValues (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , value DATETIME NULL
	 , originalValue UNIVARCHAR(50) NOT NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.Description (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , abstract UNITEXT
     , lang UNIVARCHAR(3) NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.Publisher (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , name UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.Format (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , schema_f UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.Identifier (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , identifier UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.DuplicatePossibilities (
       object_id NUMERIC(38) NOT NULL
     , duplicate_id NUMERIC(38) NOT NULL
     , percentage NUMERIC(7,3) NOT NULL
	 , reverse_percentage NUMERIC(7,3) NOT NULL
     , PRIMARY KEY (object_id, duplicate_id)
);

CREATE TABLE dbo.WorkflowDB (
       workflow_id NUMERIC(38) IDENTITY
     , object_id NUMERIC(38) NOT NULL
     , time DATETIME
     , service_id NUMERIC(38) NOT NULL
     , PRIMARY KEY (workflow_id)
);


CREATE TABLE dbo.Worklist (
       worklist_id NUMERIC(38) IDENTITY
     , object_id NUMERIC(38) NOT NULL
     , time DATETIME
     , service_id NUMERIC(38) NOT NULL
     , PRIMARY KEY (worklist_id)
);



CREATE TABLE dbo.RawData (
       object_id NUMERIC(38) NOT NULL
     , repository_timestamp DATETIME NOT NULL
     , MetaDataFormat UNIVARCHAR(255) NOT NULL
     , data UNITEXT
     , precleaned_data UNITEXT NULL
     , PRIMARY KEY (object_id, repository_timestamp, MetaDataFormat)
)

CREATE TABLE dbo.TypeValue (
       type_id NUMERIC(38) IDENTITY
     , object_id NUMERIC(38) NOT NULL
     , value UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (type_id)
);


CREATE TABLE dbo.FullTextLinks (
       object_id NUMERIC(38) NOT NULL
     , mimeformat UNIVARCHAR(255) NOT NULL
	 , link  UNIVARCHAR(255) NULL
     , PRIMARY KEY (object_id, mimeformat)
);

ALTER TABLE dbo.Object
  ADD CONSTRAINT FK_Object_1
      FOREIGN KEY (repository_id)
      REFERENCES dbo.Repositories (repository_id);



ALTER TABLE dbo.Titles
  ADD CONSTRAINT FK_Titles_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);


ALTER TABLE dbo.TypeValue
  ADD CONSTRAINT FK_TypeValue_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);


ALTER TABLE dbo.Object2Author
  ADD CONSTRAINT FK_Object2Author_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Object2Author
  ADD CONSTRAINT FK_Object2Author_3
      FOREIGN KEY (person_id)
      REFERENCES dbo.Person (person_id);

ALTER TABLE dbo.Object2Contributor
  ADD CONSTRAINT FK_Object2Contributor_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Object2Contributor
  ADD CONSTRAINT FK_Object2Contributor_2
      FOREIGN KEY (person_id)
      REFERENCES dbo.Person (person_id);

ALTER TABLE dbo.Object2Editor
  ADD CONSTRAINT FK_Object2Editor_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Object2Editor
  ADD CONSTRAINT FK_Object2Editor_3
      FOREIGN KEY (person_id)
      REFERENCES dbo.Person (person_id);

ALTER TABLE dbo.Object2Keywords
  ADD CONSTRAINT FK_Object2Keywords_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Object2Keywords
  ADD CONSTRAINT FK_Object2Keywords_2
      FOREIGN KEY (keyword_id)
      REFERENCES dbo.Keywords (keyword_id);

ALTER TABLE dbo.DDC_Classification
  ADD CONSTRAINT FK_DDC_Classification_1
      FOREIGN KEY (DDC_Categorie)
      REFERENCES dbo.DDC_Categories (DDC_Categorie);

ALTER TABLE dbo.DDC_Classification
  ADD CONSTRAINT FK_DDC_Classification_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.DNB_Classification
  ADD CONSTRAINT FK_DNB_Classification_1
      FOREIGN KEY (DNB_Categorie)
      REFERENCES dbo.DNB_Categories (DNB_Categorie);

ALTER TABLE dbo.DNB_Classification
  ADD CONSTRAINT FK_DNB_Classification_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.DINI_Set_Classification
  ADD CONSTRAINT FK_DINI_Set_Classification_1
      FOREIGN KEY (DINI_set_id)
      REFERENCES dbo.DINI_Set_Categories (DINI_set_id);

ALTER TABLE dbo.DINI_Set_Classification
  ADD CONSTRAINT FK_DINI_Set_Classification_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Other_Classification
  ADD CONSTRAINT FK_Other_Classification_1
      FOREIGN KEY (other_id)
      REFERENCES dbo.Other_Categories (other_id);

ALTER TABLE dbo.Other_Classification
  ADD CONSTRAINT FK_Other_Classification_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.DateValues
  ADD CONSTRAINT FK_DateValues_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Description
  ADD CONSTRAINT FK_Description_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Publisher
  ADD CONSTRAINT FK_Publisher_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Format
  ADD CONSTRAINT FK_Format_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Identifier
  ADD CONSTRAINT FK_Identifier_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.DuplicatePossibilities
  ADD CONSTRAINT FK_DuplicatePossibilities_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.DuplicatePossibilities
  ADD CONSTRAINT FK_DuplicatePossibilities_2
      FOREIGN KEY (duplicate_id)
      REFERENCES dbo.Object (object_id);


ALTER TABLE dbo.ServiceNotify
  ADD CONSTRAINT FK_ServiceNotify_1
      FOREIGN KEY (service_id)
      REFERENCES dbo.Services (service_id);


ALTER TABLE dbo.WorkflowDB
  ADD CONSTRAINT FK_WorkflowDB_1
      FOREIGN KEY (service_id)
      REFERENCES dbo.Services (service_id);

ALTER TABLE dbo.WorkflowDB
  ADD CONSTRAINT FK_WorkflowDB_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Worklist
  ADD CONSTRAINT FK_Worklist_1
      FOREIGN KEY (service_id)
      REFERENCES dbo.Services (service_id);

ALTER TABLE dbo.Worklist
  ADD CONSTRAINT FK_Worklist_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);



ALTER TABLE dbo.RawData
  ADD CONSTRAINT FK_RawData_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);


ALTER TABLE dbo.AggregatorMetadata
  ADD CONSTRAINT FK_Object_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Object2Language
  ADD CONSTRAINT FK_Object2Language_1
      FOREIGN KEY (language_id)
      REFERENCES dbo.Language (language_id);

ALTER TABLE dbo.Object2Iso639Language
  ADD CONSTRAINT FK_Object2Iso639Language_1
      FOREIGN KEY (language_id)
      REFERENCES dbo.Iso639Language (language_id);


ALTER TABLE dbo.Object2Language
  ADD CONSTRAINT FK_Object2Language_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.Object2Iso639Language
  ADD CONSTRAINT FK_Object2Iso639Language_2
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);


ALTER TABLE dbo.ServicesOrder
  ADD CONSTRAINT FK_ServicesOrder_1
      FOREIGN KEY (service_id)
      REFERENCES dbo.Services (service_id);

ALTER TABLE dbo.ServicesOrder
  ADD CONSTRAINT FK_ServicesOrder_2
      FOREIGN KEY (predecessor_id)
      REFERENCES dbo.Services (service_id);

ALTER TABLE dbo.FullTextLinks
  ADD CONSTRAINT FK_FullTextLinks_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);


CREATE TRIGGER worklisttest
ON dbo.WorkflowDB
FOR INSERT AS
DECLARE @object_id NUMERIC(38) 
DECLARE @service_id NUMERIC(38)
DECLARE @zeit DATETIME
SELECT @object_id = object_id FROM inserted 
SELECT @service_id = service_id FROM inserted
SELECT @zeit = time FROM inserted
BEGIN 
   INSERT INTO dbo.Worklist (object_id, time, service_id) values (@object_id, @zeit, @service_id)
END;


-- Daten fuer den OAI-Export (ListRecords)
CREATE TABLE dbo.OAIExportCache (
       object_id NUMERIC(38) NOT NULL
     , create_date DATETIME NOT NULL
	 , xml UNITEXT
     , PRIMARY KEY (object_id, create_date)
);


ALTER TABLE dbo.OAIExportCache
  ADD CONSTRAINT FK_Object_2_OAIExportCache
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);


-- Daten fuer den DDC-Browsing
CREATE TABLE dbo.DDC_Browsing_Help (
       DDC_Categorie UNIVARCHAR(10) NOT NULL
     , name_deu UNIVARCHAR(255) NOT NULL
	 , name_eng UNIVARCHAR(255) NOT NULL
	 , direct_count INT DEFAULT 0
	 , sub_count INT DEFAULT 0
	 , parent_DDC UNIVARCHAR(10) NULL
     , PRIMARY KEY (DDC_Categorie)
);

ALTER TABLE dbo.DDC_Browsing_Help
  ADD CONSTRAINT FK_DDC_Browsing_Help_2_DDC_Categories
      FOREIGN KEY (DDC_Categorie)
      REFERENCES dbo.DDC_Categories (DDC_Categorie);

CREATE TABLE dbo.Interpolated_DDC_Categories (
       Interpolated_DDC_Categorie UNIVARCHAR(10) NOT NULL
     , name UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (Interpolated_DDC_Categorie)
);

CREATE TABLE dbo.Interpolated_DDC_Classification (
       object_id NUMERIC(38) NOT NULL
     , Interpolated_DDC_Categorie UNIVARCHAR(10) NOT NULL
     , percentage NUMERIC(7,3) NOT NULL
     , PRIMARY KEY (object_id, Interpolated_DDC_Categorie)
);

CREATE TABLE dbo.UsageData_Metrics (
       metrics_id NUMERIC(38) IDENTITY
     , name UNIVARCHAR(255) NOT NULL
     , PRIMARY KEY (metrics_id)
);

CREATE TABLE dbo.UsageData_Months (
       object_id NUMERIC(38) NOT NULL
     , metrics_id NUMERIC(38) NOT NULL
     , counter NUMERIC(38) DEFAULT 0
     , counted_for_date DATETIME NOT NULL
     , PRIMARY KEY (object_id, metrics_id, counted_for_date)
);

ALTER TABLE dbo.UsageData_Months
  ADD CONSTRAINT FK_UsageData_Months_2_UsageData_Metrics
      FOREIGN KEY (metrics_id)
      REFERENCES dbo.UsageData_Metrics (metrics_id);

CREATE TABLE dbo.UsageData_Overall (
       object_id NUMERIC(38) NOT NULL
     , metrics_id NUMERIC(38) NOT NULL
     , counter NUMERIC(38) DEFAULT 0
     , last_update DATETIME NOT NULL
     , PRIMARY KEY (object_id, metrics_id)
);

ALTER TABLE dbo.UsageData_Overall
  ADD CONSTRAINT FK_UsageData_Overall_2_UsageData_Metrics
      FOREIGN KEY (metrics_id)
      REFERENCES dbo.UsageData_Metrics (metrics_id);