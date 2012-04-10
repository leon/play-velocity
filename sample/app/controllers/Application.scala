package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import se.radley.plugin.velocity._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(view("main.vm", Map(
      "title" -> "Hello There"
    )))
  }
}
