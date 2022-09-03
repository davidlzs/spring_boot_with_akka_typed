package com.dliu.springbootwithakkatyped.service;

import com.dliu.springbootwithakkatyped.actors.WeatherStation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Scheduler;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import reactor.core.publisher.Mono;

@Service
public class WeatherStationService {

    public static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final ClusterSharding clusterSharding;
    private final Scheduler scheduler;

    @Autowired
    public WeatherStationService(
            ClusterSharding clusterSharding,
            Scheduler scheduler
    ) {
        this.clusterSharding = clusterSharding;
        this.scheduler = scheduler;
    }

    //public void getValue(String id){
    //    weatherStationShardRegion.tell(new ShardingEnvelope<>(id, new WeatherStation.Record(data, DateTime.now(), )));
    //}

    public Mono<WeatherStation.DataRecorded> recordData(Long wsid, WeatherStation.Data data){
        /*CompletionStage<Integer> willBeResponse = AskPattern.ask(
                counterShardRegion,
                (ActorRef<Integer> replyTo) -> new ShardingEnvelope<>(id, new Counter.GetValue(replyTo)),
                TIMEOUT,
                scheduler
        );*/

        //WeatherStation.Data data = new WeatherStation.Data(System.currentTimeMillis(), WeatherStation.DataType.Temperature, 23.3d);
        return Mono.fromCompletionStage(entityRefFor(wsid)
            .ask((ActorRef<WeatherStation.DataRecorded> replyTo) -> new WeatherStation.Record(data, System.currentTimeMillis(), replyTo),
            TIMEOUT));
    }

    private EntityRef<WeatherStation.Command> entityRefFor(Long wsid) {
        return clusterSharding.entityRefFor(WeatherStation.TypeKey, Long.toString(wsid));
    }

    public Mono<WeatherStation.QueryResult> query(Long wsid, WeatherStation.DataType type, WeatherStation.Function function) {
        return Mono.fromCompletionStage(entityRefFor(wsid)
            .ask((ActorRef<WeatherStation.QueryResult> replyTo) -> new WeatherStation.Query(type, function, replyTo),
            TIMEOUT));
    }
}