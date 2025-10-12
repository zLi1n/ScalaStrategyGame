package planetgame.view

import scalafx.animation.RotateTransition
import scalafx.util.Duration
import javafx.fxml.FXML
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import planetgame.{AudioManager, MainApp}
import scalafx.scene.transform.Rotate
import scalafx.Includes.*

class HomeController:
  @FXML private var planetImage: ImageView = _
  @FXML private var homeRoot: StackPane = _

  /** Initialize rotation animation for planet image */
  @FXML
  def initialize(): Unit =
    // Create a rotation animation (10 seconds per full rotation)
    val rotation = new RotateTransition(Duration(10000), planetImage)
    rotation.byAngle = 360
    rotation.cycleCount = RotateTransition.Indefinite
    rotation.setAxis(Rotate.ZAxis)
    rotation.play()

  /** Handle Start Game button click */
  @FXML
  def handleStartGame(): Unit =
    AudioManager.playEffect("/audio/click.mp3") // Play button click sound
    MainApp.showDifficultyLayout() // Switch to difficulty selection
