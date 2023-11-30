package com.example.sehandroidproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    val db : FirebaseFirestore = Firebase.firestore
    val itemCollectionRef = db.collection("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val pw = findViewById<EditText>(R.id.password)

        pw.transformationMethod = AsteriskPasswordTransformationMethod()

        findViewById<Button>(R.id.back)?.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }

        findViewById<Button>(R.id.login)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.username)?.text.toString()
            val password = pw.text.toString()
            val nickname = findViewById<EditText>(R.id.nickname)?.text.toString()
            val birth1 = findViewById<EditText>(R.id.signBirth)?.text.toString()
            val birth2 = findViewById<EditText>(R.id.signBirth2)?.text.toString()
            val birth3 = findViewById<EditText>(R.id.signBirth3)?.text.toString()
            val birth = "$birth1-$birth2-$birth3"
            if(userEmail == "") { Toast.makeText(this, "email을 입력해주세요.", Toast.LENGTH_SHORT).show() }
            else if(password == "") { Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show() }
            else if(password.length <= 5) { Toast.makeText(this, "비밀번호는 최소 6자리 입력해주세요.", Toast.LENGTH_SHORT).show() }
            else if(nickname == "") { Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show() }
            else if(birth1 == "" || birth2 == "" || birth3 == "") { Toast.makeText(this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show() }
            else { createUser(userEmail, password, nickname, birth) }
        }
    }

    private fun createUser(userEmail : String, password : String, nickname : String, birth : String) {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if(it.isSuccessful) {
                    val itemMap = hashMapOf(
                        "name" to nickname,
                        "birth" to birth
                    )
                    Firebase.auth.currentUser?.let { it1 -> itemCollectionRef.document(it1.uid).set(itemMap) }
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                } else {
                    Log.w("LoginActivity", "createUserEmail", it.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}