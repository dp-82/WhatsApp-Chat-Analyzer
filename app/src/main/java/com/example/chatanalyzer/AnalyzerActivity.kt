package com.example.chatanalyzer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_analyzer.*

class AnalyzerActivity : AppCompatActivity() {

    private lateinit var homeFragment:HomeFragment
    private lateinit var analyzeFragment: AnalyzeFragment
    private lateinit var graphsFragment: GraphsFragment
    private var chatData = mutableListOf<String>()
    private val allUsers = "##**allUsers**##"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyzer)

        Log.d("AnalyzerActivity","started")

        ChatFileData.chatObj.exploreChat()

        ChatFileData.mostUsedThings=ChatFileData.chatObj.mostUsedThings()

        val usersSet: MutableSet<String> = ChatFileData.chatObj.userDataMap.keys
        for(it in usersSet){
            if(it!=allUsers && it[0]!='+'){
                ChatFileData.spinnerUsersList.add(it)
            }
        }
        ChatFileData.spinnerUsersList.sort()
        ChatFileData.spinnerUsersList.add(0,"Everybody")
        for(it in usersSet){
            if(it!=allUsers && it[0]=='+'){
                ChatFileData.spinnerUsersList.add(it)
            }
        }

        homeFragment= HomeFragment(ChatFileData.chatObj.userDataMap,ChatFileData.mostUsedThings)
        analyzeFragment= AnalyzeFragment(ChatFileData.chatObj.userDataMap,ChatFileData.spinnerUsersList)
        graphsFragment= GraphsFragment(ChatFileData.chatObj.userDataMap,ChatFileData.spinnerUsersList)

        Log.d("Hey","after")

        bottomNavView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bnvHome -> changeFragment(homeFragment)
                R.id.bnvAnalyze -> changeFragment(analyzeFragment)
                R.id.bnvGrapghs -> changeFragment(graphsFragment)
                else -> changeFragment(homeFragment)
            }
            true
        }
        bottomNavView.selectedItemId=R.id.bnvHome

        Log.d("AnalyzerActivity","ended")
    }


    private fun changeFragment(fragment:Fragment){
        Log.d("AnalyzerActivity","changeFragment started")
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout,fragment)
            commit()
        }
        Log.d("AnalyzerActivity","changeFragment ended")
    }

    private fun showToast(toastMsg: String) {
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show()
    }

}