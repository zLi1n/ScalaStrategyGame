package planetgame.model

object ActionLibrary:
  val growCrops = GrowCrops()
  val irrigateFields = IrrigateFields()
  val restoreSoil = RestoreSoil()
  val upgradeTech = UpgradeTech()
  val holdFestival = HoldFestival()

  val allActions: Seq[Action] = Seq(
    growCrops,
    irrigateFields,
    restoreSoil,
    upgradeTech,
    holdFestival
  )

  /** Finds an action by its title (case-insensitive) */
  def findByTitle(title: String): Option[Action] =
    allActions.find(_.effect.title.equalsIgnoreCase(title))
