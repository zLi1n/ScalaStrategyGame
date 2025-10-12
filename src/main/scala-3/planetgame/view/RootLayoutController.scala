package planetgame.view

import javafx.fxml.FXML
import planetgame.MainApp
import scalafx.application.Platform
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.layout.Region
import scalafx.scene.control.Alert.AlertType

class RootLayoutController:
  /** Handle restart game request */
  @FXML
  def handleRestart(): Unit =
    if MainApp.selectedDifficulty.isDefined && MainApp.selectedRegion.isDefined then
      val alert = new Alert(AlertType.Confirmation) {
        title = "Restart Game"
        headerText = None
        contentText = "Are you sure you want to restart the current game?"
      }

      alert.showAndWait() match
        case Some(ButtonType.OK) =>
          MainApp.resetGame()
          MainApp.showGameMainLayout()
        case _ =>
    else
      val alert = new Alert(AlertType.Information) {
        title = "No Game Running"
        headerText = None
        contentText = "There is no active game to restart."
      }
      alert.showAndWait()

  /** Handle start a new game request */
  @FXML
  def handleNewGame(): Unit =
    val alert = new Alert(Alert.AlertType.Confirmation){
      title = "Confirm New Game"
      headerText = None
      contentText = "Are you sure you want to start a new game? Current progress will be lost."
    }

    alert.showAndWait() match
      case Some(ButtonType.OK) =>
        MainApp.resetGame()
        MainApp.showHomeLayout()
      case _ =>

  /** Handle exit game request */
  @FXML
  def handleExit(): Unit =
    val alert = new Alert(Alert.AlertType.Confirmation){
      title = "Exit Game"
      headerText = None
      contentText = "Are you sure you want to exit the game?"
    }

    alert.showAndWait() match
      case Some(ButtonType.OK) =>
        Platform.exit()
      case _ =>

  /** Show About game popup */
  @FXML
  def handleAbout(): Unit =
    val content =
      """Grow Your Own Planet
        |Version 1.0
        |
        |A turn-based strategy game where you must restore balance to your chosen region.
        |Manage your resources — Food, Water, Soil, Technology, and Mood — to survive.
        |
        |© 2025 PlanetGame Studios
        |""".stripMargin

    val alert = new Alert(Alert.AlertType.Information)
    alert.setTitle("About Grow Your Own Planet")
    alert.setHeaderText("About the Game")
    alert.setContentText(content)
    alert.showAndWait()

  /** Show How To Play guide */
  @FXML
  def handleHowToPlay(): Unit =
    val content =
      """HOW TO PLAY
        |
        |1. Select a difficulty: Easy, Medium, or Hard.
        |2. Choose one of the three regions, each with different starting stats.
        |3. Each year, pick actions to improve resources:
        |   - Food
        |   - Water
        |   - Soil
        |   - Technology
        |   - Mood
        |4. Watch out for events — they may help or harm your progress.
        |5. Maintain all resources above the target value when the years run out to win.
        |
        |TIPS:
        |- Balance your choices, don't let any one stat drop too low.
        |- Mood can boost or reduce other resource effects.
        |- Higher difficulty means higher win thresholds.
        |""".stripMargin

    val alert = new Alert(Alert.AlertType.Information)
    alert.setTitle("How To Play")
    alert.setHeaderText("Grow Your Own Planet Guide")
    alert.setContentText(content)
    alert.getDialogPane.setMinHeight(Region.USE_PREF_SIZE)
    alert.showAndWait()
