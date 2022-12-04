package notificationservice.tools.ratelimiter

data class RateLimitRequestCommand (
    val type: RateLimitEventOrigin,
    val userId: String
)