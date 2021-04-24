package com.example.movies.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movies.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

class FacebookLoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton

    private val loginManager = LoginManager.getInstance()


    private val emailTv by lazy { findViewById<TextView>(R.id.email) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook_login)

        firebaseAuth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()

        buttonFacebookLogin = findViewById(R.id.button_facebook)
        buttonFacebookLogin.setPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("facebook", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("facebook", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("facebook", "facebook:onError", error)
            }
        })
    }

    fun loginFacebook(){
        loginManager.logInWithReadPermissions(this, arrayListOf("email", "public_profile"))
        loginManager.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("facebook", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("facebook", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("facebook", "facebook:onError", error)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("facebook", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("facebook", "signInWithCredential:success")
                    val name = firebaseAuth.currentUser?.displayName
                    setUserName(name)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("facebook", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    setUserName("Usuário desconectado")
                }
            }
    }

    private fun setUserName(name: String?) {
        emailTv.text = name ?: ""
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        setUserName(currentUser?.displayName)
    }

    fun signinFace(view: View){
        loginFacebook()
    }

    fun signout(view: View) {
        firebaseAuth.signOut()
        loginManager.logOut()

        setUserName("Usuário desconectado")
    }
}