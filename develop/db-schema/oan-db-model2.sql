CREATE TABLE dbo.AggregatorMetadata (
       object_id NUMERIC(38) NOT NULL
     , harvested DATETIME NOT NULL
     , PRIMARY KEY (object_id)
);


CREATE TABLE dbo.Person (
       person_id NUMERIC(38) IDENTITY
     , firstname VARCHAR(255) NULL
     , lastname VARCHAR(255)
     , title VARCHAR(255) NULL
     , institution VARCHAR(255) NULL
     , email VARCHAR(255) NULL
     , PRIMARY KEY (person_id)
);

CREATE TABLE dbo.Language (
       language_id NUMERIC(38) IDENTITY
     , language VARCHAR(255) NOT NULL
     , PRIMARY KEY (language_id)
);

CREATE TABLE dbo.Keywords (
       keyword_id NUMERIC(38) IDENTITY
     , keyword VARCHAR(255) NOT NULL
     , lang CHAR(3) NULL
     , PRIMARY KEY (keyword_id)
);

CREATE TABLE dbo.DCC_Categories (
       DCC_Categorie CHAR(10) NOT NULL
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (DCC_Categorie)
);

CREATE TABLE dbo.DNB_Categories (
       DNB_Categorie NUMERIC(38) IDENTITY
     , name VARCHAR(255)
     , PRIMARY KEY (DNB_Categorie)
);

CREATE TABLE dbo.DINI_Set_Categories (
       DINI_set_id NUMERIC(38) IDENTITY
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (DINI_set_id)
);

CREATE TABLE dbo.Other_Categories (
       other_id NUMERIC(38) IDENTITY
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (other_id)
);

CREATE TABLE dbo.Services (
       service_id NUMERIC(38) IDENTITY
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (service_id)
);

CREATE TABLE dbo.ServicesOrder (
       service_id NUMERIC(38) NOT NULL
     , predecessor_id NUMERIC(38) NULL
     , PRIMARY KEY (service_id)
);

CREATE TABLE dbo.Repositories (
       repository_id NUMERIC(38) IDENTITY
     , name VARCHAR(255) NOT NULL
     , url VARCHAR(255)
     , oai_url VARCHAR(255)
	 , test_data BIT default 1
	 , harvest_amount INT default 10
	 , harvest_pause INT default 5000
		
     , PRIMARY KEY (repository_id)
);

CREATE TABLE dbo.Object (
       object_id NUMERIC(38) IDENTITY
     , repository_id NUMERIC(38) NOT NULL
     , harvested DATETIME NOT NULL
     , repository_datestamp DATETIME NOT NULL
     , repository_identifier VARCHAR(255) NOT NULL
     , testdata BIT DEFAULT 0
     , failure_counter INTEGER DEFAULT 0
     , PRIMARY KEY (object_id)
);

CREATE TABLE dbo.Titles (
       object_id NUMERIC(38) NOT NULL
     , title VARCHAR(255) NOT NULL
     , qualifier VARCHAR(50) NOT NULL
     , lang VARCHAR(3) NULL
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

CREATE TABLE dbo.DCC_Classification (
       object_id NUMERIC(38) NOT NULL
     , DCC_Categorie CHAR(10) NOT NULL
     , PRIMARY KEY (object_id, DCC_Categorie)
);

CREATE TABLE dbo.DNB_Classification (
       object_id NUMERIC(38) NOT NULL
     , DNB_Categorie NUMERIC(38) NOT NULL
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
     , value DATETIME
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.Description (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , abstract TEXT
     , lang VARCHAR(3) NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.Publisher (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.Format (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , schema_f VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.Identifier (
       object_id NUMERIC(38) NOT NULL
     , number INTEGER NOT NULL
     , identifier VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
);

CREATE TABLE dbo.DuplicatePossibilties (
       object_id NUMERIC(38) NOT NULL
     , duplicate_id NUMERIC(38) NOT NULL
     , percentage NUMERIC NOT NULL
     , PRIMARY KEY (object_id)
);

CREATE TABLE dbo.WorkflowDB (
       workflow_id NUMERIC(38) IDENTITY
     , object_id NUMERIC(38) NOT NULL
     , time DATETIME
     , service_id NUMERIC(38) NOT NULL
     , PRIMARY KEY (workflow_id)
);



CREATE TABLE dbo.RawData (
       object_id NUMERIC(38) NOT NULL
     , repository_timestamp DATETIME NOT NULL
     , MetaDataFormat VARCHAR(255) NOT NULL
     , data TEXT
     , precleaned_data TEXT NULL
     , PRIMARY KEY (object_id, repository_timestamp, MetaDataFormat)
)

CREATE TABLE dbo.TypeValue (
       type_id NUMERIC(38) IDENTITY
     , object_id NUMERIC(38) NOT NULL
     , value VARCHAR(255) NOT NULL
     , PRIMARY KEY (type_id)
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

ALTER TABLE dbo.DCC_Classification
  ADD CONSTRAINT FK_DCC_Classification_1
      FOREIGN KEY (DCC_Categorie)
      REFERENCES dbo.DCC_Categories (DCC_Categorie);

ALTER TABLE dbo.DCC_Classification
  ADD CONSTRAINT FK_DCC_Classification_2
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

ALTER TABLE dbo.DuplicatePossibilties
  ADD CONSTRAINT FK_DuplicatePossibilties_1
      FOREIGN KEY (object_id)
      REFERENCES dbo.Object (object_id);

ALTER TABLE dbo.DuplicatePossibilties
  ADD CONSTRAINT FK_DuplicatePossibilties_2
      FOREIGN KEY (duplicate_id)
      REFERENCES dbo.Object (object_id);


ALTER TABLE dbo.WorkflowDB
  ADD CONSTRAINT FK_WorkflowDB_1
      FOREIGN KEY (service_id)
      REFERENCES dbo.Services (service_id);

ALTER TABLE dbo.WorkflowDB
  ADD CONSTRAINT FK_WorkflowDB_2
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

ALTER TABLE dbo.Object2Language
  ADD CONSTRAINT FK_Object2Language_2
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
