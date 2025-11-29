package beeve.biz.recommend.dto.response

class DayPlanResponse {
    var day: String? = null

    var focus: String? = null

    var warmUp: String? = null

    var exerciseList: MutableList<ProgramPlanResponse?>? = null
}
