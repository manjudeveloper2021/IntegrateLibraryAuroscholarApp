package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class LeaderboardDataResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: LeaderboardData? = LeaderboardData()
) {
    data class LeaderboardData(

        @SerializedName("userData") var userData: UserData? = UserData(),
        @SerializedName("leaderboard") var leaderboard: List<Leaderboard>? = null

    ) {
        data class UserData(

            @SerializedName("student_name") var studentName: String? = null,
            @SerializedName("rank") var rank: Int? = null,
            @SerializedName("score") var score: String? = null,
            @SerializedName("awardData") var awardData: String? = null,
            @SerializedName("badgeCount") var badgeCount: String? = null

        )

        data class Leaderboard(

            @SerializedName("student_name") var studentName: String? = null,
            @SerializedName("userId") var userId: String? = null,
            @SerializedName("score") var score: String? = null,
            @SerializedName("awardData") var awardData: String? = null,
            @SerializedName("badgeCount") var badgeCount: String? = null,
            @SerializedName("imgurl") var imgurl: String? = null,
            @SerializedName("rank") var rank: Int? = null

        )
    }
}