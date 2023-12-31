package com.infinity.fashionity.consultants.repository;

import com.infinity.fashionity.consultants.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    @Query("select s from ScheduleEntity s where s.seq = :scheduleSeq")
    Optional<ScheduleEntity> findBySeq(@Param("scheduleSeq") Long scheduleSeq);

    @Query(value = "select * from consultant_schedule where DATE(available_datetime) = :date and consultant_info_seq = :consultantSeq order by available_datetime asc", nativeQuery = true)
    Optional<List<ScheduleEntity>> findByDate(@Param("date") String date, @Param("consultantSeq") Long consultantSeq);
    
    void deleteById(Long scheduleSeq);
}
