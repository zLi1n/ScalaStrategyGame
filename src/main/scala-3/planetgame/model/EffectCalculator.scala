package planetgame.model

object EffectCalculator:
  def previewEffect(effect: ActionEffect, gameState: GameState): ActionEffect =
    val moodPercent = gameState.mood

    // Determine mood modifier based on thresholds
    val moodModifier =
      if moodPercent >= GameConfig.MoodHighThreshold then 1.2
      else if moodPercent < GameConfig.MoodLowThreshold then 0.8
      else 1.0

    // Adjust mood change depending on effect type
    val moodChange =
      if effect.mood > 0 then math.min(1.0, effect.mood * moodModifier)
      else if effect.food > 0 || effect.water > 0 || effect.soil > 0 || effect.tech > 0 then -0.05
      else 0.0

    // Apply mood modifier and clamp values to valid ranges
    effect.copy(
      food = math.min(1.0, effect.food * moodModifier),
      water = math.min(1.0, effect.water * moodModifier),
      soil = math.min(1.0, effect.soil * moodModifier),
      tech = math.min(1.0, effect.tech * moodModifier),
      mood = math.max(-1.0, math.min(1.0, moodChange))
    )
