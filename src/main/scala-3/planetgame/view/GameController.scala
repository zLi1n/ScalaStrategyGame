package planetgame.view

import javafx.animation.{KeyFrame, Timeline}
import javafx.fxml.FXML
import javafx.scene.control.{Alert, Button, Label}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{HBox, Pane}
import javafx.util.Duration
import planetgame.{AudioManager, MainApp}
import planetgame.model.{ActionEffect, EffectCalculator, GameConfig, GameState, GrowCrops, HoldFestival, IrrigateFields, RestoreSoil, UpgradeTech}
import scalafx.Includes.*
import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.scene.control.Alert.AlertType

class GameController:
  // UI components
  @FXML private var confirmButton: Button = _
  @FXML private var clearAllButton: Button = _
  @FXML private var farmerImage: ImageView = _
  @FXML private var countdownOverlay: HBox = _
  @FXML private var yearLabel: Label = _
  @FXML private var turnLabel: Label = _

  @FXML var foodValueLabel: Label = _
  @FXML var waterValueLabel: Label = _
  @FXML var soilValueLabel: Label = _
  @FXML var techValueLabel: Label = _
  @FXML var moodValueLabel: Label = _

  @FXML private var actionSlot1: Pane = _
  @FXML private var actionSlot2: Pane = _
  @FXML private var actionSlot3: Pane = _
  @FXML private var actionSlot4: Pane = _

  // Game state tracking
  private val selectedActionsCount = IntegerProperty(0)
  private val confirmButtonText = StringProperty(s"Plan Action (0/${GameConfig.MaxActions})")
  private var countdown = 3

  private val selectedImages = scala.collection.mutable.Buffer.empty[ImageView]
  private val cumulativeDeltas = scala.collection.mutable.Map(
    "food" -> 0.0,
    "water" -> 0.0,
    "soil" -> 0.0,
    "tech" -> 0.0,
    "mood" -> 0.0
  )
  private val selectedActionEffects = scala.collection.mutable.Buffer.empty[ActionEffect]

  /** Initialize game state and UI */
  @FXML
  def initialize(): Unit =
    // Save start of turn state for possible reset later
    MainApp.startOfTurnState = MainApp.gameState.copy()

    confirmButton.textProperty().bind(confirmButtonText)
    confirmButtonText <== selectedActionsCount.asString(s"Plan Action (%d/${GameConfig.MaxActions})")
    confirmButton.setDisable(true)
    updateYearLabel(MainApp.yearLeft)
    startCountdown()
    loadInitialOrSavedStatus(MainApp.gameState)

  /** Start countdown overlay at beginning of turn */
  def startCountdown(): Unit =
    val timeline = new Timeline()
    timeline.setCycleCount(4)

    val keyFrame = new KeyFrame(
      Duration.seconds(1),
      _ =>
        if countdown > 0 then countdown -= 1
        else countdownOverlay.setVisible(false)
    )
    timeline.getKeyFrames.add(keyFrame)
    timeline.play()

  /** Update year and turn labels */
  def updateYearLabel(yearsLeft: Int): Unit =
    turnLabel.setText(s"$yearsLeft Years Left")
    yearLabel.setText((yearsLeft).toString)

  /** Update resource status labels with optional deltas */
  def updateStatusLabels(deltas: Map[String, Double] = Map.empty): Unit =
    def format(value: Double, delta: Double): String =
      val base = (value * 100).toInt
      if delta == 0.0 then base.toString
      else
        val sign = if delta > 0 then "+" else "-"
        val amount = (delta * 100).abs.toInt
        s"$base ($sign$amount)"

    val gs = MainApp.gameState
    foodValueLabel.setText(format(gs.food, deltas.getOrElse("food", 0.0)))
    waterValueLabel.setText(format(gs.water, deltas.getOrElse("water", 0.0)))
    soilValueLabel.setText(format(gs.soil, deltas.getOrElse("soil", 0.0)))
    techValueLabel.setText(format(gs.tech, deltas.getOrElse("tech", 0.0)))
    moodValueLabel.setText(format(gs.mood, deltas.getOrElse("mood", 0.0)))

  /** Apply an action, update game state and UI */
  def applyAction(effect: ActionEffect): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    if selectedActionsCount.value < GameConfig.MaxActions then
      val slot = getFirstEmptySlot()
      slot.foreach { pane =>
        val img = new ImageView(new Image(getClass.getResourceAsStream(effect.imagePath)))
        img.setFitWidth(60)
        img.setFitHeight(60)
        pane.getChildren.clear()
        pane.getChildren.add(img)
        selectedImages += img
        selectedActionEffects += effect
        selectedActionsCount.value += 1
        if selectedActionsCount.value == GameConfig.MaxActions then
          confirmButton.setDisable(false)
      }

      val finalEffect = EffectCalculator.previewEffect(effect, MainApp.gameState)

      MainApp.gameState = MainApp.gameState.withStatsDelta(
        finalEffect.food,
        finalEffect.water,
        finalEffect.soil,
        finalEffect.tech,
        finalEffect.mood
      )

      cumulativeDeltas("food")  += finalEffect.food
      cumulativeDeltas("water") += finalEffect.water
      cumulativeDeltas("soil")  += finalEffect.soil
      cumulativeDeltas("tech")  += finalEffect.tech
      cumulativeDeltas("mood")  += finalEffect.mood

      updateStatusLabels(cumulativeDeltas.toMap)
    else
      val alert = new Alert(AlertType.Warning)
      alert.setTitle("Action Limit")
      alert.setHeaderText(null)
      alert.setContentText(s"You can only select ${GameConfig.MaxActions} actions.")
      alert.showAndWait()

  /** Get the first available action slot */
  def getFirstEmptySlot(): Option[Pane] =
    if actionSlot1.getChildren.isEmpty then Some(actionSlot1)
    else if actionSlot2.getChildren.isEmpty then Some(actionSlot2)
    else if actionSlot3.getChildren.isEmpty then Some(actionSlot3)
    else if actionSlot4.getChildren.isEmpty then Some(actionSlot4)
    else None

  /** Load initial or saved status values */
  def loadInitialOrSavedStatus(gs: GameState): Unit =
    if gs.food == 0.0 && gs.water == 0.0 && gs.soil == 0.0 && gs.tech == 0.0 && gs.mood == 0.0 then
      gs.region.foreach { r =>
        MainApp.gameState = gs.copy(
          food = r.baseFood,
          water = r.baseWater,
          soil = r.baseSoil,
          tech = r.baseTech,
          mood = r.baseMood
        )
      }
    updateStatusLabels()

  /** Handle crop-growing action */
  @FXML def handleGrowCrops(): Unit = applyAction(GrowCrops().effect)
  /** Handle irrigation action */
  @FXML def handleIrrigateFields(): Unit = applyAction(IrrigateFields().effect)
  /** Handle soil restoration action */
  @FXML def handleRestoreSoil(): Unit = applyAction(RestoreSoil().effect)
  /** Handle tech upgrade action */
  @FXML def handleUpgradeTech(): Unit = applyAction(UpgradeTech().effect)
  /** Handle festival action */
  @FXML def handleHoldFestival(): Unit = applyAction(HoldFestival().effect)

  /** Clear selected actions and restore state */
  @FXML
  def handleClear(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    selectedActionsCount.value = 0
    confirmButton.setDisable(true)
    selectedImages.clear()
    selectedActionEffects.clear()
    actionSlot1.getChildren.clear()
    actionSlot2.getChildren.clear()
    actionSlot3.getChildren.clear()
    actionSlot4.getChildren.clear()
    cumulativeDeltas.keys.foreach(k => cumulativeDeltas(k) = 0.0)
    // Restore game state to the start of turn snapshot
    MainApp.gameState = MainApp.startOfTurnState.copy()

    updateStatusLabels()

  /** Confirm selected actions and proceed to animation */
  @FXML
  def handleConfirm(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    if selectedActionsCount.value < GameConfig.MaxActions then
      val alert = new Alert(AlertType.Error)
      alert.setTitle("Insufficient Actions")
      alert.setHeaderText(null)
      alert.setContentText(s"Please select exactly ${GameConfig.MaxActions} actions to continue.")
      alert.showAndWait()
    else
      MainApp.selectedActionEffects  = selectedActionEffects.toSeq
      MainApp.showActionAnimationLayout()