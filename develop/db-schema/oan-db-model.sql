CREATE TABLE dbo.AggregatorMetadata (
       object_id INTEGER NOT NULL
     , harvested datetime NOT NULL
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
     , harvested datetime NOT NULL
     , repository_datestamp DATETIME
     , repository_identifier VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id)
     , FOREIGN KEY (repository_id)
                  REFERENCES dbo.Repositories (repository_id)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.AggregatorMetadata (object_id)
);

CREATE TABLE dbo.Titles (
       object_id INTEGER NOT NULL
     , title VARCHAR(255) NOT NULL
     , qualifier VARCHAR(50) NOT NULL
     , lang VARCHAR(3)
     , PRIMARY KEY (object_id, qualifier)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Object2Author (
       object_id INTEGER NOT NULL
     , author_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, author_id)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
     , FOREIGN KEY (author_id)
                  REFERENCES dbo.Person (person_id)
);

CREATE TABLE dbo.Object2Contributor (
       object_id INTEGER NOT NULL
     , contributor_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, contributor_id)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
     , FOREIGN KEY (contributor_id)
                  REFERENCES dbo.Person (person_id)
);

CREATE TABLE dbo.Object2Editor (
       object_id INTEGER NOT NULL
     , editor_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, editor_id)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
     , FOREIGN KEY (editor_id)
                  REFERENCES dbo.Person (person_id)
);

CREATE TABLE dbo.Object2Keywords (
       object_id INTEGER NOT NULL
     , keyword_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, keyword_id)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
     , FOREIGN KEY (keyword_id)
                  REFERENCES dbo.Keywords (keyword_id)
);

CREATE TABLE dbo.DCC_Classification (
       object_id INTEGER NOT NULL
     , DCC_Categorie CHAR(10) NOT NULL
     , PRIMARY KEY (object_id, DCC_Categorie)
     , FOREIGN KEY (DCC_Categorie)
                  REFERENCES dbo.DCC_Categories (DCC_Categorie)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.DNB_Classification (
       object_id INTEGER NOT NULL
     , DNB_Categorie INTEGER NOT NULL
     , PRIMARY KEY (object_id, DNB_Categorie)
     , FOREIGN KEY (DNB_Categorie)
                  REFERENCES dbo.DNB_Categories (DNB_Categorie)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.DINI_Set_Classification (
       object_id INTEGER NOT NULL
     , DINI_set_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, DINI_set_id)
     , FOREIGN KEY (DINI_set_id)
                  REFERENCES dbo.DINI_Set_Categories (DINI_set_id)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Other_Classification (
       object_id INTEGER NOT NULL
     , other_id INTEGER NOT NULL
     , PRIMARY KEY (object_id, other_id)
     , FOREIGN KEY (other_id)
                  REFERENCES dbo.Other_Categories (other_id)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.DateValues (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , value datetime
     , PRIMARY KEY (object_id, number)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Description (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , abstract TEXT
     , lang VARCHAR(3)
     , PRIMARY KEY (object_id, number)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Publisher (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , name VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Format (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , schema_f VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.Identifier (
       object_id INTEGER NOT NULL
     , number INTEGER NOT NULL
     , identifier VARCHAR(255) NOT NULL
     , PRIMARY KEY (object_id, number)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);

CREATE TABLE dbo.RawData (
       object_id INTEGER NOT NULL
     , collected datetime NOT NULL
     , data TEXT
     , PRIMARY KEY (object_id, collected)
     , FOREIGN KEY (object_id)
                  REFERENCES dbo.Object (object_id)
);
