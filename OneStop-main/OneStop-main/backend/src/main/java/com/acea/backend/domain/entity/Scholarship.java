package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "scholarships", schema = "scholarship_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "eligibilities")
public class Scholarship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "scholarship_name", nullable = false, length = 250)
    private String scholarshipName;

    @Column(name = "provider", nullable = false, length = 200)
    private String provider;

    @Column(name = "scholarship_type", nullable = false, length = 50)
    private String scholarshipType; // Government | Private | NGO | Corporate | International | University

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "official_website", length = 250)
    private String officialWebsite;

    @Column(name = "application_link", length = 250)
    private String applicationLink;

    @Column(name = "application_mode", nullable = false, length = 50)
    @Builder.Default
    private String applicationMode = "ONLINE"; // ONLINE | OFFLINE

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "last_date", nullable = false)
    private LocalDate lastDate;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country", nullable = false, length = 100)
    @Builder.Default
    private String country = "India";

    @Column(name = "education_level", nullable = false, length = 50)
    private String educationLevel; // UNDERGRADUATE | POSTGRADUATE

    @Column(name = "course", length = 100)
    private String course;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "minority_eligible", nullable = false)
    @Builder.Default
    private Boolean minorityEligible = false;

    @Column(name = "disability_eligible", nullable = false)
    @Builder.Default
    private Boolean disabilityEligible = false;

    @Column(name = "income_limit", precision = 12, scale = 2)
    private BigDecimal incomeLimit;

    @Column(name = "minimum_marks", precision = 5, scale = 2)
    private BigDecimal minimumMarks;

    @Column(name = "entrance_exam", length = 100)
    private String entranceExam;

    @Column(name = "renewable", nullable = false)
    @Builder.Default
    private Boolean renewable = false;

    @Column(name = "required_documents", nullable = false, columnDefinition = "TEXT")
    private String requiredDocuments; // Comma-separated list

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE | EXPIRED

    @OneToMany(mappedBy = "scholarship", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ScholarshipEligibility> eligibilities = new ArrayList<>();
}
