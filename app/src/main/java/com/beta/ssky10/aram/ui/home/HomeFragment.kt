package com.beta.ssky10.aram.ui.home

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.beta.ssky10.aram.R
import com.beta.ssky10.aram.customRecyclerViewAdapter.mealAdapter.*
import com.beta.ssky10.aram.netWork.NetworkCoroutin
//import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.*
import java.util.Calendar
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(), CoroutineScope {
    companion object{
        var homeViewModel:HomeViewModel? = null
        val cal = Calendar.getInstance()
        var isFirst = true
    }

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    //lateinit var dialog:Dialog
    lateinit var adapter: MealAdapter
    var weekStr = arrayOf("일", "월", "화", "수", "목", "금", "토")
    lateinit var rvMainMeal: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if(homeViewModel == null){
            homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        }
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mJob = Job()
        rvMainMeal = root!!.rv_main_meal
        //dialog = SpotsDialog(context)
        adapter = MealAdapter(requireContext())

        rvMainMeal.adapter = adapter
        rvMainMeal.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
        )

        val mealIndicatorDecoration = MealIndicatorDecoration()

        rvMainMeal.addItemDecoration(mealIndicatorDecoration)

        rvMainMeal.addOnItemTouchListener(MealTouchListener(mealIndicatorDecoration.touchAreas))

        // RecyclerView item 단위로 이동 설정
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvMainMeal)
        val listener = MealScrollListener(
                snapHelper,
                MealScrollListener.ON_SCROLL,
                true,
                object : MealScrollListener.OnChangeListener {
                    override fun onSnapped(position: Int) {
                        if (position in 1..homeViewModel!!.mealData.value!!.size) {
                            homeViewModel!!.setTitle(position - 1)
                        }
                    }
                }
        )
        rvMainMeal.addOnScrollListener(listener)

        homeViewModel!!.title.observe(viewLifecycleOwner, Observer {
            root.tv_main_title!!.text = it
        })

        homeViewModel!!.mealData.observe(viewLifecycleOwner, Observer {
            if(it != null) adapter.mealsList = it.toList()
        })

        val today = Calendar.getInstance()
        root.tv_main_today!!.text = "오늘은 ${today.get(Calendar.MONTH)+1}월 ${today.get(Calendar.DATE)}일 (${weekStr[(today.get(Calendar.DAY_OF_WEEK)+6)%7]}) 입니다"

        if(isFirst){
            setMealData()
        }else{
            rvMainMeal.scrollToPosition((cal.get(Calendar.DAY_OF_WEEK)+5)%7+1)
        }

        adapter.onClickInterface = object : MealItemClickInterface {
            override fun onClickPreWeekItem() {
                cal.add(Calendar.DATE, -((cal.get(Calendar.DAY_OF_WEEK)+5)%7+1))
                setMealData()
            }

            override fun onClickNextWeekItem() {
                cal.add(Calendar.DATE, (8-cal.get(Calendar.DAY_OF_WEEK))%7+1)
                setMealData()
            }
        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!this::mJob.isInitialized) return
        mJob.cancel()
        //if(dialog.isShowing){
            //dialog.cancel()
       // }
    }

    fun setMealData(){
        launch{
            try{
                //dialog.show()
                val deferred = async(Dispatchers.Default) {
                    //백그라운드 스레드 에서 동작합니다
                    val loadDate = Calendar.getInstance()
                    loadDate.timeInMillis = cal.timeInMillis
                    loadDate.add(Calendar.DATE, -1)
                    NetworkCoroutin.getMeal(loadDate.get(Calendar.YEAR), loadDate.get(Calendar.MONTH)+1, loadDate.get(Calendar.DATE))
                }
                val result = deferred.await()

                homeViewModel!!.clearMealData()
                val date = Calendar.getInstance()
                date.timeInMillis = cal.timeInMillis
                date.add(Calendar.DATE, -((cal.get(Calendar.DAY_OF_WEEK)+5)%7))
                for (i in 0 until 7){
                    homeViewModel!!.addMael("${date.get(Calendar.MONTH)+1}월 ${date.get(Calendar.DATE)}일(${weekStr[date.get(Calendar.DAY_OF_WEEK)-1]})", result.getValue("breakfast")[i], result.getValue("lunch")[i], result.getValue("dinner")[i])
                    date.add(Calendar.DATE,1)
                }
                homeViewModel!!.updateMealData()
                Log.d("setMealData", "setMealData: ${cal.get(Calendar.DAY_OF_WEEK)}")
                rvMainMeal.scrollToPosition((cal.get(Calendar.DAY_OF_WEEK)+5)%7+1)
                isFirst = false
            }finally {
                //dialog.cancel()
            }
        }
    }
}
