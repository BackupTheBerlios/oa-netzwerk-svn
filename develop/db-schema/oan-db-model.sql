CREATE TABLE dbo.AggregatorMetadata (
       object_id INTEGER NOT NULL
     , harvested TIMESTAMP NOT NULL
     , PRIMARY KEY (object_id)
);

CREATE TABLE dbo.Person (
       person_id INTEGER NOT NULL
     , number INTEGER
     , firstname VARCHAR(255)
     , lastname VARCHAR(255)
     , title VARCHAR(255)
     , institution VARCHAR(255)
     , email VARCHAR(255)
     , PRIMARY KEY (person_id)
);

CREATE TABLE dbo.Keywords (
       keyword_id INTEGER NOT NULL
     , keyword VARCHAR(255)
     , lang CHAR(3)
     , PRIMARY KEY (keyword_id)
);

CREATE TABLE dbo.DCC_Categories (
       DCC_Categorie CHAR(10) NOT NULL
     , name VARCHAR(255)
     , PRIMARY KEY (DCC_Categorie)
);

CREATE TABLE dbo.DNB_Categories (
       DNB_Categorie INTEGER NOT NULL
     , name VARCHAR(255)
     , PRIMARY KEY (DNB_Categorie)
);

CREATE TABLE dbo.DINI_Set_Categories (
       DINI_set_id INTEGER NOT NULL
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (DINI_set_id)
);

CREATE TABLE dbo.Other_Categories (
       other_id INTEGER NOT NULL
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (other_id)
);

CREATE TABLE dbo.Repositories (
       repository_id INTEGER NOT NULL
     , PRIMARY KEY (repository_id)
);

CREATE TABLE dbo.Object (
       object_id INTEGER NOT NULL
     , repository_id INTEGER NOT NULL
     , harvested TIMESTAMP NOT NULL
     , repository_datestamp DATE
     , repository_identifier VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id)
     , FK_Object_1 FOREIGN KEY (repository_id)
                  REFERENCES dbo.Repositories (repository_id)
     , FK_Object_2 FOREIGN KEY (object_id)
                  REFERENCES dbo.AggregatorMetadata (object_id)
);

CREATE TABLE dbo.Titles (
       object_id INTEGER NOT NULL
     , title VARCHAR(255) NOT NULL
     , qualifier VARCHAR(50) NOT NULL
     , lang VARCHAR(3)
     , PRIMARY KEY (object_id, qualifier)
     , FK_Titles_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Object2Author (
       object_id INTEGER NOT NULL
     , author_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, author_id)
     , FK_Object2Author_2 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
     , FK_Object2Author_3 FOREIGN KEY (author_id)
                  REFERENCES dbo.Person (person_id)
);

CREATE TABLE dbo.Object2Contributor (
       object_id INTEGER NOT NULL
     , contributor_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, contributor_id)
     , FK_Object2Contributor_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
     , FK_Object2Contributor_2 FOREIGN KEY (contributor_id)
                  REFERENCES dbo.Person (person_id)
);

CREATE TABLE dbo.Object2Editor (
       object_id INTEGER NOT NULL
     , editor_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, editor_id)
     , FK_Object2Editor_2 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
     , FK_Object2Editor_1 FOREIGN KEY (editor_id)
                  REFERENCES dbo.Person (person_id)
);

CREATE TABLE dbo.Object2Keywords (
       object_id INTEGER NOT NULL
     , keyword_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, keyword_id)
     , FK_Object2Keywords_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
     , FK_Object2Keywords_2 FOREIGN KEY (keyword_id)
                  REFERENCES dbo.Keywords (keyword_id)
);

CREATE TABLE dbo.DCC_Classification (
       object_id INTEGER NOT NULL
     , DCC_Categorie CHAR(10) NOT NULL
     , PRIMARY KEY (object_id, DCC_Categorie)
     , FK_DCC_Classification_1 FOREIGN KEY (DCC_Categorie)
                  REFERENCES dbo.DCC_Categories (DCC_Categorie)
     , FK_DCC_Classification_2 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.DNB_Classification (
       object_id INTEGER NOT NULL
     , DNB_Categorie INTEGER NOT NULL
     , PRIMARY KEY (object_id, DNB_Categorie)
     , FK_DNB_Classification_1 FOREIGN KEY (DNB_Categorie)
                  REFERENCES dbo.DNB_Categories (DNB_Categorie)
     , FK_DNB_Classification_2 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.DINI_Set_Classification (
       object_id INTEGER NOT NULL
     , DINI_set_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, DINI_set_id)
     , FK_DINI_Set_Classification_1 FOREIGN KEY (DINI_set_id)
                  REFERENCES dbo.DINI_Set_Categories (DINI_set_id)
     , FK_DINI_Set_Classification_2 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Other_Classification (
       object_id INTEGER NOT NULL
     , other_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, other_id)
     , FK_Other_Classification_1 FOREIGN KEY (other_id)
                  REFERENCES dbo.Other_Categories (other_id)
     , FK_Other_Classification_2 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.DateValues (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , value TIMESTAMP
     , PRIMARY KEY (object_id, number)
     , FK_DateValues_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Description (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , abstract BLOB
     , lang VARCHAR(3)
     , PRIMARY KEY (object_id, number)
     , FK_Description_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Publisher (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
     , FK_Publisher_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Format (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , schema VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
     , FK_Format_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Identifier (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , identifier VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
     , FK_Identifier_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.RawData (
       object_id INTEGER NOT NULL
     , collected TIMESTAMP NOT NULL
     , data CHAR
     , PRIMARY KEY (object_id, collected)
     , FK_RawData_1 FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

