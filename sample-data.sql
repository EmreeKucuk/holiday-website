-- Sample data for translations and audiences
-- Run these SQL statements after creating the database tables

-- First, let's insert some sample audiences (using MERGE to avoid duplicates)
MERGE INTO audiences a
USING (SELECT 'general' as code, 'General Public' as audience_name FROM dual) d
ON (a.code = d.code)
WHEN NOT MATCHED THEN
  INSERT (code, audience_name) VALUES (d.code, d.audience_name);

MERGE INTO audiences a
USING (SELECT 'government' as code, 'Government' as audience_name FROM dual) d
ON (a.code = d.code)
WHEN NOT MATCHED THEN
  INSERT (code, audience_name) VALUES (d.code, d.audience_name);

MERGE INTO audiences a
USING (SELECT 'religious' as code, 'Religious' as audience_name FROM dual) d
ON (a.code = d.code)
WHEN NOT MATCHED THEN
  INSERT (code, audience_name) VALUES (d.code, d.audience_name);

MERGE INTO audiences a
USING (SELECT 'educational' as code, 'Educational' as audience_name FROM dual) d
ON (a.code = d.code)
WHEN NOT MATCHED THEN
  INSERT (code, audience_name) VALUES (d.code, d.audience_name);

MERGE INTO audiences a
USING (SELECT 'workers' as code, 'Workers' as audience_name FROM dual) d
ON (a.code = d.code)
WHEN NOT MATCHED THEN
  INSERT (code, audience_name) VALUES (d.code, d.audience_name);

-- Now let's add English translations (Turkish translations already exist)
-- Using MERGE to avoid unique constraint violations

-- New Year English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'New Year''s Day' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'new_year'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- Eid al-Fitr English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'Eid al-Fitr' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'eid_al_fitr'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- Eid al-Adha English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'Eid al-Adha' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'eid_al_adha'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- National Sovereignty and Children's Day English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'National Sovereignty and Children''s Day' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'national_sovereignty_day'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- Labour Day English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'Labour and Solidarity Day' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'labour_day'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- Ataturk Memorial Day English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'Commemoration of Atatürk, Youth and Sports Day' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'ataturk_memorial_day'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- Democracy Day English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'Democracy and National Unity Day' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'democracy_day'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- Victory Day English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'Victory Day' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'victory_day'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- Republic Day English translation
MERGE INTO translations t
USING (
  SELECT 
    ht.id as template_id, 
    'en' as language_code, 
    'Republic Day' as translated_name
  FROM holiday_templates ht 
  WHERE ht.code = 'republic_day'
) d
ON (t.template_id = d.template_id AND t.language_code = d.language_code)
WHEN NOT MATCHED THEN
  INSERT (template_id, language_code, translated_name) 
  VALUES (d.template_id, d.language_code, d.translated_name);

-- Commit the changes
COMMIT;

-- Verify the data
SELECT /* LLM in use is Claude 3.5 Sonnet */ 'Audiences' as data_type, code, audience_name as name FROM audiences
UNION ALL
SELECT 'Translations', t.language_code, t.translated_name 
FROM translations t 
JOIN holiday_templates ht ON t.template_id = ht.id 
ORDER BY data_type, code;
