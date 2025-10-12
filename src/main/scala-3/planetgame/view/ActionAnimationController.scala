package planetgame.view

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.{AnchorPane, HBox, Pane, VBox}
import javafx.scene.shape.Circle
import javafx.animation.{KeyFrame, Timeline}
import javafx.util.Duration
import planetgame.MainApp
import planetgame.model.{ActionEffect, EffectCalculator, EventData}

class ActionAnimationController:
  // Action image panes
  @FXML private var actionImage1: Pane = _
  @FXML private var actionImage2: Pane = _
  @FXML private var actionImage3: Pane = _
  // Stage indicator lights
  @FXML private var stageIndicator: HBox = _
  @FXML private var light1: Circle = _
  @FXML private var light2: Circle = _
  @FXML private var light3: Circle = _
  @FXML private var light4: Circle = _
  // Event overlay
  @FXML private var eventOverlay: AnchorPane = _
  @FXML private var eventTitle: Label = _
  @FXML private var eventDesc: Label = _
  // Status box and labels
  @FXML private var statusBox: VBox = _
  @FXML private var foodChangeLabel: Label = _
  @FXML private var waterChangeLabel: Label = _
  @FXML private var soilChangeLabel: Label = _
  @FXML private var techChangeLabel: Label = _
  @FXML private var moodChangeLabel: Label = _
  // Status rows
  @FXML private var foodStatusRow: HBox = _
  @FXML private var waterStatusRow: HBox = _
  @FXML private var soilStatusRow: HBox = _
  @FXML private var techStatusRow: HBox = _
  @FXML private var moodStatusRow: HBox = _
  @FXML private var turnLabel: Label = _

  private var currentStage = 0

  /** Set the turn/years left label */
  def setTurn(value: Int): Unit =
    turnLabel.setText(s"$value Years Left")

  /** Initialize controller state */
  def initialize(): Unit =
    resetStatusOverlay()
    playNextAction(0)

  /** Play the next action in sequence */
  def playNextAction(index: Int): Unit =
    if index >= MainApp.selectedActionEffects.length then
      saveFinalStatusAndShowReport()
      return

    resetStatusOverlay()
    currentStage = index + 1
    updateProgressIndicator(currentStage)

    val action = MainApp.selectedActionEffects(index)

    val baseEffect = previewEffect(action)
    updateStatusOverlay(baseEffect)

    animateActionImages(action, () => {
      EventData.getEventForAction(action) match
        case Some((title, desc)) =>
          val boosted = action.copy(
            food = action.food * 2,
            water = action.water * 2,
            soil = action.soil * 2,
            tech = action.tech * 2,
            mood = action.mood * 2
          )
          val boostedEffect = previewEffect(boosted)
          updateStatusOverlay(boostedEffect)
          commitEffect(boostedEffect)
          showEventOverlay(title, desc, () => playNextAction(index + 1))

        case None =>
          commitEffect(baseEffect)
          val delay = new Timeline(new KeyFrame(Duration.seconds(1.5), _ => playNextAction(index + 1)))
          delay.play()
    })

  /** Preview effect without applying */
  def previewEffect(effect: ActionEffect): ActionEffect =
    EffectCalculator.previewEffect(effect, MainApp.gameState)

  /** Commit effect to game state */
  def commitEffect(effect: ActionEffect): Unit =
    MainApp.gameState = MainApp.gameState.applyEffect(effect)

  /** Animate action image sequence */
  def animateActionImages(effect: ActionEffect, onFinish: () => Unit): Unit =
    val imagePaths = effect.imagePaths
    val imagePanes = Seq(actionImage1, actionImage2, actionImage3)

    val timeline = new Timeline()
    val frame1 = new KeyFrame(Duration.seconds(1), _ => {
      imagePanes(0).setStyle(s"-fx-background-image: url('${imagePaths(0)}'); -fx-background-size: cover; -fx-background-position: center;")
    })
    val frame2 = new KeyFrame(Duration.seconds(2), _ => {
      imagePanes(1).setStyle(s"-fx-background-image: url('${imagePaths(1)}'); -fx-background-size: cover; -fx-background-position: center;")
    })
    val frame3 = new KeyFrame(Duration.seconds(3), _ => {
      imagePanes(2).setStyle(s"-fx-background-image: url('${imagePaths(2)}'); -fx-background-size: cover; -fx-background-position: center;")
    })
    val reset = new KeyFrame(Duration.seconds(4), _ => onFinish())

    timeline.getKeyFrames.addAll(frame1, frame2, frame3, reset)
    animatePaneWidths()
    timeline.play()

  /** Show event overlay with title and description */
  def showEventOverlay(title: String, desc: String, after: () => Unit): Unit =
    eventTitle.setText(title)
    eventDesc.setText(desc)
    eventOverlay.setVisible(true)
    eventOverlay.setOnMouseClicked(_ => {
      eventOverlay.setVisible(false)
      val delay = new Timeline(new KeyFrame(Duration.seconds(1.5), _ => after()))
      delay.play()
    })

  /** Update progress indicator lights */
  def updateProgressIndicator(stage: Int): Unit =
    Seq(light1, light2, light3, light4).zipWithIndex.foreach { case (circle, idx) =>
      if idx == stage - 1 then circle.getStyleClass.setAll("stage-on")
      else circle.getStyleClass.setAll("stage-off")
    }

  /** Animate widths of action panes */
  def animatePaneWidths(): Unit =
    val timeline = new Timeline()
    val step1 = new KeyFrame(Duration.seconds(1), _ => {
      actionImage1.setPrefWidth(1300)
      actionImage2.setPrefWidth(0)
      actionImage3.setPrefWidth(0)
    })
    val step2 = new KeyFrame(Duration.seconds(2), _ => {
      actionImage1.setPrefWidth(230)
      actionImage2.setPrefWidth(700)
      actionImage3.setPrefWidth(0)
    })
    val step3 = new KeyFrame(Duration.seconds(3), _ => {
      actionImage1.setPrefWidth(230)
      actionImage2.setPrefWidth(310)
      actionImage3.setPrefWidth(450)
    })
    timeline.getKeyFrames.addAll(step1, step2, step3)
    timeline.play()

  /** Reset the status overlay visibility */
  def resetStatusOverlay(): Unit =
    Seq(foodStatusRow, waterStatusRow, soilStatusRow, techStatusRow, moodStatusRow).foreach { row =>
      row.setVisible(false)
      row.setManaged(false)
    }

  /** Update status overlay with effect values */
  def updateStatusOverlay(effect: ActionEffect): Unit =
    def showRow(row: HBox, label: Label, value: Double): Unit =
      val intValue = (value * 100).toInt
      if intValue != 0 then
        label.setText(f"${if intValue > 0 then "+" else ""}$intValue")
        row.setVisible(true)
        row.setManaged(true)

    showRow(foodStatusRow, foodChangeLabel, effect.food)
    showRow(waterStatusRow, waterChangeLabel, effect.water)
    showRow(soilStatusRow, soilChangeLabel, effect.soil)
    showRow(techStatusRow, techChangeLabel, effect.tech)
    showRow(moodStatusRow, moodChangeLabel, effect.mood)

    if effect.mood == 0.0 then
      showRow(moodStatusRow, moodChangeLabel, -0.05)

    statusBox.setVisible(true)

  /** Save final state and show report */
  def saveFinalStatusAndShowReport(): Unit =
    MainApp.showStatusReportLayout()