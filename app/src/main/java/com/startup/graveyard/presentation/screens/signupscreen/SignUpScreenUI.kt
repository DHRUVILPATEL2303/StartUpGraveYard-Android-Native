package com.startup.graveyard.presentation.screens.signupscreen

import android.graphics.Paint
import android.widget.Button
import android.widget.Space
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.presentation.viewmodels.AuthViewModel

@Composable
fun SignUpScreenUI(modifier: Modifier = Modifier, authViewModel: AuthViewModel = hiltViewModel(),firebaseAuth: FirebaseAuth) {

    val email = remember { mutableStateOf("") }
    val password = remember {
        mutableStateOf("")
    }
    val key= remember {
        mutableStateOf(0)
    }

    LaunchedEffect(key) {
        key.value++
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column() {


            OutlinedTextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                },

                )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                }
            )


            Button(
                onClick = {

                    authViewModel.createAccount(
                        CreateAccountModel(
                            email = email.value,
                            password = password.value
                        )
                    )

                    key.value++
                }
            ) {
                Text("Create Accoutn")
            }

            Text(text = firebaseAuth.currentUser?.email.toString())
        }



    }

}