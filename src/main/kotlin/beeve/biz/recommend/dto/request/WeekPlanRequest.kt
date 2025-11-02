package beeve.biz.recommend.dto.request

import beeve.biz.fitness.enum.MeasurePlace
import beeve.biz.member.enum.Gender
import beeve.biz.recommend.enum.PurposeType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "맞춤형 운동 추천을 위한 회원 정보")
class WeekPlanRequest (

    @field:Schema(description = "성별(F/M)")
    val contents: List<Content>
) {
    companion object {
        fun toGeminiRequest(
            gender: Gender,
            age: Int,
            measurePlace: MeasurePlace,
            purpose: PurposeType,
            contraindications: String
        ): WeekPlanRequest {

            val rule = buildString {
                append("Rule:")
                append("-운동 처방 코치 역할")
                append("-의학적 진단 금지")
                append("-금기사항 반드시 우선 고려,있으면 해당 동작 제외/대체안")
                append("-출력:지정 JSON 스키마만")
                append("-목표 해당 운동 제시")
                append("-주 1회 밸런스 세션 포함")
                append("-환경:제약 준수")
                append("-강도:RPE 또는 쉬운 진행규칙으로 단계 상승")
                append("-초보:하루 push/pull/하체 중 1~2범주만")
                append("-세션:25~35분 기본, 휴식/워밍업 포함")
                append("-warm_up, cool_down은 1줄 문구")
                append("-notes는 20자 이내 요약-exercises는 3~4개만")
                append("-불필요한 설명 금지")
            }

            val jsonTemplate = buildString {
                append("JSON:")
                append("{workout_plan: [{day:'',focus:'',warm_up:''," +
                        "exercises:[{ name:'',sets:'', reps:'',rest_seconds:'',rpe:''}],cool_down:''}],notes:''}")
            }

            val profile = buildString {
                append("Profile:{")
                append("성별:").append(
                    when (gender) {
                        Gender.F -> "여성"; Gender.M -> "남성"; else -> "기타"
                    }
                ).append(",")
                append("나이:").append(age).append(",")
                append("부상/금기:")
                append(contraindications.ifEmpty { "X" })
                    .append(",")
                append("환경:").append(
                    when (measurePlace) {
                        MeasurePlace.HOME -> "집"; MeasurePlace.GYM -> "헬스장"; MeasurePlace.OUTDOOR -> "야외"; else -> "야외"
                    }
                ).append(",")
                append("목표:")
                append(
                    when (purpose) {
                        PurposeType.CARDIO_ENDURANCE -> "심폐지구력 향상"
                        PurposeType.FAT_LOSS -> "체지방 감소"
                        PurposeType.MUSCLE_GAIN -> "근비대"
                        PurposeType.GENERAL_FITNESS -> "전반 체력 향상"
                    }
                )
                append("}")
            }

            val text = buildString {
                append(rule).append(jsonTemplate).append(profile)
            }

            val content = Content(
                parts = listOf(Part(text = text))
            )

            return WeekPlanRequest(listOf(content))
        }
    }
}

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)
/*
{
    "contents": [
    {
        "parts": [
        {
            "text":
            "Rule:
            -운동 처방 코치 역할
            -의학적 진단 금지
            -금기사항 반드시 우선 고려,있으면 해당 동작 제외/대체안
            -출력:지정 JSON 스키마만-목표 해당 운동 제시
            -주 1회 밸런스 세션 포함
            -환경:제약 준수
            -강도:RPE 또는 쉬운 진행규칙으로 단계 상승
            -초보:하루 push/pull/하체 중 1~2범주만
            -세션:25~35분 기본, 휴식/워밍업 포함-warm_up, cool_down은 1줄 문구
            -notes는 20자 이내 요약-exercises는 3~4개만
            -불필요한 설명 금지
            JSON:{workout_plan: [{day:'',focus:'',warm_up:'',exercises:[{ name:'',sets:'', reps:'',rest_seconds:'',rpe:''}],cool_down:''}],notes:''}
            Profile:{성별:여성,나이:27,경험레벨:초보,부상/금기:어깨충돌증후군,환경:집,목표:심폐지구력 향상}"
        }
        ]
    }
    ]
}*/
