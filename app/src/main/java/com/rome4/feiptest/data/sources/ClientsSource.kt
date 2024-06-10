package com.rome4.feiptest.data.sources

import com.rome4.feiptest.data.models.Client
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface ClientsSource {

    @GET(" ")
    suspend fun getClients(
        @Query("results") limit: Int = 15,
        @Query("seed") seed: String = "abc",
        @Query("inc") include: String = "name,email,picture"
    ): GetClientsResponse
}

@Serializable
data class GetClientsResponse(
    @SerialName("results") val results: List<GetClientsResponseClient>
) {
    fun mapToListOfClients(): List<Client> {
        return results.map {
            Client(
                id = it.email,
                name = "${it.name.first} ${it.name.last}",
                email = it.email,
                photoUrl = it.picture.image
            )
        }
    }
}

@Serializable
data class GetClientsResponseClient(
    @SerialName("name") val name: GetClientsResponseName,
    @SerialName("email") val email: String,
    @SerialName("picture") val picture: GetClientsResponseImages,
)

@Serializable
data class GetClientsResponseName(
    @SerialName("first") val first: String,
    @SerialName("last") val last: String,
)


@Serializable
data class GetClientsResponseImages(
    @SerialName("large") val image: String,
)