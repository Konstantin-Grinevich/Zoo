package com.example.appstudents.data

import java.util.*

data class Faculty (
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    var size: String = ""
){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Faculty

        if (id != other.id) return false
        if (name != other.name) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }
}