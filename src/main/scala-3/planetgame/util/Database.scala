package planetgame.util

import scalikejdbc.*
import planetgame.model.PlayerProgress

trait Database:
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true;"

  // Load driver & set up connection pool
  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "me", "mine")

  // Provide default AutoSession for all DB calls
  given AutoSession = AutoSession

object Database extends Database:
  def setupDB(): Unit =
    if !hasDBInitialize then
      PlayerProgress.initializeTable()

  def hasDBInitialize: Boolean =
    DB.getTable("player_progress") match
      case Some(_) => true
      case None    => false
