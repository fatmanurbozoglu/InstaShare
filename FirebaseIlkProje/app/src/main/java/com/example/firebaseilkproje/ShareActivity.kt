package com.example.firebaseilkproje

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build.VERSION_CODES.S
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseilkproje.databinding.ActivityShareBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ShareActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShareBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerviewAdapter : ShareAdapter

    val db = Firebase.firestore
    var descriptionsList = ArrayList<Share>()

    val storage = Firebase.storage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_first)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        firebaseGetData()

        // recyclerview için
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        recyclerviewAdapter = ShareAdapter(descriptionsList)
        binding.recyclerView.adapter = recyclerviewAdapter

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.signOut){
            auth.signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        else if (item.itemId == R.id.makeaStatement){
            val intent = Intent(this, DescriptionsActivity::class.java)
            startActivity(intent)
            println("İKİNCİ ACTİVİTY'E GECTİ")
        }
        return super.onOptionsItemSelected(item)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun firebaseGetData(){
        db.collection("Descriptions").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener { snapshot, error->
            if (error != null){
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if (!snapshot.isEmpty){
                        val documents = snapshot.documents
                        descriptionsList.clear()
                        for (document in documents){
                            val sharedDescriptions = document.get("sharedDescriptions") as String?
                            val username = document.get("username") as String?
                            val imageUrl = document.get("imageUrl") as String?

                            var downloadShare = Share(sharedDescriptions, username, imageUrl)

                            descriptionsList.add(downloadShare)
                        }
                        recyclerviewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}