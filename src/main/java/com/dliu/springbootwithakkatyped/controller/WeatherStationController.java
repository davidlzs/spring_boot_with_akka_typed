package com.dliu.springbootwithakkatyped.controller;

import com.dliu.springbootwithakkatyped.actors.WeatherStationActor;
import com.dliu.springbootwithakkatyped.service.WeatherStationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/weather")
public class WeatherStationController {

    private final WeatherStationService weatherStationService;

    public WeatherStationController(WeatherStationService weatherStationService) {
        this.weatherStationService = weatherStationService;
    }

    @GetMapping("/{wsid}")
    private Mono<WeatherStationActor.QueryResult> query(@PathVariable Long wsid,
                                                        @RequestParam WeatherStationActor.DataType type,
                                                        @RequestParam WeatherStationActor.Function function) {
        System.out.println("query data type: " + type +  " function: " + function);
        return weatherStationService.query(wsid, type, function);
    }

    @PostMapping("/{wsid}")
    private Mono<WeatherStationActor.DataRecorded> record(@PathVariable Long wsid,
                                                          @RequestBody WeatherStationActor.Data data ) {
        return weatherStationService.recordData(wsid, data);
    }
}
