package com.pawemie.twittermonitor.repository;

import com.pawemie.twittermonitor.model.RecordSet;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomRecordRepository {

    void save(RecordSet records, String databaseCollection);

    List<RecordSet> findAllByOrderByTimestampDesc(Pageable pageable, String databaseCollection);
}
