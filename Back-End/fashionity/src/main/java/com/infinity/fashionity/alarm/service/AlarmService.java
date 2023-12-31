package com.infinity.fashionity.alarm.service;

import com.infinity.fashionity.alarm.dto.AlarmDTO;
import com.infinity.fashionity.alarm.dto.AlarmDeleteDTO;
import com.infinity.fashionity.alarm.dto.AlarmSendDTO;

import java.util.List;

public interface AlarmService {
    public void sendAlarm(AlarmSendDTO.Request dto);

    public List<AlarmDTO> findAll(Long memberSeq);

    public Boolean readAlarm(Long ownerSeq,Long alarmSeq);
    public Boolean deleteAlarm(Long ownerSeq,Long alarmSeq);
}
