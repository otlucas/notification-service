package notificationservice.tools.ratelimiter

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import notificationservice.tools.ratelimiter.validator.RateLimitValidator
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


class RateLimiterImplTests {

    @org.junit.Test
    fun `When receiving rate limit request then validator is called accordingly`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.MARKETING,
            "userId"
        )
        val validator: RateLimitValidator = mockk()
        val validatorThatDoesNotApply: RateLimitValidator = mockk()

        val rateLimiterImpl = RateLimiterImpl(listOf(validatorThatDoesNotApply, validator))
        every { validator.appliesFor(command) } returns true
        every { validatorThatDoesNotApply.appliesFor(command) } returns false

        every { validator.validate(command) } returns Mono.just(true)

        // When
        StepVerifier.create(
            rateLimiterImpl.validate(command)
        ).expectNext(true).verifyComplete()

        // Then
        verify(exactly = 1) {
            validator.appliesFor(command)
        }
        verify(exactly = 1) {
            validatorThatDoesNotApply.appliesFor(command)
        }
        verify (exactly = 1) {
            validator.validate(command)
        }
    }

    @org.junit.Test
    fun `When receiving rate limit request then validate is not called if validators do not apply`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.MARKETING,
            "userId"
        )
        val validator: RateLimitValidator = mockk()
        val validatorThatDoesNotApply: RateLimitValidator = mockk()

        val rateLimiterImpl = RateLimiterImpl(listOf(validatorThatDoesNotApply, validator))
        every { validator.appliesFor(command) } returns false
        every { validatorThatDoesNotApply.appliesFor(command) } returns false

        every { validator.validate(command) } returns Mono.just(true)

        // When
        StepVerifier.create(
            rateLimiterImpl.validate(command)
        ).expectError().verify()

        // Then
        verify(exactly = 1) {
            validator.appliesFor(command)
        }
        verify(exactly = 1) {
            validatorThatDoesNotApply.appliesFor(command)
        }
        verify (exactly = 0) {
            validator.validate(command)
        }
    }

    @org.junit.Test
    fun `When receiving rate limit request if validate returns false then exception is thrown`() {
        // Given
        val command = RateLimitRequestCommand(
            RateLimitEventOrigin.MARKETING,
            "userId"
        )
        val validator: RateLimitValidator = mockk()
        val validatorThatDoesNotApply: RateLimitValidator = mockk()

        val rateLimiterImpl = RateLimiterImpl(listOf(validatorThatDoesNotApply, validator))
        every { validator.appliesFor(command) } returns true
        every { validatorThatDoesNotApply.appliesFor(command) } returns false

        every { validator.validate(command) } returns Mono.just(false)

        // When
        StepVerifier.create(
            rateLimiterImpl.validate(command)
        ).expectError().verify()

        // Then
        verify(exactly = 1) {
            validator.appliesFor(command)
        }
        verify(exactly = 1) {
            validatorThatDoesNotApply.appliesFor(command)
        }
        verify (exactly = 1) {
            validator.validate(command)
        }
    }
}