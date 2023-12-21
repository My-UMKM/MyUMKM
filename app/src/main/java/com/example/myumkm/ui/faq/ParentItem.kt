package com.example.myumkm.ui.faq

data class ParentItem(
    val tag : String,
    val childList : List<ChildItem>,
    var isExpandable: Boolean = false
)