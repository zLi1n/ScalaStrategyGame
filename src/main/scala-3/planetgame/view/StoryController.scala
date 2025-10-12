package planetgame.view

import javafx.fxml.FXML
import javafx.scene.control.{Button, Label}
import javafx.scene.image.ImageView
import planetgame.{AudioManager, MainApp}
import planetgame.model.{Difficulty, StoryData}

class StoryController:

  @FXML var characterName: Label = _ // Label for displaying the speaker's name
  @FXML var storyText: Label = _ // Label for displaying dialog text
  @FXML var nextButton: Button = _ // Button to advance the story
  @FXML var characterImage: ImageView = _ // Image representing the speaker

  private var dialogIndex = 0 // Tracks current line in dialog sequence
  private var dialogSequence: Seq[(String, String)] = Seq() // Holds story lines (speaker, text)

  /** Initializes the story sequence based on selected region and difficulty */
  @FXML
  def initialize(): Unit =
    val region = MainApp.selectedRegion.getOrElse("Unknown")
    val difficulty = MainApp.selectedDifficulty.getOrElse(Difficulty.Easy)

    dialogSequence = StoryData.getStory(region, difficulty)

    showNextLine()

  /** Displays the next line of dialog or starts the game if finished */
  private def showNextLine(): Unit =
    if dialogIndex < dialogSequence.length then
      val (speaker, line) = dialogSequence(dialogIndex)
      characterName.setText(speaker)
      storyText.setText(line)
      dialogIndex += 1

      characterImage.setVisible(speaker != "You") // Hide image if player is speaking

      if dialogIndex == dialogSequence.length then
        nextButton.setText("Start Game →") // Change button text at last line
    else
      MainApp.showGameMainLayout() // Switch to main game when story ends

  /** Handles the "Next" button click: plays sound and advances dialog */
  @FXML
  def handleNext(): Unit =
    AudioManager.playEffect("/audio/click.mp3")
    showNextLine()
