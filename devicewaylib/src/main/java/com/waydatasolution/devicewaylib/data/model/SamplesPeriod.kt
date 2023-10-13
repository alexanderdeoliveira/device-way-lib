package com.waydatasolution.devicewaylib.data.model

enum class SamplesPeriod(val code: Int) {
    READ_ALL_SAMPLES(0),
    READ_LAST_DAY_SAMPLES(1),
    READ_LAST_TREE_DAYS_SAMPLES(2),
    READ_LAST_WEEK_SAMPLES(3),
    READ_LAST_MONTH_SAMPLES(4)
}