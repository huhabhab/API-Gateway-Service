package com.monty.apigatewayservice.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for resolving and caching rate plan-based buckets.
 */
@Service
public class RatePlanService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        RatePlan ratePlan = RatePlan.resolvePlanFromApiKey(apiKey);
        return bucket(ratePlan.getLimit());
    }

    private Bucket bucket(Bandwidth limit) {
        return Bucket.builder().addLimit(limit).build();
    }
}
