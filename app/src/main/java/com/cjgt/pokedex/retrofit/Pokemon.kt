import com.google.gson.annotations.SerializedName

class Pokemon {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String = ""

    @SerializedName("types")
    var types: List<Type> = emptyList()

    @SerializedName("sprites")
    var sprites: Sprites = Sprites("")
}

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String
)

data class Type(
    @SerializedName("slot")
    val slot: Int,
    @SerializedName("type")
    val type: TypeInfo
)

data class TypeInfo(
    @SerializedName("name")
    val name: String
)