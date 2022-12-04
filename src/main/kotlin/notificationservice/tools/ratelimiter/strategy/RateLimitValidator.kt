package notificationservice.tools.ratelimiter.strategy

import notificationservice.tools.ratelimiter.RateLimitRequestCommand
import reactor.core.publisher.Mono

interface RateLimitValidator {
    fun validate(requestCommand: RateLimitRequestCommand): Mono<Boolean>
    fun appliesFor(requestCommand: RateLimitRequestCommand): Boolean
}