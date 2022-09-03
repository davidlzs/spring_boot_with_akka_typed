package com.dliu.springbootwithakkatyped.service;

import com.dliu.springbootwithakkatyped.actors.Counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.cluster.sharding.typed.ShardingEnvelope;
import reactor.core.publisher.Mono;

@Service
public class CounterService {

    public static final Duration TIMEOUT = Duration.ofSeconds(10);
    private final ActorRef<ShardingEnvelope<Counter.Command>> counterShardRegion;
    private final Scheduler scheduler;

    @Autowired
    public CounterService(
            ActorRef<ShardingEnvelope<Counter.Command>> counterShardRegion,
            Scheduler scheduler
    ) {
        this.counterShardRegion = counterShardRegion;
        this.scheduler = scheduler;
    }

    public void getValue(String id){
        counterShardRegion.tell(new ShardingEnvelope<>(id, Counter.Increment.INSTANCE));
    }
    public Mono<Integer> incrementByOne(String id){
        CompletionStage<Integer> willBeResponse = AskPattern.ask(
                counterShardRegion,
                (ActorRef<Integer> replyTo) -> new ShardingEnvelope<>(id, new Counter.GetValue(replyTo)),
                TIMEOUT,
                scheduler
        );
        return Mono.fromCompletionStage(willBeResponse);
    }
}