package com.example.sehandroidproject

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

data class ChatRoom(
    val id: String,
    val sellerUid: String,
    val customerUid: String,
) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["sellerUid"].toString(), doc["customerUid"].toString())
}

data class Chat(
    val id: Int,
    val writerUid: String,
    val content: String,
) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id.toInt(), doc["writerUid"].toString(), doc["content"].toString())
}