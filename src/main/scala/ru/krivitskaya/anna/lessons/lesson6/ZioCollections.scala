package ru.krivitskaya.anna.lessons.lesson6

import zio._

object ZioCollections extends ZIOAppDefault {
  val userIds: List[Int] = List(1, 2, 3, 4, 5)

  def fetchUser(id: Int): Task[String] =
    ZIO.sleep(1.second) *> ZIO.logInfo(s"Got user for id $id").as(s"User-$id")

  val listOfEffects: List[Task[String]] = userIds.map(fetchUser)

  val allUsers: Task[List[String]] = ZIO.foreach(userIds)(fetchUser)

  val parallel: Task[List[String]] = ZIO.foreachPar(userIds)(fetchUser)

  val limited: Task[List[String]] = ZIO.foreachPar(userIds)(fetchUser).withParallelism(2)
  
  //  foreachDiscard
  val emails: List[String] = List("alice@mail.com", "bob@mail.com", "carol@mail.com")

  val sendAll: UIO[Unit] =
    ZIO.foreachDiscard(emails)(email => ZIO.logInfo(s"Sending notification to $email"))

  val sendAllPar: UIO[Unit] =
    ZIO.foreachParDiscard(emails)(email => ZIO.logInfo(s"Sending notification to $email"))

  //  filter
  def isActive(userId: Int): Task[Boolean] = ZIO.succeed(userId % 2 == 0)

  val activeUsers: Task[List[Int]] = ZIO.filter(userIds)(isActive)

  val activeUsersPar: Task[List[Int]] = ZIO.filterPar(userIds)(isActive)

  //  collect: foreach + фильтрация
  val rawInputs: List[String] = List("42", "-1", "abc", "7", "0")

  val positives: UIO[List[Int]] =
    ZIO.collect(rawInputs) { raw =>
      ZIO.fromOption(raw.toIntOption.filter(_ > 0))
    }

  // partition
  def processOrder(id: Int): IO[String, String] =
    if (id > 0) ZIO.succeed(s"Order-$id processed")
    else ZIO.fail(s"Invalid order: $id")

  val orderIds: List[Int] = List(1, -2, 3, -4, 5)

  val partitioned: UIO[(Iterable[(Int, String)], Iterable[String])] =
    ZIO.partition(orderIds) { id =>
      processOrder(id).mapError(err => (id, err))
    }

  val foreachNonFailFast: UIO[List[String]] =
    ZIO
      .foreach(orderIds) { id =>
        processOrder(id).mapError(err => (id, err)).either
      }
      .flatMap { results =>
        val errors    = results.collect { case Left((id, err)) => s"id $id failed with err $err" }
        val successes = results.collect { case Right(value)    => value }
        ZIO
          .when(errors.nonEmpty)(
            ZIO.logError(s"Failures when processing: ${errors.mkString("; ")}")
          )
          .as(successes)
      }

  // validate
  case class FormField(name: String, value: String)

  def validateField(field: FormField): IO[String, String] =
    if (field.value.nonEmpty) ZIO.succeed(field.value)
    else ZIO.fail(s"${field.name} is required")

  val formFields: List[FormField] = List(
    FormField("name", "Alice"),
    FormField("email", ""),
    FormField("phone", "")
  )

  val validated: IO[List[String], List[String]] = ZIO.validate(formFields)(validateField)

  //  foldLeft
  val numbers: List[Int] = List(1, 2, 3, 4, 5)

  val runningSum: UIO[Int] =
    ZIO.foldLeft(numbers)(0) { (acc, n) =>
      ZIO.succeed(acc + n).tap(s => ZIO.logInfo(s"Running sum: $s"))
    }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = partitioned.debug
}
