package beeve.biz.member.entity

import beeve.biz.auth.dto.request.SignupRequest
import beeve.biz.member.dto.request.MemberProfileRequest
import beeve.biz.member.enum.Gender
import beeve.common.base.SoftDeletableTimeStamped
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode
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
    var name: String,

    var birthDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    var gender: Gender,

    @Column(precision = 5, scale = 2)
    var height: BigDecimal,

    @Column(precision = 5, scale = 2)
    var weight: BigDecimal,

    // BMI = kg / (m^2)
    @Column(precision = 5, scale = 2)
    var bmi: BigDecimal,

    @Column(length = 255)
    var profileUrl: String? = null,

    @Column(length = 255)
    var withdrawReason: String? = null

) : SoftDeletableTimeStamped() {

    companion object {
        fun create(req: SignupRequest): Member {
            return Member(
                email = req.email,
                name = req.name,
                birthDate = req.birthDate,
                gender = req.gender,
                height = req.height,
                weight = req.weight,
                bmi = calculateBmi(req.height, req.weight),
                profileUrl = req.profileUrl,
                withdrawReason = null
            )
        }

        private fun calculateBmi(height: BigDecimal, weight: BigDecimal): BigDecimal {
            if (height <= BigDecimal.ZERO || weight <= BigDecimal.ZERO) {
                throw GlobalException(ErrorStatus.HEIGHT_WEIGHT_INVALID)
            }
            val heightMeter = height.divide(BigDecimal(100), 4, RoundingMode.HALF_UP)
            val denominator = heightMeter * heightMeter
            return weight.divide(denominator, 2, RoundingMode.HALF_UP)
        }
    }

    fun createAndUpdateProfile(req: MemberProfileRequest): Member = apply {
        req.name?.let { name = it }
        req.birthDate?.let { birthDate = it }
        req.gender?.let { gender = it }
        req.height?.let { height = it }
        req.weight?.let { weight = it }
        req.profileUrl?.let { profileUrl = it }
        bmi = calculateBmi(height, weight)
    }

    fun withdraw(reason: String?): Member = apply {
        withdrawReason = reason
        deletedYn = "Y"
    }
}