package notificationservice.tools.ratelimiter.validator

import notificationservice.tools.ratelimiter.RateLimitRequestCommand
import reactor.core.publisher.Mono

interface RateLimitValidator {
    fun validate(command: RateLimitRequestCommand): Mono<Boolean>
    fun appliesFor(command: RateLimitRequestCommand): Boolean
}