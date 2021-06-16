package com.minirogue.starwarscanontracker.core.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "media_notes",
    foreignKeys = [ForeignKey(entity = MediaItem::class,
        parentColumns = ["id"],
        childColumns = ["media_id"],
        onDelete = ForeignKey.CASCADE)])
class MediaNotes(
    @field:PrimaryKey
    @ColumnInfo(name = "media_id")
    val mediaId: Int,
    @ColumnInfo(name = "checkbox_1")
    var isBox1Checked: Boolean = false,
    @ColumnInfo(name = "checkbox_2")
    var isBox2Checked: Boolean = false,
    @ColumnInfo(name = "checkbox_3")
    var isBox3Checked: Boolean = false,
) {

    fun flipCheck1() {
        isBox1Checked = !isBox1Checked
    }

    fun flipCheck2() {
        isBox2Checked = !isBox2Checked
    }

    fun flipCheck3() {
        isBox3Checked = !isBox3Checked
    }
}
