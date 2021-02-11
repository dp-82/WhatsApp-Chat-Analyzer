package com.example.chatanalyzer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment(private var userDataMap:HashMap<String,ChatData>,private val mostUsedThings:HashMap<String,Pair<String,Int>>) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val allUsers = "##**allUsers**##"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val totalSentMessages:Int=userDataMap[allUsers]!!.sentMessages
        val totalDays:Int=userDataMap[allUsers]!!.daysChatted
        val startingYear:String=getStartingYear().toString()

        view?.tvSummary!!.text="You have been chatting since $startingYear with an average of ${totalSentMessages/totalDays} messages per day!"
        view?.tvMessages.text=totalSentMessages.toString()
        view?.tvUsers.text=(userDataMap[allUsers]!!.userDataMap.size-1).toString()
        view?.tvDays.text=mostUsedThings["ActualDays"]!!.second.toString()
        view?.tvMostTalkActivePerson.text=mostUsedThings["MostTalkActive"]!!.first
        view?.tvMostTalkActivePersonPercentage.text="~${mostUsedThings["MostTalkActive"]!!.second.toString()}% of total messages"
        view?.tvInfluencer.text=mostUsedThings["Influencer"]!!.first
        view?.tvInfluencerPersonPercentage.text="~${mostUsedThings["Influencer"]!!.second}% of total photos,videos and audio messages"
        view?.tvLinker.text=mostUsedThings["Linker"]!!.first
        view?.tvLinkerPersonPercentage.text="~${mostUsedThings["Linker"]!!.second}% of total links"
        view?.tvEarlyBird.text=mostUsedThings["EarlyBird"]!!.first
        view?.tvEarlyBirdPersonPercentage.text="Most messages sent in the interval 5AM-12PM(${mostUsedThings["Linker"]!!.second}%)"
        view?.tvNightOwl.text=mostUsedThings["NightOwl"]!!.first
        view?.tvNightOwlPercentage.text="Most messages sent in the interval 9PM-4AM(${mostUsedThings["NightOwl"]!!.second}%)"
        view?.tvInitiator.text=mostUsedThings["Initiator"]!!.first
        view?.tvInitiatorPercentage.text="Their messages started ~${mostUsedThings["Initiator"]!!.second}% of the conversations"
        view?.tvMostConsecutiveDaysChatted.text=mostUsedThings["MostConsecutiveDaysChatted"]!!.second.toString()
        view?.tvMostConsecutiveDaysChattedDates.text=mostUsedThings["MostConsecutiveDaysChatted"]!!.first
        return view
    }

    private fun getStartingYear():Int{
        for (i in userDataMap[allUsers]!!.yearArray.indices){
            if(userDataMap[allUsers]!!.yearArray[i]>0){
                return 2000+i
            }
        }
        return 2000
    }
}