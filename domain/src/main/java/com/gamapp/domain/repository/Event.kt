package com.gamapp.domain.repository

sealed class Event<T>(val data: T?, val message: String?, val success: Boolean) {
    class Success<T>(data: T, message: String? = null) :
        Event<T>(data = data, message = message, success = true)

    class Failure<T>(message: String?, data: T? = null) :
        Event<T>(data = data, message = message, success = false)
}