package ru.krivitskaya.anna.tasks.lesson10

import zio._

case class AppConfig(appName: String, connectionUrl: String, timeout: Int) {
  println("AppConfig initialized")
}

case class Database(config: AppConfig) {
  println("Database initialized")
}

case class FirstRepo(database: Database) {
  println("FirstRepo initialized")
}

case class SecondRepo(database: Database) {
  println("SecondRepo initialized")
}

case class FirstService(firstRepo: FirstRepo) {
  println("FirstService initialized")
}

case class SecondService(secondRepo: SecondRepo) {
  println("SecondService initialized")
}

case class App(config: AppConfig, firstService: FirstService, secondService: SecondService) {
  println("App initialized")

  def launchApp: UIO[Unit] = ZIO.logInfo(s"App named ${config.appName} is launched")
}

object ComplicatedDepsTask extends ZIOAppDefault {
  val configLayer =
    ZLayer.succeed(AppConfig("aboba", "jdbc:postgresql://localhost:5432/mydb", 1000))

  val databaseLayer = ZLayer.fromFunction(Database.apply _)

  val firstRepoLayer = ZLayer.fromFunction(FirstRepo.apply _)

  val secondRepoLayer = ZLayer.fromFunction(SecondRepo.apply _)

  val firstServiceLayer = ZLayer.fromFunction(FirstService.apply _)

  val secondServiceLayer = ZLayer.fromFunction(SecondService.apply _)

  val appLayer = ZLayer.fromFunction(App.apply _)

  // Реализуйте ручную сборку через операторы >>> и ++
  val manualLayersComplicated: ULayer[App] = ???

  // Реализуйте ручную сборку через оператор >+>
  val manualLayersAlternative: ULayer[App] = ???

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      _ <- Console.printLine("=== Manual ===")
      _ <- ZIO.serviceWithZIO[App](_.launchApp).provide(manualLayersComplicated.debug)
      _ <- Console.printLine("=== Manual alternative ===")
      _ <- ZIO.serviceWithZIO[App](_.launchApp).provide(manualLayersAlternative.debug)
    } yield ()
}
