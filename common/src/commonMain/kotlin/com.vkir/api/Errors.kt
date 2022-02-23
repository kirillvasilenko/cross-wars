package com.vkir.api
import kotlinx.serialization.Serializable

private val raiserByCode = mutableMapOf<Int, (ApiError.ErrorArgs) -> Nothing>()

class ApiError(
    val code: Int,
    val exceptionRaiser: (ErrorArgs) -> Nothing
) {
    operator fun invoke(
        message: String? = null,
        cause: Throwable? = null,
        args: List<String>? = null
    ): Nothing {
        exceptionRaiser(ErrorArgs(message, code, cause, args))
    }

    data class ErrorArgs(
        val message: String?,
        val code: Int,
        val cause: Throwable?,
        val args: List<String>?
    ) {
        fun raise(f: (String?, Int, Throwable?, List<String>?) -> Throwable): Nothing =
            throw f(message, code, cause, args)

        fun raise(f: (String?, Int, Throwable?) -> Throwable): Nothing =
            throw f(message, code, cause)

        fun raise(f: (String?, Int) -> Throwable): Nothing =
            throw f(message, code)

        fun raise(f: (Int) -> Throwable): Nothing =
            throw f(code)
    }
}

fun error(code: Int, raiser: ApiError.ErrorArgs.() -> Nothing): ApiError {
    raiserByCode[code] = raiser
    return ApiError(code, raiser)
}

@Serializable
data class ApiErrorDto(
    val message: String,
    val errorCode: Int,
    /**
     * Is needed to build localized messages on client.
     * */
    val errorArgs: List<String>? = null
)

open class ApiException(
    message: String? = null,
    val errorCode: Int,
    cause: Throwable? = null,
    val errorArgs: List<String>? = null,
) : Throwable(message, cause)

open class UserFaultException(
    message: String? = null,
    errorCode: Int,
    cause: Throwable? = null,
    errorArgs: List<String>? = null
): ApiException(
    message = message,
    errorCode = errorCode,
    cause = cause,
    errorArgs = errorArgs
)

class UnauthorizedException(
    message: String?,
    errorCode: Int,
    cause: Throwable? = null,
    errorArgs: List<String>? = null
): UserFaultException(
    message = message,
    errorCode = errorCode,
    cause = cause,
    errorArgs = errorArgs
)

open class AppFaultException(
    message: String? = null,
    errorCode: Int,
    cause: Throwable? = null,
    errorArgs: List<String>? = null,
): ApiException(
    message = message,
    errorCode = errorCode,
    cause = cause,
    errorArgs = errorArgs
)

class InternalServerException(
    message: String?,
    errorCode: Int,
    cause: Throwable? = null,
    errorArgs: List<String>? = null
): AppFaultException(
    message = message,
    errorCode = errorCode,
    cause = cause,
    errorArgs = errorArgs
)

fun ApiErrorDto.raise(): Nothing {
    val raiser = raiserByCode[errorCode]
    if (raiser != null) {
        raiser(ApiError.ErrorArgs(message, errorCode, null, errorArgs))
    } else {
        throw AppFaultException(message, errorCode, null, errorArgs)
    }
}

fun ApiException.toDto() : ApiErrorDto = ApiErrorDto(
    message = message ?: "",
    errorCode = errorCode,
    errorArgs = errorArgs
)

object CommonError {
    val BadRequest = error(400) { raise(::UserFaultException) }
    val Unauthorized = error(401) { raise(::UnauthorizedException) }
    val InternalServerError = error(500) { raise(::InternalServerException) }
}
