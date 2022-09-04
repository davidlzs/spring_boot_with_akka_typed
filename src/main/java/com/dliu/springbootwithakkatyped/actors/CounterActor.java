package com.dliu.springbootwithakkatyped.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class CounterActor extends AbstractBehavior<CounterActor.Command> {

    public interface Command {}

    public enum Increment implements Command {
        INSTANCE
    }

    public static class GetValue implements Command {
        private final ActorRef<Integer> replyTo;

        public GetValue(ActorRef<Integer> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static Behavior<Command> create(String entityId) {
        return Behaviors.setup(context -> new CounterActor(context, entityId));
    }

    private final String entityId;
    private int value = 0;

    private CounterActor(ActorContext<Command> context, String entityId) {
        super(context);
        this.entityId = entityId;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Increment.class, msg -> onIncrement())
                .onMessage(GetValue.class, this::onGetValue)
                .build();
    }

    private Behavior<Command> onIncrement() {
        value++;
        getContext().getLog().info("CounterActor [{}], incremented value to: {}", entityId, value);
        return this;
    }

    private Behavior<Command> onGetValue(GetValue msg) {
        getContext().getLog().info("CounterActor [{}], get value: {}", entityId, value);
        msg.replyTo.tell(value);
        return this;
    }
}
