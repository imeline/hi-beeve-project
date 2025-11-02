package beeve.biz.member.dto.response

import beeve.biz.member.entity.Member
import beeve.biz.member.enum.Gender
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(
    description = """
회원 프로필 조회 응답
- 키/몸무게/생년월일 등 미등록 항목은 null 로 반환됩니다.
"""
)
data class MemberProfileResponse(
    @field:Schema(description = "이름", nullable = true)
    val name: String? = null,

    @field:Schema(description = "생년월일(yyyy-MM-dd)", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    val birthDate: LocalDate? = null,

    @field:Schema(description = "성별(F/M)", nullable = true)
    val gender: Gender? = null,

    @field:Schema(description = "키(cm), 소수점 둘째 자리까지", nullable = true)
    val height: BigDecimal? = null,

    @field:Schema(description = "몸무게(kg), 소수점 둘째 자리까지", nullable = true)
    val weight: BigDecimal? = null,

    @field:Schema(description = "프로필 이미지 URL", nullable = true)
    val profileUrl: String? = null,
) {
    companion object {
        fun from(member: Member): MemberProfileResponse = MemberProfileResponse(
            name = member.name,
            birthDate = member.birthDate,
            gender = member.gender,
            height = member.height,
            weight = member.weight,
            profileUrl = member.profileUrl
        )
    }
}
