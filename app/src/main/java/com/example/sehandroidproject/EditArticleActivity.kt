package com.example.sehandroidproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditArticleActivity : AppCompatActivity() {
    private val db : FirebaseFirestore = Firebase.firestore
    private val itemCollectionRef = db.collection("article")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articleedit)
        val title = findViewById<EditText>(R.id.title)
        val content = findViewById<EditText>(R.id.content)
        val price = findViewById<EditText>(R.id.price)
        val isSold = findViewById<SwitchCompat>(R.id.isSold)

        val btn = findViewById<Button>(R.id.add)
        findViewById<Button>(R.id.back).setOnClickListener { finish() }

        val intent1 = intent.getStringExtra("type")
        if(intent1 == "add") {
            isSold.isEnabled = false
            isSold.alpha = 0F
            btn.setOnClickListener {
                if(title.text.toString() == "") { Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show() }
                else if(content.text.toString() == "") { Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show() }
                else if(price.text.toString() == "") { Toast.makeText(this, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show() }
                else {
                    val itemMap = hashMapOf(
                        "uid" to Firebase.auth.currentUser?.uid,
                        "title" to title.text.toString(),
                        "content" to content.text.toString(),
                        "price" to price.text.toString().toInt(),
                        "isSold" to false
                    )
                    itemCollectionRef.add(itemMap)
                    finish()
                }
            }
        }
        else if(intent1 == "edit") {
            findViewById<TextView>(R.id.text1).text = "판매글 수정"
            isSold.isEnabled = true
            isSold.alpha = 1F
            btn.text = "수정하기"
            itemCollectionRef.document(intent.getStringExtra("id")?:"").get().addOnSuccessListener {
                val map = it.data
                if (map != null) {
                    title.setText(map["title"].toString())
                    price.setText(map["price"].toString())
                    content.setText(map["content"].toString())
                    isSold.isChecked = map["isSold"].toString().toBoolean()
                }
            }
            btn.setOnClickListener {
                if(title.text.toString() == "") { Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show() }
                else if(content.text.toString() == "") { Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show() }
                else if(price.text.toString() == "") { Toast.makeText(this, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show() }
                else {
                    val itemMap : Map<String, Any> = hashMapOf(
                        "title" to title.text.toString(),
                        "content" to content.text.toString(),
                        "price" to price.text.toString().toInt(),
                        "isSold" to isSold.isChecked
                    )
                    itemCollectionRef.document(intent.getStringExtra("id").toString()).update(itemMap)
                    finish()
                }
            }

        }
    }
}