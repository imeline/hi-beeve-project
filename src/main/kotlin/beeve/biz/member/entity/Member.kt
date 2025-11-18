package beeve.biz.member.entity

import beeve.biz.member.dto.request.MemberProfileRequest
import beeve.biz.member.enum.Gender
import beeve.common.base.TimeStamped
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

    @field:Min(1)
    @field:Max(5)
    @Column(columnDefinition = "SMALLINT")
    val totalGrade: Short? = null,

    @Column(length = 255)
    var withdrawReason: String? = null,

    @Column(length = 1, nullable = false)
    var deletedYn: String = "N"

) : TimeStamped() {

    @get:Transient
    val isPresentProfile: Boolean
        get() = birthDate != null && gender != null && height != null && weight != null

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