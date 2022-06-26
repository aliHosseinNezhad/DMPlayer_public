package com.gamapp.dmplayer.framework.utils


@PublishedApi
internal fun rangeCheck(size: Int, fromIndex: Int, toIndex: Int) {
    when {
        fromIndex > toIndex -> throw IllegalArgumentException("fromIndex ($fromIndex) is greater than toIndex ($toIndex).")
        fromIndex < 0 -> throw IndexOutOfBoundsException("fromIndex ($fromIndex) is less than zero.")
        toIndex > size -> throw IndexOutOfBoundsException("toIndex ($toIndex) is greater than size ($size).")
    }
}


/**
 * Searches this list or its range for an element for which the given [comparison] function by value1:elements of list and value2: [target] value returns zero using the binary search algorithm.
 *
 * The list is expected to be sorted so that the signs of the [comparison] function's return values ascend on the list elements,
 * i.e. negative values come before zero and zeroes come before positive values.
 * Otherwise, the result is undefined.
 *
 * @param [target] that should be founded
 * @param [getKey] used to get key of element
 * @param [comparison]  function that returns zero when value1 is equal to value2 .
 * when value1 coming before the value2 ,the function must return negative values .
 * when value1 coming after the value2, the function must return positive values.
 *
 * @return the index of the found element, if it is contained in the list within the specified range; otherwise,return null
 * */
inline fun <K, T> List<T>.binarySearch(
    target: K,
    fromIndex: Int = 0,
    toIndex: Int = size,
    getKey: (T) -> K,
    comparison: (value1: K, value2: K) -> Int
): Int? {
    rangeCheck(size, fromIndex, toIndex)
    var low = fromIndex
    var high = toIndex - 1
    var mid: Int
    check(target = target, low = low, high = high, selector = getKey, comparison = comparison) {
        return it
    }
    while (low <= high) {
        mid = (low + high).ushr(1)
        val midKey = getKey(get(mid))
        val cmp = comparison(midKey, target)
        if (cmp > 0) high = mid - 1
        else if (cmp < 0) low = mid + 1
        else return mid
    }
    return null
}

/**
 * to check whether [target] value is in bound of list
 * */
@PublishedApi
internal inline fun <T, K> List<T>.check(
    target: K,
    low: Int,
    high: Int,
    selector: (T) -> K,
    comparison: (K, K) -> Int,
    out: (Int?) -> Unit
) {
    val lowKey = selector(get(low))
    val lowCmp = comparison(target, lowKey)
    if (lowCmp < 0) out(null)
    if (lowCmp == 0) out(low)

    val highKey = selector(get(high))
    val highCmp = comparison(target, highKey)
    if (highCmp > 0) out(null)
    if (highCmp == 0) out(high)
}

/**
 * Search for index of [targets] elements in List<T>,
 * [targets] and List<T> expected to be sorted ascend by the way [comparison] function compare elements
 *
 * @param [getKey] used to get key of element
 * @param [comparison]  function that returns zero when value1 is equal to value2 .
 * when value1 coming before the value2 ,the function must return negative values .
 *
 * @return the indices of the found elements, if it is contained in the list within the specified range; otherwise,return null
 * */
fun <T, K> List<T>.binarySearch(
    targets: List<K>,
    fromIndex: Int = 0,
    toIndex: Int = size,
    getKey: (T) -> K,
    comparison: (K, K) -> Int
): List<Int?> {
    val output = arrayOfNulls<Int>(targets.size)

    var from = fromIndex
    var to = toIndex

    var start = 0
    var end = targets.lastIndex

    fun search(target: K): Int? {
        return binarySearch(
            target = target,
            fromIndex = from,
            toIndex = to,
            getKey = getKey,
            comparison = comparison
        )
    }

    while (start <= end) {
        if (start == end) {
            output[start] = search(targets[start])
            break
        }
        val s1 = search(targets[start])
        output[start] = s1
        from = s1 ?: from

        val s2 = search(targets[end])
        output[end] = s2
        to = if (s2 != null) s2 + 1 else to

        start++
        end--
    }
    return output.toList()
}