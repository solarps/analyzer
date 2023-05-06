package com.duop.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sheets")
public class Sheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String faculty;

    private Integer number;

    @Transient
    private SheetType sheetType;

    @Column(name = "form_id")
    private int sheetTypeCode;

    @Column(name = "posting_date")
    @Temporal(TemporalType.DATE)
    private LocalDate postingDate;

    @Column(name = "education_year")
    private Short year;

    private Byte course;

    @OneToOne
    @JoinColumn(name = "lector_id")
    private Lector lector;

    @OneToOne
    @JoinColumn(name = "control_lector_id")
    private Lector controlLector;

    @OneToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @PostLoad
    void fillTransient() {
        if (sheetTypeCode > 0) {
            this.sheetType = SheetType.of(sheetTypeCode);
        }
    }

    @PrePersist
    void fillPersistent() {
        if (sheetType != null) {
            this.sheetTypeCode = sheetType.getCode();
        }
    }
}
