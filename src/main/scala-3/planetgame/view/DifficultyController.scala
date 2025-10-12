package planetgame.view

import javafx.fxml.FXML
import javafx.scene.image.ImageView
import planetgame.{AudioManager, MainApp}
import planetgame.model.Difficulty
import scalafx.scene.control.Alert

@FXML
class DifficultyController:
  @FXML private var mediumLock: ImageView = _
  @FXML private var hardLock: ImageView = _

  @FXML
  def initialize(): Unit =
    // Hide lock if unlocked
    mediumLock.setVisible(!MainApp.playerProgress.unlockedMedium.value)
    hardLock.setVisible(!MainApp.playerProgress.unlockedHard.value)

  /** Handle Easy difficulty selection */
  @FXML def handleSelectEasy(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    MainApp.selectedDifficulty = Some(Difficulty.Easy)
    MainApp.showMapLayout()

  /** Handle Medium difficulty selection */
  @FXML
  def handleSelectMedium(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    if !MainApp.playerProgress.unlockedMedium.value then
      showLockedPopup("Medium difficulty is locked. Finish Easy first.")
    else
      MainApp.selectedDifficulty = Some(Difficulty.Medium)
    MainApp.showMapLayout()

  /** Handle Hard difficulty selection */
  @FXML
  def handleSelectHard(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    if !MainApp.playerProgress.unlockedHard.value then
      showLockedPopup("Hard difficulty is locked. Finish Medium first.")
    else
      MainApp.selectedDifficulty = Some(Difficulty.Hard)
    MainApp.showMapLayout()

  /** Show popup message when difficulty is locked */
  private def showLockedPopup(msg: String): Unit =
    val alert = new Alert(Alert.AlertType.Information)
    alert.setTitle("Difficulty Locked")
    alert.setHeaderText(null)
    alert.setContentText(msg)
    alert.showAndWait()

