package com.pawemie.twittermonitor.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class RecordSet {

    private Instant timestamp;
    private List<Record> records;
}
