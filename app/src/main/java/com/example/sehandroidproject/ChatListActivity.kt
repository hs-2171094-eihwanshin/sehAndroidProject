package com.example.sehandroidproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatAdapter(private var items: List<ChatRoom>) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val id = v.findViewById<TextView>(R.id.id)
        val title = v.findViewById<TextView>(R.id.title)
        init {
            v.setOnClickListener {
                val intent = Intent(v.context, ChatActivity::class.java)
                intent.putExtra("id", id.text)
                v.context.startActivity(intent)
            }
        }
    }

    fun updateList(newList: List<ChatRoom>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.chatlist, parent, false)
        val viewHolder = MyViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.id.text = items[position].id
        Firebase.firestore.collection("users").document(items[position].sellerUid).get().addOnSuccessListener {
            Firebase.firestore.collection("users").document(items[position].customerUid).get().addOnSuccessListener { it2 ->
                holder.title.text = holder.itemView.context.getString(R.string.chat_title, it["name"].toString(), it2["name"].toString())
            }
        }
    }

}

class ChatListActivity : AppCompatActivity() {
    private val db : FirebaseFirestore = Firebase.firestore
    private val itemCollectionRef = db.collection("ChatRoom")
    private lateinit var adapter: ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlist)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(emptyList())
        recyclerView.adapter = adapter

        updateList()

        findViewById<FloatingActionButton>(R.id.fab_back).setOnClickListener { finish() }

        itemCollectionRef.addSnapshotListener { snapShots, _->
            if(snapShots != null)
                updateList()
        }
    }

    private fun updateList() {
        itemCollectionRef.get().addOnSuccessListener {
            val items = mutableListOf<ChatRoom>()
            for(doc in it) {
                if(doc["customerUid"].toString() == Firebase.auth.currentUser?.uid) {
                    items.add(ChatRoom(doc))
                }
                else if(doc["sellerUid"].toString() == Firebase.auth.currentUser?.uid) {
                    items.add(ChatRoom(doc))
                }
            }
            adapter.updateList(items)
            if(items.isEmpty())
                findViewById<TextView>(R.id.text1).alpha = 1F
            else
                findViewById<TextView>(R.id.text1).alpha = 0F
        }
    }
}