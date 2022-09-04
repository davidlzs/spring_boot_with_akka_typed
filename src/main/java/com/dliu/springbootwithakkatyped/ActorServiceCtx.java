package com.dliu.springbootwithakkatyped;

import com.dliu.springbootwithakkatyped.service.WeatherStationService;

import org.springframework.beans.factory.annotation.Autowired;

public class ActorServiceCtx {
    @Autowired
    private WeatherStationService weatherStationService;

    public WeatherStationService getWeatherStationService() {
        return weatherStationService;
    }
}
