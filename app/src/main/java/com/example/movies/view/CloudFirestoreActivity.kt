package com.example.movies.view

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
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CloudFirestoreActivity : AppCompatActivity() {
    private var firestoreDb = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    private val emailTv by lazy { findViewById<TextView>(R.id.emailTv) }
    private val emailEt by lazy { findViewById<EditText>(R.id.email) }
    private val passEt by lazy { findViewById<EditText>(R.id.pass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_login)

        firebaseAuth = FirebaseAuth.getInstance()
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

            firestoreDb.collection("users")
                .document(user.uid)
                .set(userDb)
                .addOnSuccessListener {
                    it
                }.addOnFailureListener {
                    it
                }
        }
    }

    fun getuser(view: View) {
        firebaseAuth.currentUser?.let { user ->
            firestoreDb.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)
                    user.toString()
                }.addOnFailureListener {
                    it
                }
        }

//        firebaseAuth.currentUser?.let { user ->
//            firestoreDb.collection("users")
//                .limit(2)
//                .orderBy("email")
//                .get()
//                .addOnSuccessListener {
//                    it.documents[0].toObject(User::class.java)
//                }.addOnFailureListener {
//                    it
//                }
//        }
    }
}