package com.dliu.springbootwithakkatyped.config;

import com.dliu.springbootwithakkatyped.actors.Counter;
import com.dliu.springbootwithakkatyped.actors.WeatherStation;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.SpawnProtocol;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.management.javadsl.AkkaManagement;


@Configuration
public class ServiceConfig {

    @Autowired
    private Environment environment;

    @Bean
    public Scheduler systemScheduler() {
        return system().scheduler();
    }

    @Bean
    public Config akkaConfig() {
        return ConfigFactory.load();
    }

    @Bean(destroyMethod = "terminate")
    public ActorSystem<SpawnProtocol.Command> system() {
        Config config = akkaConfig();
        return ActorSystem.create(
                Behaviors.setup(ctx -> {
                    akka.actor.ActorSystem unTypedSystem = Adapter.toClassic(ctx.getSystem());
                    AkkaManagement.get(unTypedSystem).start();
                    ClusterBootstrap.get(unTypedSystem).start();
                    WeatherStation.initSharding(ctx.getSystem());
                    return SpawnProtocol.create();
                }), config.getString("counter.cluster.name"));
    }

    @Bean
    public ClusterSharding clusterSharding() {
        return ClusterSharding.get(system());
    }

    @Bean
    public ActorRef<ShardingEnvelope<Counter.Command>> counterShardRegion() {
        ClusterShardingSettings settings = ClusterShardingSettings
                .create(system().classicSystem());
        EntityTypeKey<Counter.Command> typeKey = EntityTypeKey.create(Counter.Command.class, "Counter");
        return clusterSharding().init(Entity.of(typeKey, ctx -> Counter.create(ctx.getEntityId())));
    }
}