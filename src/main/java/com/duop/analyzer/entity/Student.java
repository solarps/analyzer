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

    private StudentEducationType educationType;

    @ManyToOne
    @JoinColumn(name = "details_id")
    private StudentDetails details;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
