package planetgame.model

case class ActionEffect(
                         title: String,
                         description: String,
                         imagePath: String,
                         imagePaths: Seq[String] = Seq(),
                         food: Double = 0.0,
                         water: Double = 0.0,
                         soil: Double = 0.0,
                         tech: Double = 0.0,
                         mood: Double = 0.0
                       )
