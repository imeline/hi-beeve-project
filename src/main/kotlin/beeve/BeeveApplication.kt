package beeve

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
class BeeveApplication

fun main(args: Array<String>) {
    runApplication<BeeveApplication>(*args)
}
