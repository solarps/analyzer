package com.duop.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lectors")
public class Lector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "lector", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Subject> subjects = new HashSet<>();

    public Lector(String name) {
        this.name = name;
    }
}
