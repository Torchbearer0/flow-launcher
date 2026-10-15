package com.flowlauncher.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flowlauncher.R
import com.flowlauncher.databinding.ActivityWidgetPickerBinding

class WidgetPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWidgetPickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val providers = appWidgetManager.installedProviders

        if (providers.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = WidgetAdapter(providers.map { it.provider }) { provider ->
                // Handle widget selection
                finish()
            }
        }
    }

    inner class WidgetAdapter(
        private val providers: List<android.content.ComponentName>,
        private val onSelect: (android.content.ComponentName) -> Unit
    ) : RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return WidgetViewHolder(view)
        }

        override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
            holder.bind(providers[position])
            holder.itemView.setOnClickListener { onSelect(providers[position]) }
        }

        override fun getItemCount() = providers.size

        inner class WidgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(android.R.id.text1)

            fun bind(provider: android.content.ComponentName) {
                textView.text = provider.shortClassName
            }
        }
    }
}
