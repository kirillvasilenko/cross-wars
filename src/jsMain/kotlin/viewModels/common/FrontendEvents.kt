package viewModels.common

import model.UserDto

open class FrontendEvent(val source: ViewModel){
    var handled: Boolean = false
}


open class ErrorHappened(source: ViewModel, val cause: Throwable): FrontendEvent(source)

class Unauthorized(source: ViewModel, cause: Throwable): ErrorHappened(source, cause)


class UserLoggedIn(source: ViewModel, val user: UserDto): FrontendEvent(source)

class UserLoggedOut(source: ViewModel): FrontendEvent(source)
