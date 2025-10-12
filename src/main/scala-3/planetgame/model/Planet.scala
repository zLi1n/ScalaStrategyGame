package planetgame.model

case class Planet(name: String, difficulty: Difficulty)

object PlanetData:
  /** This code was generated with Chatgpt */
  val planets: Map[Difficulty, Planet] = Map(
    Difficulty.Easy   -> Planet("Gaialuna", Difficulty.Easy),
    Difficulty.Medium -> Planet("Pyronox", Difficulty.Medium),
    Difficulty.Hard   -> Planet("Xeravon", Difficulty.Hard)
  )

  def getPlanet(difficulty: Difficulty): Planet =
    planets.getOrElse(difficulty, Planet("Unknown", difficulty))
