package com.vkir.model

fun nowUtcMills(): Long = System.currentTimeMillis()

fun userFault(message: String): Nothing{
    throw IncorrectUserActionException(message)
}

open class UserFaultException(message: String):Throwable(message)

class IncorrectUserActionException(message: String): UserFaultException(message)