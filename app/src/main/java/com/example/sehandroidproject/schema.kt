package com.example.sehandroidproject

import com.google.firebase.firestore.QueryDocumentSnapshot

data class Article(
    val id: String,
    val uid: String,
    val title: String,
    val content: String,
    val price: Int,
    val isSold: Boolean
) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["uid"].toString(), doc["title"].toString(), doc["content"].toString(), doc["price"].toString().toIntOrNull()?: 0, doc["isSold"].toString().toBoolean())
}