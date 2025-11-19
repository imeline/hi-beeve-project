package beeve.biz.member.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "회원 탈퇴 요청")
data class MemberWithdrawRequest(

    @field:Schema(description = "탈퇴 사유", nullable = true)
    @field:Size(max = 255, message = "탈퇴 사유는 255자 이하여야 합니다.")
    val withdrawReason: String? = null
)