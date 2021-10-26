package com.pawemie.twittermonitor;

import com.github.redouane59.twitter.IAPIEventListener;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.pawemie.twittermonitor.model.RecordEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IAPIEventListenerImpl implements IAPIEventListener {

    private final EntryProcessor entryProcessor;

    public IAPIEventListenerImpl(EntryProcessor entryProcessor) {
        this.entryProcessor = entryProcessor;
    }

    @Override
    public void onStreamError(int httpCode, String error) {
        log.error("stream error. httpCode = " + httpCode + ", error = " + error);
    }

    @Override
    public void onTweetStreamed(Tweet tweet) {
        entryProcessor.put(new RecordEntry(tweet.getLang(), tweet.getCreatedAt()));
    }

    @Override
    public void onUnknownDataStreamed(String json) {
        log.error("Unknown data streamed");
    }

    @Override
    public void onStreamEnded(Exception e) {
        log.error("Stream ended " + e.getMessage());
        entryProcessor.reconnect();
    }
}
