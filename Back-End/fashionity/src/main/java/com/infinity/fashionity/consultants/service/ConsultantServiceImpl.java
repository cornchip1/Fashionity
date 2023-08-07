package com.infinity.fashionity.consultants.service;

import com.infinity.fashionity.consultants.dto.*;
import com.infinity.fashionity.consultants.entity.ConsultantEntity;
import com.infinity.fashionity.consultants.entity.ImageEntity;
import com.infinity.fashionity.consultants.entity.ReviewEntity;
import com.infinity.fashionity.consultants.entity.ScheduleEntity;
import com.infinity.fashionity.consultants.repository.ConsultantRepository;
import com.infinity.fashionity.consultants.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConsultantServiceImpl implements ConsultantService {
    private final ConsultantRepository consultantRepository;
    private final ReservationRepository reservationRepository;

    // [공통] 컨설턴트 목록 조회
    @Override
    @Transactional(readOnly = true)
    public ConsultantListDTO.Response getAllConsultants(Long memberSeq, ConsultantListDTO.Request dto) {

        int page = dto.getPage();
        int size = dto.getSize();
        List<ConsultantSummary> consultantSummaries = new ArrayList<>();

        // 내림차순 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 최신순으로 컨설턴트 가져오기
        Page<ConsultantEntity> result = consultantRepository.findAll(pageable);


        result.stream().forEach(entity -> {
            ConsultantSummary consultantSummary = ConsultantSummary.builder()
                    .seq(entity.getSeq())
                    .nickname(entity.getNickname())
                    .profileUrl(entity.getMember().getProfileUrl())
                    .level(entity.getLevel())
                    .avgGrade(consultantRepository.avgGrade(entity.getNickname()))
                    .totalCnt(consultantRepository.totalCnt(entity.getNickname()))
                    .build();
            consultantSummaries.add(consultantSummary);
        });

        return ConsultantListDTO.Response.builder()
                .prev(result.hasPrevious())
                .next(result.hasNext())
                .page(result.getNumber())
                .consultants(consultantSummaries)
                .build();
    }


    // [공통] 컨설턴트 상세 정보 조회
    @Override
    @Transactional(readOnly = true)
    public ConsultantInfoDTO.Response getConsultantDetail(Long memberSeq, String consultantNickname){

        List<ConsultantDetail> consultantDetails = new ArrayList<>();

        List<ConsultantEntity> result = consultantRepository.findConsultantDetail(consultantNickname);

        result.forEach(entity -> {
            List<ReviewEntity> reviewEntities = consultantRepository.findConsultantReviewsById(entity.getSeq());
            List<Review> reviews = reviewEntities.stream().map(e -> {
                LocalDateTime createdAt = e.getCreatedAt();
                LocalDateTime updatedAt = e.getUpdatedAt();
                LocalDateTime deletedAt = e.getDeletedAt();
                Long reviewSeq = e.getSeq();
                Float reviewGrade = e.getGrade();
                String reviewContent = e.getContent();
                Long reservationSeq = e.getReservation().getSeq();
                return Review.builder()
                        .reviewSeq(reviewSeq)
                        .reviewGrade(reviewGrade)
                        .reviewContent(reviewContent)
                        .createdAt(createdAt)
                        .updatedAt(updatedAt)
                        .deletedAt(deletedAt)
                        .reservationSeq(reservationSeq)
                        .build();
            }).collect(Collectors.toList());

            List<ScheduleEntity> scheduleEntities = consultantRepository.findConsultantSchedules(entity.getSeq());
            List<Schedule> schedules = scheduleEntities.stream().map(e-> {
                Long scheduleSeq = e.getSeq();
                LocalDateTime availableDateTime = e.getAvailableDateTime();
                Boolean isAvailable = e.getIsAvailable();
                return Schedule.builder()
                        .scheduleSeq(scheduleSeq)
                        .availableDateTime(availableDateTime)
                        .isAvailable(isAvailable)
                        .build();
            }).collect(Collectors.toList());

            ConsultantDetail consultantDetail = ConsultantDetail.builder()
                    .seq(entity.getSeq())
                    .nickname(entity.getMember().getNickname())
                    .profileUrl(entity.getMember().getProfileUrl())
                    .level(entity.getLevel())
                    .avgGrade(consultantRepository.avgGrade(entity.getNickname()))
                    .totalCnt(consultantRepository.totalCnt(entity.getNickname()))
                    .reviews(reviews)
                    .schedules(schedules)
                    .build();
            consultantDetails.add(consultantDetail);
        });
        return ConsultantInfoDTO.Response.builder()
                .consultant(consultantDetails)
                .build();
    }

    // [공통] 예약 내역 조회
    @Override
    @Transactional(readOnly = true)
    public UserReservationListDTO.Response getUserReservationsList(Long memberSeq) {

        List<UserReservationSummary> result = reservationRepository.findUserReservations(memberSeq);

        return UserReservationListDTO.Response.builder()
                .userReservationSummaries(result)
                .build();
    }

    // [컨설턴트] 예약 목록 조회
    @Override
    @Transactional(readOnly = true)
    public ConsultantReservationListDTO.Response getConsultantReservationsList(Long memberSeq, String consultantNickname) {

        List<ConsultantReservationSummary> result = reservationRepository.findConsultantReservations(consultantNickname);
        return ConsultantReservationListDTO.Response.builder()
                .consultantReservationSummaries(result)
                .build();
    }

    // [컨설턴트] 예약 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ConsultantReservationInfoDTO.Response getConsultantReservationDetail(Long memberSeq, String consultantNickname, Long reservationSeq){

        List<ConsultantReservationDetail> result = reservationRepository.findConsultantReservation(consultantNickname, reservationSeq);

        List<ConsultantReservationDetail> details = result.stream().map(entity -> {
            List<ImageEntity> imageEntities = reservationRepository.findReservationImages(entity.getReservationSeq());
            List<Image> images = imageEntities.stream().map(e->{
                Long imageSeq = e.getSeq();
                String imageUrl = e.getUrl();
                return Image.builder()
                        .imageSeq(imageSeq)
                        .imageUrl(imageUrl)
                        .build();
            }).collect(Collectors.toList());
            return ConsultantReservationDetail.builder()
                    .reservationSeq(entity.getReservationSeq())
                    .memberNickname(entity.getMemberNickname())
                    .reservationDateTime(entity.getReservationDateTime())
                    .reservationDetail(entity.getReservationDetail())
                    .images(images)
                    .build();
        }).collect(Collectors.toList());

        return ConsultantReservationInfoDTO.Response.builder()
                .consultantReservationDetails(details)
                .build();

    }

    // [컨설턴트] 전체 후기, 평점 조회
    @Override
    @Transactional
    public ConsultantReviewListDTO.Response getConsultantReviewsList(Long memberSeq, String consultantNickname){
        List<ConsultantReviewSummary> result = consultantRepository.findConsultantReviewsByNickname(consultantNickname);
        return ConsultantReviewListDTO.Response.builder()
                .consultantReviewSummaries(result)
                .build();
    };

     //[컨설턴트] 평점 통계, 수익 조회
    @Override
    public ConsultantStatisticsDTO.Response getConsultantStatistics(Long memberSeq, String consultantNickname){
        return ConsultantStatisticsDTO.Response.builder()
                .avgGrade(consultantRepository.avgGrade(consultantNickname))
                .totalConsultingCnt(consultantRepository.totalCnt(consultantNickname))
                .totalUndeletedReviewCnt(consultantRepository.totalUndeletedReviewCnt(consultantNickname))
                .totalDeletedReviewCnt(consultantRepository.totalDeletedReviewCnt(consultantNickname))
                .totalSalary(consultantRepository.totalSalary(consultantNickname))
                .build();

    }





}





