drop table if exists marks cascade;
drop table if exists subjects cascade;
drop table if exists lectors cascade;
drop table if exists students cascade;
drop table if exists sheets cascade;
drop table if exists form_types cascade;
drop table if exists groups cascade;
drop type if exists form_type cascade;

create table if not exists groups
(
    id     serial primary key,
    name   varchar(5)  not null,
    number smallint not null,
    unique (name, number)
);

create table if not exists students
(
    id       serial primary key,
    name     varchar(255) not null,
    group_id integer,
    foreign key (group_id) references groups (id),
    unique (name, group_id)
);

create table if not exists lectors
(
    id   serial primary key,
    name varchar(255) not null
);

create table if not exists subjects
(
    id   serial primary key,
    name varchar(255) not null
);

create table form_types
(
    id   serial primary key,
    name varchar(5) not null
);


create table if not exists sheets
(
    id                serial primary key,
    number            integer unique not null,
    faculty           varchar(255)   not null,
    posting_date      date,
    form_id           integer        not null,
    education_year    smallint,
    course            smallint,
    lector_id         int            not null,
    control_lector_id int,
    subject_id        int            not null,
    foreign key (form_id) references form_types (id),
    foreign key (subject_id) references subjects (id),
    foreign key (lector_id) references lectors (id),
    foreign key (control_lector_id) references lectors (id)
);

create table if not exists marks
(
    id         serial primary key,
    student_id int,
    sheet_id   int,
    mark       int not null default 0,
    foreign key (student_id) references students (id),
    foreign key (sheet_id) references sheets (id)
);

insert into form_types (name)
values ('ІСП');
insert into form_types (name)
values ('ЗАЛ');
insert into form_types (name)
values ('КР');
insert into form_types (name)
values ('КП');