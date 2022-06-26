package com.gamapp.dmplayer.presenter.ui.screen.player

import java.nio.*

fun FloatArray.toBuffer(): FloatBuffer {
    val buffer = ByteBuffer.allocateDirect(size * 4)
    val fb = buffer.order(ByteOrder.nativeOrder()).asFloatBuffer()
    fb.put(this)
    fb.position(0)
    return fb
}

fun ShortArray.toBuffer(): ShortBuffer {
    val buffer = ByteBuffer.allocateDirect(size * 2)
    val fb = buffer.order(ByteOrder.nativeOrder()).asShortBuffer()
    fb.put(this)
    fb.position(0)
    return fb
}

fun IntArray.toBuffer(): IntBuffer {
    val buffer = ByteBuffer.allocateDirect(size * 4)
    val fb = buffer.order(ByteOrder.nativeOrder()).asIntBuffer()
    fb.put(this)
    fb.position(0)
    return fb
}

fun ByteArray.toBuffer(): ByteBuffer {
    val buffer = ByteBuffer.allocateDirect(size * 1)
    val fb = buffer.order(ByteOrder.nativeOrder())
    fb.put(this)
    fb.position(0)
    return fb
}

fun LongArray.toBuffer(): LongBuffer {
    val buffer = ByteBuffer.allocateDirect(size * 8)
    val fb = buffer.order(ByteOrder.nativeOrder()).asLongBuffer()
    fb.put(this)
    fb.position(0)
    return fb
}

fun DoubleArray.toBuffer(): DoubleBuffer {
    val buffer = ByteBuffer.allocateDirect(size * 8)
    val fb = buffer.order(ByteOrder.nativeOrder()).asDoubleBuffer()
    fb.put(this)
    fb.position(0)
    return fb
}

fun CharArray.toBuffer(): CharBuffer {
    val buffer = ByteBuffer.allocateDirect(size * 2)
    val fb = buffer.order(ByteOrder.nativeOrder()).asCharBuffer()
    fb.put(this)
    fb.position(0)
    return fb
}