package pt.ipca.roomies.data.entities

import User

data class Habitation (
    val habitationId: String,
    val landlordId: String,
    val address: String,
    val city : Cities,
    val numberOfRooms : Int,
    val numberOfBathrooms : Int,
    val habitationType : HabitationType,
    val description : String,
    val habitationAmenities: HabitationAmenities,
    val petsAllowed : Boolean,
    val smokingPolicy : SmokingPolicies,
    val noiseLevel : NoiseLevels,
    val guestPolicy : GuestPolicies,
    val securityMeasures: SecurityMeasures,
    val tenants : List<User>,




)
enum class SecurityMeasures(s: String) {
    SECURITY_CAMERAS("Security cameras"),
    KEY_ENTRANCE("Locked entrance"),
    SECURITY_GUARD("Security guard"),
    CODED_ENTRANCE("Coded entrance"),
    CARDED_ENTRANCE("Carded entrance"),

    // Add other types as needed
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

enum class HabitationAmenities(s: String) {

    INTERNET("Internet"),
    PARKING("Parking"),
    KITCHEN("Kitchen"),
    LAUNDRY("Laundry"),


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