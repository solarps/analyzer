package com.duop.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;


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
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private Integer course;
    private String name;
    @Column(name = "class")
    private String group;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKeyJoinColumn(name = "subject_id")
    private Map<Subject, Mark> marks;
}
