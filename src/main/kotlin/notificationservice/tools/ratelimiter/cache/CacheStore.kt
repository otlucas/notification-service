package notificationservice.tools.ratelimiter.cache

import reactor.core.publisher.Mono

interface CacheStore {
    fun getRateLimiterPermit(
        userId: String,
        type: String,
        rate: Long,
        rateInterval: Long,
        intervalUnit: String
    ): Mono<Boolean>
}