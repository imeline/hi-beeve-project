package beeve.common.exception

class GlobalException : RuntimeException {
    val status: ErrorStatus

    constructor(status: ErrorStatus) : super(status.message) {
        this.status = status
    }

    constructor(status: ErrorStatus, message: String) : super(message) {
        this.status = status
    }
}
