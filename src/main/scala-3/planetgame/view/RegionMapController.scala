package planetgame.view

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import javafx.scene.control.{Button, Label, ProgressBar}
import javafx.scene.layout.StackPane
import planetgame.{AudioManager, MainApp}
import planetgame.model.{Difficulty, GameState, RegionData}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class RegionMapController:

  // Overlay pane showing region status
  @FXML var statusOverlayPane: AnchorPane = _
  // Root container for the region map screen
  @FXML var rootStack: StackPane = _
  // Labels for planet/region info
  @FXML private var planetName: Label = _
  @FXML private var regionName: Label = _
  @FXML private var winValueLabel: Label = _
  @FXML private var backBtn: Button = _

  // Progress bars and labels for stats
  @FXML var foodBar: ProgressBar = _
  @FXML var waterBar: ProgressBar = _
  @FXML var soilBar: ProgressBar = _
  @FXML var techBar: ProgressBar = _
  @FXML var moodBar: ProgressBar = _
  @FXML var foodValueLabel: Label = _
  @FXML var waterValueLabel: Label = _
  @FXML var soilValueLabel: Label = _
  @FXML var techValueLabel: Label = _
  @FXML var moodValueLabel: Label = _

  // Buttons for selecting regions
  @FXML var regionAButton: Button = _
  @FXML var regionBButton: Button = _
  @FXML var regionCButton: Button = _

  // Currently selected region ID
  private var selectedRegionId: Option[String] = None

  /** Show region details based on regionId and difficulty */
  def showRegion(regionId: String, difficulty: Difficulty): Unit =
    selectedRegionId = Some(regionId)

    val region = RegionData.getRegionWithDifficulty(regionId, difficulty)

    showRegionStatus(
      region.baseFood,
      region.baseWater,
      region.baseSoil,
      region.baseTech,
      region.baseMood
    )

    regionName.setText(region.name)

    planetName.setText(difficulty match
      case Difficulty.Easy   => "Gaialuna"
      case Difficulty.Medium => "Pyronox"
      case Difficulty.Hard   => "Xeravon"
    )

    winValueLabel.setText(difficulty match
      case Difficulty.Easy   => ">= 60"
      case Difficulty.Medium => ">= 70"
      case Difficulty.Hard   => ">= 80"
    )

  /** Initialize region map screen with difficulty-based settings */
  @FXML
  def initialize(): Unit =
    val difficulty = MainApp.selectedDifficulty.getOrElse(Difficulty.Easy)

    planetName.setText(difficulty match
      case Difficulty.Easy => "Gaialuna"
      case Difficulty.Medium => "Pyronox"
      case Difficulty.Hard => "Xeravon"
    )
    winValueLabel.setText(difficulty match
      case Difficulty.Easy => ">=60"
      case Difficulty.Medium => ">=70"
      case Difficulty.Hard => ">=80"
    )

    rootStack.getStyleClass.removeAll("easy-mode", "medium-mode", "hard-mode")
    rootStack.getStyleClass.add(difficulty match
      case Difficulty.Easy => "easy-mode"
      case Difficulty.Medium => "medium-mode"
      case Difficulty.Hard => "hard-mode"
    )

    difficulty match
      case Difficulty.Easy =>
        regionAButton.setText("Forest Edge")
        regionBButton.setText("Desert Core")
        regionCButton.setText("Ocean Rim")

      case Difficulty.Medium =>
        regionAButton.setText("Twilight Forest")
        regionBButton.setText("Ashen Dunes")
        regionCButton.setText("Storm Break")

      case Difficulty.Hard =>
        regionAButton.setText("Rotwood Hollow")
        regionBButton.setText("Blister Wastes")
        regionCButton.setText("Abyssal Rift")

    regionAButton.setOnAction(_ => showRegion("A", difficulty))
    regionBButton.setOnAction(_ => showRegion("B", difficulty))
    regionCButton.setOnAction(_ => showRegion("C", difficulty))

  /** Handle confirm region selection */
  @FXML
  def handleSelectRegion(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    selectedRegionId match
      case Some(id) =>
        MainApp.selectedRegion = Some(id)
        val regionWithDiff = planetgame.model.RegionData.getRegionWithDifficulty(
          id,
          MainApp.selectedDifficulty.getOrElse(planetgame.model.Difficulty.Easy)
        )
        MainApp.gameState = MainApp.gameState.copy(region = Some(regionWithDiff))

        MainApp.showStoryLayout()
      case None =>
        val alert = new Alert(AlertType.Warning)
        alert.setTitle("No Region Selected")
        alert.headerText = None
        alert.contentText = "Please select a region before continuing."
        alert.showAndWait()

  /** Handle closing the status overlay */
  @FXML
  def handleCloseOverlay(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    statusOverlayPane.setVisible(false)

  /** Show region status values and update preview bars */
  def showRegionStatus(food: Double, water: Double, soil: Double, tech: Double, mood: Double): Unit =
    // Build a preview GameState (not starting game yet)
    MainApp.gameState = GameState(
      difficulty = MainApp.selectedDifficulty.getOrElse(Difficulty.Easy),
      region = None,
      food = food,
      water = water,
      soil = soil,
      tech = tech,
      mood = mood
    )

    // Update UI
    foodBar.setProgress(food)
    foodValueLabel.setText((food * 100).toInt.toString)

    waterBar.setProgress(water)
    waterValueLabel.setText((water * 100).toInt.toString)

    soilBar.setProgress(soil)
    soilValueLabel.setText((soil * 100).toInt.toString)

    techBar.setProgress(tech)
    techValueLabel.setText((tech * 100).toInt.toString)

    moodBar.setProgress(mood)
    moodValueLabel.setText((mood * 100).toInt.toString)

    selectedRegionId.foreach { id =>
      val name = id match
        case "A" => regionAButton.getText
        case "B" => regionBButton.getText
        case "C" => regionCButton.getText
      regionName.setText(name)
    }

    statusOverlayPane.setVisible(true)

  /** Handle back button click (return to difficulty selection) */
  @FXML
  def handleBack(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    MainApp.showDifficultyLayout()
