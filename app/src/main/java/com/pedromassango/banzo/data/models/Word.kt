package com.pedromassango.banzo.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Pedro Massango on 6/23/18.
 */
@Entity
class Word(
        @PrimaryKey
        var ptWord: String,
        var translation: String,
        var learning: Boolean = false,
        var learned: Boolean = false,
        var failCount: Int = 0,
        var hitCounter: Int = 0): Parcelable{
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readByte() != 0.toByte())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(ptWord)
                parcel.writeString(translation)
                parcel.writeByte(if (learning) 1 else 0)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<Word> {
                override fun createFromParcel(parcel: Parcel): Word {
                        return Word(parcel)
                }

                override fun newArray(size: Int): Array<Word?> {
                        return arrayOfNulls(size)
                }
        }

}