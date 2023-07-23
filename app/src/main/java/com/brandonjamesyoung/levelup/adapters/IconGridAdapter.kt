package com.brandonjamesyoung.levelup.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.data.SelectedIcon
import com.brandonjamesyoung.levelup.fragments.IconSelectDirections
import com.brandonjamesyoung.levelup.utility.TypeConverter
import com.brandonjamesyoung.levelup.constants.Mode
import com.google.android.material.floatingactionbutton.FloatingActionButton

class IconGridAdapter(
    private val iconList: List<Icon>
) :
    RecyclerView.Adapter<IconGridAdapter.IconGridViewHolder>() {
    private lateinit var getModeCallback: () -> Mode?

    private var selectedIcons: MutableList<SelectedIcon> = mutableListOf()

    // Store view data in view holder and how to bind data from view to view holder
    class IconGridViewHolder(val view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        val iconButton: FloatingActionButton = view.findViewById(R.id.QuestIcon)

        private val nameView: TextView = view.findViewById(R.id.IconName)

        fun bind(icon: Icon) {
            val drawable = TypeConverter.convertByteArrayToDrawable(
                byteArray = icon.image,
                resources = context.resources
            )

            iconButton.setImageDrawable(drawable)
            nameView.text = if (icon.name != null) icon.name else "???"
        }
    }

    // Create view and pass to view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : IconGridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quest_icon, parent, false)
        return IconGridViewHolder(view, parent.context)
    }

    private fun navigateToNewQuest(icon: Icon, fragment: Fragment) {
        val action = IconSelectDirections.actionIconSelectToNewQuest(iconId = icon.id)
        NavHostFragment.findNavController(fragment).navigate(action)
        Log.i(TAG, "Selected ${icon.name} icon. Going from Icon Select to New Quest.")
    }

    private fun isIconSelected(iconId: Int) : Boolean {
        return selectedIcons.any { icon -> icon.id == iconId }
    }

    private fun checkIcon(
        icon: Icon,
        button: FloatingActionButton,
        fragment: Fragment,
        adapterPosition: Int,
    ) {
        Log.i(TAG, "Select icon ${icon.name}")

        val context = fragment.requireContext()

        val checkmarkIcon = ResourcesCompat.getDrawable(
            fragment.resources,
            R.drawable.check_icon_green,
            context.theme
        )

        val selectedIcon = SelectedIcon(icon.id, button, adapterPosition)
        selectedIcons.add(selectedIcon)
        button.setImageDrawable(checkmarkIcon)
        button.imageTintList = getColorStateList(context, R.color.confirm)
    }

    private fun uncheckIcon(icon: Icon, button: FloatingActionButton, fragment: Fragment) {
        Log.i(TAG, "De-select icon ${icon.name}")

        val drawable = TypeConverter.convertByteArrayToDrawable(
            byteArray = icon.image,
            resources = fragment.resources
        )

        selectedIcons.removeIf { selectedIcon -> selectedIcon.id == icon.id }
        button.setImageDrawable(drawable)
        button.imageTintList = getColorStateList(fragment.requireContext(), R.color.icon_primary)
    }

    private fun selectIcon(icon: Icon, viewHolder: IconGridViewHolder) {
        val button = viewHolder.iconButton
        val fragment: Fragment = viewHolder.view.findFragment()

        if (!isIconSelected(icon.id)) {
            val adapterPosition = viewHolder.adapterPosition
            checkIcon(icon, button, fragment, adapterPosition)
        } else {
            uncheckIcon(icon, button, fragment)
        }
    }

    private fun setupIconFunctionality(icon: Icon, viewHolder: IconGridViewHolder) {
        Log.d(TAG, "${icon.name} selected in ??? mode")
        val fragment: Fragment = viewHolder.view.findFragment()

        when (getModeCallback()) {
            Mode.DEFAULT -> navigateToNewQuest(icon, fragment)
            Mode.EDIT -> selectIcon(icon, viewHolder)
            else -> Log.e(TAG, "Unknown mode detected")
        }
    }

    // Tell adapter what to do with each item in the list
    override fun onBindViewHolder(viewHolder: IconGridViewHolder, position: Int) {
        val icon = iconList[position]
        viewHolder.bind(icon)

        viewHolder.iconButton.setOnClickListener {
            setupIconFunctionality(icon, viewHolder)
        }
    }

    override fun getItemCount(): Int {
        return iconList.size
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun getIconIds(): IntArray {
        return iconList.map { it.id }.toIntArray()
    }

    fun setGetModeCallback(callback: () -> Mode?) {
        getModeCallback = callback
    }

    fun clearSelectedIcons(fragment: Fragment) {
        val selectedIconsCopy: MutableList<SelectedIcon> = mutableListOf()
        for (selectedIcon in selectedIcons) selectedIconsCopy.add(selectedIcon)

        for (selectedIcon in selectedIconsCopy) {
            val icon = iconList.find { it.id == selectedIcon.id }
            if (icon != null) uncheckIcon(icon, selectedIcon.button, fragment)
        }

        selectedIcons.clear()
    }

    fun getSelectedIcons(): List<SelectedIcon> {
        return selectedIcons
    }

    companion object {
        private const val TAG = "IconGridAdapter"
    }
}