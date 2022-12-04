package notificationservice.tools.ratelimiter.strategy

import notificationservice.repository.cache.CacheStore
import notificationservice.tools.ratelimiter.RateLimitEventOrigin
import notificationservice.tools.ratelimiter.RateLimitRequestCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MarketingRateLimitValidator
@Autowired
constructor(
    private val cacheStore: CacheStore
): RateLimitValidator {
    override fun validate(command: RateLimitRequestCommand): Mono<Boolean> {
        return cacheStore.getRateLimiterPermit(
            command.userId, command.type.toString(), 3, 1, "HOURS"
        )
    }

    override fun appliesFor(requestCommand: RateLimitRequestCommand): Boolean {
        return requestCommand.type == RateLimitEventOrigin.MARKETING
    }
}