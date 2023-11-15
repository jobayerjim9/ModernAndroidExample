package com.jobaer.example.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "matches",
    indices = [Index(value = ["date", "home", "away"], unique = true)]
)
data class Match(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String? = "",
    val description: String? = "",
    val home: String? = "",
    val away: String? = "",
    val winner: String? = "", //Only available for previous matches
    val highlights: String? = "", //Only available for previous matches
    var isNotificationSet: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(date)
        parcel.writeString(description)
        parcel.writeString(home)
        parcel.writeString(away)
        parcel.writeString(winner)
        parcel.writeString(highlights)
        parcel.writeByte(if (isNotificationSet) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Match> {
        override fun createFromParcel(parcel: Parcel): Match {
            return Match(parcel)
        }

        override fun newArray(size: Int): Array<Match?> {
            return arrayOfNulls(size)
        }
    }
}

data class MatchesResponse(
    val matches: Matches
)

data class Matches(
    val previous: List<Match> = mutableListOf(),
    val upcoming: List<Match> = mutableListOf(),
)



