package beeve.biz.recommend.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class DayPlanResponse(

    var day: String? = null,

    var focus: String? = null,

    @JsonProperty("warm_up")
    var warmUp: String? = null,

    @JsonProperty("cool_down")
    var coolDown: String? = null,

    @JsonProperty("exercises")
    var exerciseList: List<ProgramPlanResponse> = emptyList(),
)

data class ProgramPlanResponse(
    var name: String? = null,
    var sets: String? = null,
    var reps: String? = null,
    @JsonProperty("rest_seconds")
    var restSeconds: String? = null,
    var rpe: String? = null,
)


