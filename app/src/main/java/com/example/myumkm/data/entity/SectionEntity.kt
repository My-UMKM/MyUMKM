package com.example.myumkm.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SectionEntity (
    val sectionId: String? = null,
    val sectionName: String,
    val userId: String
): Parcelable