package com.dliu.springbootwithakkatyped.repository;

import com.dliu.springbootwithakkatyped.model.WeatherStation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherStationRepository extends JpaRepository<WeatherStation, Long> {
}
