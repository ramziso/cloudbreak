-- // CB-9 SDX Management can post a valid cluster request to Cloudbreak
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE sdx_cluster_id_seq START WITH 1
                                          INCREMENT BY 1
                                          NO MINVALUE
                                          NO MAXVALUE
                                          CACHE 1;

CREATE TABLE sdxcluster (

    id bigint NOT NULL,
    accountid character varying(255) NOT NULL,
    clustername character varying(255) NOT NULL,
    status character varying(255),
    stackid bigint

);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE IF EXISTS sdxcluster;

DROP SEQUENCE IF EXISTS sdx_cluster_id_seq;
