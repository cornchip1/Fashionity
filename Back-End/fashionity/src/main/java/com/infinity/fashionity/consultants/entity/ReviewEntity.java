package com.infinity.fashionity.consultants.entity;

import com.infinity.fashionity.global.entity.CUDEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name="reviews")
@SQLDelete(sql = "UPDATE reviews SET deleted_at = now() WHERE review_seq = ?")
@Where(clause = "deleted_at is null")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewEntity extends CUDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_seq")
    private Long seq;

    @Column(name = "review_content", unique = false, nullable = true, length = 200)
    private String content;

    @Max(value=5)
    @Min(value=0)
    @Column(name = "review_grade", unique = false, nullable = true)
    private Float grade;

    @JoinColumn(name = "reservation_seq")
    @OneToOne(fetch = FetchType.LAZY)
    private ReservationEntity reservation;

    public void updateContent(String content){
        this.content= content;
    }

}
