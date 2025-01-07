package com.nrr.taskmanagement

import com.nrr.model.Task
import com.nrr.model.TaskType
import com.nrr.taskmanagement.Customize.Filter
import com.nrr.taskmanagement.Customize.Sort
import com.nrr.taskmanagement.Customize.Sort.Assigned
import com.nrr.taskmanagement.Customize.Sort.LatestUpdate
import com.nrr.taskmanagement.Customize.Sort.Newest
import com.nrr.taskmanagement.Customize.Sort.Oldest

internal sealed class Customize(open val name: String) {
    sealed class Filter(name: String) : Customize(name) {
        data object All : Filter("All")
        data object Personal : Filter("Personal")
        data object Work : Filter("Work")
        data object Learning : Filter("Learning")
        data object Health : Filter("Health")
        data object Reflection : Filter("Reflection")
        data object Special : Filter("Special")

        companion object {
            val entries = listOf(
                All,
                Personal,
                Work,
                Learning,
                Health,
                Reflection,
                Special
            )
        }
    }

    sealed class Sort(name: String) : Customize(name) {
        data object LatestUpdate : Sort("Latest Update")
        data object Assigned : Sort("Set")
        data object Newest : Sort("Newest")
        data object Oldest : Sort("Oldest")

        companion object {
            val entries = listOf(
                LatestUpdate,
                Assigned,
                Newest,
                Oldest
            )
        }
    }
}

internal fun List<Task>.sort(type: Sort) = when (type) {
    is LatestUpdate -> {
        this.sortedByDescending { it.updateAt }
    }
    is Assigned -> {
        this.sortedBy { it.activeStatus?.period?.ordinal ?: Int.MAX_VALUE }
    }
    is Newest -> {
        this.sortedByDescending { it.createdAt }
    }
    is Oldest -> {
        this.sortedBy { it.createdAt }
    }
}

internal fun List<Task>.filter(type: Filter) = filter {
    it.taskType == when (type) {
        Filter.All -> it.taskType
        Filter.Health -> TaskType.HEALTH
        Filter.Learning -> TaskType.LEARNING
        Filter.Personal -> TaskType.PERSONAL
        Filter.Reflection -> TaskType.REFLECTION
        Filter.Special -> TaskType.SPECIAL
        Filter.Work -> TaskType.WORK
    }
}

internal fun List<Task>.sortAndFilter(
    sortType: Sort,
    filterType: Filter
) = sort(sortType).filter(filterType)