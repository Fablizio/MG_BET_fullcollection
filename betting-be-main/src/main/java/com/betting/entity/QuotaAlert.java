package com.betting.entity;

import com.betting.enumeration.ConditionType;
import com.betting.enumeration.NotificationType;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "quota_alert")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuotaAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "match_id", nullable = false)
    @ManyToOne
    private Odd matchId;

    @Column(nullable = false, length = 1)
    private String esito; // "1", "X", "2"

    @Column(name = "quota_target", nullable = false, precision = 6, scale = 2)
    private Double quotaTarget;

    @Column(name = "condition_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @PrePersist
    protected void onCreate() {
        this.notificationType = NotificationType.ACTIVE;
    }


}
