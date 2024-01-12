package pt.ipca.roomies.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.Exclude
import pt.ipca.roomies.data.entities.Converters.*
import javax.annotation.Nonnull

//representacao de tabela na base de dados, @primarykey,TypeConverters...
//typeconvereters permite a conver~sao e recuperacao de tipo de dados, de forma a que o room compreenda,pode  sre aplicado a classe para todos os campos ou metodo soemete a um 
@Entity(tableName = "habitations")
@TypeConverters(HabitationUserListConverter::class,
    HabitationAmenitiesListConverter::class,
    SecurityMeasuresListConverter::class,
    CitiesConverter::class,
    HabitationTypeConverter::class,
    NoiseLevelsConverter::class,
    SmokingPoliciesConverter::class,
    GuestPoliciesConverter::class)
data class Habitation(
    @PrimaryKey
    var habitationId: String = "",
    val landlordId: String = "",
    val address: String = "",
    val city: Cities = Cities.AVEIRO,
    val numberOfRooms: Int = 0,
    val numberOfBathrooms: Int = 0,
    val habitationType: HabitationType = HabitationType.APARTMENT,
    val description: String = "",
    val habitationAmenities: List<HabitationAmenities> = listOf(),
    val securityMeasures: List<SecurityMeasures> = listOf(),
    val petsAllowed: Boolean = false,
    val smokingPolicy: SmokingPolicies = SmokingPolicies.SMOKING_NOT_ALLOWED,
    val noiseLevel: NoiseLevels = NoiseLevels.QUIET,
    val guestPolicy: GuestPolicies = GuestPolicies.GUESTS_NOT_ALLOWED,
    val tenants: List<User> = listOf()
) {

}


@TypeConverters(HabitationTypeConverter::class)
enum class HabitationType(val type: String) {
    APARTMENT("Apartment"),
    HOUSE("House"),
    ROOM("Room"),
    STUDIO("Studio"),
    // Add other types as needed
}

@TypeConverters(CitiesConverter::class)
enum class Cities(val city: String) {
    AVEIRO("Aveiro"),
    BEJA("Beja"),
    BRAGA("Braga"),
    BRAGANCA("Bragança"),
    CASTELO_BRANCO("Castelo Branco"),
    COIMBRA("Coimbra"),
    EVORA("Évora"),
    FARO("Faro"),
    GUARDA("Guarda"),
    LEIRIA("Leiria"),
    LISBOA("Lisboa"),
    PORTALEGRE("Portalegre"),
    PORTO("Porto"),
    SANTAREM("Santarém"),
    SETUBAL("Setúbal"),
    VIANA_DO_CASTELO("Viana do Castelo"),
    VILA_REAL("Vila Real"),
    VISEU("Viseu"),
    ILHA_DA_MADEIRA("Ilha da Madeira")
}

@TypeConverters(HabitationAmenitiesConverter::class)
enum class HabitationAmenities(val amenity: String) {
    INTERNET("Internet"),
    PARKING("Parking"),
    KITCHEN("Kitchen"),
    LAUNDRY("Laundry")
}

@TypeConverters(SecurityMeasuresConverter::class)
enum class SecurityMeasures(val measure: String) {
    SECURITY_CAMERAS("Security Cameras"),
    KEY_ENTRANCE("Key Entrance"),
    SECURITY_GUARD("Security Guard"),
    CODED_ENTRANCE("Coded Entrance"),
    CARDED_ENTRANCE("Carded Entrance")
}

@TypeConverters(GuestPoliciesConverter::class)
enum class GuestPolicies(val policy: String) {
    GUESTS_ALLOWED("Guests allowed"),
    GUESTS_NOT_ALLOWED("Guests not allowed"),
    GUESTS_ALLOWED_WITH_RESTRICTIONS("Guests allowed with restrictions")
}

@TypeConverters(NoiseLevelsConverter::class)
enum class NoiseLevels(val level: String) {
    QUIET("Quiet"),
    MODERATE("Moderate"),
    LOUD("Loud")
}

@TypeConverters(SmokingPoliciesConverter::class)
enum class SmokingPolicies(val policy: String) {
    SMOKING_ALLOWED("Smoking allowed"),
    SMOKING_NOT_ALLOWED("Smoking not allowed"),
    SMOKING_ALLOWED_OUTSIDE_ONLY("Smoking allowed outside only")
}
