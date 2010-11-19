-- Setzen von WorkflowDB-Eintraegen;

INSERT dbo.WorkflowDB (object_id, time, service_id)
SELECT object_id, GetDate(), 4
FROM dbo.WorkflowDB
WHERE service_id = 3; 


-- Provisorisches Einfuegen von FulltextLinks

INSERT dbo.FullTextLinks (object_id, mimeformat, link)
SELECT object_id, 'application/pdf', identifier
FROM dbo.Identifier
WHERE RIGHT(identifier, 3) = 'pdf'

-- ohne doppelte Eintraege

INSERT dbo.FullTextLinks (object_id, mimeformat, link)
SELECT object_id, 'application/pdf', MIN(identifier)
FROM dbo.Identifier
WHERE RIGHT(identifier, 3) = 'pdf'
GROUP BY object_id






-- DDC_Browsing_Help


-- Funktion, die alle Werte und in übergebener und in der Unterkategorie zählt
CREATE FUNCTION fnDDCCount (@ddc UNIVARCHAR(10))
RETURNS INT
AS
BEGIN
    declare @res int
    SET @res = 0
    DECLARE @i INT
    
    IF (CAST(@ddc AS Float) % 100 = 0)
        SET @i = 1 
    ELSE
        IF (CAST(@ddc AS Float) % 10 = 0)
            SET @i = 2
        ELSE
            IF (FLOOR(CAST(@ddc AS Float)) = CAST(@ddc AS Float))
               SET @i = 3
            ELSE 
               SET @i =5
    SELECT @res= COUNT(*) FROM (SELECT object_id FROM dbo.DDC_Classification d WHERE LEFT(d.DDC_Categorie,@i) = LEFT(@ddc, @i) GROUP BY object_id) AS t 
    return @res 
END

-- Update der Sub-Count-Werte

UPDATE dbo.DDC_Browsing_Help set sub_count = fnDDCCount(DDC_Categorie)



-- alle Objekte neu Aggregieren

INSERT dbo.WorkflowDB (object_id, time, service_id)
SELECT object_id, GetDate(), 1
FROM dbo.Object


-- nur neu Aggregiere, wo noch kein Identifier gefunden wurde
INSERT dbo.WorkflowDB (object_id, time, service_id)
SELECT object_id, GetDate(), 1
 o.object_id FROM dbo.Object o LEFT OUTER JOIN dbo.Identifier i ON o.object_id = i.object_id where i.identifier is null



