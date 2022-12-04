package notificationservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import notificationservice.model.NotificationResult
import notificationservice.model.request.SendNotificationRequest
import notificationservice.model.response.SendNotificationResponse
import notificationservice.usecases.SendNotificationUseCases
import notificationservice.usecases.command.SendNotificationCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class NotificationController
@Autowired
constructor(
    private val sendNotificationUseCases: SendNotificationUseCases
) {
    companion object{
        private const val INTERNAL_SERVER_ERROR = "Internal Server Error"
        private const val ERROR = "ERROR"
    }

    @PostMapping(path = ["/notification"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Sends notification for specified user")
    fun sendNotification(
        @RequestBody
        @Parameter(description = "Describes the notification to be sent")
        sendNotificationRequest: SendNotificationRequest
    ): Mono<ResponseEntity<SendNotificationResponse>>
    {
        return this.sendNotificationUseCases.send(SendNotificationCommand.from(sendNotificationRequest))
            .map(this::handleResponse)
            .onErrorResume(this::handleError)
    }

    private fun handleResponse(
        notificationResult: NotificationResult
    ): ResponseEntity<SendNotificationResponse> {
        return ResponseEntity.ok().body(SendNotificationResponse(notificationResult.status, notificationResult.message))
    }

    private fun handleError(
        error: Throwable
    ): Mono<ResponseEntity<SendNotificationResponse>> {
        return Mono.just(
            ResponseEntity.badRequest().body(
                SendNotificationResponse(ERROR, error.message ?: INTERNAL_SERVER_ERROR)
            ))
    }
}