package notificationservice.usecases.command

import notificationservice.model.request.SendNotificationRequest

data class SendNotificationCommand (
    val userId: String,
    val type: String,
    val message: String
){
    companion object {
        fun from(sendNotificationRequest: SendNotificationRequest): SendNotificationCommand {
            return SendNotificationCommand(
                sendNotificationRequest.userId,
                sendNotificationRequest.notificationType,
                sendNotificationRequest.message
            )
        }
    }
}