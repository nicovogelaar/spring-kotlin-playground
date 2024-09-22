import com.nicovogelaar.playground.SecurityConfig
import com.nicovogelaar.playground.ServerApplicationConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Import

@SpringBootConfiguration
@Import(
	ServerApplicationConfig::class,
	SecurityConfig::class,
)
@EnableAutoConfiguration
class PlaygroundApplication

fun main(args: Array<String>) {
	SpringApplication.run(PlaygroundApplication::class.java, *args)
}
