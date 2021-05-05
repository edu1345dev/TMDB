package com.example.movies.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movies.R
import com.example.movies.model.FavoriteMovie
import com.example.movies.model.Subject
import com.example.movies.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FirebaseLoginActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    private val emailTv by lazy { findViewById<TextView>(R.id.emailTv) }
    private val emailEt by lazy { findViewById<EditText>(R.id.email) }
    private val passEt by lazy { findViewById<EditText>(R.id.pass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_login)

        firebaseAuth = Firebase.auth
        firebaseDatabase = Firebase.database
        if (firebaseAuth.currentUser != null){
            startActivity(Intent(this, FirebaseStorageActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        setUserEmail(currentUser?.email ?: "Usuário desconectado")
    }

    fun createUser(view: View) {
        val email = emailEt.text.toString()
        val pass = passEt.text.toString()

        createUserWithEmailPass(email, pass)
    }

    fun signin(view: View) {
        val email = emailEt.text.toString()
        val pass = passEt.text.toString()

        firebaseAuthWithEmailPass(email, pass)
    }

    private fun createUserWithEmailPass(email: String, pass: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                setUserEmail(user?.email ?: "Usuário desconectado")
            } else {
                setUserEmail(task.exception?.message!!)
            }
        }
    }

    private fun firebaseAuthWithEmailPass(email: String, pass: String) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                setUserEmail(user?.email ?: "Usuário desconectado")
                startActivity(Intent(this, FirebaseStorageActivity::class.java))
            } else {
                setUserEmail(task.exception?.message!!)
            }
        }
    }

    private fun setUserEmail(email: String) {
        emailTv.text = email
    }

    fun signout(view: View) {
        firebaseAuth.signOut()
        setUserEmail("Usuário desconectado")
    }

    fun addUser(view: View) {
        firebaseAuth.currentUser?.let { user ->
            val subject = Subject("Firebase Database")
            val userDb = User(
                user.email ?: "",
                "Jose Santos",
                subject,
                FavoriteMovie(listOf("Interestelar", "Run"))
            )

            val reference = firebaseDatabase.getReference("users")

            reference
                .child(user.uid)
                .setValue(userDb)
            reference.child(user.uid).child("favorite_movies")
                .setValue(FavoriteMovie(listOf("Interestelar", "Run")))

            reference.child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    handleUser(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    error
                }
            })
        }
    }

    private fun handleUser(snapshot: DataSnapshot) {
        val user = snapshot.getValue(User::class.java)
        user.toString()
    }

    fun handleUserData(snapshot: DataSnapshot) {
        firebaseAuth.currentUser?.let {
            val uid = it.uid
            val data: HashMap<String, String> = snapshot.value as HashMap<String, String>
            val user: HashMap<String, String> = data[uid] as HashMap<String, String>
            val email = user["email"] ?: ""
            val name = user["name"] ?: ""
            val subject = (user["subject"] as HashMap<String, String>)["type"] ?: ""

            val userMapped =
                User(email = email, name = name, subject = Subject((subject)), movies = null)

            Toast.makeText(this, userMapped.toString(), Toast.LENGTH_LONG).show()
        }
    }
}