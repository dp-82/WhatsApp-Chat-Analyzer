package com.example.chatanalyzer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_graphs.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GraphsFragment(private var userDataMap:HashMap<String,ChatData>,private val spinnerUsersList:MutableList<String>) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val allUsers = "##**allUsers**##"
    private val monthLabels = mutableListOf("Jan","Feb","Mar","Aprl","May","June","July","Aug","Sep","Oct","Nov","Dec")
    private val weekDayLabels = mutableListOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
    private val hourLabels = mutableListOf("12am","1am","2am","3am","4am","5am","6am","7am","8am","9am","10am","11am","12pm","1pm","2pm","3pm","4pm","5pm","6pm","7pm","8pm","9pm","10pm","11pm")

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
        Log.d("GraphsFragment","onCreateView Started")
        var view = inflater.inflate(R.layout.fragment_graphs, container, false)

        val spinner: Spinner =view?.spinnerGraphs!!
        val adapter = ArrayAdapter<String>(
            container?.context!!,
            R.layout.support_simple_spinner_dropdown_item,
            spinnerUsersList.toTypedArray()
        )
        spinner.adapter = adapter

        updateSpinner(spinner,container,view)

        Log.d("GraphsFragment","onCreateView ended")
        return view
    }

    private fun updateLayoutForSpecificPerson(name:String,view:View,container: ViewGroup?){
        Log.d("GraphsFragment","updateLayoutForSpecificPerson Started")
        var Name=name
        if (Name=="Everybody")Name=allUsers

        updatePieChart(view?.pieEachUser,userDataMap[Name]!!.userDataMap)
        updateLineChart(view?.lcEachHour,userDataMap[Name]!!.hourArray)
        updateData(view?.bcEachDay,userDataMap[Name]!!.weekDayArray)
        updateLineChart(view?.lcEachMonth, userDataMap[Name]!!.monthArray)
        updateData(view?.bcEachYear, userDataMap[Name]!!.yearArray)
        Log.d("GraphsFragment","updateLayoutForSpecificPerson Ended")
    }

    private fun updateSpinner(spinner: Spinner,container: ViewGroup?,viewTop:View) {
        Log.d("GraphsFragment","updateSpinner Started")
        spinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateLayoutForSpecificPerson(parent?.getItemAtPosition(position).toString(),viewTop,container)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showToast("Nothing",container)
            }
        }
        Log.d("GraphsFragment","updateSpinner Ended")
    }

    private fun updateData(barChart: BarChart,intArray: IntArray) {
        Log.d("GraphsFragment","updateData Started")
        var labels = mutableListOf<String>()
        val entries= mutableListOf<BarEntry>()
        if(intArray.size==30){
            labels.clear()
            var startInd=0; var endInd=0
            for (i in 1..30){
                if (intArray[i-1]>0){
                    if(startInd==0)startInd=i-1
                    endInd=i-1
                }
            }
            for (i in startInd..endInd){
                labels.add((2000+i).toString())
                entries.add(BarEntry((i-startInd).toFloat(),intArray[i].toFloat()))
            }
        }else if(intArray.size==7){
            labels=weekDayLabels
            for(i in intArray.indices){
                entries.add(BarEntry(i.toFloat(),intArray[i].toFloat()))
            }
        }
        val barDataSet= BarDataSet(entries,"Number of messages")
        barDataSet.setDrawValues(false)
        val barData= BarData(barDataSet)

        barChart.apply {
            data=barData
            animateY(1000)
            xAxis.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            xAxis.position= XAxis.XAxisPosition.BOTTOM
            description.isEnabled=false
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            axisRight.isEnabled=false
            xAxis.valueFormatter=IndexAxisValueFormatter(labels)
        }
        Log.d("GraphsFragment","upData ended")
    }

    private fun updateLineChart(lineChart: LineChart,intArray: IntArray){
        Log.d("GraphsFragment","updateLineChart Started")
        var labels= mutableListOf<String>()
        if (intArray.size==12){
            labels=monthLabels
        }else if (intArray.size==24){
            labels=hourLabels
        }
        val entries= mutableListOf<Entry>()
        for(i in intArray.indices){
            entries.add(Entry(i.toFloat(),intArray[i].toFloat()))
        }
        val lineDataSet= LineDataSet(entries,"Number of messages")
        lineDataSet.apply {
            setDrawFilled(true)
            setDrawValues(false)
            mode=LineDataSet.Mode.CUBIC_BEZIER
        }
        val lineData= LineData(lineDataSet)

        lineChart.apply {
            data=lineData
            animateY(1000)
            xAxis.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled=false
            xAxis.position= XAxis.XAxisPosition.BOTTOM
            description.isEnabled=false
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            xAxis.valueFormatter=IndexAxisValueFormatter(labels)

        }
        Log.d("GraphsFragment","updateLineChart Ended")
    }

    private fun updatePieChart(pieChart:PieChart,map:HashMap<String,Int>){
        Log.d("GraphsFragment","updatePieChart Started")
        val pieUsersCount= minOf(5,map.size)
        val labels= mutableListOf<String>()
        val points= mutableListOf<Int>()

        for ((key,value) in map){
            if (points.size<pieUsersCount){
                points.add(value)
                labels.add(key)
            }else{
                if (points[pieUsersCount-1]<value){
                    points[pieUsersCount-1]=value
                    labels[pieUsersCount-1]=key
                }
            }
            for (i in points.indices){
                for (j in i+1 until points.size){
                    if(points[i]<points[j]){
                        val tmp=points[i]
                        points[i]=points[j]
                        points[j]=tmp
                        val str=labels[i]
                        labels[i]=labels[j]
                        labels[j]=str
                    }
                }
            }
        }
        val listPieEntry:MutableList<PieEntry> = mutableListOf()
        for (i in 0 until pieUsersCount){
            Log.d("if","${points[i]} ${labels[i]}")
            listPieEntry.add(PieEntry(points[i].toFloat(),labels[i]))
        }
        val pieDataSet = PieDataSet(listPieEntry,"")
        pieDataSet.apply {
            colors= ColorTemplate.MATERIAL_COLORS.toMutableList()
            sliceSpace=2f
        }
        val pieData=PieData(pieDataSet)
        pieChart.apply {
            data=pieData
            centerText="Users Vs Number of messages"
            description.isEnabled=false
            setDrawRoundedSlices(true)
            animateY(1000)
            legend.isEnabled=false
            invalidate()
        }
        Log.d("GraphsFragment","updatePieChart ended")
    }

    private fun showToast(toastMsg: String,container: ViewGroup?) {
        Toast.makeText(container?.context!!, toastMsg, Toast.LENGTH_SHORT).show()
    }
}