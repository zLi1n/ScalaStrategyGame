package planetgame.view

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import planetgame.{AudioManager, MainApp}
import scalafx.application.Platform
import scalafx.scene.control.{Alert, ButtonType}

class EndGameController:

  @FXML private var endTitle: Label = _
  @FXML private var rootPane: AnchorPane = _

  // Final stat labels
  @FXML private var foodLabel: Label = _
  @FXML private var waterLabel: Label = _
  @FXML private var soilLabel: Label = _
  @FXML private var techLabel: Label = _
  @FXML private var moodLabel: Label = _

  @FXML
  def initialize(): Unit =
    updateUI()

  /** Update UI based on GameState */
  private def updateUI(): Unit =
    val gs = MainApp.gameState

    // Remove old classes (in case of replay)
    rootPane.getStyleClass.removeAll("win-background", "lose-background")

    if gs.isWin then
      AudioManager.playEffect("/audio/victory.mp3")
      rootPane.getStyleClass.add("win-background")
      endTitle.setText("Mission Complete!")
    else
      AudioManager.playEffect("/audio/losing.mp3")
      rootPane.getStyleClass.add("lose-background")
      endTitle.setText("Mission Failed!")

    // Final stats from GameState
    foodLabel.setText((gs.food * 100).toInt.toString)
    waterLabel.setText((gs.water * 100).toInt.toString)
    soilLabel.setText((gs.soil * 100).toInt.toString)
    techLabel.setText((gs.tech * 100).toInt.toString)
    moodLabel.setText((gs.mood * 100).toInt.toString)

  /** Restart game */
  @FXML
  def handlePlayAgain(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    MainApp.resetGame()
    MainApp.showGameMainLayout()

  /** Go back to home screen */
  @FXML
  def handleBackToHome(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    MainApp.showHomeLayout()

  /** Exit application */
  @FXML
  def handleExit(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    val alert = new Alert(Alert.AlertType.Confirmation){
      title = "Exit Game"
      headerText = None
      contentText = "Are you sure you want to exit the game?"
    }

    alert.showAndWait() match
      case Some(ButtonType.OK) =>
        Platform.exit()
      case _ =>
