package com.startup.graveyard.presentation.screens.sellerscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.presentation.viewmodels.AssetViewModel
import com.startup.graveyard.utils.uploadImageToFirebase

@Composable
fun CreateAssetScreen(
    assetViewModel: AssetViewModel,
    firebaseAuth: FirebaseAuth
) {
    val context = LocalContext.current
    val state by assetViewModel.createAssetState.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isNegotiable by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isNegotiable, onCheckedChange = { isNegotiable = it })
            Text("Negotiable")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pick Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                imageUri?.let {
                    uploadImageToFirebase(
                        userId =firebaseAuth.uid.toString() ,
                        context = context,
                        uri = it
                    ) { imageUrl ->

                        assetViewModel.createAsset(
                            CreateAssetRequestModel(
                                asset_type = "codebase",
                                title = title,
                                description = description,
                                price = price.toInt(),
                                image_url = imageUrl,
                                is_negotiable = isNegotiable,
                                is_sold = false,
                                user_uuid = ""
                            )
                        )
                    }
                }
            }
        ) {
            Text("Create Asset")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> CircularProgressIndicator()
            state.error.isNotEmpty() -> Text(state.error, color = Color.Red)
            state.data != null -> Text("Asset Created Successfully ")
        }
    }
}