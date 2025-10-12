package planetgame.model

import scalafx.beans.property.BooleanProperty
import scala.util.Try
import scalikejdbc.*
import planetgame.util.Database

class PlayerProgress extends Database:
  var unlockedEasy   = BooleanProperty(true)   // Always true
  var unlockedMedium = BooleanProperty(false)
  var unlockedHard   = BooleanProperty(false)

  /** Saves player progress into the database */
  def save(): Try[Int] =
    Try {
      DB.autoCommit { implicit session: DBSession =>
        sql"""
          update player_progress
          set unlockedEasy = ${unlockedEasy.value},
              unlockedMedium = ${unlockedMedium.value},
              unlockedHard = ${unlockedHard.value}
          where id = 1
        """.update.apply()
      }
    }

/** Companion object for PlayerProgress with DB helpers */
object PlayerProgress extends Database:
  /** Creates the player_progress table and inserts default row */
  def initializeTable(): Unit =
    DB.autoCommit { implicit session: DBSession =>
      sql"""
        create table player_progress (
          id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          unlockedEasy boolean,
          unlockedMedium boolean,
          unlockedHard boolean
        )
      """.execute.apply()

      // Insert default progress row
      sql"""
        insert into player_progress (unlockedEasy, unlockedMedium, unlockedHard)
        values (true, false, false)
      """.update.apply()
    }

  /** Loads player progress from the database */
  def load(): PlayerProgress =
    DB.readOnly { implicit session: DBSession =>
      sql"select * from player_progress where id = 1"
        .map { rs =>
          val p = new PlayerProgress()
          p.unlockedEasy.value = rs.boolean("unlockedEasy")
          p.unlockedMedium.value = rs.boolean("unlockedMedium")
          p.unlockedHard.value = rs.boolean("unlockedHard")
          p
        }.single.apply()
    }.getOrElse(new PlayerProgress())