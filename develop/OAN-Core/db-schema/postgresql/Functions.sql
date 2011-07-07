-- Function to calculate the number of objects that resolve to the given ddc value, 
-- purpose: for visualization on the ddc-list in oan-search

-- language sql

SELECT COUNT(*) FROM "DDC_Classification" d WHERE d."DDC_Categorie" = $1;

-- function call
UPDATE "DDC_Browsing_Help" SET direct_count = "fnDDCDirectCount"("DDC_Categorie");


-- Function to calculate the sub count values of ddc categories, 
-- purpose: for visualization on the ddc-list in oan-search

-- language PL/pgsql
DECLARE  
	res int;
	i int;

BEGIN
    res = 0;

		IF (length(ddc) > 3) THEN
			RETURN 0;
		END IF;

    IF (CAST(ddc AS int) % 100 = 0) THEN
        i = 1; 
    ELSE
        IF (CAST(ddc AS int) % 10 = 0) THEN
            i = 2;
        ELSE
            IF (FLOOR(CAST(ddc AS int)) = CAST(ddc AS int)) THEN
               i = 3;
            ELSE 
               i =5;
						END IF;
					END IF;
			END IF;
		
		res = COUNT(*) FROM (SELECT object_id FROM "DDC_Classification" d WHERE substr(d."DDC_Categorie",0, i + 1) = substr(ddc, 0, i + 1) GROUP BY object_id) AS t ;
		RETURN res; 
END


-- function call
UPDATE "DDC_Browsing_Help" SET sub_count = "fnDDCCount"("DDC_Categorie");