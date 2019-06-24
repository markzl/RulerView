package com.jiedu.android.simpletest.todo.ruler

import android.content.Context
import android.view.View

/**
 * 描述：添加类的描述
 * @author yourName
 * @e-mail XXX@XX.com
 * @time   2019-06-19
 */
class Utils {
    companion object {

        @JvmStatic
        fun dp2px(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density).toInt()
        }

        @JvmStatic
        fun sp2px(context: Context, sp: Int): Int {
            val scaleDensity = context.resources.displayMetrics.scaledDensity
            return (scaleDensity * sp).toInt()
        }

        @JvmStatic
        fun getDefaultWidth(widthMeasureSpec: Int): Int {
            val mode = View.MeasureSpec.getMode(widthMeasureSpec)
            var size = 0
            when (mode) {
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST, View.MeasureSpec.EXACTLY ->
                    size = View.MeasureSpec.getSize(widthMeasureSpec);
            }
            return size
        }

        @JvmStatic
        fun getDefaultHeight(heightMeasureSpec: Int): Int {
            val mode = View.MeasureSpec.getMode(heightMeasureSpec)
            var size = 0
            when (mode) {
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST, View.MeasureSpec.EXACTLY ->
                    size = View.MeasureSpec.getSize(heightMeasureSpec)
            }
            return size;
        }

    }
}