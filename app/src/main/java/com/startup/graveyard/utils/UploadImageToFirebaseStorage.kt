package com.startup.graveyard.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImageToFirebase(
    userId : String,
    context: Context,
    uri: Uri,
    onSuccess: (String) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance()
        .reference
        .child("assets/${userId}.jpg")

    storageRef.putFile(uri)
        .continueWithTask { storageRef.downloadUrl }
        .addOnSuccessListener {
            onSuccess(it.toString())
        }
        .addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
}