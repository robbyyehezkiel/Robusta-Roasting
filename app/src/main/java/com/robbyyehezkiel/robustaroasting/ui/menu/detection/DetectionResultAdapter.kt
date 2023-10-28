package com.robbyyehezkiel.robustaroasting.ui.menu.detection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.robbyyehezkiel.robustaroasting.R

class DetectionResultAdapter(private val resultList: List<Pair<String, Double>>) :
    RecyclerView.Adapter<DetectionResultAdapter.ResultViewHolder>() {

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultLabel: TextView = itemView.findViewById(R.id.resultLabel)
        val resultPercentage: TextView = itemView.findViewById(R.id.resultPercentage)
        val resultProgress: ProgressBar = itemView.findViewById(R.id.resultProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val (label, percentage) = resultList[position]
        holder.resultLabel.text = label

        // Correct the formatting to "%.2f" without the extra '%'
        holder.resultPercentage.text = String.format("%.2f%%", percentage)

        // Calculate the progress value from the percentage (0-100)
        val progress = (percentage.coerceIn(0.0, 100.0)).toInt()
        holder.resultProgress.progress = progress
    }


    override fun getItemCount(): Int {
        return resultList.size
    }
}
