package se.radley.plugin.velocity

import play.api._
import i18n.{Lang, Messages}
import play.api.mvc._
import play.api.Play.current
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.runtime._
import org.apache.velocity.tools.ToolManager
import java.io._
import templates._
import VelocityExceptions._
import org.apache.velocity.exception._
import org.apache.velocity.runtime.parser.ParseException
import collection.mutable.HashMap

class VelocityPlugin(app: Application) extends Plugin {

  lazy val configuration = app.configuration.getConfig("velocity").getOrElse(Configuration.empty)

  lazy val viewPath = configuration.getString("views").getOrElse("app/views/")

  // @todo should we be able to disable this plugin?
  override def enabled = true

  override def onStart() {
    configure()
    Logger("velocity").info("velocity 1.7")
  }

  private var isInitialized = false

  /**
   * Setup Velocity
   * http://velocity.apache.org/engine/releases/velocity-1.7/developer-guide.html
   */
  def configure(additionalConfiguration: Map[String, AnyRef] = Map.empty) {
    Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Logger.getClass)
    Velocity.setProperty("runtime.log.logsystem.log4j.logger", "velocity")

    Velocity.setProperty("resource.loader", "file")
    Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader")

    val resourcePath = app.getExistingFile(viewPath).getOrElse(new File(viewPath))
    Velocity.setProperty("file.resource.loader.path", Option(resourcePath).filter(_.exists).map { file => file.getAbsolutePath }.getOrElse(sys.error(resourcePath.getAbsolutePath + " is missing")))
    Velocity.setProperty("input.encoding", "utf-8")

    // If true, having a right hand side of a #set() statement with an invalid reference or null value will set the left hand side to null. If false, the left hand side will stay the same.
    Velocity.setProperty("directive.set.null.allowed", true)

    // This setting will also throw an exception if an attempt is made to call a non-existing property on an object or if the object is null.
    Velocity.setProperty("runtime.references.strict", true)

    if (new File(resourcePath, "global.vm").exists) {
      /* Multi-valued key. Will accept CSV for value. Filename(s) of Velocimacro library to be loaded when the Velocity Runtime engine starts.
       * These Velocimacros are accessable to all templates. The file is assumed to be relative to the root of the file loader resource path. */
       Velocity.setProperty("velocimacro.library", "global.vm")
    }

    if (Play.isProd) {
      Velocity.setProperty("file.resource.loader.cache", "true")
      // @todo @maybe we should be able to changed files on the server?
      Velocity.setProperty("file.resource.loader.modificationCheckInterval", "0")
    } else {
      Velocity.setProperty("file.resource.loader.cache", "false")
    }

    if (!additionalConfiguration.isEmpty) {
      additionalConfiguration.foreach { case (key, value) =>
        Velocity.setProperty(key, value)
      }
    }

    try {
      Velocity.init()
      isInitialized = true
    } catch {
      case e => {
        throw configuration.reportError("velocity", "Cannot initialize velocity engine", Some(e.getCause))
      }
    }
  }

  private def getContext(request: Request[AnyContent]): VelocityContext = {
    val toolManager = new ToolManager()
    val toolContext = toolManager.createContext()

    val context = if (!VelocityPlugin.globalContext.isEmpty) {
      val globalContext = new VelocityContext(toolContext)
      VelocityPlugin.globalContext.foreach {
        case (key, value) => globalContext.put(key, value)
      }
      new VelocityContext(globalContext)
    } else {
      new VelocityContext(toolContext)
    }

    import scala.collection.JavaConverters._

    context.put("play", Play.current)
    context.put("request", request)

    context.put("messages", TemplateMessages)
    context.put("session", request.session.data.asJava)
    context.put("flash", request.flash.data.asJava)
    context.put("routes", Play.routes)

    context
  }

  /*
    Globals
   */
  def addGlobal(key: String, value: AnyRef) = VelocityPlugin.globalContext.put(key, value)
  def removeGlobal(key: String) = VelocityPlugin.globalContext.remove(key)
  def clearGlobals() = VelocityPlugin.globalContext.clear()

  /*
    Rendering
   */
  def render(templatePath: String, values: Map[String, AnyRef] = Map.empty)(implicit request: Request[AnyContent]) = {
    try {
      val template = Velocity.getTemplate(templatePath, "utf-8")
      val out = new StringWriter()
      val context = getContext(request)
      if (!values.isEmpty)
        values.foreach { case (key, value) => context.put(key, value) }

      template.merge(context, out)
      out.close()
      out.toString
    } catch {
      case e: ResourceNotFoundException => throw TemplateNotFoundException(templatePath, e.getMessage)
      //case parse: ParseException => throw new ParseErrorException(parse, templatePath)
      case e: ParseErrorException => throw TemplateParseException(new File(viewPath + "/" + templatePath), e.getInvalidSyntax, e.getLineNumber, e.getColumnNumber)
      case e: MethodInvocationException => throw TemplateMethodInvocationException(new File(viewPath + "/" + templatePath), e.getMethodName, e.getLineNumber, e.getColumnNumber)
      case e => throw PlayException("Velocity Error", e.getMessage, Option(e.getCause))
    }
  }

  def html(templatePath: String, values: Map[String, AnyRef] = Map.empty)(implicit request: Request[AnyContent]) = Html(render(templatePath, values))

  def txt(templatePath: String, values: Map[String, AnyRef] = Map.empty)(implicit request: Request[AnyContent]) = Txt(render(templatePath, values))

  def xml(templatePath: String, values: Map[String, AnyRef] = Map.empty)(implicit request: Request[AnyContent]) = Xml(render(templatePath, values))
}

object TemplateMessages {
  def get(key: String, args: AnyRef*) = Messages(key, args)
}

object VelocityPlugin {
  lazy val globalContext = HashMap[String, AnyRef]()
}
