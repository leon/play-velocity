package se.radley.plugin

import play.api._
import mvc.{AnyContent, Request}
import play.api.Play.current
import templates.Html

package object velocity {
  def view(view: String, values: Map[String, AnyRef] = Map.empty)(implicit app: Application, request: Request[AnyContent]): Html = {
    app.plugin[VelocityPlugin].map(_.render(view, values)).getOrElse(throw new Exception("VelocityPlugin is not registered."))
  }

  def addGlobal(key: String, obj:AnyRef)(implicit app: Application) {
    app.plugin[VelocityPlugin].map(_.addGlobal(key, obj)).getOrElse(throw new Exception("VelocityPlugin is not registered."))
  }
}

