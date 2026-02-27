package com.siteshkumar.zomato_clone_backend.enums;

import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    CREATED,
    PLACED,
    CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
                CREATED, Set.of(PLACED, CANCELLED),
                PLACED, Set.of(CONFIRMED, CANCELLED),
                CONFIRMED, Set.of(PREPARING, CANCELLED),
                PREPARING, Set.of(OUT_FOR_DELIVERY),
                OUT_FOR_DELIVERY, Set.of(DELIVERED),
                DELIVERED, Set.of(),
                CANCELLED, Set.of()
    );

    public boolean canTransitionTo(OrderStatus next){

        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }
}
