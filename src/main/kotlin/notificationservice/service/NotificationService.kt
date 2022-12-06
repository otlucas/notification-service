package notificationservice.service

import notificationservice.model.entity.Notification
import notificationservice.tools.ratelimiter.RateLimitEventOrigin
import notificationservice.tools.ratelimiter.RateLimitRequestCommand
import notificationservice.tools.ratelimiter.RateLimiter
import notificationservice.usecases.command.SendNotificationCommand
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class NotificationService(
    private val rateLimiter: RateLimiter
){
    fun sendNotification(command: SendNotificationCommand): Mono<Notification> {
        return rateLimiter.validate(RateLimitRequestCommand(RateLimitEventOrigin.valueOf(command.type), command.userId))
            .map { Notification("mocked id") }
    }
}