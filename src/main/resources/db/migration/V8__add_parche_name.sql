-- Notification MS contract A.3: event.linked.to.parche must carry parcheName.
-- Local parche read model gets a nullable name column, backfilled by parche.created events from Parches MS.

ALTER TABLE parche_visibility ADD COLUMN name VARCHAR(255);
