package com.example.chatanalyzer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_analyze.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AnalyzeFragment(private var userDataMap:HashMap<String,ChatData>,private val spinnerUsersList:MutableList<String>) : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_analyze, container, false)

        val spinner: Spinner =view?.spinnerAnalytics!!
        val adapter= ArrayAdapter<String>(container?.context!!,R.layout.support_simple_spinner_dropdown_item,spinnerUsersList.toTypedArray())
        spinner.adapter=adapter
        updateSpinner(spinner,container,view)
        return view
    }

    private fun updateLayoutForSpecificPerson(name:String, view:View){
        var nameOfKey=name
        if (nameOfKey=="Everybody")nameOfKey=allUsers

        view?.tvMostUsedWord.text= userDataMap[nameOfKey]!!.mostUsedWord.capitalize()
        view?.tvLongestMessage.text= userDataMap[nameOfKey]!!.longestMessage.capitalize()
        view?.tvSentWords.text= userDataMap[nameOfKey]!!.sentWords.toString()
        view?.tvSentMessages.text= userDataMap[nameOfKey]!!.sentMessages.toString()
        view?.tvSentPhotoVideoVoiceMessages.text= userDataMap[nameOfKey]!!.sentPhotosVideosAudios.toString()
        view?.tvSentLinks.text= userDataMap[nameOfKey]!!.sentLinks.toString()
        view?.tvEarlyMorningMessages.text= userDataMap[nameOfKey]!!.earlyMorningMessages.toString()
        view?.tvLateNightMessages.text= userDataMap[nameOfKey]!!.lateNightMessages.toString()
        view?.tvChattedDays.text= userDataMap[nameOfKey]!!.daysChatted.toString()
    }

    private fun updateSpinner(spinner: Spinner,container: ViewGroup?,viewTop: View){

        spinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateLayoutForSpecificPerson(parent?.getItemAtPosition(position).toString(),viewTop)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showToast("Nothing Selected",container)
            }
        }
    }

    private fun showToast(toastMsg: String,container: ViewGroup?) {
        Toast.makeText(container?.context!!, toastMsg, Toast.LENGTH_SHORT).show()
    }
}