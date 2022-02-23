package com.vkir.utils

import kotlin.random.Random

// todo implement proper uuid
// https://github.com/benasher44/uuid may help in the future
actual fun generateGuid(): String = Random.nextLong().toString()