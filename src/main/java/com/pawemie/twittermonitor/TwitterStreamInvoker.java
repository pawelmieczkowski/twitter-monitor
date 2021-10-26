package com.pawemie.twittermonitor;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.github.scribejava.core.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Slf4j
@Component
public class TwitterStreamInvoker {

    private final IAPIEventListenerImpl listener;

    private final TwitterClient twitterClient;

    private Future<Response> sampledStream;

    public TwitterStreamInvoker(@Value("${twitterApi.accessToken}") String accessToken,
                                @Value("${twitterApi.accessTokenSecret}") String accessTokenSecret,
                                @Value("${twitterApi.apiKey}") String apiKey,
                                @Value("${twitterApi.apiSecretKey}") String apiSecretKey,
                                IAPIEventListenerImpl listener) {
        this.listener = listener;
        twitterClient = new TwitterClient(TwitterCredentials.builder()
                .accessToken(accessToken)
                .accessTokenSecret(accessTokenSecret)
                .apiKey(apiKey)
                .apiSecretKey(apiSecretKey)
                .build());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connectStream() {
        sampledStream = twitterClient.startSampledStream(listener);
    }

    public void reconnect() {
        if (sampledStream.isDone() || sampledStream.isCancelled()) {
            log.info("Trying to reconnect");
            this.connectStream();
        }
    }
}
