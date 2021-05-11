package com.example.movies.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.movies.R
import com.example.movies.model.User
import com.example.projectapp.FileHelper
import com.example.projectapp.PermissionsHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.File
import java.util.regex.Pattern

class FirebaseStorageActivity : AppCompatActivity() {

    companion object {
        const val FILE_AUTHORITY = "com.example.tmdb"
    }

    private var imageUri: Uri? = null
    private val firebaseAuth = Firebase.auth
    private val firebaseStorage = Firebase.storage
    private var userAuth: FirebaseUser? = null

    val picture by lazy { findViewById<ImageView>(R.id.picture) }

    var fileShare: File? = null
    private lateinit var permissionsHelper: PermissionsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_storage)
        permissionsHelper = PermissionsHelper(this)
        userAuth = firebaseAuth.currentUser
        getCurrentUserPicture()
    }


    private fun getCurrentUserPicture() {
        userAuth?.let { user ->
            firebaseStorage.getReference("uploads")
                .child(user.uid)
                .downloadUrl
                .addOnSuccessListener { url ->
                    Toast.makeText(this, "Picture url downloaded with success", Toast.LENGTH_LONG)
                        .show()
                    Picasso.get().load(url).into(picture)
                }.addOnFailureListener {
                    Toast.makeText(this, "Error downloading: ${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }

    fun upload(view: View) {
        imageUri?.let { image ->
            userAuth?.let { user ->
                firebaseStorage.getReference("uploads")
                    .child(user.uid)
                    .putFile(image)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Picture uploaded with success.", Toast.LENGTH_LONG)
                            .show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error uploadind: ${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }.addOnProgressListener {
                        it
                    }
            }
        }
    }

    fun takePhoto(view: View) {
        val permissions =
            listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionsHelper.requestAllPermission(permissions)) {
            openChooser()
        }
    }

    private fun openChooser() {
        val intentList = mutableListOf<Intent>()

        //takePhotoIntent
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = FileHelper.createFileInStorage(this)
        val uri = FileProvider.getUriForFile(
            this,
            FILE_AUTHORITY,
            file!!
        )

        fileShare = file

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)


        //pickImageIntent
        val pickIntent = Intent()
        pickIntent.type = "image/*"
        pickIntent.action = Intent.ACTION_GET_CONTENT

        //Adiciona na lista de intents
        intentList.add(pickIntent)
        intentList.add(takePhotoIntent)

        val chooserIntent = Intent.createChooser(intentList[0], "Escolha como tirar a fotografia:")
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            intentList.toTypedArray()
        )

        startActivityForResult(chooserIntent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (isIntentFromCamera(requestCode, resultCode, intent)) {
            val image = BitmapFactory.decodeFile(fileShare?.path)
            picture.background = null
            picture.setImageBitmap(image)

            imageUri = FileProvider.getUriForFile(
                applicationContext,
                FILE_AUTHORITY,
                fileShare!!
            )
        } else if (isIntentFromFiles(requestCode, resultCode, intent)) {
            val pic = intent?.data as Uri
            imageUri = pic
            picture.background = null
            picture.setImageURI(pic)
        }
    }

    private fun isIntentFromFiles(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) = requestCode == 200 && resultCode == Activity.RESULT_OK && intent?.data != null

    private fun isIntentFromCamera(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) =
        requestCode == 200 && resultCode == Activity.RESULT_OK && (intent == null || intent.extras == null) && intent?.data == null
}