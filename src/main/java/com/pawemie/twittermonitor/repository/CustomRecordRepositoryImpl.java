package com.pawemie.twittermonitor.repository;

import com.pawemie.twittermonitor.model.RecordSet;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@AllArgsConstructor
public class CustomRecordRepositoryImpl implements CustomRecordRepository {

    MongoTemplate mongoTemplate;

    @Override
    public List<RecordSet> findAllByOrderByTimestampDesc(Pageable pageable, String databaseCollection) {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.with(pageable);
        return mongoTemplate.find(query, RecordSet.class, databaseCollection);
    }

    @Override
    public void save(RecordSet records, String databaseCollection) {
        mongoTemplate.save(records, databaseCollection);


    }
}
