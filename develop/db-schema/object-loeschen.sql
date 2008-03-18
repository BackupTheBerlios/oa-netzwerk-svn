# alle einfach verbundenen Daten werden geloescht
DELETE FROM dbo.Description WHERE object_id = ?
DELETE FROM dbo.DateValues WHERE object_id = ?
DELETE FROM dbo.Format WHERE object_id = ?
DELETE FROM dbo.Identifier WHERE object_id = ?
DELETE FROM dbo.TypeValue WHERE object_id = ?
DELETE FROM dbo.Titles WHERE object_id = ?
DELETE FROM dbo.RawData WHERE object_id = ?
DELETE FROM dbo.DuplicatePossibilities WHERE object_id = ?

# bei mehrfach verbundenen Daten werden die Zwischentabellen zuerst geloescht
# die Frage hierbei ist, ob gleichzeitig die verknuepften Tabellen auch bereinigt werden sollten oder
# evtl ein COALECE in der SQL-Foreign-Key-Beziehung ausreicht
DELETE FROM  dbo.Object2Author WHERE object_id=?
DELETE FROM  dbo.Object2Editor WHERE object_id=?
DELETE FROM  dbo.Object2Contributor WHERE object_id=?
DELETE FROM  dbo.Object2Language WHERE object_id=?
DELETE FROM  dbo.Keywords WHERE object_id=?
DELETE FROM  dbo.Other_Classification WHERE object_id=?

# hier sollten die weiter referenzierten Tabellen nicht gelöscht werden, die Werte können noch genutzt werden
# bzw. sind wie bei den 
DELETE FROM  dbo.DDC_Classification WHERE object_id=?
DELETE FROM  dbo.DNB_Classification WHERE object_id=?
DELETE FROM  dbo.DINI_Set_Classification WHERE object_id=?

#Object-Eintrag muss auch geloescht werden
DELETE FROM  dbo.Object WHERE object_id=?



# Loeschen der ueberzaehligen Personen
DELETE FROM dbo.Person WHERE 
(person_id NOT IN (SELECT person_id FROM dbo.Object2Author GROUP BY person_id) 
AND person_id NOT IN (SELECT person_id FROM dbo.Object2Editor GROUP BY person_id)
AND person_id NOT IN (SELECT person_id FROM dbo.Object2Contributor GROUP BY person_id))

# Loeschen der ueberzaehligen Personen
DELETE FROM dbo.Other_Categories WHERE 
(other_id NOT IN (SELECT other_id FROM dbo.Other_Classification GROUP BY other_id) )