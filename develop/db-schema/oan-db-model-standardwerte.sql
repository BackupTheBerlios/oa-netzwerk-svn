insert into dbo.Repositories (name, url, oai_url) values ('EDOC HU', 'http://edoc.hu-berlin.de','http://http://edoc.hu-berlin.de/OAI-2.0');

insert into dbo.Services (service_id, name) values ('Harvester');
insert into dbo.Services (name) values ('Aggregator');

insert into dbo.ServicesOrder (service_id, predecessor_id) values (1, null);
insert into dbo.ServicesOrder (service_id, predecessor_id) values (2, 1);
# insert into dbo.Services (service_id, name) values (1, 'Harvester');
# insert into dbo.Repositories (repository_id, name, url, oai_url) values (1, 'EDOC HU', 'http://edoc.hu-berlin.de','http://http://edoc.hu-berlin.de/OAI-2.0');

### DDC-Werte
insert into dbo.DDC_Categories (DDC_Categorie, name) values('000', 'Allgemeines, Wissenschaft')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('004', 'Informatik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('010', 'Bibliografien')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('020', 'Bibliotheks- und Informationswissenschaft')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('030', 'Enzyklopädien')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('050', 'Zeitschriften, fortlaufende Sammelwerke')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('060', 'Organisationen, Museumswissenschaft')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('070', 'Geografie, Reisen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('080', 'Allgemeine Sammelwerke')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('090', 'Handschriften, seltene Bücher')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('100', 'Philosophie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('130', 'Parapsychologie, Okkultismus')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('150', 'Psychologie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('200', 'Religion, Religionsphilosophie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('220', 'Bibel')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('230', 'Theologie, Christentum')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('290', 'Andere Religionen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('300', 'Sozialwissenschaften, Soziologie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('310', 'Statistik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('320', 'Politik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('330', 'Wirtschaft')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('340', 'Recht')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('350', 'Öffentliche Verwaltung')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('355', 'Militär')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('360', 'Soziale Probleme, Sozialarbeit')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('370', 'Erziehung, Schul- und Bildungswesen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('380', 'Handel, Kommunikation, Verkehr')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('390', 'Ethnologie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('400', 'Sprachwissenschaft, Linguistik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('420', 'Englisch')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('430', 'Deutsch')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('439', 'Andere germanische Sprachen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('440', 'Französisch, romanische Sprachen allgemein')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('450', 'Italienisch, Rumänisch, Rätoromanisch')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('460', 'Spanisch, Portugiesisch')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('470', 'Latein')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('480', 'Griechisch')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('490', 'Andere Sprachen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('500', 'Naturwissenschaften')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('510', 'Mathematik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('520', 'Astronomie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('530', 'Physik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('540', 'Chemie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('550', 'Geowissenschaften')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('560', 'Paläontologie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('570', 'Biowissenschaften, Biologie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('580', 'Pflanzen (Botanik)')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('590', 'Tiere (Zoologie)')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('600', 'Technik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('610', 'Medizin')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('620', 'Ingenieurwissenschaften')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('630', 'Landwirtschaft, Veterinärmedizin')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('640', 'Hauswirtschaft')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('650', 'Management')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('660', 'Technische Chemie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('670', 'Industrielle Fertigung')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('690', 'Hausbau, Bauhandwerk')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('700', 'Künste, Bildende Kunst allgemein')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('710', 'Landschaftsgestaltung, Raumplanung')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('720', 'Architektur')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('730', 'Plastik, Numismatik, Keramik, Metallkunst')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('740', 'Zeichnung, Kunsthandwerk')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('741.5', 'Comics, Cartoons, Karikaturen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('750', 'Malerei')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('760', 'Grafische Verfahren, Drucke')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('770', 'Fotografie, Computerkunst')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('780', 'Musik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('791', 'Öffentliche Darbietungen, Film, Rundfunk')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('792', 'Theater, Tanz')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('793', 'Spiel')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('796', 'Sport')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('800', 'Literatur, Rhetorik, Literaturwissenschaft')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('810', 'Englische Literatur Amerikas')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('820', 'Englische Literatur')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('830', 'Deutsche Literatur')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('839', 'Literatur in anderen germanischen Sprachen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('840', 'Französische Literatur')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('850', 'Italienische, rumänische, rätoromanische Literatur')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('860', 'Spanische und portugiesische Literatur')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('870', 'Lateinische Literatur')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('880', 'Griechische Literatur')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('890', 'Literatur in anderen Sprachen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('900', 'Geschichte')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('910', 'Geografie, Reisen')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('914.3', 'Landeskunde Deutschlands')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('920', 'Biografie, Genealogie, Heraldik')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('930', 'Alte Geschichte, Archäologie')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('940', 'Geschichte Europas')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('943', 'Geschichte Deutschlands')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('950', 'Geschichte Asiens')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('960', 'Geschichte Afrikas')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('970', 'Geschichte Nordamerikas')
insert into dbo.DDC_Categories (DDC_Categorie, name) values('980', 'Geschichte Südamerikas')

### DNB-Werte

INSERT INTO DNB_Categories (DNB_Categorie, name) VALUES ('01','Wissenschaft und Kultur allgemein')

### DINI-Werte
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type','Objects having a formal publication type', 'Objekte mit einem formalen Publikationstyp')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:monograph','Books, Monographs', 'Bücher, Monographien')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:article','Journal Articles', 'Zeitschriftenartikel')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:dissertation','Dissertations and Professional Dissertations', 'Dissertationen und Habilitationen')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:masterthesis','Diploma Theses', 'Diplomarbeiten')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:report','Reports', 'Berichte')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:paper','Papers', 'Papers')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:conf-proceeding','Conference Proceedings', 'Tagungs- und Konferenzbeiträge')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:lecture','Lectures', 'Vorlesungen')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:music','Music', 'Musik')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:program','Programs / Software', 'Programme / Software')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:play','Plays', 'Schauspiele / Theaterstücke')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:news','News', 'Nachrichten')
INSERT INTO DINI_Set_Categories (name, setNameEng, setNameDeu) VALUES ('pub-type:standards','Standards', 'Standards')