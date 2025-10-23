package beeve.global.exception

import org.springframework.http.HttpStatus

enum class ErrorStatus {
    // 공통
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다."),
    NOT_READABLE(HttpStatus.BAD_REQUEST, "NOT_READABLE", "요청 본문을 해석할 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),

    // AUTH
    DUPLICATE_LOGIN_EMAIL("AUTH101", "기존 사용중인 이메일입니다."),
    INVALID_LOGIN_CREDENTIALS("AUTH102", "아이디 또는 비밀번호가 일치하지 않습니다."),
    NOT_FOUND_TENANT_ID("AUTH103", "테넌트 ID가 설정되지 않았습니다."),
    SIGNUP_FAIL("AUTH104", "회원가입에 실패했습니다."),
    NOT_FOUND_ERP_ACCOUNT("AUTH105", "계정을 찾을 수 없습니다.");

    val httpStatus: HttpStatus
    val status: String
    val message: String

    // status, message 만 쓸 경우, 기본 HttpStatus.BAD_REQUEST 사용
    constructor(status: String, message: String) {
        this.httpStatus = HttpStatus.BAD_REQUEST // 커스텀 에러의 상태 코드 기본값
        this.status = status
        this.message = message
    }

    // httpStatus, status, message 모두 쓸 경우
    constructor(httpStatus: HttpStatus, status: String, message: String) {
        this.httpStatus = httpStatus
        this.status = status
        this.message = message
    }
}
