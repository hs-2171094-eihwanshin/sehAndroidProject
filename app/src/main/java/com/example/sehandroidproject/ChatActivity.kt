package com.example.sehandroidproject

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatViewAdapter(private var items: List<Chat>) : RecyclerView.Adapter<ChatViewAdapter.MyViewHolder>() {
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val content = v.findViewById<TextView>(R.id.content)
    }

    fun updateList(newList: List<Chat>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if(viewType == 1) {
            val view = layoutInflater.inflate(R.layout.chat_right, parent, false)
            val viewHolder = MyViewHolder(view)
            return viewHolder
        }
        else {
            val view = layoutInflater.inflate(R.layout.chat, parent, false)
            val viewHolder = MyViewHolder(view)
            return viewHolder
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        if(items[position].writerUid == Firebase.auth.currentUser?.uid) {
            return 1
        }
        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.content.text = items[position].content
    }

}

class ChatActivity : AppCompatActivity() {
    private val db : FirebaseFirestore = Firebase.firestore
    private lateinit var itemCollectionRef : CollectionReference
    private lateinit var adapter: ChatViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val path = "ChatRoom/" + intent.getStringExtra("id").toString() + "/Chat"
        itemCollectionRef = db.collection(path)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            this.stackFromEnd = true
        }
        adapter = ChatViewAdapter(emptyList())
        recyclerView.adapter = adapter

        updateList()

        findViewById<Button>(R.id.send).setOnClickListener {
            val msg = findViewById<EditText>(R.id.sendMessage).text.toString()
            if(msg == "") { Toast.makeText(this, "메세지를 입력해주세요.", Toast.LENGTH_SHORT).show()}
            else {
                val itemMap = hashMapOf(
                    "writerUid" to Firebase.auth.currentUser?.uid,
                    "content" to msg
                )
                itemCollectionRef.get().addOnSuccessListener {
                    itemCollectionRef.document(it.size().toString()).set(itemMap)
                    findViewById<EditText>(R.id.sendMessage).setText("")
                }
            }
        }

        findViewById<FloatingActionButton>(R.id.fab_back).setOnClickListener { finish() }

        itemCollectionRef.addSnapshotListener { snapShots, _->
            if(snapShots != null)
                updateList()
        }
    }

    private fun updateList() {
        itemCollectionRef.get().addOnSuccessListener {
            val items = mutableListOf<Chat>()
            for(doc in it) {
                items.add(Chat(doc))
            }
            adapter.updateList(items)
        }
    }
}