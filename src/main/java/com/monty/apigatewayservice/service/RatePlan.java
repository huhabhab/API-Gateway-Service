package com.monty.apigatewayservice.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.UUID;
/**
 * Enumeration representing different rate plans with associated TPS (Transactions Per Second) limits.
 */
public enum RatePlan {

    L0(60, UUID.fromString("3c7ec7c0-86d9-46e3-bd82-55f26227f51a")), // TPS 1
    L1(120, UUID.fromString("f7c624fc-9f04-4fd4-b12a-58fb0dc3937f")), //TPS 2
    L2(240, UUID.fromString("4eb931f0-22d6-4914-87e6-d8989058000d")), // TPS 4
    L3(480, UUID.fromString("a24e42be-903c-4384-8f1d-cff22607c97d")), // TPS 8
    L4(960, UUID.fromString("f954c4a0-8b6f-410c-aa5d-f7de9315f38a")), // TPS 16
    L5(1920, UUID.fromString("1b2b80f4-dabd-4f8c-a621-2c30993dbe55")); // TPS 32

    private int bucketCapacity;
    private UUID planId;

    private RatePlan(int bucketCapacity, UUID planId) {

        this.bucketCapacity = bucketCapacity;
        this.planId = planId;
    }
    /**
     * Resolves a rate plan based on the provided API key.
     *
     * @param apiKey The API key associated with a rate plan.
     * @return The resolved rate plan, or the default plan (L0) if the key is null or empty.
     */
    static RatePlan resolvePlanFromApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return L0;
        }

        for (RatePlan plan : RatePlan.values()) {
            if (apiKey.equals(plan.planId.toString())) {
                return plan;
            }
        }

        return L0;
    }

    Bandwidth getLimit() {
        return Bandwidth.classic(bucketCapacity, Refill.intervally(bucketCapacity, Duration.ofMinutes(1)));
    }

    public int bucketCapacity() {
        return bucketCapacity;
    }
}
