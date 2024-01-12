package pt.ipca.roomies.data.entities

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson


class Converters {
    object StringListConverter {
        @TypeConverter
        fun fromStringList(value: List<String>): String {
            val gson = Gson()
            return gson.toJson(value)
        }

        @TypeConverter
        fun toStringList(value: String): List<String> {
            val gson = Gson()
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(value, type)
        }

    }

    class HabitationTypeConverter {
        @TypeConverter
        fun fromHabitationType(value: String?): HabitationType? {
            return value?.let { type ->
                when (type) {
                    "Apartment" -> HabitationType.APARTMENT
                    "House" -> HabitationType.HOUSE
                    "Room" -> HabitationType.ROOM
                    "Studio" -> HabitationType.STUDIO
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toHabitationType(value: HabitationType?): String? {
            return value?.type
        }
    }

    class CitiesConverter {
        @TypeConverter
        fun fromCities(value: String?): Cities? {
            return value?.let { city ->
                when (city) {
                    "Aveiro" -> Cities.AVEIRO
                    "Beja" -> Cities.BEJA
                    "Braga" -> Cities.BRAGA
                    "Bragança" -> Cities.BRAGANCA
                    "Castelo Branco" -> Cities.CASTELO_BRANCO
                    "Coimbra" -> Cities.COIMBRA
                    "Évora" -> Cities.EVORA
                    "Faro" -> Cities.FARO
                    "Guarda" -> Cities.GUARDA
                    "Leiria" -> Cities.LEIRIA
                    "Lisboa" -> Cities.LISBOA
                    "Portalegre" -> Cities.PORTALEGRE
                    "Porto" -> Cities.PORTO
                    "Santarém" -> Cities.SANTAREM
                    "Setúbal" -> Cities.SETUBAL
                    "Viana do Castelo" -> Cities.VIANA_DO_CASTELO
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toCities(value: Cities?): String? {
            return value?.city
        }
    }

    class HabitationUserListConverter {
        @TypeConverter
        fun fromUserList(users: List<User>): String {
            val gson = Gson()
            return gson.toJson(users)
        }

        @TypeConverter
        fun toUserList(usersString: String): List<User> {
            val gson = Gson()
            val type = object : TypeToken<List<User>>() {}.type
            return gson.fromJson(usersString, type)
        }
    }

    class HabitationAmenitiesConverter {
        @TypeConverter
        fun fromHabitationAmenities(value: String?): HabitationAmenities? {
            return value?.let { amenity ->
                when (amenity) {
                    "Internet" -> HabitationAmenities.INTERNET
                    "Parking" -> HabitationAmenities.PARKING
                    "Kitchen" -> HabitationAmenities.KITCHEN
                    "Laundry" -> HabitationAmenities.LAUNDRY
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toHabitationAmenities(value: HabitationAmenities?): String? {
            return value?.amenity
        }
    }


    class SecurityMeasuresConverter {
        @TypeConverter
        fun fromSecurityMeasures(value: String?): SecurityMeasures? {
            return value?.let { measure ->
                when (measure) {
                    "Security Cameras" -> SecurityMeasures.SECURITY_CAMERAS
                    "Key Entrance" -> SecurityMeasures.KEY_ENTRANCE
                    "Security Guard" -> SecurityMeasures.SECURITY_GUARD
                    "Coded Entrance" -> SecurityMeasures.CODED_ENTRANCE
                    "Carded Entrance" -> SecurityMeasures.CARDED_ENTRANCE
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toSecurityMeasures(value: SecurityMeasures?): String? {
            return value?.measure
        }
    }

    class GuestPoliciesConverter {
        @TypeConverter
        fun fromGuestPolicies(value: String?): GuestPolicies? {
            return value?.let { policy ->
                when (policy) {
                    "Guests allowed" -> GuestPolicies.GUESTS_ALLOWED
                    "Guests not allowed" -> GuestPolicies.GUESTS_NOT_ALLOWED
                    "Guests allowed with restrictions" -> GuestPolicies.GUESTS_ALLOWED_WITH_RESTRICTIONS
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toGuestPolicies(value: GuestPolicies?): String? {
            return value?.policy
        }
    }

    class NoiseLevelsConverter {
        @TypeConverter
        fun fromNoiseLevels(value: String?): NoiseLevels? {
            return value?.let { level ->
                when (level) {
                    "Quiet" -> NoiseLevels.QUIET
                    "Moderate" -> NoiseLevels.MODERATE
                    "Loud" -> NoiseLevels.LOUD
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toNoiseLevels(value: NoiseLevels?): String? {
            return value?.level
        }
    }

    class SmokingPoliciesConverter {
        @TypeConverter
        fun fromSmokingPolicies(value: String?): SmokingPolicies? {
            return value?.let { policy ->
                when (policy) {
                    "Smoking allowed" -> SmokingPolicies.SMOKING_ALLOWED
                    "Smoking not allowed" -> SmokingPolicies.SMOKING_NOT_ALLOWED
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toSmokingPolicies(value: SmokingPolicies?): String? {
            return value?.policy
        }
    }


    class OccupationConverter {
        @TypeConverter
        fun fromOccupation(value: String?): Occupation? {
            return value?.let { occupation ->
                when (occupation) {
                    "STUDENT" -> Occupation.STUDENT
                    "STUDENT_WORKER" -> Occupation.STUDENT_WORKER
                    "WORKER" -> Occupation.WORKER
                    "OTHER" -> Occupation.OTHER
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toOccupation(value: Occupation?): String? {
            return value?.name
        }
    }

    class GenderConverter {
        @TypeConverter
        fun fromGender(value: String?): Gender? {
            return value?.let { gender ->
                when (gender) {
                    "MALE" -> Gender.MALE
                    "FEMALE" -> Gender.FEMALE
                    "NON_BINARY" -> Gender.NON_BINARY
                    "OTHER" -> Gender.OTHER
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toGender(value: Gender?): String? {
            return value?.name
        }
    }

    class RoomTypeConverter {
        @TypeConverter
        fun fromRoomType(value: String?): RoomType? {
            return value?.let { type ->
                when (type) {
                    "Single" -> RoomType.SINGLE
                    "Double" -> RoomType.DOUBLE
                    "Triple" -> RoomType.TRIPLE
                    "Quadruple" -> RoomType.QUADRUPLE
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toRoomType(value: RoomType?): String? {
            return value?.type
        }
    }

    class RoomAmenitiesConverter {
        @TypeConverter
        fun fromRoomAmenities(value: String?): RoomAmenities? {
            return value?.let { amenity ->
                when (amenity) {
                    "Air Conditioning" -> RoomAmenities.AIR_CONDITIONING
                    "Heating" -> RoomAmenities.HEATING
                    "Balcony" -> RoomAmenities.BALCONY
                    "Private Bathroom" -> RoomAmenities.PRIVATE_BATHROOM
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toRoomAmenities(value: RoomAmenities?): String? {
            return value?.amenity
        }
    }

    class RoomStatusConverter {
        @TypeConverter
        fun fromRoomStatus(value: String?): RoomStatus? {
            return value?.let { status ->
                when (status) {
                    "Available" -> RoomStatus.AVAILABLE
                    "Unavailable" -> RoomStatus.UNAVAILABLE
                    "Pending" -> RoomStatus.PENDING
                    "Rented" -> RoomStatus.RENTED
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toRoomStatus(value: RoomStatus?): String? {
            return value?.status
        }
    }

    class RoomSizeConverter {
        @TypeConverter
        fun fromRoomSize(value: String?): RoomSize? {
            return value?.let { size ->
                when (size) {
                    "Small" -> RoomSize.SMALL
                    "Medium" -> RoomSize.MEDIUM
                    "Large" -> RoomSize.LARGE
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toRoomSize(value: RoomSize?): String? {
            return value?.size
        }
    }

    class LeaseDurationConverter {
        @TypeConverter
        fun fromLeaseDuration(value: String?): LeaseDuration? {
            return value?.let { duration ->
                when (duration) {
                    "1 Month" -> LeaseDuration.ONE_MONTH
                    "3 Months" -> LeaseDuration.THREE_MONTHS
                    "6 Months" -> LeaseDuration.SIX_MONTHS
                    "1 Year" -> LeaseDuration.ONE_YEAR
                    else -> null
                }
            }
        }

        @TypeConverter
        fun toLeaseDuration(value: LeaseDuration?): String? {
            return value?.duration
        }
    }


    @TypeConverter
    fun fromLeaseDuration(value: LeaseDuration): String {
        return value.duration
    }

    @TypeConverter
    fun toLeaseDuration(value: String): LeaseDuration {
        return when (value) {
            "1 Month" -> LeaseDuration.ONE_MONTH
            "3 Months" -> LeaseDuration.THREE_MONTHS
            "6 Months" -> LeaseDuration.SIX_MONTHS
            "1 Year" -> LeaseDuration.ONE_YEAR
            else -> throw IllegalArgumentException("Unsupported LeaseDuration: $value")
        }
    }

    @TypeConverter
    fun fromRoomType(value: RoomType): String {
        return value.type
    }

    @TypeConverter
    fun toRoomType(value: String): RoomType {
        return when (value) {
            "Single" -> RoomType.SINGLE
            "Double" -> RoomType.DOUBLE
            "Triple" -> RoomType.TRIPLE
            "Quadruple" -> RoomType.QUADRUPLE
            else -> throw IllegalArgumentException("Unsupported RoomType: $value")
        }
    }

    @TypeConverter
    fun fromRoomStatus(value: RoomStatus): String {
        return value.status
    }

    @TypeConverter
    fun toRoomStatus(value: String): RoomStatus {
        return when (value) {
            "Available" -> RoomStatus.AVAILABLE
            "Unavailable" -> RoomStatus.UNAVAILABLE
            "Pending" -> RoomStatus.PENDING
            "Rented" -> RoomStatus.RENTED
            else -> throw IllegalArgumentException("Unsupported RoomStatus: $value")
        }
    }

    @TypeConverter
    fun fromRoomSize(value: RoomSize): String {
        return value.size
    }

    @TypeConverter
    fun toRoomSize(value: String): RoomSize {
        return when (value) {
            "Small" -> RoomSize.SMALL
            "Medium" -> RoomSize.MEDIUM
            "Large" -> RoomSize.LARGE
            else -> throw IllegalArgumentException("Unsupported RoomSize: $value")
        }
    }

    class RoomAmenitiesListConverter {
        @TypeConverter
        fun fromRoomAmenitiesList(value: List<RoomAmenities>): String {
            val gson = Gson()
            return gson.toJson(value)
        }

        @TypeConverter
        fun toRoomAmenitiesList(value: String): List<RoomAmenities> {
            val gson = Gson()
            val type = object : TypeToken<List<RoomAmenities>>() {}.type
            return gson.fromJson(value, type)
        }
    }

    class SecurityMeasuresListConverter {
        @TypeConverter
        fun fromSecurityMeasuresList(value: List<SecurityMeasures>): String {
            val gson = Gson()
            return gson.toJson(value)
        }

        @TypeConverter
        fun toSecurityMeasuresList(value: String): List<SecurityMeasures> {
            val gson = Gson()
            val type = object : TypeToken<List<SecurityMeasures>>() {}.type
            return gson.fromJson(value, type)
        }
    }

    class  HabitationAmenitiesListConverter {
        @TypeConverter
        fun fromHabitationAmenitiesList(value: List<HabitationAmenities>): String {
            val gson = Gson()
            return gson.toJson(value)
        }

        @TypeConverter
        fun toHabitationAmenitiesList(value: String): List<HabitationAmenities> {
            val gson = Gson()
            val type = object : TypeToken<List<HabitationAmenities>>() {}.type
            return gson.fromJson(value, type)
        }
    }


}

