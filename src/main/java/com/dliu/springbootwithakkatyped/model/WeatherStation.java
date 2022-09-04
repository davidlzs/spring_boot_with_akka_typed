package com.dliu.springbootwithakkatyped.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "weather_stations")
@Data
@ToString
public class WeatherStation {
    @Id
    private long id;
}
