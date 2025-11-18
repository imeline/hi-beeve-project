package beeve.common.base

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class SoftDeletableTimeStamped : TimeStamped() {

    @Column(length = 1, nullable = false)
    var deletedYn: String = "N"
        protected set
}
