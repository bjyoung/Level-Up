package com.brandonjamesyoung.levelup.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.fragments.IconSelectDirections
import com.brandonjamesyoung.levelup.shared.ByteArrayHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class IconGridAdapter(private val iconList: List<Icon>) :
    RecyclerView.Adapter<IconGridAdapter.IconGridViewHolder>() {
    // Store view data in view holder and how to bind data from view to view holder
    class IconGridViewHolder(val view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        private val iconButton: FloatingActionButton = view.findViewById(R.id.QuestIcon)

        private val nameView: TextView = view.findViewById(R.id.IconName)

        private fun navigateToNewQuest(icon: Icon) {
            val action = IconSelectDirections.actionIconSelectToNewQuest(iconId = icon.id)
            NavHostFragment.findNavController(view.findFragment()).navigate(action)
            Log.i(TAG, "Selected ${icon.name} icon. Going from Icon Select to New Quest.")
        }

        fun bind(icon: Icon) {
            val drawable = ByteArrayHelper.convertByteArrayToDrawable(
                byteArray = icon.image,
                resources = context.resources
            )

            iconButton.setImageDrawable(drawable)

            iconButton.setOnClickListener {
                navigateToNewQuest(icon)
            }

            nameView.text = if (icon.name != null) icon.name else "???"
        }
    }

    // Create view and pass to view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconGridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quest_icon, parent, false)
        return IconGridViewHolder(view, parent.context)
    }

    // Tell adapter what to do with each item in the list
    override fun onBindViewHolder(viewHolder: IconGridViewHolder, position: Int) {
        val icon = iconList[position]
        viewHolder.bind(icon)
    }

    override fun getItemCount(): Int {
        return iconList.size
    }

    companion object {
        private const val TAG = "IconGridAdapter"
    }
}