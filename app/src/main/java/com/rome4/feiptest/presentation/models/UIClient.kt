package com.rome4.feiptest.presentation.models

import androidx.compose.runtime.Immutable
import com.rome4.feiptest.data.models.Client

@Immutable
data class UIClient(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String,
)

fun Client.mapToUI() = UIClient(
    id = id,
    name = name,
    email = email,
    photoUrl = photoUrl
)
