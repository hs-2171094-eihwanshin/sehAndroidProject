package com.example.sehandroidproject

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyAdapter(private var items: List<Article>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val id = v.findViewById<TextView>(R.id.id)
        val title = v.findViewById<TextView>(R.id.title)
        val price = v.findViewById<TextView>(R.id.price)
        val isSold = v.findViewById<TextView>(R.id.isSold)
        init {
            v.setOnClickListener {
                val intent = Intent(v.context, ArticleActivity::class.java)
                intent.putExtra("id", id.text)
                v.context.startActivity(intent)
            }
        }
    }

    fun updateList(newList: List<Article>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.articlelist, parent, false)
        val viewHolder = MyViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.id.text = items[position].id
        holder.title.text = items[position].title
        holder.price.text = items[position].price.toString() + "￦"
        if(items[position].isSold)
            holder.isSold.text = "판매 완료"
        else
            holder.isSold.text = "판매 중"

    }

}
class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private var isFabOpen = false
    private var filtered = "all"
    private val db : FirebaseFirestore = Firebase.firestore
    private val itemCollectionRef = db.collection("article")
    private lateinit var filterText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Firebase.auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java))
            finish()
        }

        filterText = findViewById(R.id.filterText)
        filterText.inputType = InputType.TYPE_NULL
        val spinner = findViewById<Spinner>(R.id.select)
        spinner.adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when(position) {
                    0 -> {
                        filtered = "all"
                        filterText.text = null
                        filterText.inputType = InputType.TYPE_NULL
                        filterText.alpha = 0F
                    }
                    1 -> {
                        filtered = "isNotSold"
                        filterText.text = null
                        filterText.inputType = InputType.TYPE_NULL
                        filterText.alpha = 0F
                    }
                    2 -> {
                        filtered = "isSold"
                        filterText.text = null
                        filterText.inputType = InputType.TYPE_NULL
                        filterText.alpha = 0F
                    }
                    3 -> {
                        filtered = "priceDown"
                        filterText.inputType = InputType.TYPE_CLASS_NUMBER
                        filterText.alpha = 1F
                    }
                    4 -> {
                        filtered = "priceUp"
                        filterText.inputType = InputType.TYPE_CLASS_NUMBER
                        filterText.alpha = 1F
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                filtered = "all"
            }
        }

        findViewById<Button>(R.id.filter).setOnClickListener {
            updateList()
        }


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(emptyList())
        recyclerView.adapter = adapter

        updateList()

        findViewById<FloatingActionButton>(R.id.fab_main)?.setOnClickListener {
            if (isFabOpen) {
                ObjectAnimator.ofFloat(findViewById<FloatingActionButton>(R.id.fab_newArticle), "translationX", 0f).apply { duration=1200; start() }
                ObjectAnimator.ofFloat(findViewById<FloatingActionButton>(R.id.fab_chatList), "translationX", 0f).apply { duration=1200; start() }
                ObjectAnimator.ofFloat(findViewById<FloatingActionButton>(R.id.fab_logout), "translationX", 0f).apply { duration=1200; start() }
                ObjectAnimator.ofFloat(findViewById<FloatingActionButton>(R.id.fab_main), View.ROTATION, -360f, 0f).apply { duration=1200; start() }
            } else {
                ObjectAnimator.ofFloat(findViewById<FloatingActionButton>(R.id.fab_newArticle), "translationX", -540f).apply { duration=1200; start() }
                ObjectAnimator.ofFloat(findViewById<FloatingActionButton>(R.id.fab_chatList), "translationX", -360f).apply { duration=1200; start() }
                ObjectAnimator.ofFloat(findViewById<FloatingActionButton>(R.id.fab_logout), "translationX", -180f).apply { duration=1200; start() }
                ObjectAnimator.ofFloat(findViewById<FloatingActionButton>(R.id.fab_main), View.ROTATION, 0f, -360f).apply { duration=1200; start() }
            }
            isFabOpen = !isFabOpen
        }

        findViewById<FloatingActionButton>(R.id.fab_newArticle)?.setOnClickListener {
            val intent1 = Intent(this, EditArticleActivity::class.java)
            intent1.putExtra("type", "add")
            startActivity(intent1)
        }

        findViewById<FloatingActionButton>(R.id.fab_chatList)?.setOnClickListener {
            startActivity(
                Intent(this, ChatListActivity::class.java))
        }

        findViewById<FloatingActionButton>(R.id.fab_logout)?.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(
                Intent(this, LoginActivity::class.java))
            finish()
        }

        itemCollectionRef.addSnapshotListener { snapShots, _->
            if(snapShots != null)
                updateList()
        }

    }

    private fun updateList() {
        itemCollectionRef.get().addOnSuccessListener {
            val items = mutableListOf<Article>()
            for(doc in it) {
                if(filtered == "all") { items.add(Article(doc)) }
                else if(filtered == "isNotSold" && !(doc["isSold"] as Boolean)) { items.add(Article(doc)) }
                else if(filtered == "isSold" && doc["isSold"] as Boolean) { items.add(Article(doc)) }
                else if(filtered == "priceDown" && doc["price"].toString().toInt() <= (filterText.text.toString().toIntOrNull() ?: 0)
                ) { items.add(Article(doc)) }
                else if(filtered == "priceUp" && doc["price"].toString().toInt() >= (filterText.text.toString().toIntOrNull() ?: 0)
                ) { items.add(Article(doc)) }

            }
            adapter.updateList(items)
        }
    }
}