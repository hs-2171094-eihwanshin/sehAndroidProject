package com.example.sehandroidproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//사용자 계정 생성과 로그인 기능
//판매 글 목록 보기 기능
//판매 글 목록 보기에서 필터 기능(판매된 상품 제외, 가격 조건)
//중고물품 판매 글 등록/수정 기능, 판매 글에는 최소한 판매자, 제목, 내용, 가격, 판매여부를 포함할 것, 그림 업로드 구현은 필수는 아니지만, 글은 반드시 파이어베이스 DB에 저장되어야 함
//판매자와 구매 희망자 간에 메시지 보내기 기능, DB활용하여 구현

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.textUID)?.text = Firebase.auth.currentUser?.uid ?: "No User"

        findViewById<Button>(R.id.button_signout)?.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(
                Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}