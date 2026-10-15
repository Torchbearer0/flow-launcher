package com.flowlauncher.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flowlauncher.R
import com.flowlauncher.data.PreferencesManager
import com.flowlauncher.databinding.ActivityThemePickerBinding

class ThemePickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThemePickerBinding

    private val themes = listOf(
        ThemeOption("Midnight", Color.parseColor("#FF1A1A2E"), Color.parseColor("#FFFFFFFF")),
        ThemeOption("Ocean", Color.parseColor("#FF0F3443"), Color.parseColor("#FFE0F7FA")),
        ThemeOption("Forest", Color.parseColor("#FF1B5E20"), Color.parseColor("#FFE8F5E9")),
        ThemeOption("Sunset", Color.parseColor("#FF4A148C"), Color.parseColor("#FFF3E5F5")),
        ThemeOption("Light", Color.parseColor("#FFF5F5F5"), Color.parseColor("#FF2D3436")),
        ThemeOption("Sepia", Color.parseColor("#FF3E2723"), Color.parseColor("#FFFBE9E7"))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = getString(R.string.theme_picker_title)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.themeRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.themeRecyclerView.adapter = ThemeAdapter(themes) { position ->
            PreferencesManager.themeIndex = position
            PreferencesManager.backgroundColor = themes[position].backgroundColor
            PreferencesManager.textColor = themes[position].textColor
            finish()
        }
    }

    data class ThemeOption(
        val name: String,
        val backgroundColor: Int,
        val textColor: Int
    )

    inner class ThemeAdapter(
        private val themes: List<ThemeOption>,
        private val onSelect: (Int) -> Unit
    ) : RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {

        private var selectedPosition = PreferencesManager.themeIndex

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_theme, parent, false)
            return ThemeViewHolder(view)
        }

        override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
            holder.bind(themes[position], position == selectedPosition)
            holder.itemView.setOnClickListener {
                val oldPos = selectedPosition
                selectedPosition = position
                notifyItemChanged(oldPos)
                notifyItemChanged(position)
                onSelect(position)
            }
        }

        override fun getItemCount() = themes.size

        inner class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val colorView: View = itemView.findViewById(R.id.theme_color)
            private val nameView: TextView = itemView.findViewById(R.id.theme_name)
            private val checkView: ImageView = itemView.findViewById(R.id.theme_check)

            fun bind(theme: ThemeOption, isSelected: Boolean) {
                colorView.setBackgroundColor(theme.backgroundColor)
                nameView.text = theme.name
                nameView.setTextColor(theme.textColor)
                checkView.visibility = if (isSelected) View.VISIBLE else View.GONE
            }
        }
    }
}
