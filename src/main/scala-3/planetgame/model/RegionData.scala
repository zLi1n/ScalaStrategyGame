package planetgame.model

object RegionData:
  val regions: Seq[Region] = Seq(
    /** Code assisted by AI (ChatGPT) */
    Region(
      id = "A",
      name = "Forest Edge",
      description = "A lush forest teeming with wildlife and natural resources.",
      baseFood = 0.4,
      baseWater = 0.3,
      baseSoil = 0.2,
      baseTech = 0.3,
      baseMood = 0.2
    ),
    Region(
      id = "B",
      name = "Desert Core",
      description = "A harsh desert with scarce water but rich mineral deposits.",
      baseFood = 0.2,
      baseWater = 0.2,
      baseSoil = 0.2,
      baseTech = 0.2,
      baseMood = 0.2
    ),
    Region(
      id = "C",
      name = "Ocean Rim",
      description = "A coastal region with abundant seafood and trade routes.",
      baseFood = 0.4,
      baseWater = 0.5,
      baseSoil = 0.2,
      baseTech = 0.3,
      baseMood = 0.1
    )
  )

  /** Retrieves a region by ID and adjusts stats based on difficulty */
  def getRegionWithDifficulty(id: String, difficulty: Difficulty): Region =
    val region = regions.find(_.id == id).getOrElse(
      throw new IllegalArgumentException(s"Region $id not found")
    )

    /** Difficulty multiplier: Easy=1.0, Medium=0.75, Hard=0.5 */
    val multiplier = difficulty match
      case Difficulty.Easy   => 1.0
      case Difficulty.Medium => 0.75
      case Difficulty.Hard   => 0.5

    /** Returns a copy of region with scaled attributes */
    region.copy(
      baseFood  = region.baseFood  * multiplier,
      baseWater = region.baseWater * multiplier,
      baseSoil  = region.baseSoil  * multiplier,
      baseTech  = region.baseTech  * multiplier,
      baseMood  = region.baseMood  * multiplier
    )
