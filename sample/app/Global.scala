import play.api._
import play.api.Play.current
import models._
import se.radley.plugin._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    velocity.addGlobal("globalUtil", utils)
  }

  object utils {

    def hello(name: String): String = {
      "Hello " + name
    }
  }

}
