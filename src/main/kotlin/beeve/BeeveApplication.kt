package beeve

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BeeveApplication

fun main(args: Array<String>) {
    runApplication<BeeveApplication>(*args)
}
