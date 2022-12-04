package notificationservice.service

import notificationservice.tools.ratelimiter.RateLimiter
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val rateLimiter: RateLimiter
){
}