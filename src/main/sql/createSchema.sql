begin;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists Users (
  uid serial PRIMARY KEY NOT NULL,
  token uuid UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
  email varchar(80) UNIQUE NOT NULL CHECK (email LIKE '%_@__%.__%'),
  name varchar(80) NOT NULL,
  password varchar(100) NOT NULL
);

create table if not exists Clubs(
    cid serial,
    ownerid integer NOT NULL,
    name varchar(80) UNIQUE NOT NULL,
    FOREIGN KEY (ownerid) REFERENCES Users(uid),
    PRIMARY KEY(cid, ownerid)
);

create table if not exists Courts(
   crid serial,
   cid integer,
   ownerid integer,
   name varchar(80) NOT NULL,
   PRIMARY KEY (crid, cid, ownerid),
   FOREIGN KEY (cid, ownerid) REFERENCES Clubs(cid, ownerid)
);

create table if not exists Rentals(
    rid serial,
    uid integer,
    cid integer,
    crid integer,
    ownerid integer NOT NULL,
    startdate date NOT NULL,
    starthour integer NOT NULL,
    duration integer NOT NULL CHECK( duration > 0 ),
    FOREIGN KEY (uid) REFERENCES Users(uid),
    FOREIGN KEY (crid, cid, ownerid) REFERENCES Courts(crid, cid, ownerid),
    PRIMARY KEY (rid)
);

commit;