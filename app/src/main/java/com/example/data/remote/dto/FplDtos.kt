package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FplBootstrapDto(
    @SerializedName("events")
    val events: List<FplEventDto> = emptyList(),
    @SerializedName("teams")
    val teams: List<FplTeamDto> = emptyList()
)

data class FplEventDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("deadline_time")
    val deadlineTime: String? = null,
    @SerializedName("deadline_time_epoch")
    val deadlineTimeEpoch: Long? = null,
    @SerializedName("finished")
    val finished: Boolean? = false,
    @SerializedName("is_previous")
    val isPrevious: Boolean? = false,
    @SerializedName("is_current")
    val isCurrent: Boolean? = false,
    @SerializedName("is_next")
    val isNext: Boolean? = false
)

data class FplTeamDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("short_name")
    val shortName: String,
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("strength")
    val strength: Int? = null
)

data class FplFixtureDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("code")
    val code: Long? = null,
    @SerializedName("event")
    val event: Int? = null,
    @SerializedName("finished")
    val finished: Boolean = false,
    @SerializedName("finished_provisional")
    val finishedProvisional: Boolean? = false,
    @SerializedName("kickoff_time")
    val kickoffTime: String? = null,
    @SerializedName("minutes")
    val minutes: Int? = 0,
    @SerializedName("provisional_start_time")
    val provisionalStartTime: Boolean? = false,
    @SerializedName("started")
    val started: Boolean? = false,
    @SerializedName("team_a")
    val teamA: Int,
    @SerializedName("team_a_score")
    val teamAScore: Int? = null,
    @SerializedName("team_h")
    val teamH: Int,
    @SerializedName("team_h_score")
    val teamHScore: Int? = null
)
