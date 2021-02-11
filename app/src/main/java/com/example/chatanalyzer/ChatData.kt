package com.example.chatanalyzer

data class ChatData(
    val userDataMap:HashMap<String,Int> = hashMapOf(),
    val hourArray:IntArray= IntArray(24),
    val weekDayArray:IntArray = IntArray(7),
    val monthArray:IntArray = IntArray(12),
    val yearArray:IntArray = IntArray(30),
    val wordsDataMap:HashMap<String,Int> = hashMapOf(),
    var mostUsedWord:String="",
    var longestMessage:String="",
    var sentWords:Int=0,
    var sentMessages:Int=0,
    var sentPhotosVideosAudios:Int=0,
    var sentLinks:Int=0,
    var daysChatted:Int=0,
    var earlyMorningMessages: Int =0,
    var lateNightMessages: Int =0,
    var initiatedMessagesCount:Int=0
)