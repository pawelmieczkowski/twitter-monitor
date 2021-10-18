package com.pawemie.twittermonitor;

import com.pawemie.twittermonitor.model.Record;
import com.pawemie.twittermonitor.model.RecordEntry;
import com.pawemie.twittermonitor.model.RecordSet;
import com.pawemie.twittermonitor.repository.RecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntryProcessorTest {

    @Mock
    RecordRepository recordRepository;
    @InjectMocks
    EntryProcessor entryProcessor;

    @Test
    void shouldReturnRecordSet() {
        //given
        LocalDateTime periodEnd = LocalDateTime.of(2020, Month.JANUARY, 29, 19, 30, 0);

        RecordEntry recordEntry1 = new RecordEntry("lang1",
                LocalDateTime.of(2020, Month.JANUARY, 29, 19, 29, 10));
        entryProcessor.put(recordEntry1);
        RecordEntry recordEntry2 = new RecordEntry("lang1",
                LocalDateTime.of(2020, Month.JANUARY, 29, 19, 29, 12));
        entryProcessor.put(recordEntry2);
        RecordEntry recordEntry4 = new RecordEntry("lang2",
                LocalDateTime.of(2020, Month.JANUARY, 29, 19, 29, 0));
        entryProcessor.put(recordEntry4);
        RecordEntry recordEntry5 = new RecordEntry("lang3",
                LocalDateTime.of(2020, Month.JANUARY, 29, 19, 29, 59));
        entryProcessor.put(recordEntry5);
        RecordEntry recordEntry6 = new RecordEntry("lang4",
                LocalDateTime.of(2020, Month.JANUARY, 29, 19, 30, 10));
        entryProcessor.put(recordEntry6);
        //when
        RecordSet results = entryProcessor.processEveryMinute(periodEnd);
        //then
        assertThat(results.getRecords().size()).isEqualTo(3);
        assertThat(results.getTimestamp()).isEqualTo(Instant.parse("2020-01-29T19:30:00.00Z"));
        assertThat(results.getRecords().get(0).getOccurrence()).isEqualTo(2);
    }


    @Test
    void shouldReturnRecordSetForDataFromRepository() {
        //given
        List<RecordSet> records = new ArrayList<>();
        RecordSet recordSet1 = new RecordSet();
        recordSet1.setTimestamp(Instant.parse("2020-01-29T19:30:00.00Z"));

        Record record1 = new Record();
        record1.setLang("lang1");
        record1.setOccurrence(2L);

        Record record2 = new Record();
        record2.setLang("lang2");
        record2.setOccurrence(5L);

        recordSet1.setRecords(Arrays.asList(record1, record2));
        records.add(recordSet1);

        RecordSet recordSet2 = new RecordSet();
        recordSet2.setTimestamp(Instant.parse("2020-01-29T19:35:00.00Z"));

        Record record3 = new Record();
        record3.setLang("lang1");
        record3.setOccurrence(4L);

        Record record4 = new Record();
        record4.setLang("lang3");
        record4.setOccurrence(4L);

        recordSet2.setRecords(Arrays.asList(record3, record4));
        records.add(recordSet2);

        when(recordRepository.findAllByOrderByTimestampDesc(any(), anyString()))
                .thenReturn(records);
        //when
        RecordSet results = entryProcessor.processPeriod(2, "database");
        //then
        assertThat(results.getRecords().size()).isEqualTo(3);
        assertThat(results.getTimestamp()).isEqualTo(Instant.parse("2020-01-29T19:30:00.00Z"));
        assertThat(results.getRecords().get(0).getOccurrence()).isEqualTo(6L);
        assertThat(results.getRecords().get(1).getOccurrence()).isEqualTo(5L);
    }
}