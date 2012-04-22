package se.radley.plugin

import play.api._
import mvc.{AnyContent, Request}
import play.api.Play.current
import templates.{Xml, Txt, Html}

package object velocity {

  private def error = throw new Exception("VelocityPlugin is not registered.")

  /**
   * Render a velocity view with the supplied parameters
   * @param view the path to a view relative to the app/views folder
   * @param values a map with the view variables
   * @return a String
   */
  def render(view: String, values: Map[String, AnyRef] = Map.empty)(implicit app: Application, request: Request[AnyContent]): String = {
    app.plugin[VelocityPlugin].map(_.render(view, values)).getOrElse(error)
  }

  /**
   * Render a velocity view with the supplied parameters
   * @param view the path to a view relative to the app/views folder
   * @param values a map with the view variables
   * @return a rendered Html view
   */
  def html(view: String, values: Map[String, AnyRef] = Map.empty)(implicit app: Application, request: Request[AnyContent]): Html = {
    app.plugin[VelocityPlugin].map(_.html(view, values)).getOrElse(error)
  }

  /**
   * Render a velocity view with the supplied parameters
   * @param view the path to a view relative to the app/views folder
   * @param values a map with the view variables
   * @return a rendered Txt view
   */
  def txt(view: String, values: Map[String, AnyRef] = Map.empty)(implicit app: Application, request: Request[AnyContent]): Txt = {
    app.plugin[VelocityPlugin].map(_.txt(view, values)).getOrElse(error)
  }

  /**
   * Render a velocity view with the supplied parameters
   * @param view the path to a view relative to the app/views folder
   * @param values a map with the view variables
   * @return a rendered Xml view
   */
  def xml(view: String, values: Map[String, AnyRef] = Map.empty)(implicit app: Application, request: Request[AnyContent]): Xml = {
    app.plugin[VelocityPlugin].map(_.xml(view, values)).getOrElse(error)
  }

  /**
   * Add a variable that will be available to all views for the duration of the request
   * @param key
   * @param value
   */
  def addGlobal(key: String, value: AnyRef)(implicit app: Application) {
    app.plugin[VelocityPlugin].map(_.addGlobal(key, value)).getOrElse(error)
  }

  /**
   * Remove a global variable
   * @param key
   */
  def removeGlobal(key: String)(implicit app: Application) {
    app.plugin[VelocityPlugin].map(_.removeGlobal(key)).getOrElse(error)
  }

  /**
   * Removes all globals
   */
  def clearGlobals()(implicit app: Application) {
    app.plugin[VelocityPlugin].map(_.clearGlobals()).getOrElse(error)
  }
}

