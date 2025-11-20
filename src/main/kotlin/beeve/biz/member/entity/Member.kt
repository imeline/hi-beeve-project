package beeve.biz.member.entity

import beeve.biz.auth.dto.request.SignupRequest
import beeve.biz.member.dto.request.MemberProfileRequest
import beeve.biz.member.enum.Gender
import beeve.common.base.SoftDeletableTimeStamped
import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
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
    var name: String,

    var birthDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    var gender: Gender,

    @Column(precision = 5, scale = 2)
    var height: BigDecimal,

    @Column(precision = 5, scale = 2)
    var weight: BigDecimal,

    @Column(length = 255)
    var profileUrl: String? = null,

    @field:Min(1)
    @field:Max(5)
    @Column(columnDefinition = "SMALLINT")
    val totalGrade: Short? = null,

    @Column(length = 255)
    var withdrawReason: String? = null

) : SoftDeletableTimeStamped() {

    companion object {
        fun create(req: SignupRequest): Member =
            Member(
                email = req.email,
                name = req.name,
                birthDate = req.birthDate,
                gender = req.gender,
                height = req.height,
                weight = req.weight,
                profileUrl = req.profileUrl,
                totalGrade = null,
                withdrawReason = null
            )
    }

    // 프로필 생성, 수정
    // req의 각 필드가 null이 아니면 해당 필드를 덮어씀
    fun createAndUpdateProfile(req: MemberProfileRequest): Member = apply {
        req.name?.let { name = it }
        req.birthDate?.let { birthDate = it }
        req.gender?.let { gender = it }
        req.height?.let { height = it }
        req.weight?.let { weight = it }
        req.profileUrl?.let { profileUrl = it }
    }

    fun withdraw(reason: String?): Member = apply {
        withdrawReason = reason
        deletedYn = "Y"
    }
}