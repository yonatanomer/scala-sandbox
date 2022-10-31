package crawler.validation

package typelevel_tests.cats_tests

import cats.implicits._
import cats.data.Validated._

import cats.data._

final case class RegistrationData(username: String, password: String)

object AppErrors extends Enumeration {
  type Err = Value
  val UserNameError = Value("Username cannot contain special characters.")
  val PasswordError = Value("Password should contain at least 5 characters")
}

sealed trait ValidateAll {

  import AppErrors._

  type ValidationResult[A] = ValidatedNec[Err, A]

  private def validateUserName(userName: String): ValidationResult[String] =
    if (userName.matches("^[a-zA-Z0-9]+$")) userName.validNec else UserNameError.invalidNec

  private def validatePassword(password: String): ValidationResult[String] =
    if (password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) password.validNec
    else PasswordError.invalidNec

  def validateForm(username: String, password: String): ValidationResult[RegistrationData] = {
    (
      validateUserName(username),
      validatePassword(password)
    ).mapN(RegistrationData)
  }
}

object ValidateAll extends ValidateAll with App {

  val testValidation = ValidateAll.validateForm(
    username = "fakeUs$$$rname",
    password = "password"
  )

  println("validation test: " + testValidation)
}
