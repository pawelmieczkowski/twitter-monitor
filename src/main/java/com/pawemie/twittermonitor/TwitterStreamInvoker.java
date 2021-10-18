package com.pawemie.twittermonitor;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.github.scribejava.core.model.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class TwitterStreamInvoker {

    private final EntryProcessor entryProcessor;

    private final TwitterClient twitterClient;

    public TwitterStreamInvoker(@Value("${twitterApi.accessToken}") String accessToken,
                                @Value("${twitterApi.accessTokenSecret}") String accessTokenSecret,
                                @Value("${twitterApi.apiKey}") String apiKey,
                                @Value("${twitterApi.apiSecretKey}") String apiSecretKey,
                                EntryProcessor entryProcessor) {
        this.entryProcessor = entryProcessor;
        twitterClient = new TwitterClient(TwitterCredentials.builder()
                .accessToken(accessToken)
                .accessTokenSecret(accessTokenSecret)
                .apiKey(apiKey)
                .apiSecretKey(apiSecretKey)
                .build());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connectStream() {
        Future<Response> sampledStream = twitterClient.startSampledStream(new IAPIEventListenerImpl(entryProcessor, this));
        sampledStream.isDone();
    }
}
