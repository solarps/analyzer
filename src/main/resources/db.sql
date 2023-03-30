drop table if exists STUDENTS cascade;
drop table if exists lectors cascade;
drop table if exists subjects cascade;
drop table if exists marks cascade;
create table if not exists STUDENTS
(
    id     int primary key auto_increment,
    name   varchar,
    course smallint,
    class  varchar
);
create table if not exists lectors
(
    id   int primary key auto_increment,
    name varchar
);
create table if not exists subjects
(
    id        int primary key auto_increment,
    name      varchar,
    lector_id int,
    foreign key (lector_id) references lectors (id)
    );
create table if not exists marks
(
    student_id int,
    subject_id int,
    mark       int,
    foreign key (student_id) references STUDENTS (id),
    foreign key (subject_id) references subjects (id),
    primary key (student_id, subject_id)
    );
