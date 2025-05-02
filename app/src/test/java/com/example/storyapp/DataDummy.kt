package com.example.storyapp

import com.example.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items : MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "2022-01-08T06:34:18.598Z",
                "User $i",
                "Description $i",
                lon = 0.0,
                id = "0",
                lat = 0.0
            )
            items.add(story)
        }
        return items
    }
}