package com.example.sehandroidproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private val db : FirebaseFirestore = Firebase.firestore
private val itemCollectionRef = db.collection("article")
class ArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val title = findViewById<TextView>(R.id.title)
        val price = findViewById<TextView>(R.id.price)
        val isSold = findViewById<TextView>(R.id.isSold)
        val seller = findViewById<TextView>(R.id.seller)
        val content = findViewById<TextView>(R.id.content)
        val btn = findViewById<Button>(R.id.chat)

        itemCollectionRef.document(intent.getStringExtra("id")?:"").get().addOnSuccessListener {
            val map = it.data
            if (map != null) {
                title.text = map["title"].toString()
                price.text = map["price"].toString() + "￦"
                db.collection("users").document(map["uid"].toString()).get().addOnSuccessListener {
                    seller.text  = it["name"].toString()
                }
                content.text = map["content"].toString()
                if(map["isSold"] as Boolean)
                    isSold.text = "판매 완료"
                else
                    isSold.text = "판매 중"
                if(Firebase.auth.currentUser?.uid == map["uid"].toString()) {
                    btn.text = "수정하기"
                    btn.setOnClickListener {
                        val intent1 = Intent(this, EditArticleActivity::class.java)
                        intent1.putExtra("type", "edit")
                        intent1.putExtra("id", intent.getStringExtra("id"))
                        startActivity(intent1)
                        finish()
                    }
                }
                else {
                    btn.text = "판매자와 채팅하기"
                    btn.setOnClickListener {

                    }
                }
            }
        }
        findViewById<Button>(R.id.back).setOnClickListener { finish() }
    }
}