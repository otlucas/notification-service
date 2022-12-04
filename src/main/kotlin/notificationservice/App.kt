package notificationservice

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(
    title = "Notification service",
    description = "Sends notifications to users, with options for rate limiting"
)
)
class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello World!")

            // Try adding program arguments via Run/Debug configuration.
            // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
            println("Program arguments: ${args.joinToString()}")
        }
    }
}
