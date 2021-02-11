package com.example.chatanalyzer

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {
    private var chatData = mutableListOf<String>()
    private val allUsers = "##**allUsers**##"
    private var lst1 = mutableListOf<Int>()
    private var lst2 = mutableListOf<Int>()
    private var lst3 = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        tvLoading.visibility=View.INVISIBLE

        Log.d("InfoActivity","started")

        btnAnalyze.setOnClickListener {
            analyzeChats()
        }

        Log.d("InfoActivity","ended")
    }

    private fun analyzeChats(){
        btnAnalyze.visibility= View.INVISIBLE
        progressBar.visibility=View.VISIBLE
        tvLoading.visibility=View.VISIBLE

        val clipData: ClipData?=intent.clipData
        if (clipData!=null){
            var chat=""
            for(ind in (0 until clipData.itemCount)){
                val uri: Uri =clipData.getItemAt(ind).uri
                val inputStream=contentResolver.openInputStream(uri)
                val byteArray=ByteArray(DEFAULT_BUFFER_SIZE)
                var ptr=inputStream!!.read(byteArray)
                while (ptr!=-1){
                    val str = byteArray.toString(charset("UTF-8"))
                    chat+=str
                    ptr=inputStream!!.read(byteArray)
                    if(chat.length>=1024){
                        for (item in chat.split('\n')){
                            chatData.add(item)
                        }
                        chat=""
                    }
                }
            }
            if (chat!=""){
                for (item in chat.split('\n')){
                    chatData.add(item)
                }
            }

            reformatChat()

            for (item in lst1)Log.d("pp","first - $item")
            for (item in lst2)Log.d("pp","second - $item")
            for (item in lst3)Log.d("pp","third - $item")

            ChatFileData.chatObj=WhatsAppChatData(chatData)

            Intent(this,AnalyzerActivity::class.java).also {
                startActivity(it)
            }
        }else
            Toast.makeText(this,"Some error occured",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun convertTo24HoursFormat(hour:String,minutes:String,format:String):Pair<String,String>{
        var h=hour;var m=minutes
        if (format=="am"){
            if(h=="12")h="00"
        }else{
            if(h!="12"){
                h = (h.toInt()+12).toString()
            }
        }
        return h to m
    }

    private fun isStringUnderOneOfChatFormats(data:String):Boolean{
        var ind = data.indexOf(',')
        if(ind==-1)return false
        val part = data.substring(0,ind).trim()
        var lst = part.split('/')
        if (lst.size!=3)return false
        for (item in lst){
            try {
                item.toInt()
            }catch (e:Exception){
                return false
            }
        }
        val validList = mutableListOf(lst[0],lst[1],lst[2])
        val ind2 = data.indexOf('-')
        if(ind==-1 || ind2<=ind)return false
        val time = data.substring(ind+1,ind2).trim()
        if (!time.contains(':'))return false
        lst = time.split(':')
        if (lst.size!=2)return false
        try {
            lst[0].toInt()
        }catch (e:Exception){
            return false
        }
        var pair = "0" to "0"
        if (lst[1].contains(' ')){
            val tt=lst[0]
            lst = lst[1].split(' ')
            try {
                lst[0].toInt()
            }catch (e:Exception){
                return false
            }
            if(lst[1]!="am" && lst[1]!="pm")return false
            pair = convertTo24HoursFormat(tt,lst[0],lst[1])

        }else{
            try {
                lst[1].toInt()
            }catch (e:Exception){
                return false
            }
            pair = lst[0] to lst[1]
        }
        if (!lst1.contains(validList[0].toInt()))lst1.add(validList[0].toInt())
        if (!lst2.contains(validList[1].toInt()))lst2.add(validList[1].toInt())
        if (!lst3.contains(validList[2].toInt()))lst3.add(validList[2].toInt())
        validList.add(pair.first)
        validList.add(pair.second)
        return true
    }

    private fun reformatChat(){
        Log.d("AnalyzerActivity","reformatChat started")
        var chat= mutableListOf<String>()
        //TODO should check pattern correctly
        val pattern="^[\\d]+/[\\d]+/[\\d]+, [\\d]+:[\\d]+ "
        val regex=Regex(pattern)
        for(i in chatData.indices){
            val line=chatData[i]
            if(isStringUnderOneOfChatFormats(line)){
                chat.add(line)
            }else{
                if(chat.isNotEmpty()){
                    chat[chat.size-1]+=line
                }
            }
        }
        chatData.clear()
        for(i in chat.indices)chatData.add(chat[i])
        Log.d("AnalyzerActivity","reformatChat ended")
    }

}