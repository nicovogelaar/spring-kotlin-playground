import com.nicovogelaar.playground.DatabaseConfig
import com.nicovogelaar.playground.SecurityConfig
import com.nicovogelaar.playground.ServerApplicationConfig
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@SpringBootConfiguration
@Import(
    ServerApplicationConfig::class,
    SecurityConfig::class,
    DatabaseConfig::class,
)
@EnableAutoConfiguration
@ComponentScan(basePackages = ["com.nicovogelaar.playground"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
