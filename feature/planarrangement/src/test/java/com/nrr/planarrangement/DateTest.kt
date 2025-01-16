package com.nrr.planarrangement

import kotlinx.datetime.Clock
import org.junit.Test

class DateTest {
    @Test
    fun conversion_isConsistent() {
        val date = Clock.System.now().toDate()
        val date2 = date.toInstant().toDate()
        val instant = date.toInstant()
        val instant2 = date2.toInstant()
        val date3 = date2.toInstant().toDate()

        println(date)
        println(date2)
        assert(date == date2 && instant == instant2 && date2 == date3 && date3 == date)
    }
}