package com.dliu.springbootwithakkatyped.service;

import com.dliu.springbootwithakkatyped.actors.WeatherStationActor;
import com.dliu.springbootwithakkatyped.model.WeatherStation;
import com.dliu.springbootwithakkatyped.repository.WeatherStationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Scheduler;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WeatherStationService {

    public static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final ClusterSharding clusterSharding;
    private final Scheduler scheduler;

    private final WeatherStationRepository weatherStationRepository;

    @Autowired
    public WeatherStationService(
            ClusterSharding clusterSharding,
            Scheduler scheduler,
            WeatherStationRepository weatherStationRepository) {
        this.clusterSharding = clusterSharding;
        this.scheduler = scheduler;
        this.weatherStationRepository = weatherStationRepository;
    }

    //public void getValue(String id){
    //    weatherStationShardRegion.tell(new ShardingEnvelope<>(id, new WeatherStationActor.Record(data, DateTime.now(), )));
    //}

    public Mono<WeatherStationActor.DataRecorded> recordData(Long wsid, WeatherStationActor.Data data){
        /*CompletionStage<Integer> willBeResponse = AskPattern.ask(
                counterShardRegion,
                (ActorRef<Integer> replyTo) -> new ShardingEnvelope<>(id, new CounterActor.GetValue(replyTo)),
                TIMEOUT,
                scheduler
        );*/

        //WeatherStationActor.Data data = new WeatherStationActor.Data(System.currentTimeMillis(), WeatherStationActor.DataType.Temperature, 23.3d);
        return Mono.fromCompletionStage(entityRefFor(wsid)
            .ask((ActorRef<WeatherStationActor.DataRecorded> replyTo) -> new WeatherStationActor.Record(data, System.currentTimeMillis(), replyTo),
            TIMEOUT));
    }

    private EntityRef<WeatherStationActor.Command> entityRefFor(Long wsid) {
        return clusterSharding.entityRefFor(WeatherStationActor.TypeKey, Long.toString(wsid));
    }

    public Mono<WeatherStationActor.QueryResult> query(Long wsid, WeatherStationActor.DataType type, WeatherStationActor.Function function) {
        return Mono.fromCompletionStage(entityRefFor(wsid)
            .ask((ActorRef<WeatherStationActor.QueryResult> replyTo) -> new WeatherStationActor.Query(type, function, replyTo),
            TIMEOUT));
    }

    public WeatherStation save(WeatherStation weatherStation) {
        WeatherStation savedWeatherStation = weatherStationRepository.save(weatherStation);
        log.info("saved {}", savedWeatherStation);
        return savedWeatherStation;
    }

    public Optional<WeatherStation> findById(long wsid) {
        return weatherStationRepository.findById(wsid);
    }
}