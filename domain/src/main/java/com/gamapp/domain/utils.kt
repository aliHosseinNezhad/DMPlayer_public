package com.gamapp.domain

import kotlin.random.Random

fun main() {
    val size = 100
    val index = -100
    println(index % size)
}

object Shuffle {
    private fun Array<Int>.hasMore(index: Int): Boolean {
        if (index !in indices) return false
        return index + 1 in indices
    }

    private fun Array<Int>.split(start: Int, end: Int, random: Int, index: Int): Int {
        require(random in start..end) { "$random is not between  [$start .. $end] " }
        when {
            start == end -> {
                return 0
            }
            start == random -> {
                this[index] = random + 1
                this[index + 1] = end
                return 2
            }
            end == random -> {
                this[index] = start
                this[index + 1] = random - 1
                return 2
            }
            else -> {
                this[index] = start
                this[index + 1] = random - 1
                this[index + 2] = random + 1
                this[index + 3] = end
                return 4
            }
        }
    }

    fun make(start: Int, end: Int, _index: Int): Array<Int> {
        var s = start
        var e = end
        var index = 0
        var added = 2
        val bounds = Array((e - s + 1) * 2) { 0 }
        val shuffles = Array(e - s + 1) { 0 }
        bounds[0] = s
        bounds[1] = e
        while (bounds.hasMore(index)) {
            s = bounds[index]
            e = bounds[index + 1]
            val random =
                if (index == 0) _index else Random(System.currentTimeMillis()).nextInt(s, e + 1)

            if (index == 0) {
                shuffles[_index] = _index
            } else {
                val j = index / 2
                val i = j - 1
                if (i in 0 until _index) {
                    shuffles[i] = random
                } else shuffles[j] = random
            }
            index += 2
            added += bounds.split(s, e, random, added)
        }
        println(bounds.toList())
        return shuffles
    }
}