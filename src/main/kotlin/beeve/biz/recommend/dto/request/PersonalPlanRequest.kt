package beeve.biz.recommend.dto.request

import beeve.biz.fitness.enum.MeasurePlace
import beeve.biz.member.enum.Gender
import beeve.biz.recommend.enum.PurposeType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "맞춤형 운동 추천을 위한 회원 정보")
class PersonalPlanRequest (

    @field:Schema(description = "성별(F/M)")
    var gender: Gender,

    @field:Schema(description = "나이")
    var age: Int,

    @field:Schema(description = "부상/금기")
    var contraindications: String? = null,

    @field:Schema(description = "환경")
    var measurePlace: MeasurePlace? = null,

    @field:Schema(description = "목적")
    var purpose: PurposeType
)
