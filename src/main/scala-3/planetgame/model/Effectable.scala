package planetgame.model

trait Effectable:
  def applyEffect(effect: ActionEffect): Effectable
