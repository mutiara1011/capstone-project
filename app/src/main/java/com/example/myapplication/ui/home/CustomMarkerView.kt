package com.example.myapplication.ui.home

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.example.myapplication.R
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvPrice: TextView = findViewById(R.id.tvPrice)

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        val value = entry?.y ?: 0f
        val formattedValue = if (value.toString().length > 8) {
            "Val: ${value.toString().substring(0, 7)}"
        } else {
            "Val: $value"
        }
        tvPrice.text = formattedValue
        super.refreshContent(entry, highlight)
    }

    override fun getOffsetForDrawingAtPoint(xpos: Float, ypos: Float): MPPointF {
        return MPPointF(-width / 2f, -height - 10f) // Sesuaikan posisi marker
    }
}

