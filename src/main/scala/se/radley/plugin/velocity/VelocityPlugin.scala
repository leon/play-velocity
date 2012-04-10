package se.radley.plugin.velocity

import play.api._
import i18n.Messages
import play.api.mvc._
import play.api.Play.current
import play.api.templates.Html
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.runtime._
import java.io.StringWriter
import play.mvc.Http

class VelocityPlugin(app: Application) extends Plugin {

  lazy val configuration = app.configuration.getConfig("velocity").getOrElse(Configuration.empty)

  // @todo should we be able to disable this plugin?
  override def enabled = true

  override def onStart() {
    initEngine()
    Logger("velocity").info("velocity 1.7")
  }

  private def initEngine() {

    Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Logger.getClass)
    Velocity.setProperty("resource.loader", "file")
    Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader")
    Velocity.setProperty("file.resource.loader.path", app.getExistingFile("app/views").map { file => file.getAbsolutePath }.getOrElse(sys.error("app/views is missing")))
    Velocity.setProperty("file.resource.loader.cache", "false")
    Velocity.setProperty("file.resource.loader.modificationCheckInterval", "0")
    Velocity.setProperty("input.encoding", "utf-8")
    Velocity.setProperty("directive.set.null.allowed", true)
    Velocity.setProperty("foreach.provide.scope.control", true)
    Velocity.setProperty("velocimacro.library", "global.vm")
    try {
      Velocity.init()
    } catch {
      case e => {
        throw configuration.reportError("velocity", "Cannot initialize velocity engine", Some(e.getCause))
      }
    }
  }

  private def getContext(request: Request[AnyContent]): VelocityContext = {
    val context = new VelocityContext()
    context.put("play", Play)
    context.put("messages", Messages)
    context.put("session", request.session)
    context.put("flash", request.flash)
    context.put("session", request.session)
    context.put("request", request)
    context.put("routes", Play.routes)

    //import scala.collection.JavaConversions._
    //Http.Context.current().args.foreach { case (key, value) => context.put(key, value) }

    context
  }
  /*lazy val globals = MutableMap[String, AnyRef]*/
  def addGlobal(key: String, obj: AnyRef) {
    // @todo get this working globals += (key -> obj)
  }

  def render(templatePath: String, values: Map[String, AnyRef] = Map.empty)(implicit request: Request[AnyContent]): Html = {
    app.getExistingFile("app/views/" + templatePath).getOrElse(sys.error("template [" + templatePath + "] could not be found"))
    try {
      val template = RuntimeSingleton.getTemplate(templatePath, "utf-8")
      val out = new StringWriter()
      val context = getContext(request)
      if (!values.isEmpty)
        values.foreach { case (key, value) => context.put(key, value) }

      template.merge(context, out)
      out.close()
      Html(out.toString)
    } catch {
      case e => {
        throw new Exception("Could not parse template: " + e)
      }
    }
  }
}
