-- Add more audiences to match what's used in holidays
INSERT INTO audiences (code, audience_name) VALUES ('general', 'General Public');
INSERT INTO audiences (code, audience_name) VALUES ('government', 'Government');
INSERT INTO audiences (code, audience_name) VALUES ('religious', 'Religious');
INSERT INTO audiences (code, audience_name) VALUES ('educational', 'Educational');
INSERT INTO audiences (code, audience_name) VALUES ('workers', 'Workers');

-- Check existing audiences
SELECT * FROM audiences;
