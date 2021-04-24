package com.example.movies.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.movies.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class LoginActivity: AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val emailTv by lazy { findViewById<TextView>(R.id.email) }
    private val loginBt by lazy { findViewById<SignInButton>(R.id.googleSign) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestProfile()
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignOptions)

        loginBt.setSize(SignInButton.SIZE_STANDARD)
        loginBt.setOnClickListener { signin(it) }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        setUserEmail(currentUser?.email ?: "Usuário desconectado")
    }

    fun signin(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 200) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GoogleSign", "firebaseAuthWithGoogle:" + account.idToken)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("GoogleSign", "Google sign in failed", e)
            }catch (e: Exception) {
                setUserEmail("Erro ao efetuar login")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("GoogleSign", "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    setUserEmail(user?.email ?: "Usuário desconectado")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("GoogleSign", "signInWithCredential:failure", task.exception)
                    setUserEmail("Erro ao efetuar login")
                }
            }
    }

    private fun setUserEmail(email: String) {
        emailTv.text = email
    }

    fun signout(view: View) {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        setUserEmail("Usuário desconectado")
    }
}