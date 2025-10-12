package planetgame.model

case class GameResult(
                       difficulty: Difficulty,
                       regionId: String,
                       yearsPlayed: Int,
                       win: Boolean,
                       finalFood: Double,
                       finalWater: Double,
                       finalSoil: Double,
                       finalTech: Double,
                       finalMood: Double
                     )
