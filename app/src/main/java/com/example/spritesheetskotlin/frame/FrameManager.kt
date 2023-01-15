package com.example.spritesheetskotlin.frame

class FrameManager(private val frameList: ArrayList<Frame>) {
    constructor() : this(ArrayList<Frame>())

    fun getFrameByIndex(index: Int) : Frame{
        val size = this.frameList.size
        return this.frameList[index % size]
    }

    fun addFrame(frame: Frame) {
        this.frameList.add(frame)
    }
}