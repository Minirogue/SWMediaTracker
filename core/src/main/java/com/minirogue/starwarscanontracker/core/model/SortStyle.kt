package com.minirogue.starwarscanontracker.core.model

import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotes
import kotlin.math.sign

class SortStyle(val style: Int, val ascending: Boolean) : Comparator<MediaAndNotes> {

    companion object {
        const val SORT_TITLE = 1
        const val SORT_TIMELINE = 2
        const val SORT_DATE = 4
        const val DEFAULT_STYLE = SORT_DATE
        fun getSortText(sortType: Int): String {
            return when (sortType) {
                SORT_TITLE -> "Title"
                SORT_TIMELINE -> "Timeline"
                SORT_DATE -> "Date Released"
                else -> "SortTypeNotFound"
            }
        }

        fun getAllStyles(): Array<Int> {
            return arrayOf(SORT_TITLE, SORT_TIMELINE, SORT_DATE)
        }
    }

    // TODO
    @Suppress("ReturnCount")
    override fun compare(p0: MediaAndNotes?, p1: MediaAndNotes?): Int {
        if (p0 == null || p1 == null) {
            if (p0 == null && p1 == null) {
                return 0
            } else if (p0 == null) {
                return 1
            } else if (p1 == null) {
                return -1
            }
        }
        val compNum: Int = when (style) {
            SORT_TITLE -> compareTitles(p0, p1)
            SORT_TIMELINE -> sign(p0.mediaItem.timeline - p1.mediaItem.timeline).toInt()
            SORT_DATE -> compareDates(p0.mediaItem, p1.mediaItem)
            else -> compareTitles(p0, p1)
        }
        return if (ascending) {
            compNum
        } else {
            -compNum
        }
    }

    private fun compareTitles(p0: MediaAndNotes, p1: MediaAndNotes): Int {
        val title1 = p0.mediaItem.title
        val title2 = p1.mediaItem.title

        return title1.compareTo(title2)
    }

    private fun compareDates(item1: MediaItem, item2: MediaItem): Int {
        val splitDate1 = item1.date.split("/")
        val splitDate2 = item2.date.split("/")
        var compare = (splitDate1[2].toInt() - splitDate2[2].toInt())
        if (compare == 0) {
            compare = (splitDate1[0].toInt() - splitDate2[0].toInt())
        }
        if (compare == 0) {
            compare = (splitDate1[1].toInt() - splitDate2[1].toInt())
        }
        return compare
    }

    fun getText(): String {
        return getSortText(style)
    }

    fun isAscending(): Boolean {
        return ascending
    }

    override fun reversed(): SortStyle {
        return SortStyle(style, !ascending)
    }
}
