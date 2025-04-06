package com.brandonjamesyoung.levelup.data

import com.google.android.material.floatingactionbutton.FloatingActionButton

data class SelectedIcon(
    val id: Int = 0,
    val button: FloatingActionButton,
    val adapterPosition: Int = 0
)