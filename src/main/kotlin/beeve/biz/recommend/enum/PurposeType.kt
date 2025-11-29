package beeve.biz.recommend.enum

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "목표")
enum class PurposeType {
    CARDIO_ENDURANCE,
    FAT_LOSS,
    MUSCLE_GAIN,
    GENERAL_FITNESS
}