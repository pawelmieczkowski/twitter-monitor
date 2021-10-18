package com.pawemie.twittermonitor.service;

import com.pawemie.twittermonitor.model.RecordSet;
import com.pawemie.twittermonitor.repository.RecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RecordService {

    private final RecordRepository recordRepository;

    public List<RecordSet> getRecentInMinutes1(int pageSize) {
        return getRecent(pageSize, "minutes1");
    }

    public List<RecordSet> getRecentInMinutes5(int pageSize) {
        return getRecent(pageSize, "minutes5");
    }

    public List<RecordSet> getRecentInMinutes60(int pageSize) {
        return getRecent(pageSize, "minutes60");
    }

    public List<RecordSet> getRecent(int pageSize, String database) {
        return recordRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, pageSize), database);
    }
}
