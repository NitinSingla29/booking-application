package com.example.core.domain.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
public abstract class RelationEntity implements Comparable<RelationEntity> {

    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Getter
    @Setter
    @Column(name = "version", nullable = false)
    @Version
    private Long version;

    @Override
    public int compareTo(RelationEntity other) {
        if (this.id == null && other.id == null) {
            return 0;
        }
        if (this.id == null) {
            return -1;
        }
        if (other.id == null) {
            return 1;
        }
        return this.id.compareTo(other.id);
    }
}
