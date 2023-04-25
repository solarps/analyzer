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
        query = "SELECT mark FROM Mark mark WHERE mark.id.studentId = :student_id")
public class Mark {

    @EmbeddedId
    private MarkId id;

    @ManyToOne
    @MapsId("studentId")
    private Student student;

    @ManyToOne
    @MapsId("subjectId")
    private Subject subject;

    @Column(name = "mark")
    private Integer value;

    public Mark(Integer value) {
        this.value = value;
    }

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class MarkId implements Serializable {
        @Column(name = "student_id")
        private Long studentId;

        @Column(name = "subject_id")
        private Long subjectId;
    }
}
