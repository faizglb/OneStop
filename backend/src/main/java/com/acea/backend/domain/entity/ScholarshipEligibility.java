package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "scholarship_eligibilities", schema = "scholarship_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "scholarship")
public class ScholarshipEligibility {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scholarship_id", nullable = false)
    private Scholarship scholarship;

    @Column(name = "eligibility_rule", nullable = false, length = 250)
    private String eligibilityRule;

    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType; // INCOME | MARKS | CATEGORY | GENDER | STATE

    @Column(name = "rule_value", nullable = false, length = 200)
    private String ruleValue;
}
