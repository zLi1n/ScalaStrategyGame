package planetgame.view

import javafx.fxml.FXML
import javafx.scene.control.{Label, ProgressBar}
import javafx.scene.image.ImageView
import javafx.scene.layout.{AnchorPane, HBox, Pane, VBox}
import planetgame.{AudioManager, MainApp}

class StatusReportController:
  // Root pane of the status report
  @FXML private var rootPane: AnchorPane = _

  // Progress bars
  @FXML var foodProgressBar: ProgressBar = _
  @FXML var waterProgressBar: ProgressBar = _
  @FXML var soilProgressBar: ProgressBar = _
  @FXML var techProgressBar: ProgressBar = _
  @FXML var moodProgressBar: ProgressBar = _

  // Value labels
  @FXML var foodValueLabel: javafx.scene.control.Label = _
  @FXML var waterValueLabel: javafx.scene.control.Label = _
  @FXML var soilValueLabel: javafx.scene.control.Label = _
  @FXML var techValueLabel: javafx.scene.control.Label = _
  @FXML var moodValueLabel: javafx.scene.control.Label = _

  // Upgrade / Downgrade arrows
  @FXML var foodUpgradeImg: ImageView = _
  @FXML var foodDowngradeImg: ImageView = _
  @FXML var waterUpgradeImg: ImageView = _
  @FXML var waterDowngradeImg: ImageView = _
  @FXML var soilUpgradeImg: ImageView = _
  @FXML var soilDowngradeImg: ImageView = _
  @FXML var techUpgradeImg: ImageView = _
  @FXML var techDowngradeImg: ImageView = _
  @FXML var moodUpgradeImg: ImageView = _
  @FXML var moodDowngradeImg: ImageView = _

  private var continueCallback: () => Unit = _

  /** Set the callback function for Continue button */
  def setOnContinue(callback: () => Unit): Unit =
    continueCallback = callback

  /** Handle Continue button click */
  @FXML
  def handleContinue(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    if continueCallback != null then continueCallback()

  /** Initialize the status report UI with current game state */
  def initialize(): Unit =
    val gs = MainApp.gameState
    val initial = gs.region.getOrElse(
      throw new IllegalStateException("No region found in game state")
    )

    // Set progress bar values
    foodProgressBar.setProgress(gs.food)
    waterProgressBar.setProgress(gs.water)
    soilProgressBar.setProgress(gs.soil)
    techProgressBar.setProgress(gs.tech)
    moodProgressBar.setProgress(gs.mood)

    // Set numeric values
    foodValueLabel.setText((gs.food * 100).toInt.toString)
    waterValueLabel.setText((gs.water * 100).toInt.toString)
    soilValueLabel.setText((gs.soil * 100).toInt.toString)
    techValueLabel.setText((gs.tech * 100).toInt.toString)
    moodValueLabel.setText((gs.mood * 100).toInt.toString)

    // Show arrows depending on improvements or declines
    updateArrowVisibility(initial.baseFood, gs.food, foodUpgradeImg, foodDowngradeImg)
    updateArrowVisibility(initial.baseWater, gs.water, waterUpgradeImg, waterDowngradeImg)
    updateArrowVisibility(initial.baseSoil, gs.soil, soilUpgradeImg, soilDowngradeImg)
    updateArrowVisibility(initial.baseTech, gs.tech, techUpgradeImg, techDowngradeImg)
    updateArrowVisibility(initial.baseMood, gs.mood, moodUpgradeImg, moodDowngradeImg)

  /** Update arrow icons depending on stat change */
  private def updateArrowVisibility(initial: Double, finalVal: Double, upImg: ImageView, downImg: ImageView): Unit =
    if finalVal > initial then
      upImg.setVisible(true)
      downImg.setVisible(false)
    else if finalVal < initial then
      upImg.setVisible(false)
      downImg.setVisible(true)
    else
      upImg.setVisible(false)
      downImg.setVisible(false)