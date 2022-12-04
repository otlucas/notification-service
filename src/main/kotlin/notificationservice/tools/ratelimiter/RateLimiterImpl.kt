package notificationservice.tools.ratelimiter

import notificationservice.tools.ratelimiter.strategy.RateLimitValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RateLimiterImpl
@Autowired
constructor(
    private val validators: List<RateLimitValidator>
): RateLimiter {
    override fun validate(requestCommand: RateLimitRequestCommand) {
        validators.first { it.appliesFor(requestCommand) }.validate(requestCommand)
    }
}