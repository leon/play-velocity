package se.radley.plugin.velocity

import org.specs2.mutable.Specification
import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import java.io.File
import play.api.Play.current
import se.radley.plugin.velocity.VelocityExceptions._
import templates.{Xml, Txt, Html}

object VelocitySpec extends Specification {

  val sourceDir = new File("src/test/templates")

  lazy val fakeApp = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.velocity.VelocityPlugin"),
    additionalConfiguration = Map(("velocity.views" -> sourceDir.getAbsolutePath))
  )

  def velocity = fakeApp.plugin[VelocityPlugin].get

  "Velocity" should {

    "start" in {
      running(fakeApp) {
        velocity must beAnInstanceOf[VelocityPlugin]
      }
    }
    "render template" in {
      running(fakeApp) {
        val result = velocity.render("simple.vm")(FakeRequest())
        result must beAnInstanceOf[String]
        result must equalTo("Hello")
      }
    }
    "render template with variable" in {
      running(fakeApp) {
        val result = velocity.render("hello.vm", Map(
          "name" -> "Leon"
        ))(FakeRequest())
        result must equalTo("Hello Leon")
      }
    }
    "render template with global variable" in {
      running(fakeApp) {
        velocity.addGlobal("name", "Leon")
        val result = velocity.render("hello.vm")(FakeRequest())
        result must equalTo("Hello Leon")
      }
    }
    "render template as html" in {
      running(fakeApp) {
        val result = velocity.html("simple.vm")(FakeRequest())
        result must beAnInstanceOf[Html]
        result.body must equalTo("Hello")
      }
    }
    "render template as txt" in {
      running(fakeApp) {
        val result = velocity.txt("simple.vm")(FakeRequest())
        result must beAnInstanceOf[Txt]
        result.body must equalTo("Hello")
      }
    }
    "render template as xml" in {
      running(fakeApp) {
        val result = velocity.xml("xml.vm")(FakeRequest())
        result must beAnInstanceOf[Xml]
        result.body must equalTo("""<?xml version="1.0" encoding="UTF-8"?><body>Hello</body>""")
      }
    }

    // Failures
    "fail when template doesn't exist" in {
      running(fakeApp) {
        velocity.render("does-not-exist.vm")(FakeRequest()) must throwAn[TemplateNotFoundException]
      }
    }
    "fail when rendering template with variable that doesn't exist" in {
      running(fakeApp) {
        velocity.render("fail-variable.vm")(FakeRequest()) must throwAn[TemplateMethodInvocationException]
      }
    }
    "fail when rendering template with invalid syntax" in {
      running(fakeApp) {
        velocity.render("fail-syntax.vm")(FakeRequest()) must throwAn[TemplateParseException]
      }
    }
  }
}
