
-- Create a column for the Marker&Eraser to count the peculiarity (the number of harvestings a object could not be retrieved from a repository)
ALTER TABLE dbo.Object ADD peculiar_counter int DEFAULT 0 NOT NULL;


-- add SSOAR Repository
INSERT
INTO
    dbo.Repositories
    (
        name,
        url,
        oai_url,
        test_data,
        harvest_amount,
        harvest_pause,
        last_full_harvest_begin,
        listrecords
    )
    VALUES
    (
        'SSOAR - Social Science Open Access Repository',
        'http://www.ssoar.info/',
        'http://www.ssoar.info/ssoar/',
        1,
        10,
        5000,
        null,
        0
    )
    

-- add datestamp field for time of last marker&eraser run
ALTER TABLE dbo.Repositories ADD last_markereraser_begin datetime NULL;


-- 23.06.2010
-- add DB Thüringen Repository
INSERT
INTO
    dbo.Repositories
    (
        name,
        url,
        oai_url,
        test_data,
        harvest_amount,
        harvest_pause,
        last_full_harvest_begin,
        listrecords
    )
    VALUES
    (
        'Digitale Bibliothek Thüringen',
        'http://www.db-thueringen.de/',
        'http://www.db-thueringen.de/servlets/OAIDataProvider',
        1,
        10,
        5000,
        null,
        0
    )
    
 -- add TU-Berlin Repository
INSERT
INTO
    dbo.Repositories
    (
        name,
        url,
        oai_url,
        test_data,
        harvest_amount,
        harvest_pause,
        last_full_harvest_begin,
        listrecords
    )
    VALUES
    (
        'Digitales Repositorium der TU Berlin',
        'http://opus.kobv.de/tuberlin/',
        'http://opus.kobv.de/tuberlin/oai2/oai2.php',
        1,
        10,
        5000,
        null,
        0
    )

-- fixing oai_url for ssoar
UPDATE Repositories SET oai_url = 'http://www.ssoar.info/ssoar/OAIHandler' where repository_id = 34;

-- add field indicating if a repository should be actively harvested
ALTER TABLE dbo.Repositories ADD active BIT DEFAULT 1;



-- add table for repository sets
CREATE TABLE dbo.Repository_Sets (
       name CHAR(30) NOT NULL
     , repository_id NUMERIC(38,0) NOT NULL
     , PRIMARY KEY (name)
     , FOREIGN KEY (repository_id) REFERENCES dbo.Repositories (repository_id)
);

INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('HUB_EDOC_OAN', 1);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('THAA_OAN', 2);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('FRA_EPRINTS_OAN', 3);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('TUCH_MONARCH_OAN', 4);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UHE_ARTDOK_OAN', 5);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UHE_PROPYL_OAN', 6);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UHE_SAVIFA_OAN', 7);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UPO_OAN', 8);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UBI_BIESON_OAN', 9);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('TUHH_TUBDOK_OAN', 10);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UBR_OAN', 11);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UKO_KOPS_OAN', 12);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UKAS_KOBRA_OAN', 13);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('SUBG_WEBDOC_OAN', 14);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('HBKH_ASK23_OAN', 15);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('SLUBD_HSSS_OAN', 16);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UMAN_MADOC_OAN', 17);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UMAI_ARCHIMED_OAN', 18);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UHE_HEIDOK_OAN', 19);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('USAA_SCIDOK_OAN', 20);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('USAA_PSYDOK_OAN', 21);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UST_OAN', 22);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UTU_TOBIASLIB_OAN', 23);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UDUI_DUET_OAN', 24);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UHO_OAN', 25);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UULM_VTS_OAN', 26);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('FHHAN_SERWISS_OAN', 28);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('UKAR_EVASTAR_OAN', 29);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('DUE_PUBLICO_OAN', 30);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('RKI_OAN', 31);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('EUVF_OAN', 32);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('QUCOSA_OAN', 33);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('SSOAR_OAN', 34);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('DBTH_OAN', 35);
INSERT INTO dbo.Repository_Sets (name, repository_id) VALUES ('TUB_DR_OAN', 36);


-- index on repository-identifier field, as oaipmh gives out original repository identifiers
CREATE INDEX REPO_IDENTIFIER_IDX ON dbo.Object(repository_identifier);



-- DDC classification update (DINI2010)

ALTER TABLE dbo.DDC_Categories ADD name_en UNIVARCHAR(255) DEFAULT '';

INSERT INTO dbo.DDC_Categories VALUES ('333.7', 'Nat�rliche Ressourcen, Energie, Umwelt', 'Natural ressources, energy and environment');
INSERT INTO dbo.DDC_Categories VALUES ('790', 'Freizeitgestaltung, Darstellende Kunst', 'Recreational & performing arts');
INSERT INTO dbo.DDC_Categories VALUES ('990', 'Geschichte der �brigen Welt', 'General history of other areas');

UPDATE DDC_Categories SET name_en = 'Generalities, Science' WHERE DDC_Categorie = '000';
UPDATE DDC_Categories SET name_en = 'Data processing, Computer science' WHERE DDC_Categorie = '004';
UPDATE DDC_Categories SET name_en = 'Bibliography' WHERE DDC_Categorie = '010';
UPDATE DDC_Categories SET name_en = 'Library & information sciences' WHERE DDC_Categorie = '020';
UPDATE DDC_Categories SET name_en = 'General encyclopedic works' WHERE DDC_Categorie = '030';
UPDATE DDC_Categories SET name_en = 'General serials & their indexes' WHERE DDC_Categorie = '050';
UPDATE DDC_Categories SET name_en = 'General organization & museology' WHERE DDC_Categorie = '060';
UPDATE DDC_Categories SET name_en = 'News media, journalism, publishing' WHERE DDC_Categorie = '070';
UPDATE DDC_Categories SET name_en = 'General collections' WHERE DDC_Categorie = '080';
UPDATE DDC_Categories SET name_en = 'Manuscripts & rare books' WHERE DDC_Categorie = '090';
UPDATE DDC_Categories SET name_en = 'Philosophy' WHERE DDC_Categorie = '100';
UPDATE DDC_Categories SET name_en = 'Paranormal phenomena' WHERE DDC_Categorie = '130';
UPDATE DDC_Categories SET name_en = 'Psychology' WHERE DDC_Categorie = '150';
UPDATE DDC_Categories SET name_en = 'Religion' WHERE DDC_Categorie = '200';
UPDATE DDC_Categories SET name_en = 'Bible' WHERE DDC_Categorie = '220';
UPDATE DDC_Categories SET name_en = 'Christian theology' WHERE DDC_Categorie = '230';
UPDATE DDC_Categories SET name_en = 'Other & comparative religions' WHERE DDC_Categorie = '290';
UPDATE DDC_Categories SET name_en = 'Social sciences' WHERE DDC_Categorie = '300';
UPDATE DDC_Categories SET name_en = 'General statistics' WHERE DDC_Categorie = '310';
UPDATE DDC_Categories SET name_en = 'Political science' WHERE DDC_Categorie = '320';
UPDATE DDC_Categories SET name_en = 'Economics' WHERE DDC_Categorie = '330';
UPDATE DDC_Categories SET name_en = 'Natural ressources, energy and environment' WHERE DDC_Categorie = '333.7';
UPDATE DDC_Categories SET name_en = 'Law' WHERE DDC_Categorie = '340';
UPDATE DDC_Categories SET name_en = 'Public administration' WHERE DDC_Categorie = '350';
UPDATE DDC_Categories SET name_en = 'Military science' WHERE DDC_Categorie = '355';
UPDATE DDC_Categories SET name_en = 'Social services, association' WHERE DDC_Categorie = '360';
UPDATE DDC_Categories SET name_en = 'Education' WHERE DDC_Categorie = '370';
UPDATE DDC_Categories SET name_en = 'Commerce, communications, transport' WHERE DDC_Categorie = '380';
UPDATE DDC_Categories SET name_en = 'Customs, etiquette, folklore' WHERE DDC_Categorie = '390';
UPDATE DDC_Categories SET name_en = 'Language, Linguistics' WHERE DDC_Categorie = '400';
UPDATE DDC_Categories SET name_en = 'English' WHERE DDC_Categorie = '420';
UPDATE DDC_Categories SET name_en = 'Germanic' WHERE DDC_Categorie = '430';
UPDATE DDC_Categories SET name_en = 'Other Germanic languages' WHERE DDC_Categorie = '439';
UPDATE DDC_Categories SET name_en = 'Romance languages, French' WHERE DDC_Categorie = '440';
UPDATE DDC_Categories SET name_en = 'Italian, Romanian, Rhaeto-Romanic' WHERE DDC_Categorie = '450';
UPDATE DDC_Categories SET name_en = 'Spanish & Portugese languages' WHERE DDC_Categorie = '460';
UPDATE DDC_Categories SET name_en = 'Italic Latin' WHERE DDC_Categorie = '470';
UPDATE DDC_Categories SET name_en = 'Hellenic languages, Classical Greek' WHERE DDC_Categorie = '480';
UPDATE DDC_Categories SET name_en = 'Other languages' WHERE DDC_Categorie = '490';
UPDATE DDC_Categories SET name_en = 'Natural sciences & mathematics' WHERE DDC_Categorie = '500';
UPDATE DDC_Categories SET name_en = 'Mathematics' WHERE DDC_Categorie = '510';
UPDATE DDC_Categories SET name_en = 'Astronomy & allied sciences' WHERE DDC_Categorie = '520';
UPDATE DDC_Categories SET name_en = 'Physics' WHERE DDC_Categorie = '530';
UPDATE DDC_Categories SET name_en = 'Chemistry & allied sciences' WHERE DDC_Categorie = '540';
UPDATE DDC_Categories SET name_en = 'Earth sciences ' WHERE DDC_Categorie = '550';
UPDATE DDC_Categories SET name_en = 'Paleontology, Paleozoology' WHERE DDC_Categorie = '560';
UPDATE DDC_Categories SET name_en = 'Life sciences' WHERE DDC_Categorie = '570';
UPDATE DDC_Categories SET name_en = 'Botanical sciences ' WHERE DDC_Categorie = '580';
UPDATE DDC_Categories SET name_en = 'Zoological sciences' WHERE DDC_Categorie = '590';
UPDATE DDC_Categories SET name_en = 'Technology (Applied sciences)' WHERE DDC_Categorie = '600';
UPDATE DDC_Categories SET name_en = 'Medical sciences Medicine' WHERE DDC_Categorie = '610';
UPDATE DDC_Categories SET name_en = 'Engineering & allied operations' WHERE DDC_Categorie = '620';
UPDATE DDC_Categories SET name_en = 'Agriculture' WHERE DDC_Categorie = '630';
UPDATE DDC_Categories SET name_en = 'Home economics & family living ' WHERE DDC_Categorie = '640';
UPDATE DDC_Categories SET name_en = 'Management & auxiliary services' WHERE DDC_Categorie = '650';
UPDATE DDC_Categories SET name_en = 'Chemical engineering ' WHERE DDC_Categorie = '660';
UPDATE DDC_Categories SET name_en = 'Manufacturing' WHERE DDC_Categorie = '670';
UPDATE DDC_Categories SET name_en = 'Buildings' WHERE DDC_Categorie = '690';
UPDATE DDC_Categories SET name_en = 'The arts' WHERE DDC_Categorie = '700';
UPDATE DDC_Categories SET name_en = 'Civic & landscape art' WHERE DDC_Categorie = '710';
UPDATE DDC_Categories SET name_en = 'Architecture' WHERE DDC_Categorie = '720';
UPDATE DDC_Categories SET name_en = 'Plastic arts, Sculpture' WHERE DDC_Categorie = '730';
UPDATE DDC_Categories SET name_en = 'Drawing & decorative arts' WHERE DDC_Categorie = '740';
UPDATE DDC_Categories SET name_en = 'Comics, Cartoons' WHERE DDC_Categorie = '741.5';
UPDATE DDC_Categories SET name_en = 'Painting & paintings' WHERE DDC_Categorie = '750';
UPDATE DDC_Categories SET name_en = 'Graphic arts Printmaking & prints' WHERE DDC_Categorie = '760';
UPDATE DDC_Categories SET name_en = 'Photography & photographs' WHERE DDC_Categorie = '770';
UPDATE DDC_Categories SET name_en = 'Music' WHERE DDC_Categorie = '780';
UPDATE DDC_Categories SET name_en = 'Recreational & performing arts' WHERE DDC_Categorie = '790';
UPDATE DDC_Categories SET name_en = 'Public performances ' WHERE DDC_Categorie = '791';
UPDATE DDC_Categories SET name_en = 'Stage presentations' WHERE DDC_Categorie = '792';
UPDATE DDC_Categories SET name_en = 'Indoor games & amusements' WHERE DDC_Categorie = '793';
UPDATE DDC_Categories SET name_en = 'Athletic & outdoor sports & games ' WHERE DDC_Categorie = '796';
UPDATE DDC_Categories SET name_en = 'Literature & rhetoric' WHERE DDC_Categorie = '800';
UPDATE DDC_Categories SET name_en = 'American literature in English' WHERE DDC_Categorie = '810';
UPDATE DDC_Categories SET name_en = 'English & Old English literatures' WHERE DDC_Categorie = '820';
UPDATE DDC_Categories SET name_en = 'Literatures of Germanic languages' WHERE DDC_Categorie = '830';
UPDATE DDC_Categories SET name_en = 'Other Germanic literatures' WHERE DDC_Categorie = '839';
UPDATE DDC_Categories SET name_en = 'Literatures of Romance languages' WHERE DDC_Categorie = '840';
UPDATE DDC_Categories SET name_en = 'Italian, Romanian, Rhaeto-Romanic literatures' WHERE DDC_Categorie = '850';
UPDATE DDC_Categories SET name_en = 'Spanish & Portuguese literatures' WHERE DDC_Categorie = '860';
UPDATE DDC_Categories SET name_en = 'Italic literatures, Latin' WHERE DDC_Categorie = '870';
UPDATE DDC_Categories SET name_en = 'Hellenic literatures Classical Greek' WHERE DDC_Categorie = '880';
UPDATE DDC_Categories SET name_en = 'Literatures of other languages ' WHERE DDC_Categorie = '890';
UPDATE DDC_Categories SET name_en = 'Geography & history' WHERE DDC_Categorie = '900';
UPDATE DDC_Categories SET name_en = 'Geography & travel' WHERE DDC_Categorie = '910';
UPDATE DDC_Categories SET name_en = 'Geography & travel Germany' WHERE DDC_Categorie = '914.3';
UPDATE DDC_Categories SET name_en = 'Biography, genealogy, insignia' WHERE DDC_Categorie = '920';
UPDATE DDC_Categories SET name_en = 'History of the ancient world' WHERE DDC_Categorie = '930';
UPDATE DDC_Categories SET name_en = 'General history of Europe' WHERE DDC_Categorie = '940';
UPDATE DDC_Categories SET name_en = 'General history of Europe, Central Europe, Germany' WHERE DDC_Categorie = '943';
UPDATE DDC_Categories SET name_en = 'General history of Asia Far East' WHERE DDC_Categorie = '950';
UPDATE DDC_Categories SET name_en = 'General history of Africa' WHERE DDC_Categorie = '960';
UPDATE DDC_Categories SET name_en = 'General history of North America' WHERE DDC_Categorie = '970';
UPDATE DDC_Categories SET name_en = 'General history of South America' WHERE DDC_Categorie = '980';
UPDATE DDC_Categories SET name_en = 'General history of other areas ' WHERE DDC_Categorie = '990';

    

-- 29.04.2011
-- ServicesScheduling Table

-- add table for sybase
CREATE TABLE dbo.ServicesScheduling (
       job_id numeric identity NOT NULL,
       service_id numeric(38,0) NOT NULL,
       name UNIVARCHAR(256) NOT NULL,
       status UNIVARCHAR(100) NOT NULL,
       info univarchar(512),
       periodic BIT NOT NULL,
       nonperiodic_date DATETIME NULL,
       periodic_interval_type univarchar(20) NULL,
       periodic_interval_days tinyint,
       
       PRIMARY KEY (job_id),
       FOREIGN KEY (service_id) REFERENCES dbo.Services (service_id)
);

-- ServiceScheduling Table for PostgreSQL

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


-- Update der pub-type: Daten der DINI_Set_Categories auf die doc-type Daten der DINI2010 Spezifikation

--UPDATE "DINI_Set_Categories" SET name = 'doc-type:article' WHERE name = 'pub-type:article';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:book' WHERE name = 'pub-type:monograph';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:doctoralThesis' WHERE name = 'pub-type:dissertation';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:masterThesis' WHERE name = 'pub-type:masterthesis';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:report' WHERE name = 'pub-type:report';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:Text' WHERE name = 'pub-type:paper';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:conferenceObject' WHERE name = 'pub-type:conf-proceeding';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:lecture' WHERE name = 'pub-type:lecture';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:Sound' WHERE name = 'pub-type:music';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:Software' WHERE name = 'pub-type:program';
UPDATE "DINI_Set_Categories" SET name = 'doc-type:Other' WHERE name = 'pub-type:play';

-- has to be handled differently:
-- 		news is mapped to report and as report has already been mapped, all join entries with the news id will be transferred to the doc-type:report id
UPDATE "DINI_Set_Classification" SET "DINI_set_id" = (
	SELECT "DINI_set_id" FROM "DINI_Set_Categories" WHERE name = 'doc-type:report'
)
WHERE "DINI_set_id" = (
	SELECT "DINI_set_id" FROM "DINI_Set_Categories" WHERE name = 'pub-type:news'
);
DELETE FROM "DINI_Set_Categories" WHERE name = 'pub-type:news';



-- siehe http://nbn-resolving.de/urn:nbn:de:kobv:11-100109998 Seite 31
UPDATE "DINI_Set_Categories" SET name = 'doc-type:patent' WHERE name = 'pub-type:standards';

-- Einfügen der noch fehlenden Klassifikationen
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:preprint', 'Preprint', 'Preprint', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:workingPaper', 'Working Paper', 'Arbeitspapier', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:contributionToPeriodical', 'Contribution to a periodical', 'Beitrag zu einem Periodikum', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:PeriodicalPart', 'Part of a periodical', 'Teil eines Periodikums', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:bookPart', 'Part of a book', 'Teil eines Buches oder einer Monographie', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:Manuscript','Manuscript', 'Handschrift oder Manuskript', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:StudyThesis', 'Study Thesis', 'Studienarbeit', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:bachelorThesis', 'Bachelor thesis', 'Abschlussarbeit (Bachelor)', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:review', 'Review','Rezension', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:annotation', 'Annotation', 'Entscheidungs- oder Urteilsanmerkung', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:MusicalNotation', 'Musical Notation', 'Noten (Musik)', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:Periodical', 'Periodical', 'Periodikum', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:Image', 'Image', 'Bild', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:MovingImage', 'Moving Image', 'Bewegte Bilder', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:StillImage', 'Still Image', 'Einzelbild', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:CourseMaterial', 'Course Material', 'Lehrmaterial', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:Website', 'Web Site', 'Internetseite', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:CartographicMaterial', 'Cartographic Material', 'Kartographisches Material', null);
INSERT INTO "DINI_Set_Categories" (name, "setNameEng", "setNameDeu", setname) VALUES ('doc-type:ResearchData', 'Research Data', 'Forschungsdaten', null);

-- Update der DINI_Set_Classification Tabelle; Einfügen der Spalte "generated" für generierte Daten
ALTER TABLE "DINI_Set_Classification" ADD COLUMN generated bool;