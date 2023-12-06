package com.brandonjamesyoung.levelup.data

import java.time.Instant

interface Item {
    var id: Int
    val name: String?
    val cost: Int
    val dateCreated: Instant?
}