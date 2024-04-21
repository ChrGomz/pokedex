import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Pokemon(
    val id: Int,
    val name: String,
    val baseExperience: Int,
    val height: Int,
    val weight: Int,
    val types: List<PokemonType>,
    val abilities: List<PokemonAbility>,
    val moves: List<PokemonMove>,
    val sprites: PokemonSprites
)

@JsonClass(generateAdapter = true)
data class PokemonType(
    val slot: Int,
    val type: Type
)

@JsonClass(generateAdapter = true)
data class Type(
    val name: String
)

@JsonClass(generateAdapter = true)
data class PokemonAbility(
    val ability: Ability,
    val isHidden: Boolean,
    val slot: Int
)

@JsonClass(generateAdapter = true)
data class Ability(
    val name: String
)

@JsonClass(generateAdapter = true)
data class PokemonMove(
    val move: Move,
    val versionGroupDetails: List<VersionGroupDetail>
)

@JsonClass(generateAdapter = true)
data class Move(
    val name: String
)

@JsonClass(generateAdapter = true)
data class VersionGroupDetail(
    val levelLearnedAt: Int,
    val versionGroup: VersionGroup
)

@JsonClass(generateAdapter = true)
data class VersionGroup(
    val name: String
)

@JsonClass(generateAdapter = true)
data class PokemonSprites(
    val frontDefault: String?,
    val backDefault: String?
)