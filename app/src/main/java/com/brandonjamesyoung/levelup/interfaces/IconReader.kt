package com.brandonjamesyoung.levelup.interfaces

import com.brandonjamesyoung.levelup.data.Icon

interface IconReader {
    suspend fun getIcon(id: Int): Icon
}