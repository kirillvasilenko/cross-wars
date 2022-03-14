package com.vkir.svc

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class User {

}

class AuthSvc {

    val user = MutableStateFlow<User?>(null)

    val authenticated = MutableStateFlow<Boolean>(false)
}