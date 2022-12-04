package notificationservice.model.request

data class SendNotificationRequest (
    val userId: String,
    val notificationType: String,
    val message: String
)