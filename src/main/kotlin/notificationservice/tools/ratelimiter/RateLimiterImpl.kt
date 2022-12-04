package notificationservice.tools.ratelimiter

import notificationservice.repository.cache.CacheStore
import org.springframework.stereotype.Component

@Component
class RateLimiterImpl(
    private val cacheStore: CacheStore
): RateLimiter {
}