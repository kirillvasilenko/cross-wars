package model

import java.time.LocalDateTime
import java.time.ZoneId

fun userFault(message: String): Nothing{
    throw IncorrectUserActionException(message)
}

open class UserFaultException(message: String):Throwable(message)

class IncorrectUserActionException(message: String): UserFaultException(message)