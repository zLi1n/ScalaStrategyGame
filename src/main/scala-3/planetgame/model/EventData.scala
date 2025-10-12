package planetgame.model

object EventData:
  /** Code generated with Chatgpt */
  val events: Seq[(ActionEffect => Boolean, (String, String))] = Seq(
    (a => a.mood > 0,   ("Cultural Uplift!", "Planetary celebration doubled the mood gains!")),
    (a => a.food > 0,   ("Fertile Surprise!", "Unexpected crop boost increases food yield!")),
    (a => a.water > 0,  ("Heavy Rainfall!", "Sudden rain has doubled your water collection!")),
    (a => a.soil > 0,   ("Rich Soil Found!", "Mineral-rich soil doubles your soil recovery!")),
    (a => a.tech > 0,   ("Tech Breakthrough!", "Your tech research made faster progress!"))
  )

  /** Returns a random event (if any) that matches the given action’s effect */
  def getEventForAction(action: ActionEffect): Option[(String, String)] =
    val chance = scala.util.Random.nextDouble() // random chance threshold for this action
    if scala.util.Random.nextDouble() < chance then
      events.collectFirst { case (condition, result) if condition(action) => result }
    else
      None
