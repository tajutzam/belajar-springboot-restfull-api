

create database contact_management;

use contact_management;


create table users(
    username varchar(100) not null,
    password varchar(100) not null,
    name varchar (100) not null,
    token varchar(100),
    token_expired bigint    ,
    primary key (username)
) engine InnoDB;


CREATE TABLE contacts (
    id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    phone VARCHAR(100),
    email VARCHAR(100),
    PRIMARY KEY (id),
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

create table addresses(

    id varchar (100) not null,
    contact_id varchar(100) not null,
    street varchar(100),
    city varchar(100),
    province varchar(100),
    postal_code varchar(10),
    country varchar(100) not null,
    primary key (id),
    foreign key fk_contacts_adresses  (contact_id) references contacts(id) ON DELETE CASCADE  on update cascade
) engine InnoDB;



