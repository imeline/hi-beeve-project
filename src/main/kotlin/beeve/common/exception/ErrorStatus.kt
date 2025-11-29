package beeve.common.exception

import org.springframework.http.HttpStatus

enum class ErrorStatus {
    // 공통
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다."),
    NOT_READABLE(HttpStatus.BAD_REQUEST, "NOT_READABLE", "요청 본문을 해석할 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "토큰 인증에 실패했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_SERVER_ERROR",
        "서버 내부 오류가 발생했습니다."
    ),

    // AUTH
    SOCIAL_AUTH_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH101", "소셜 인증 가입 이력이 존재하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH102", "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH103", "리프레시 토큰이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH104", "리프레시 토큰이 일치하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH105", "리프레시 토큰이 만료되었습니다."),
    SOCIAL_AUTH_ALREADY_EXISTS(HttpStatus.UNAUTHORIZED, "AUTH106", "이미 가입된 소셜 계정입니다."),

    // MEMBER
    MEMBER_NOT_FOUND("MEMBER201", "회원이 존재하지 않습니다."),
    HEIGHT_WEIGHT_INVALID("MEMBER203", "회원 키 또는 몸무게 값이 올바르지 않습니다."),

    // FITNESS
    FITNESS_TODAY_ALREADY_EXISTS("FITNESS301", "체력 측정은 하루에 1번만 가능합니다."),
    FITNESS_NOT_FOUND("FITNESS302", "체력 측정 기록이 존재하지 않습니다."),
    RANK_STANDARD_NOT_FOUND("FITNESS303", "체력 등급 기준 정보가 존재하지 않습니다."),
    FITNESS_STRENGTH_INVALID(
        "FITNESS304",
        "근력 측정 시 wallPushUpReps, kneePushUpReps, standardPushUpReps 중 정확히 하나만 입력해야 합니다."
    ),
    FITNESS_GRADE_MISSING("FITNESS305", "필수 체력 등급 정보가 누락되었습니다."),
    FITNESS_TYPE_INVALID("FITNESS306", "유효하지 않은 체력 측정 유형입니다."),
    FITNESS_COMPARISON_DATA_INVALID("FITNESS307", "체력 측정 비교 대상 데이터가 올바르지 않습니다."),

    // RECOMMEND
    FAILED_TO_RECOMMEND_PROGRAM("RECOMMEND401", "추천 운동 생성에 실패하였습니다."),

    ;

    val httpStatus: HttpStatus
    val code: String
    val message: String

    // status, message 만 쓸 경우, 기본 HttpStatus.BAD_REQUEST 사용
    constructor(code: String, message: String) {
        this.httpStatus = HttpStatus.BAD_REQUEST // 커스텀 에러의 상태 코드 기본값
        this.code = code
        this.message = message
    }

    // httpStatus, status, message 모두 쓸 경우
    constructor(httpStatus: HttpStatus, code: String, message: String) {
        this.httpStatus = httpStatus
        this.code = code
        this.message = message
    }
}
