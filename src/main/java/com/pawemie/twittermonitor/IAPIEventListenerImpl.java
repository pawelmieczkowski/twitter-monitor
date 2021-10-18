package com.pawemie.twittermonitor;

import com.github.redouane59.twitter.IAPIEventListener;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.pawemie.twittermonitor.model.RecordEntry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IAPIEventListenerImpl implements IAPIEventListener {

    private final EntryProcessor entryProcessor;
    private final TwitterStreamInvoker invoker;

    public IAPIEventListenerImpl(EntryProcessor entryProcessor, TwitterStreamInvoker invoker) {
        this.entryProcessor = entryProcessor;
        this.invoker = invoker;
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
        invoker.connectStream();
    }
}
