package planetgame.model

import planetgame.model.Difficulty

object StoryData:
  val stories: Map[(String, Difficulty), Seq[(String, String)]] = Map(
    /** This code was generated with Chatgpt */
    ("A", Difficulty.Easy) -> Seq(
      ("Unit-7A", "The lush forest thrives with food and water."),
      ("Unit-7A", "But something dark lurks beneath the canopy..."),
      ("You", "We’ll restore balance to this sacred place.")
    ),
    ("A", Difficulty.Medium) -> Seq(
      ("Unit-7A", "The trees whisper of challenges to come."),
      ("You", "We must act quickly, before nature fights back.")
    ),
    ("A", Difficulty.Hard) -> Seq(
      ("Unit-7A", "Rot spreads through the forest."),
      ("You", "Only the strong will survive. Let’s begin.")
    ),
    ("B", Difficulty.Easy) -> Seq(
      ("Unit-23c7E", "The desert is dry, but the sky rains hope..."),
      ("You", "We’ll make this land bloom again.")
    ), 
    ("B", Difficulty.Medium) -> Seq(
      ("Unit-23c7E", "Crisis stirs beneath the sands."),
      ("You", "Will we endure this heat?")
    ),
    ("B", Difficulty.Hard) -> Seq(
      ("Unit-23c7E", "The sun shows no mercy."),
      ("You", "Then we show no weakness.")
    ),
    ("C", Difficulty.Easy) -> Seq(
      ("Unit-2832e784D3", "Tides of prosperity await..."),
      ("You", "We must stay afloat together.")
    ),
    ("C", Difficulty.Medium) -> Seq(
      ("Unit-2832e784D3", "The oceans swell, the balance tilts."),
      ("You", "We’ll keep the ship steady.")
    ),
    ("C", Difficulty.Hard) -> Seq(
      ("Unit-2832e784D3", "Waves crash harder than ever."),
      ("You", "Then we anchor ourselves with courage.")
    )
  )
    
  def getStory(regionId: String, difficulty: Difficulty): Seq[(String, String)] =
    stories.getOrElse((regionId, difficulty), Seq(("Narrator", "No story available for this region.")))
