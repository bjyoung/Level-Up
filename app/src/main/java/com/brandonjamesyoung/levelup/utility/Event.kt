package com.brandonjamesyoung.levelup.utility

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    // Returns content as long as it is new
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}