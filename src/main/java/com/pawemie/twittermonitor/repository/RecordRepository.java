package com.pawemie.twittermonitor.repository;


import com.pawemie.twittermonitor.model.RecordSet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecordRepository extends MongoRepository<RecordSet, String>, CustomRecordRepository {

}
