package com.gamapp.data.utils

import java.lang.Exception
import kotlin.random.Random
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class CompareResult<T>(
    val equals: List<Pair<T, T>>,
    val firstDiff: List<T>,
    val secondDiff: List<T>,
) {
    override fun toString(): String {
        return "$equals,$firstDiff,$secondDiff"
    }
}

inline fun <T> Iterator<T>.next(scope: () -> Unit): T? {
    return if (this.hasNext()) {
        this.next()
    } else {
        scope()
        null
    }
}

fun <T> getComparedList(
    first: List<T>,
    second: List<T>,
    read: T.() -> Long,
): CompareResult<T> {
    val equal = mutableListOf<Pair<T, T>>()
    val extraOne = mutableListOf<T>()
    val extraTwo = mutableListOf<T>()
    fun result(): CompareResult<T> {
        return CompareResult(
            equal,
            extraOne,
            extraTwo
        )
    }

    val it1 = first.iterator()
    val it2 = second.iterator()
    var one = it1.next {
        extraTwo += second
        return result()
    }!!
    var two = it2.next {
        extraOne += first
        return result()
    }!!

    fun onEqual(): CompareResult<T>? {
        one = it1.next {
            while (it2.hasNext()) {
                extraTwo += it2.next()
            }
            return result()
        }!!
        two = it2.next {
            extraOne += one
            while (it1.hasNext()) {
                extraOne += it1.next()
            }
            return result()
        }!!
        return null
    }
    do {
        if (one.read() == two.read()) {
            equal += one to two
            val result = onEqual()
            if (result != null)
                return result
        }
        while (one.read() < two.read()) {
            extraOne += one
            one = it1.next {
                return result()
            }!!
        }
        if (one.read() == two.read()) {
            equal += one to two
            val result = onEqual()
            if (result != null)
                return result
        }
        while (two.read() < one.read()) {
            extraTwo += two
            two = it2.next {
                return result()
            }!!
        }
    } while (true)
}

fun main() {
    val first = listOf("1", "3", "4", "5", "7", "9", "12", "23", "33", "40", "41", "42", "44", "56", "122").sortedBy { it.toInt() }
    val second = listOf("1", "2", "3", "12", "32", "33", "43", "45", "47", "48", "56", "121").sortedBy { it.toInt() }
    measureTimeMillis {
        getComparedList(first, second) {
            this.toLong()
        }.let {
            println(it)
        }
    }.let {
        println(it)
    }
    measureTimeMillis {
        first - second
    }.let {
        println(it)
    }
}

inline fun <T> List<T>.filterOrNull(selector: T.() -> Long, value: Long): T? {
    try {
        require(!indices.isEmpty())
    } catch (e: Exception) {
        return null
    }
    val range = indices
    var min = range.first
    var max = range.last
    var i: Int
    var v: Long
    do {
        i = (min + max) / 2
        v = this[i].selector()
        when {
            value > v -> {
                min = i
            }
            value < v -> {
                max = i
            }
        }
        when {
            this[max] == value -> return this[max]
            this[min] == value -> return this[min]
            v == value -> return this[i]
            max == min -> return null
            else -> Unit
        }
    } while (v != value)
    return null
}




