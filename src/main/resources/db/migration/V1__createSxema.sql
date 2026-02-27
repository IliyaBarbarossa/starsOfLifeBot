CREATE SCHEMA IF NOT EXISTS first;

CREATE TABLE IF NOT EXISTS persons
(
    tgid bigint NOT NULL,
    username text COLLATE pg_catalog."default",
    firstname text COLLATE pg_catalog."default",
    lastname text COLLATE pg_catalog."default",
    register date,
    name text COLLATE pg_catalog."default",
    registrationprocess text COLLATE pg_catalog."default",
    bithday date,
    chatid bigint NOT NULL,
    CONSTRAINT persons_pkey PRIMARY KEY (tgid)
);

CREATE TABLE IF NOT EXISTS prognoz
(
    tgid bigint NOT NULL,
    prognoz text COLLATE pg_catalog."default",
    part date NOT NULL,
    CONSTRAINT prognoz_pkey PRIMARY KEY (tgid, part),
    CONSTRAINT prognoz_tgid_fkey FOREIGN KEY (tgid)
        REFERENCES first.persons (tgid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
) PARTITION BY RANGE (part);


CREATE TABLE IF NOT EXISTS zadiak
(
    tgid integer NOT NULL,
    zadiak text COLLATE pg_catalog."default",
    CONSTRAINT zadiak_pkey PRIMARY KEY (tgid)
);



CREATE TABLE IF NOT EXISTS zprognoz
(
    znak text COLLATE pg_catalog."default" NOT NULL,
    prognoz text COLLATE pg_catalog."default",
    CONSTRAINT zprognoz_pkey PRIMARY KEY (znak)
);



