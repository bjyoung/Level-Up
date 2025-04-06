package com.brandonjamesyoung.levelup.data

import java.time.Instant

interface Item {
    var id: Int
    var name: String?
    var cost: Int
    val dateCreated: Instant?
}