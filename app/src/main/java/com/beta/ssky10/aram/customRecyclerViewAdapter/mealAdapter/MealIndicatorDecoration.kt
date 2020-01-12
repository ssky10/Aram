package com.beta.ssky10.aram.customRecyclerViewAdapter.mealAdapter

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beta.ssky10.aram.customRecyclerViewAdapter.mealAdapter.IndicatorInfo

class MealIndicatorDecoration : RecyclerView.ItemDecoration() {
    var weekStr = arrayOf("일", "월", "화", "수", "목", "금", "토")
    private val colorActiveRect = Color.parseColor("#ffffff")
    private val colorInactive = Color.parseColor("#ffffff")

    private val DP: Float = Resources.getSystem().displayMetrics.density

    private var mIndicatorItemLength = DP * 1 //인디케이터 아이템별 길이(onDrawOver에서 값 변경)

    private var totalLength = DP * 1 //인디케이터 전체 길이(onDrawOver에서 값 변경)

    private val startEndMargin = DP * 24 //인디케이터 좌우 마진 길이

    private val charSize = Rect()

    private val mInterpolator: Interpolator = AccelerateDecelerateInterpolator()

    private val mPaint: Paint = Paint()

    private var isFirst = true

    val touchAreas = IndicatorInfo()


    //인디케이터 그리기 기본 설정
    fun LineIndicatorDecoration() {
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.isAntiAlias = true
        mPaint.textSize = 100f
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val itemCount = parent.adapter!!.itemCount - 2
        if(itemCount == 7){
            LineIndicatorDecoration()

            mPaint.getTextBounds(weekStr[0], 0, weekStr[0].length, charSize)

            totalLength = parent.width - startEndMargin*2 // 인디케이터 전체 길이
            mIndicatorItemLength = (totalLength - charSize.width()*7)/6 // 요일 글자간 거리
            val indicatorStartX = startEndMargin //마진길이 만큼 이동하여 그리기 시작
            val indicatorPosY = charSize.height() + DP * 16

            val layoutManager = parent.layoutManager as LinearLayoutManager?
            val activePosition = layoutManager!!.findFirstVisibleItemPosition()
            when (activePosition) {
                RecyclerView.NO_POSITION -> return
                0 -> return
                8 -> return
            }

            val activeChild: View? = layoutManager.findViewByPosition(activePosition)
            val left: Int = activeChild!!.left
            val width: Int = activeChild.width

            if(left < 1 && activePosition == 7) return

            val indicatorNowPosY = indicatorPosY - (charSize.height()) / 2 + 2 * DP

            val progress: Float =
                    mInterpolator.getInterpolation(left * -1 / width.toFloat())
            Log.e("left",left.toString())

            val bitmap = Bitmap.createBitmap(c.width, c.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            drawBackgroundIndicators(canvas, indicatorStartX, indicatorPosY)

            drawHighlights(canvas, indicatorStartX - charSize.width(), indicatorNowPosY, activePosition-1, progress)

            c.drawBitmap(bitmap, Matrix(), null)
        }
    }


    //요일 글자 생성
    private fun drawBackgroundIndicators(
            c: Canvas,
            indicatorStartX: Float,
            indicatorPosY: Float
    ) {
        var posX = indicatorStartX
        mPaint.color = colorInactive
        for (i in 0..6){
            if (isFirst){
                val touchArea = Rect(charSize)
                Log.e("touchAreaBefore","${touchArea.left}, ${touchArea.top}")
                touchArea.offset(posX.toInt(),indicatorPosY.toInt())
                Log.e("touchAreaAfter","${touchArea.left}, ${touchArea.top}")
                //mPaint.getTextBounds(weekStr[i], 0, weekStr[i].length, touchArea)
                touchAreas.addTouchArea(touchArea)
            }
            c.drawText(weekStr[i],posX,indicatorPosY,mPaint)
            posX += mIndicatorItemLength + charSize.width()
        }
        isFirst = false
    }

    //인디케이터 현재 위치 막대 생성
    private fun drawHighlights(
            c: Canvas, indicatorStartX: Float, indicatorPosY: Float,
            highlightPosition: Int, progress: Float
    ) {
        val circlePaint = Paint()
        circlePaint.style = Paint.Style.FILL_AND_STROKE
        circlePaint.color = colorActiveRect
        circlePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)

        val itemWidth = mIndicatorItemLength + charSize.width()
        if (progress == 0f) {
            c.drawCircle(indicatorStartX + itemWidth * highlightPosition + mIndicatorItemLength,indicatorPosY,charSize.width().toFloat(),circlePaint)
        } else {
            val partialLength = (mIndicatorItemLength + charSize.width()) * progress
            c.drawCircle(indicatorStartX + itemWidth * highlightPosition + mIndicatorItemLength + partialLength,indicatorPosY, charSize.width().toFloat(), circlePaint)
        }
    }
}