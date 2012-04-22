package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import se.radley.plugin.velocity._

object Application extends Controller {

  def renderHtml = Action { implicit request =>
    Ok(html("main.vm", Map(
      "title" -> "Hello There",
      "name" -> "Leon"
    )))
  }

  def renderTxt = Action { implicit request =>
    Ok(txt("text.vm", Map(
      "name" -> "Leon"
    )))
  }

  def renderXml = Action { implicit request =>
    Ok(xml("xml.vm", Map(
      "name" -> "Leon"
    )))
  }
}
