package planetgame.model

case class GameState(
                      difficulty: Difficulty,
                      region: Option[Region] = None,
                      currentYear: Int = 1,
                      maxYears: Int = GameConfig.MaxYears,
                      food: Double = 0.0,
                      water: Double = 0.0,
                      soil: Double = 0.0,
                      tech: Double = 0.0,
                      mood: Double = 0.0,
                      history: Seq[ActionEffect] = Seq.empty
                    ) extends Effectable:

  // Determines if the player has won the game based on total average score
  def isWin: Boolean =
    val total = (food + water + soil + tech + mood) / 5.0 * 100 // convert average to percentage
    difficulty match
      case Difficulty.Easy   => total >= 60
      case Difficulty.Medium => total >= 70
      case Difficulty.Hard   => total >= 80

  // Updates the game state by applying deltas (increases or decreases) to the stats
  // Ensures each stat remains between 0.0 and 1.0
  def withStatsDelta(
                      foodDelta: Double,
                      waterDelta: Double,
                      soilDelta: Double,
                      techDelta: Double,
                      moodDelta: Double
                    ): GameState =
    copy(
      food = (food + foodDelta).max(0.0).min(1.0),
      water = (water + waterDelta).max(0.0).min(1.0),
      soil = (soil + soilDelta).max(0.0).min(1.0),
      tech = (tech + techDelta).max(0.0).min(1.0),
      mood = (mood + moodDelta).max(0.0).min(1.0)
    )

  // Applies an ActionEffect (which contains changes to multiple stats)
  // and appends it to the history for tracking
  def applyEffect(effect: ActionEffect): GameState =
    withStatsDelta(effect.food, effect.water, effect.soil, effect.tech, effect.mood).copy(
      history = history :+ effect
    )
