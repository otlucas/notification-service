package notificationservice.tools.ratelimiter

import notificationservice.tools.ratelimiter.validator.RateLimitValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RateLimiterImpl
@Autowired
constructor(
    private val validators: List<RateLimitValidator>
): RateLimiter {
    override fun validate(requestCommand: RateLimitRequestCommand): Mono<Boolean> {
        return (validators.firstOrNull { it.appliesFor(requestCommand) }?.validate(requestCommand)?.map {
            it
        } ?: Mono.just(false)).map {
            require(it) {
                "Rate limiter policies for ${requestCommand.type} for user ${requestCommand.userId} exceeded"
            }
            it
        }
    }
}