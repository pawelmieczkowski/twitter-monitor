package com.pawemie.twittermonitor.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RecordEntry {

    private final String lang;
    private final LocalDateTime createdAt;

    public RecordEntry(String lang, LocalDateTime createdAt) {
        this.lang = lang;
        this.createdAt = createdAt;
    }
}
