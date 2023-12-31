package com.infinity.fashionity.consultants.entity;

import com.infinity.fashionity.global.entity.CEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultant_schedule")
@DynamicInsert
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude={"reservations","consultant"})
public class ScheduleEntity extends CEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_seq")
    private Long seq;

    @Column(name = "available_datetime", unique = false, nullable = false)
    private LocalDateTime availableDateTime;

    @ColumnDefault("true")
    @Column(name="is_available", unique = false, nullable = true)
    private Boolean isAvailable;

    @Column(name = "canceled_at", unique = false, nullable = true)
    private LocalDateTime canceledAt;

    @Column(name="cancel_reason", length=200, unique = false, nullable = true)
    private String cancelReason;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "schedule")
    private ReservationEntity reservations;

    @JoinColumn(name="consultant_info_seq", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ConsultantEntity consultant;

}
