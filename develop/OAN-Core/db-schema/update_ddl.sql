
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
-- add DB ThÃ¼ringen Repository
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
        'Digitale Bibliothek ThÃ¼ringen',
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

INSERT INTO dbo.DDC_Categories VALUES ('333.7', 'Natürliche Ressourcen, Energie, Umwelt', 'Natural ressources, energy and environment');
INSERT INTO dbo.DDC_Categories VALUES ('790', 'Freizeitgestaltung, Darstellende Kunst', 'Recreational & performing arts');
INSERT INTO dbo.DDC_Categories VALUES ('990', 'Geschichte der übrigen Welt', 'General history of other areas');

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

    