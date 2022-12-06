package notificationservice.tools.ratelimiter

import reactor.core.publisher.Mono

interface RateLimiter {
    fun validate(requestCommand: RateLimitRequestCommand): Mono<Boolean>
}