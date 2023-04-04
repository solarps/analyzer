package com.duop.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "marks")
@NamedQuery(
        name = "Mark.getAllMarksForStudent",
        query = "SELECT mark FROM Mark mark WHERE mark.id.student.id = :student_id")
public class Mark {

    @EmbeddedId
    private MarkId id;

    @Column(name = "mark")
    private Integer value;

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class MarkId implements Serializable {
        @ManyToOne
        @JoinColumn(name = "student_id")
        private Student student;

        @ManyToOne
        @JoinColumn(name = "subject_id")
        private Subject subject;
    }
}
