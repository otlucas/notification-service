package notificationservice.usecases

import notificationservice.model.NotificationResult
import notificationservice.service.NotificationService
import notificationservice.usecases.command.SendNotificationCommand
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SendNotificationUseCases constructor(
    private val notificationService: NotificationService
) {
    fun send(command: SendNotificationCommand): Mono<NotificationResult>{
       return notificationService.sendNotification(command).map {
           NotificationResult("OK", "Notification was sent.")
       }
    }
}