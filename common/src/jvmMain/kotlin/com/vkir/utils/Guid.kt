package com.vkir.utils

import java.util.*

actual fun generateGuid(): String = UUID.randomUUID().toString()