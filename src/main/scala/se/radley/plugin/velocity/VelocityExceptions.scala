package se.radley.plugin.velocity

import play.api.PlayException
import java.io.File

object VelocityExceptions {
  case class TemplateNotFoundException(source: String, message: String) extends PlayException("Template not found", message)

  case class TemplateParseException(source: File, message: String, atLine: Int, column: Int) extends PlayException("Compilation error", message) with PlayException.ExceptionSource {
    def line = Some(atLine)
    def position = Some(column)
    def input = Some(scalax.file.Path(source))
    def sourceName = Some(source.getAbsolutePath)
  }

  case class TemplateMethodInvocationException(source: File, message: String, atLine: Int, column: Int) extends PlayException("Compilation error", message) with PlayException.ExceptionSource {
    def line = Some(atLine)
    def position = Some(column)
    def input = Some(scalax.file.Path(source))
    def sourceName = Some(source.getAbsolutePath)
  }
}
