package com.pawemie.twittermonitor;

import com.pawemie.twittermonitor.model.Record;
import com.pawemie.twittermonitor.model.RecordEntry;
import com.pawemie.twittermonitor.model.RecordSet;
import com.pawemie.twittermonitor.repository.RecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EntryProcessor {

    private final TwitterStreamInvoker invoker;

    private final RecordRepository recordRepository;

    private final ConcurrentLinkedQueue<RecordEntry> entries = new ConcurrentLinkedQueue<>();

    public EntryProcessor(RecordRepository recordRepository, @Lazy TwitterStreamInvoker invoker) {
        this.recordRepository = recordRepository;
        this.invoker = invoker;
    }

    public void put(RecordEntry entry) {
        entries.add(entry);
    }

    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    void runTasks() {
        if (entries.isEmpty()) {
            log.error("There are no new entries");
            this.reconnect();
            return;
        }

        LocalDateTime periodStart;
        do {
            periodStart = entries.peek().getCreatedAt();
            if (periodStart == null) {
                log.info("Date is missing from tweet, ignoring tweet.");
                entries.poll();
            } else {
                break;
            }
        } while (!entries.isEmpty());
        if (periodStart == null) {
            log.error("Date is missing from all tweets, ignoring this tweets batch.");
            return;
        }

        LocalDateTime periodEnd = periodStart
                .plusMinutes(1)
                .minusSeconds(periodStart.getSecond());

        RecordSet recordSet = processEveryMinute(periodEnd);
        recordRepository.save(recordSet, "minutes1");

        if (periodEnd.getMinute() % 5 == 0) {
            recordSet = processPeriod(5, "minutes1");
            if (recordSet != null)
                recordRepository.save(recordSet, "minutes5");
        }

        if (periodEnd.getMinute() == 0) {
            recordSet = processPeriod(20, "minutes5");
            if (recordSet != null)
                recordRepository.save(recordSet, "minutes60");
        }
    }

    void reconnect() {
        invoker.reconnect();
    }

    RecordSet processEveryMinute(LocalDateTime periodEnd) {
        Map<String, Long> latestLangs = new HashMap<>();
        while (!entries.isEmpty() && entries.peek().getCreatedAt().isBefore(periodEnd)) {
            RecordEntry entry = entries.poll();
            latestLangs.merge(entry.getLang(), 1L, Long::sum);
        }

        RecordSet recordSet = new RecordSet();
        recordSet.setTimestamp(ZonedDateTime.of(periodEnd, ZoneId.of("UTC")).toInstant());
        recordSet.setRecords(latestLangs.entrySet().stream()
                .map(v -> {
                    Record record = new Record();
                    record.setLang(v.getKey());
                    record.setOccurrence(v.getValue());
                    return record;
                })
                .sorted(Comparator.comparing(Record::getOccurrence).reversed())
                .collect(Collectors.toList()));

        return recordSet;
    }

    RecordSet processPeriod(int size, String database) {
        List<RecordSet> currentPeriod = recordRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, size), database);
        if (currentPeriod.size() < 1) {
            return null;
        }

        RecordSet recordSet = new RecordSet();
        recordSet.setTimestamp(currentPeriod.get(0).getTimestamp());
        recordSet.setRecords(calculateRecords(currentPeriod));

        return recordSet;
    }

    private List<Record> calculateRecords(List<RecordSet> currentPeriod) {
        Map<String, Long> latestTags =
                currentPeriod.stream()
                        .flatMap(records ->
                                records.getRecords().stream())
                        .collect(Collectors.groupingBy(
                                Record::getLang,
                                HashMap::new,
                                Collectors.summingLong(Record::getOccurrence)
                        ));
        return latestTags.entrySet().stream()
                .map(v -> {
                    Record record = new Record();
                    record.setLang(v.getKey());
                    record.setOccurrence(v.getValue());
                    return record;
                })
                .sorted(Comparator.comparing(Record::getOccurrence).reversed())
                .collect(Collectors.toList());
    }
}
