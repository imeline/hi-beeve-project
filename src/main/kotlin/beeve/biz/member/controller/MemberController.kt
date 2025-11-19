package beeve.biz.member.controller

import beeve.biz.auth.security.JwtTokenProvider
import beeve.biz.member.dto.request.MemberProfileRequest
import beeve.biz.member.dto.request.MemberWithdrawRequest
import beeve.biz.member.dto.response.MemberProfileResponse
import beeve.biz.member.service.MemberService
import beeve.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "Member", description = "회원 API")
@RestController
@RequestMapping("/api/v1/member")
class MemberController(
    private val memberService: MemberService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Operation(
        summary = "회원 프로필 등록 및 수정",
        description = """
        - 요청 필드 중 null이 아닌 값만 업데이트됩니다.(필드 자체를 안보내도 됨)
        - 생년월일, 성별, 키, 몸무게, 프로필 이미지를 수정할 수 있습니다.
        """
    )
    @PostMapping("/profile")
    fun createOrUpdateProfile(
        @RequestHeader("Authorization") accessHeader: String,
        @Valid @RequestBody req: MemberProfileRequest
    ): ApiResponse<Void?> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        memberService.createAndUpdateProfile(memberId, req)
        return ApiResponse.onSuccess(null)
    }

    @Operation(
        summary = "회원 프로필 조회",
        description = """
        - 회원의 프로필 정보를 조회합니다.
        - 키/몸무게/생년월일 등 미등록 항목은 null 로 반환됩니다.
        """
    )
    @GetMapping("/profile")
    fun getProfile(
        @RequestHeader("Authorization") accessHeader: String
    ): ApiResponse<MemberProfileResponse> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        val res = memberService.getProfile(memberId)
        return ApiResponse.onSuccess(res)
    }

    @Operation(
        summary = "회원 탈퇴",
        description = """
        - 탈퇴 사유는 null 가능이며, 최대 255자까지 저장됩니다.
        """
    )
    @DeleteMapping
    fun withdraw(
        @RequestHeader("Authorization") accessHeader: String,
        @Valid @RequestBody req: MemberWithdrawRequest
    ): ApiResponse<Void?> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        memberService.withdraw(memberId, req)
        return ApiResponse.onSuccess(null)
    }
}