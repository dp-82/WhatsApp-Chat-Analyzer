package com.example.chatanalyzer

class ChatFileData {
    companion object{
        var chatObj:WhatsAppChatData = WhatsAppChatData(mutableListOf())
        var mostUsedThings:HashMap<String,Pair<String,Int>> = hashMapOf()
        val spinnerUsersList:MutableList<String> = mutableListOf()
    }
}