package com.duop.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer course;
    private String name;
    @Column(name = "class")
    private String group;

    public Student(Integer course, String name, String group) {
        this.course = course;
        this.name = name;
        this.group = group;
    }
}
