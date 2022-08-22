package com.pawemie.twittermonitor.controller;

import com.pawemie.twittermonitor.model.RecordSet;
import com.pawemie.twittermonitor.service.RecordService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/records")
public class RecordController {

    private final RecordService recordService;

    @GetMapping("recent-1H")
    public List<RecordSet> getRecent() {
        return recordService.getRecentInMinutes1(60);
    }

    @GetMapping("recent-6H")
    public List<RecordSet> getRecentSixHours() {
        return recordService.getRecentInMinutes5(72);
    }

    @GetMapping("recent-1D")
    public List<RecordSet> getRecentOneDay() {
        return recordService.getRecentInMinutes5(144);
    }

    @GetMapping("recent-7D")
    public List<RecordSet> getRecentOneWeek() {
        return recordService.getRecentInMinutes60(168);
    }

    @GetMapping("recent-30D")
    public List<RecordSet> getRecentOneMonth() {
        return recordService.getRecentInMinutes60(720);
    }
}
