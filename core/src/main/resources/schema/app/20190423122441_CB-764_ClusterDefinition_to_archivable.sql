-- // CB-764 ClusterDefinition to archivable
-- Migration SQL that makes the change goes here.
ALTER TABLE IF EXISTS clusterdefinition
    ADD COLUMN IF NOT EXISTS archived boolean DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS deletionTimestamp BIGINT DEFAULT -1,
    DROP CONSTRAINT clusterdefinitionname_in_org_unique,
    ADD CONSTRAINT uk_clusterdefinition_deletiondate_workspace UNIQUE (name, deletionTimestamp, workspace_id);

-- //@UNDO
-- SQL to undo the change goes here.

UPDATE cluster SET clusterdefinition_id=null WHERE clusterdefinition_id IN (SELECT id FROM clusterdefinition WHERE archived=true);
DELETE FROM clusterdefinition WHERE archived=true;
ALTER TABLE clusterdefinition
    DROP CONSTRAINT IF EXISTS uk_clusterdefinition_deletiondate_workspace,
    ADD CONSTRAINT clusterdefinitionname_in_org_unique UNIQUE (workspace_id, name),
    DROP COLUMN IF EXISTS deletionTimestamp,
    DROP COLUMN IF EXISTS archived;


