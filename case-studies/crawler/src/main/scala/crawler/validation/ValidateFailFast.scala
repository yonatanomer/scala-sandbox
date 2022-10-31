package crawler.validation

import cats.data.Validated
import cats.implicits._
import cats.data.Validated._
import typelevel_tests.cats_tests.AppErrors._

final case class RegistrationData(username: String, password: String)

sealed trait ValidateFailFast {

  def validateUserName(userName: String): Either[Err, String] = {
    Either.cond(
      userName.matches("^[a-zA-Z0-9]+$"),
      userName,
      UserNameError
    )
  }

  def validatePassword(password: String): Either[Err, String] =
    Either.cond(
      password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"),
      password,
      PasswordError
    )

  def validateForm(
      username: String,
      password: String
  ): Either[Err, RegistrationData] = {

    for {
      validatedUserName <- validateUserName(username)
      validatedPassword <- validatePassword(password)
    } yield RegistrationData(validatedUserName, validatedPassword)
  }
}

object ValidateFailFast extends ValidateFailFast with App {

  val testValidation = ValidateFailFast.validateForm(
    username = "fakeUs3rname",
    password = "password"
  )
  println("validation test: " + testValidation)

}
