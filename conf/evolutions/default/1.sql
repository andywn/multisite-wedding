# Invitee schema
 
# --- !Ups
 
CREATE TABLE Invitee (
    irn varchar(20) NOT NULL,
    display_name varchar(255) NOT NULL,
    plus_one boolean default false,
    edinburgh boolean default false,
    australia boolean default false,
    number_invited int default 0,
    last_viewed timestamp default NULL,
    kids boolean default false,
    number_kids_invited int default 0,
    rsvp_by_date date default NULL,
    email varchar(255) default NULL,
    invite_sent boolean default false,
    PRIMARY KEY (irn)
);

CREATE TABLE Rsvp (
    id bigint NOT NULL AUTO_INCREMENT,
    invitee_id varchar(20) NOT NULL,
    name varchar(255) NOT NULL,
    rsvp varchar(16) default NULL,
    food varchar(255) default NULL,
    submitted timestamp default NULL,
    guest_number integer default 0,
    kids integer default 0,
    toddlers integer default 0,
    email varchar(64) default NULL,
    comments varchar(255) default NULL,
    PRIMARY KEY (id)
);
 
# --- !Downs
 
DROP TABLE Invitee;
DROP TABLE Rsvp;
