package com.example.myumkm.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SectionEntity (
    var id: String? = null,
    val sectionName: String? = null,
    val userId: String? = null
): Parcelable