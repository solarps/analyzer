package com.duop.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "marks")
public class Mark {

    @EmbeddedId
    private MarkId id;

    @Column(name = "mark")
    private Integer value;
}
