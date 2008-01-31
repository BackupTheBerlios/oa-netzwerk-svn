insert into dbo.Repositories (name, url, oai_url) values ('EDOC HU', 'http://edoc.hu-berlin.de','http://http://edoc.hu-berlin.de/OAI-2.0');

insert into dbo.Services (service_id, name) values ('Harvester');
insert into dbo.Services (name) values ('Aggregator');

insert into dbo.ServicesOrder (service_id, predecessor_id) values (1, null);
insert into dbo.ServicesOrder (service_id, predecessor_id) values (2, 1);
# insert into dbo.Services (service_id, name) values (1, 'Harvester');
# insert into dbo.Repositories (repository_id, name, url, oai_url) values (1, 'EDOC HU', 'http://edoc.hu-berlin.de','http://http://edoc.hu-berlin.de/OAI-2.0'); 