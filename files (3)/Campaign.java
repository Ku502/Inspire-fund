package com.inspirefund.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Campaign {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_desc")
    private String shortDesc;

    private String category;
    private String emoji;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal goal;

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal raised = BigDecimal.ZERO;

    @Column(name = "donor_count")
    @Builder.Default
    private Integer donorCount = 0;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "organizer_name")
    private String organizerName;

    @Column(name = "organizer_contact")
    private String organizerContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public int getProgressPercent() {
        if (goal.compareTo(BigDecimal.ZERO) == 0) return 0;
        return raised.multiply(BigDecimal.valueOf(100)).divide(goal, 0, java.math.RoundingMode.HALF_UP).intValue();
    }

    public enum Status { PENDING, ACTIVE, CLOSED, COMPLETED }
}
