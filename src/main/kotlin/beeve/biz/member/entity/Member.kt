package beeve.biz.member.entity

import beeve.biz.member.enum.Gender
import beeve.common.base.TimeStamped
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "member")
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val memberId: Long? = null,

    @Column(length = 255)
    var email: String? = null,

    @Column(length = 50)
    var name: String? = null,

    var birthDate: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    var gender: Gender? = null,

    @Column(precision = 5, scale = 2)
    var height: BigDecimal? = null,

    @Column(precision = 5, scale = 2)
    var weight: BigDecimal? = null,

    @Column(length = 255)
    var profileUrl: String? = null,

    @Column(length = 255)
    var withdrawReason: String? = null,

    @Column(length = 1, nullable = false)
    var deletedYn: String = "N"

) : TimeStamped()
