package com.beta.ssky10.aram.customRecyclerViewAdapter.mealAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beta.ssky10.aram.models.Meal
import com.beta.ssky10.aram.R
import kotlinx.android.synthetic.main.item_meal.view.*
import kotlinx.android.synthetic.main.item_move_week.view.*

class MealAdapter (var Act_context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    lateinit var onClickInterface: MealItemClickInterface

    var mealsList = listOf<Meal?>(null,null,null,null,null,null,null)
        set(values){
            field = values
            notifyDataSetChanged()
        }

    var isLoading = false //현재 로딩중인지 확인
        set(value) {
            notifyDataSetChanged()
            field = value
        }
    var isLastPage = false //마지막페이지까지 모두 로드되었는지 확인

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when(viewType){
            1 -> MoveWeekItemHolder(parent)
            else -> ItemHolder(parent)
        }
    }

    //일반 식단 리스트 아이템
    inner class ItemHolder(parent: ViewGroup)
        : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_meal, parent, false)){
        val llBreakfast = itemView.ll_item_meal_breakfast
        val llLunch = itemView.ll_item_meal_lunch
        val llDinner = itemView.ll_item_meal_dinner
    }

    //일반 식단 리스트 아이템
    inner class MoveWeekItemHolder(parent: ViewGroup)
        : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_move_week, parent, false)){
        val tvTitle = itemView.tv_item_move_week_title
        val ivArrow = itemView.iv_item_move_week_arrow
        val llBackground = itemView.ll_item_move_week
    }

    override fun getItemViewType(position: Int): Int {
        return if(position in 1..mealsList.size) 0 else 1
    }

    override fun getItemCount(): Int = mealsList.size + 2

    fun clear(){
        mealsList = listOf<Meal?>(null,null,null,null,null,null,null)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            in 1..mealsList.size -> {
                mealsList[position-1].let { repo ->
                    when(repo != null){
                        true -> {
                            with(holder as ItemHolder) {
                                val breakfast = TextView(Act_context)
                                val lunch = TextView(Act_context)
                                val dinner = TextView(Act_context)
                                breakfast.gravity = Gravity.CENTER_HORIZONTAL
                                lunch.gravity = Gravity.CENTER_HORIZONTAL
                                dinner.gravity = Gravity.CENTER_HORIZONTAL

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    breakfast.text = Html.fromHtml(repo.breakfast, Html.FROM_HTML_MODE_LEGACY)
                                    lunch.text = Html.fromHtml(repo.lunch, Html.FROM_HTML_MODE_LEGACY)
                                    dinner.text = Html.fromHtml(repo.dinner, Html.FROM_HTML_MODE_LEGACY)
                                }else{
                                    breakfast.text = Html.fromHtml(repo.breakfast)
                                    lunch.text = Html.fromHtml(repo.lunch)
                                    dinner.text = Html.fromHtml(repo.dinner)
                                }
                                llBreakfast.removeAllViews()
                                llLunch.removeAllViews()
                                llDinner.removeAllViews()
                                llBreakfast.addView(breakfast)
                                llLunch.addView(lunch)
                                llDinner.addView(dinner)
                            }
                        }
                    }
                }
            }
            0 -> {
                with(holder as MoveWeekItemHolder){
                    tvTitle.text = "일주일전 식단 확인하기"
                    ivArrow.setImageResource(R.drawable.ic_arrow_back_black_24dp)
                    if (::onClickInterface.isInitialized){
                        llBackground.setOnClickListener { onClickInterface.onClickPreWeekItem()}
                    }
                }
            }
            mealsList.size+1 -> {
                with(holder as MoveWeekItemHolder){
                    tvTitle.text = "일주일뒤 식단 확인하기"
                    ivArrow.setImageResource(R.drawable.ic_arrow_forward_black_24dp)
                    if (::onClickInterface.isInitialized){
                        llBackground.setOnClickListener { onClickInterface.onClickNextWeekItem()}
                    }
                }
            }
        }

    }

}