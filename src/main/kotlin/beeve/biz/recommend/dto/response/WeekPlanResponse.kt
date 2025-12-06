package beeve.biz.recommend.dto.response

import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "운동 추천 응답")
data class WeekPlanResponse(

    @field:Schema(description = "추천 운동 리스트")
    @JsonProperty("workout_plan")
    var weekPlanList: List<DayPlanResponse> = emptyList(),

    // JSON에 notes도 있으니까 받을 거면 같이 정의
    var notes: String? = null,
) {

    companion object {
        fun from(
            geminiRes: GeminiGenerateContentResponse,
            objectMapper: ObjectMapper
        ): WeekPlanResponse {

            val raw = geminiRes.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: throw GlobalException(ErrorStatus.FAILED_TO_RECOMMEND_PROGRAM)

            // 1) 앞뒤 공백 제거
            val trimmed = raw.trim()

            // 2) ```json / ``` 코드블록 감싸져 있으면 제거
            val withoutFence = trimmed
                .removePrefix("```json")
                .removePrefix("```JSON")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            // 3) 혹시 앞뒤로 설명 문장 더 있어도, { ... } 구간만 추출
            val jsonStart = withoutFence.indexOf('{')
            val jsonEnd = withoutFence.lastIndexOf('}')

            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                throw GlobalException(ErrorStatus.JSON_PARSING_ERROR)
            }

            val json = withoutFence.substring(jsonStart, jsonEnd + 1)

            // text 안에 이미 WeekPlanResponse 형태의 JSON이 있다고 가정
            return objectMapper.readValue(json, WeekPlanResponse::class.java)
        }
    }
}
