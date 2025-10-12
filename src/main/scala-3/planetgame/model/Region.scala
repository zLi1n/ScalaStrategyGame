package planetgame.model

case class Region(
                   id: String,
                   name: String,
                   description: String,
                   baseFood: Double,
                   baseWater: Double,
                   baseSoil: Double,
                   baseTech: Double,
                   baseMood: Double
                 ) extends GameEntity
