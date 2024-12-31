package com.nrr.taskmanagement

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