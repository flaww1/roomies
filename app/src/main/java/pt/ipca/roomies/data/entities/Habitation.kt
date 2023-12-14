package pt.ipca.roomies.data.entities

import User

data class Habitation(
    var habitationId: String? , // Set it to String? (nullable) to allow Firestore to generate the ID
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
    val tenants: List<User> = listOf(),
) {
    // No-argument constructor for Firebase Firestore deserialization
    constructor() : this("", "", "", Cities.AVEIRO, 0, 0, HabitationType.APARTMENT, "", listOf(), listOf(),
        false, SmokingPolicies.SMOKING_NOT_ALLOWED, NoiseLevels.QUIET, GuestPolicies.GUESTS_NOT_ALLOWED, listOf())
}



enum class HabitationAmenities {
    INTERNET,
    PARKING,
    KITCHEN,
    LAUNDRY
}

enum class SecurityMeasures {
    SECURITY_CAMERAS,
    KEY_ENTRANCE,
    SECURITY_GUARD,
    CODED_ENTRANCE,
    CARDED_ENTRANCE
}


enum class GuestPolicies(s: String) {
    GUESTS_ALLOWED("Guests allowed"),
    GUESTS_NOT_ALLOWED("Guests not allowed"),
    GUESTS_ALLOWED_WITH_RESTRICTIONS("Guests allowed with restrictions"),
    // Add other types as needed
}
enum class NoiseLevels(s: String) {
    QUIET("Quiet"),
    MODERATE("Moderate"),
    LOUD("Loud"),
    // Add other types as needed
}
enum class SmokingPolicies(s: String) {
    SMOKING_ALLOWED("Smoking allowed"),
    SMOKING_NOT_ALLOWED("Smoking not allowed"),
    SMOKING_ALLOWED_OUTSIDE_ONLY("Smoking allowed outside only"),
    // Add other types as needed
}


enum class HabitationType(s: String) {
    APARTMENT("Apartment"),
    HOUSE("House"),
    ROOM("Room"),
    STUDIO("Studio"),
    // Add other types as needed
}
enum class Cities(s: String) {
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
    ILHA_DA_MADEIRA("Ilha da Madeira"),

}