drop table if exists marks cascade;
drop table if exists subjects cascade;
drop table if exists lectors cascade;
drop table if exists students cascade;

create table if not exists students
(
    id     serial primary key,
    name   varchar(255),
    course smallint,
    class  varchar(255),
    unique (name, class)
);

create table if not exists lectors
(
    id   serial primary key,
    name varchar(255) unique
);

create table if not exists subjects
(
    id        serial primary key,
    name      varchar(255),
    lector_id int,
    foreign key (lector_id) references lectors (id)
);

create table if not exists marks
(
    student_id int,
    subject_id int,
    mark       int,
    primary key (student_id, subject_id),
    foreign key (student_id) references students (id),
    foreign key (subject_id) references subjects (id)
);

