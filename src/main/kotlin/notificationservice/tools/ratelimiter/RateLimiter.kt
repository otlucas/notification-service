package notificationservice.tools.ratelimiter

interface RateLimiter {
    fun validate(requestCommand: RateLimitRequestCommand)
}