package planetgame

import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes.*
import javafx.scene as jfxs
import planetgame.model.*
import planetgame.util.Database
import planetgame.view.*
import scalafx.scene.image.Image
import scalafx.scene.media.{AudioClip, Media, MediaPlayer}

object MainApp extends JFXApp3 {
  // Root pane
  var roots: Option[scalafx.scene.layout.BorderPane] = None

  // Game selections
  var selectedDifficulty: Option[Difficulty] = None // Chosen difficulty level
  var selectedRegion: Option[String] = None // Chosen region ID
  var selectedActionEffects: Seq[ActionEffect] = Seq.empty // Actions chosen per turn

  // Game state tracking
  var yearLeft: Int = GameConfig.MaxYears // Number of turns/years left
  var gameState: GameState = _ // Current state of the game

  // References to controllers (to allow updating UI between scenes)
  var gameController: GameController = _
  var actionAnimationController: ActionAnimationController = _
  var statusReportController: StatusReportController = _

  // Initial stats at the start of the game (before modifications each turn)
  var initialFood: Double = 0.0
  var initialWater: Double = 0.0
  var initialSoil: Double = 0.0
  var initialTech: Double = 0.0
  var initialMood: Double = 0.0

  // Snapshot of game state at the start of each turn (for reports)
  var startOfTurnState: GameState = _

  // Stores persistent player progress (unlocks, etc.)
  var playerProgress: PlayerProgress = _

  /** Setup before starting game: initialize database and load player progress */
  private def setupApp(): Unit =
    Database.setupDB()
    playerProgress = PlayerProgress.load()

  /** Application entry point (called when program starts) */
  override def start(): Unit =
    setupApp()
    // Play background music
    AudioManager.playBGM("/audio/bgm.mp3")

    // Load main root layout (BorderPane with menu/navigation regions)
    val rootResource = getClass.getResource("view/RootLayout.fxml")
    val loader = new FXMLLoader(rootResource)
    loader.load()
    roots = Option(loader.getRoot[jfxs.layout.BorderPane])

    // Initialize main stage (application window)
    stage = new PrimaryStage():
      title = "Grow Your Own Planet"
      icons += new Image(getClass.getResource("/images/save-the-planet.png").toExternalForm)
      scene = new Scene():
        root = roots.get

    // Start with home layout
    showHomeLayout()

  // Screen Navigation
  /** Navigate to Home screen */
  def showHomeLayout(): Unit =
    loadAndSetCenter("view/HomeLayout.fxml")

  /** Navigate to Difficulty selection screen */
  def showDifficultyLayout(): Unit =
    loadAndSetCenter("view/DifficultyLayout.fxml")

  /** Navigate to Region map screen */
  def showMapLayout(): Unit =
    loadAndSetCenter("view/RegionMapLayout.fxml")

  /** Navigate to Story introduction screen */
  def showStoryLayout(): Unit =
    loadAndSetCenter("view/StoryLayout.fxml")

  /** Navigate to Main gameplay screen */
  def showGameMainLayout(): Unit =
    val (pane, loader) = loadAndGetWithController[jfxs.layout.AnchorPane, GameController]("view/GameLayout.fxml")
    gameController = loader.getController[GameController]
    gameController.updateYearLabel(yearLeft) // Update current year
    gameController.loadInitialOrSavedStatus(gameState) // Load game state
    roots.get.center = pane

  /** Navigate to Action animation screen (shows chosen actions with animations) */
  def showActionAnimationLayout(): Unit =
    val (pane, loader) = loadAndGetWithController[jfxs.layout.AnchorPane, ActionAnimationController]("view/ActionAnimationLayout.fxml")
    actionAnimationController = loader.getController[ActionAnimationController]
    actionAnimationController.setTurn(yearLeft)
    roots.get.center = pane

  /** Navigate to Status report screen (shows results after actions) */
  def showStatusReportLayout(): Unit =
    val (pane, loader) = loadAndGetWithController[jfxs.layout.AnchorPane, StatusReportController]("view/StatusReportLayout.fxml")
    statusReportController = loader.getController[StatusReportController]

    // Define what happens when the player continues after seeing report
    statusReportController.setOnContinue(() => {
      yearLeft -= 1
      if yearLeft <= 0 then
        showEndGameLayout() // End game if years run out
      else
        updateYearAndTurnLabels()
        showGameMainLayout() // Otherwise, go back to main gameplay
    })
    roots.get.center = pane

  /** Navigate to End game screen */
  def showEndGameLayout(): Unit =
    val (pane, loader) = loadAndGetWithController[jfxs.layout.AnchorPane, EndGameController]("view/EndGameLayout.fxml")
    val controller = loader.getController[EndGameController]
    handleGameEnd()
    println(s"Final Stats: Food=${gameState.food}, Water=${gameState.water}, Soil=${gameState.soil}, Tech=${gameState.tech}, Mood=${gameState.mood}")
    roots.get.center = pane

  // Utility
  /** Load FXML and replace center pane of root */
  private def loadAndSetCenter(path: String): Unit =
    val resource = getClass.getResource(path)
    val loader = new FXMLLoader(resource)
    loader.load()
    val pane = loader.getRoot[jfxs.layout.Pane]
    roots.get.center = pane

  /** Load FXML, return both pane and controller */
  private def loadAndGetWithController[T <: jfxs.layout.Pane, C](path: String): (T, FXMLLoader) =
    val resource = getClass.getResource(path)
    val loader = new FXMLLoader(resource)
    loader.load()
    (loader.getRoot[T], loader)

  /** Update year/turn labels across controllers */
  def updateYearAndTurnLabels(): Unit =
    if gameController != null then
      gameController.updateYearLabel(yearLeft)
    if actionAnimationController != null then
      actionAnimationController.setTurn(yearLeft)

  // Game Logic
  /** Reset the game state to initial conditions */
  def resetGame(): Unit =
    if selectedDifficulty.isEmpty || selectedRegion.isEmpty then
      println("No active game to reset.")
      return

    yearLeft = GameConfig.MaxYears

    val regionId = selectedRegion.get
    val difficulty = selectedDifficulty.get

    // Get region with chosen difficulty
    val startingRegion = RegionData.getRegionWithDifficulty(regionId, difficulty)

    // Store initial stats for report/comparison
    initialFood = startingRegion.baseFood
    initialWater = startingRegion.baseWater
    initialSoil = startingRegion.baseSoil
    initialTech = startingRegion.baseTech
    initialMood = startingRegion.baseMood

    // Initialize game state
    gameState = GameState(
      difficulty = difficulty,
      region = Some(startingRegion),
      food = initialFood,
      water = initialWater,
      soil = initialSoil,
      tech = initialTech,
      mood = initialMood
    )

    // Reset chosen actions
    selectedActionEffects = Seq.empty

  /** Handle end of game (unlock difficulties if player won) */
  def handleGameEnd(): Unit =
    if gameState.isWin then
      gameState.difficulty match
        case Difficulty.Easy =>
          playerProgress.unlockedMedium.value = true
        case Difficulty.Medium =>
          playerProgress.unlockedHard.value = true
        case _ => // Hard difficulty - nothing else to unlock

      // Save updated progress
      playerProgress.save()
}

// Audio Manager
object AudioManager {
  private var bgmPlayer: Option[MediaPlayer] = None

  /** Play background music in a loop */
  def playBGM(filePath: String): Unit =
    stopBGM()
    val resource = getClass.getResource(filePath)
    if resource != null then
      val media = new Media(resource.toExternalForm)
      val player = new MediaPlayer(media)
      player.setCycleCount(MediaPlayer.Indefinite)
      player.setVolume(0.5)
      player.play()
      bgmPlayer = Some(player)

  /** Stop current background music */
  def stopBGM(): Unit =
    bgmPlayer.foreach(_.stop())
    bgmPlayer = None

  /** Play one-time sound effect */
  def playEffect(filePath: String): Unit =
    val resource = getClass.getResource(filePath)
    if resource != null then
      val clip = new AudioClip(resource.toExternalForm)
      clip.setVolume(0.6)
      clip.play()
}