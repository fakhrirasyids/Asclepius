package com.dicoding.asclepius.data.local.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @NonNull
    @ColumnInfo(name = "imageUri")
    val imageUri: String,

    @NonNull
    @ColumnInfo(name = "inferenceTime")
    val inferenceTime: String,

    @NonNull
    @ColumnInfo(name = "results")
    val results: String,
)
