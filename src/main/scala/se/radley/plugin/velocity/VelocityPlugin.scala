package se.radley.plugin.velocity

import play.api._
import play.api.mvc._
import play.api.Play.current

class VelocityPlugin(app: Application) extends Plugin {

  lazy val configuration = app.configuration.getConfig("velocity").getOrElse(Configuration.empty)

  private lazy val isDisabled = {
    //configuration.subKeys.isEmpty
    false
  }

  override def enabled = isDisabled == false

  override def onStart() {
    Logger("velocity").info("velocity 1.7 Locked an loaded")
  }

  override def onStop() {
    // @todo do we need to close the velocity connection?
  }

  def addGlobal(name: String, obj: Object) {

  }
}
