package beeve

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableFeignClients
@SpringBootApplication
class BeeveApplication

fun main(args: Array<String>) {
    runApplication<BeeveApplication>(*args)
}
