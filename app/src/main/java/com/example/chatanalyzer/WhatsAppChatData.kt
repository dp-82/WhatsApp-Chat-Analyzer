package com.example.chatanalyzer

import android.util.Log
import java.util.*

class WhatsAppChatData(private val chat: MutableList<String>){

    var userDataMap:HashMap<String, ChatData> = hashMapOf()
    private val allUsers = "##**allUsers**##"
    private val listOfDates:MutableList<Pair<Date,String>> = mutableListOf()

    //TODO : check weekDay function
    private fun getWeekDay(day: Int, month: Int, year: Int):Int{
        val date=Date(year, month, day)
        val cal:Calendar= Calendar.getInstance()
        cal.time=date
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    //TODO : Check double counting
    fun exploreChat(){
        userDataMap[allUsers] = ChatData()
        val user=allUsers
        var prev=-1

        for (totalMessage in chat){
            val dataList=splitChatData(totalMessage)
            if (dataList.isEmpty())continue
            val userName=dataList[0]; val userMessage=dataList[1]; val minutes=dataList[2];val hour=dataList[3]; val date=dataList[4];val weekDay=dataList[5]; val month=dataList[6];val year=dataList[7]

            if (!userDataMap.containsKey(userName)) userDataMap[userName]= ChatData()

            updateMaps(allUsers, userName, userMessage, minutes, hour, weekDay, month, year)
            updateMaps(userName, userName, userMessage, minutes, hour, weekDay, month, year)

            if(date.toInt()!=prev){
                listOfDates.add(Date(year.toInt(), month.toInt(), date.toInt()) to "$date/$month/$year")
                prev=date.toInt()
                userDataMap[allUsers]!!.daysChatted++
                userDataMap[userName]!!.daysChatted++
                userDataMap[userName]!!.initiatedMessagesCount++
                userDataMap[allUsers]!!.initiatedMessagesCount++
            }
        }
        for ((k,v) in userDataMap)updateDaysAndWords(k)
    }

    //ToDo check unnecessary data in userDataMap
    //ToDo check getOrElse block error

    private fun countOfCharacters(str: String, char: Char):Int{
        var cnt:Int=0
        for (i in str.indices){
            if(str[i]==char){
                ++cnt
            }
        }
        return cnt
    }

    private fun updateDaysAndWords(user: String){
        var mx=0
        for(it in userDataMap[user]!!.wordsDataMap){
            if(it.value > mx){
                mx=it.value
            }
        }
        for(it in userDataMap[user]!!.wordsDataMap){
            if(it.value == mx){
                userDataMap[user]!!.mostUsedWord=it.key
            }
        }
    }

    private fun calcPercentage(part: Int, total: Int):Int{
        return ((part.toFloat()/total.toFloat())*100).toInt()
    }

    private fun updateMaps(
        user: String,
        userName: String,
        userMessage: String,
        minutes: String,
        hour: String,
        date: String,
        month: String,
        year: String
    ){
        val weekDay = getWeekDay(date.toInt(), month.toInt(), year.toInt())
        if (hour.toInt()>=24 || hour.toInt()<0 || weekDay<1 || weekDay>7 || month.toInt()<1 || month.toInt()>12 || year.toInt()<2000 || year.toInt()>=2030)
            Log.d("Error","Array Index Out of bounds caused in WhatsAppChatData UpdatesMaps method")
        userDataMap[user]!!.userDataMap[userName] = (userDataMap[user]!!.userDataMap?.get(user)?:0)+1
        userDataMap[user]!!.hourArray[hour.toInt()]++
        userDataMap[user]!!.weekDayArray[weekDay - 1]++
        userDataMap[user]!!.monthArray[month.toInt() - 1]++
        userDataMap[user]!!.yearArray[year.toInt() % 2000]++

        val allWords = userMessage.split("\\s+".toRegex()).map { word ->
            word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
        }

        for (word in allWords){
            userDataMap[user]!!.wordsDataMap[word.toLowerCase()] = (userDataMap[user]!!.wordsDataMap?.get(word.toLowerCase()) ?: 0)+1
        }
        userDataMap[user]!!.sentWords+=allWords.size

        if(userMessage.length > userDataMap[user]!!.longestMessage.length){
            userDataMap[user]!!.longestMessage=userMessage
        }
        userDataMap[user]!!.sentMessages++

        if(userMessage.trim()=="<Media omitted>"){
            userDataMap[user]!!.sentPhotosVideosAudios++
        }

        if(userMessage.length>8 && userMessage.substring(0, 8)=="https://"){
            userDataMap[user]!!.sentLinks++
        }

        if(hour.toInt() in 5..11){
            userDataMap[user]!!.earlyMorningMessages++
        }

        if (hour.toInt() in 21..23 || hour.toInt() in 0..4){
            userDataMap[user]!!.lateNightMessages++
        }
    }

    private fun getCorrectTime(completeTime:String):Pair<String,String>{
        val n = completeTime.length
        val ind=completeTime.indexOf(':')
        var hour=completeTime.substring(0, ind).trim().toInt().toString()
        val minutes=completeTime.substring(ind + 1).trim().toInt().toString()
        if (completeTime.last()=='m'){
            var add =0
            if (completeTime[n-2]=='a' && hour=="12"){
                hour="0"
            }else if (completeTime[n-2]=='p' && hour!="12"){
                hour = (hour.toInt()+12).toString()
            }
        }
        return hour to minutes
    }

    private fun getCorrectDate(completeDate:String){
        val lst=completeDate.split('/')
        var a=lst[1].toInt()
        val b=lst[0].toInt()
        val c=lst[2].toInt()
    }

    private fun splitChatData(totalMessage: String):List<String>{
        //TODO should check different chat patterns
        //4/26/19, 08:15 - DP CSE: Hey! How are You
        // Each message is of the form dd/dd/dd, dd:dd - User : Message
        var ind=totalMessage.indexOf('-')
        if(ind==-1 || ind+2>=totalMessage.length)return listOf()
        val firstPart=totalMessage.substring(0, ind).trim() // 4/26/19, 08:15
        val secondPart=totalMessage.substring(ind + 1).trim() // User: Message
        ind=secondPart.indexOf(':')
        if(ind==-1)return listOf()
        val userName=secondPart.substring(0, ind).trim()
        val userMessage=secondPart.substring(ind + 1).trim()
        //TODO should check peoperly cases where name is changed,deleted
        if("changed" in userMessage || "deleted" in userMessage || "changed" in userName || "deleted" in userName)return listOf()
        //TODO if user name is like +9BEGIN
        if('+' in userName && userName.count {
                it.toLowerCase() in 'a'..'z'
            }>0){
            return listOf()
        }
        ind=firstPart.indexOf(',')
        if(ind==-1)return listOf()
        val completeDate=firstPart.substring(0, ind).trim() // 4/26/19
        val completeTime=firstPart.substring(ind + 1).trim() //08:15
        if(countOfCharacters(completeDate, '/')!=2 || countOfCharacters(
                completeTime,
                ':'
            )!=1){
            return listOf()
        }
        val lst=completeDate.split('/')
        var date=lst[1].toInt().toString()
        val month=lst[0].toInt().toString()
        val year=(2000+lst[2].toInt()).toString()
        val validTime = getCorrectTime(completeTime)
        val hour=validTime.first
        val minutes=validTime.second
        val weekDay=getWeekDay(date.toInt(), month.toInt(), year.toInt()).toString()
        return listOf(userName, userMessage, minutes, hour, date, weekDay, month, year)
    }

    fun mostUsedThings():HashMap<String, Pair<String, Int>>{
         var activePersonMsgCount:Int=-1;var influencerMsgCount:Int=-1;var linkerCount=-1;var earlyMasgCnt=-1;var lateMagCnt=-1;var initiatorCnt=-1
         var activePerson:String=""; var influencer:String="" ; var linker:String="" ; var earlyPerson="" ; var latePeron="" ; var initiator=""
         for ((user, userChatDataObj) in userDataMap){
             if(user!=allUsers){
                 if(userChatDataObj.sentMessages > activePersonMsgCount){
                     activePersonMsgCount=userChatDataObj.sentMessages
                     activePerson=user
                 }
                 if(userChatDataObj.sentPhotosVideosAudios > influencerMsgCount){
                     influencerMsgCount=userChatDataObj.sentPhotosVideosAudios
                     influencer=user
                 }
                 if(userChatDataObj.sentLinks > linkerCount){
                     linkerCount=userChatDataObj.sentLinks
                     linker=user
                 }
                 if(userChatDataObj.earlyMorningMessages>earlyMasgCnt){
                     earlyMasgCnt=userChatDataObj.earlyMorningMessages
                     earlyPerson=user
                 }
                 if(userChatDataObj.lateNightMessages>lateMagCnt){
                     lateMagCnt=userChatDataObj.lateNightMessages
                     latePeron=user
                 }
                 if(userChatDataObj.initiatedMessagesCount>initiatorCnt){
                     initiatorCnt=userChatDataObj.initiatedMessagesCount
                     initiator=user
                 }
             }
         }
        val map:HashMap<String, Pair<String, Int>> = hashMapOf()
        map["MostTalkActive"]= Pair(
            activePerson, calcPercentage(
                activePersonMsgCount,
                userDataMap[allUsers]!!.sentMessages
            )
        )
        map["Influencer"]= Pair(
            influencer, calcPercentage(
                influencerMsgCount,
                userDataMap[allUsers]!!.sentPhotosVideosAudios
            )
        )
        map["Linker"]= Pair(linker, calcPercentage(linkerCount, userDataMap[allUsers]!!.sentLinks))
        map["EarlyBird"]= Pair(
            earlyPerson, calcPercentage(
                earlyMasgCnt,
                userDataMap[allUsers]!!.earlyMorningMessages
            )
        )
        map["NightOwl"]= Pair(
            latePeron, calcPercentage(
                lateMagCnt,
                userDataMap[allUsers]!!.lateNightMessages
            )
        )
        map["Initiator"]=Pair(
            initiator, calcPercentage(
                initiatorCnt,
                userDataMap[allUsers]!!.initiatedMessagesCount
            )
        )
        map["ActualDays"]="" to getDiff(listOfDates[0].first,listOfDates.last().first)
        map["MostConsecutiveDaysChatted"]=findMostConsecutiveDaysChatted()
        return map
    }

    private fun findMostConsecutiveDaysChatted():Pair<String, Int>{
        var mostConsecutiveDaysChatted=1
        var cnt=1
        var startDate =listOfDates[0]
        var endDate = listOfDates[0]
        var curStartDate=startDate
        listOfDates.add(Date(2100, 1, 1) to "1/1/2100")
        for (i in 1 until listOfDates.size){
            if (getDiff(listOfDates[i-1].first,listOfDates[i].first)==1){
                ++cnt
            }else{
                if (cnt>mostConsecutiveDaysChatted){
                    mostConsecutiveDaysChatted=cnt
                    startDate=curStartDate
                    endDate=listOfDates[i - 1]
                }
                cnt=1
                curStartDate=listOfDates[i]
            }
        }
        val datesChatted="from ${startDate.second} to ${endDate.second}"
        return datesChatted to mostConsecutiveDaysChatted
    }

    private fun getDiff(from : Date,to:Date):Int{
        return ((to.time-from.time)/(1000*60*60*24)).toInt()
    }
}