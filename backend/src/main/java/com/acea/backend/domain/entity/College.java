package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "colleges", schema = "college_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"courses", "facilities"})
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "college_name", nullable = false, length = 250)
    private String collegeName;

    @Column(name = "college_type", nullable = false, length = 50)
    private String collegeType; // Government | Private

    @Column(name = "ownership", nullable = false, length = 50)
    private String ownership; // Public | Private | Semi

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "nirf_ranking")
    private Integer nirfRanking;

    @Column(name = "naac_grade", length = 10)
    private String naacGrade;

    @Column(name = "aicte_approved", nullable = false)
    @Builder.Default
    private Boolean aicteApproved = true;

    @Column(name = "ugc_approved", nullable = false)
    @Builder.Default
    private Boolean ugcApproved = true;

    @Column(name = "established_year", nullable = false)
    private Integer establishedYear;

    @Column(name = "website", length = 250)
    private String website;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "average_package", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal averagePackage = BigDecimal.ZERO;

    @Column(name = "highest_package", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal highestPackage = BigDecimal.ZERO;

    @Column(name = "median_package", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal medianPackage = BigDecimal.ZERO;

    @Column(name = "placement_percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal placementPercentage = BigDecimal.ZERO;

    @Column(name = "hostel_available", nullable = false)
    @Builder.Default
    private Boolean hostelAvailable = false;

    @Column(name = "girls_hostel", nullable = false)
    @Builder.Default
    private Boolean girlsHostel = false;

    @Column(name = "boys_hostel", nullable = false)
    @Builder.Default
    private Boolean boysHostel = false;

    @Column(name = "total_fees", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalFees = BigDecimal.ZERO;

    @Column(name = "annual_fees", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal annualFees = BigDecimal.ZERO;

    @Column(name = "campus_area", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal campusArea = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "college", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "college", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Facility> facilities = new ArrayList<>();
}
