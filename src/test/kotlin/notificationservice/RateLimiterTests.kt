package notificationservice

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.*
import notificationservice.controller.NotificationController
import notificationservice.model.request.SendNotificationRequest
import notificationservice.model.response.SendNotificationResponse
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class RateLimiterTests
constructor(
	private val notificationController: NotificationController
)
{
	private var notificationRequest: SendNotificationRequest? = null

	private var currentRequest: Mono<ResponseEntity<SendNotificationResponse>>? = null

	@Given("A notification with type {string} and user id {string}")
	fun a_notification_with_type_and_user_id(type: String, userId: String) {
		notificationRequest = SendNotificationRequest(userId, type, "text message")
	}

	@When("Trying to send notification")
	fun trying_to_send_notification() {
		currentRequest = notificationController.sendNotification(notificationRequest!!)
	}

	@Then("Notification is sent succesfully {int} times")
	fun notification_is_sent_succesfully_times(times: Int) {
		repeat(times) {
			StepVerifier.create(
				currentRequest!!
			).expectNext(
				ResponseEntity.ok(SendNotificationResponse("OK", "Notification was sent."))
			).verifyComplete()
		}
	}

	@Then("Notification is not sent succesfully")
	fun notification_is_not_sent_succesfully() {
		StepVerifier.create(
			currentRequest!!
		).expectNext(
			ResponseEntity.badRequest().body(
				SendNotificationResponse(
					"ERROR",
					"Rate limiter policies for NEWS for user USER_ID_1 exceeded"
				)
			)
		).verifyComplete()
	}

	@Then("Notification is not sent succesfully for type {string} and user id {string}")
	fun notification_is_not_sent_succesfully_for_type_and_user_id(type: String, userId: String) {
		StepVerifier.create(
			currentRequest!!
		).expectNext(
			ResponseEntity.badRequest().body(
				SendNotificationResponse(
					"ERROR",
					"Rate limiter policies for $type for user $userId exceeded"
				)
			)
		).verifyComplete()
	}

	@Then("Notification is not sent succesfully for type {string} and user id {string} after sending {int} previously")
	fun notification_is_not_sent_succesfully_for_type_and_user_id_after_sending_previously(
		type: String,
		userId: String,
		previouslySentTimes: Int
	) {
		repeat(previouslySentTimes) {
			StepVerifier.create(
				currentRequest!!
			).expectNext(
				ResponseEntity.ok(SendNotificationResponse("OK", "Notification was sent."))
			).verifyComplete()
		}
		StepVerifier.create(
			currentRequest!!
		).expectNext(
			ResponseEntity.badRequest().body(
				SendNotificationResponse(
					"ERROR",
					"Rate limiter policies for $type for user $userId exceeded"
				)
			)
		).verifyComplete()
	}

	@Then("Notification is sent succesfully {int} times, waits {int} {string}, and sent succesfully one more time")
	fun notification_is_sent_succesfully_times_waits_and_sent_succesfully_time(
		sentSuccesfullyTimes: Int,
		timeUnitWait: Int,
		timeUnitValue: String
	) {
		repeat(sentSuccesfullyTimes) {
			StepVerifier.create(
				currentRequest!!
			).expectNext(
				ResponseEntity.ok(SendNotificationResponse("OK", "Notification was sent."))
			).verifyComplete()
		}

		Instant.now(
			Clock.fixed(
				Instant.now().plus(timeUnitWait.toLong(), ChronoUnit.valueOf(timeUnitValue)), ZoneOffset.UTC
			)
		)

		StepVerifier.create(
			currentRequest!!
		).expectNext(
			ResponseEntity.ok(SendNotificationResponse("OK", "Notification was sent."))
		).verifyComplete()
	}
}
