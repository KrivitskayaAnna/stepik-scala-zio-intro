package ru.krivitskaya.anna.solutions.lesson9

import zio._

case class DatabaseConfig(host: String, port: Int)

object DatabaseConfig {
  def create(host: String, port: String): Task[DatabaseConfig] =
    ZIO.attempt(DatabaseConfig(host, port.toInt))
}

case class HttpClient(baseUrl: String) {
  def get(path: String): UIO[String] =
    ZIO.succeed(s"Response from $baseUrl$path")
}

object HttpClient {
  def create(databaseConfig: DatabaseConfig): HttpClient =
    HttpClient(s"http://${databaseConfig.host}:${databaseConfig.port}")
}

case class ApiService(client: HttpClient) {
  def fetchUsers: UIO[String] = client.get("/users")
}

object EffectfulLayer extends ZIOAppDefault {
  // Создайте слой для DatabaseConfig на основе метода DatabaseConfig.create(...)
  val dbConfigLayer: TaskLayer[DatabaseConfig] =
    ZLayer.fromZIO(DatabaseConfig.create("test", "5432"))

  // HttpClient зависит от DatabaseConfig. Создайте слой для HttpClient на основе метода HttpClient.create(...)
  val httpClientLayer: URLayer[DatabaseConfig, HttpClient] =
    ZLayer.fromFunction(HttpClient.create(_))

  val apiServiceLayer: URLayer[HttpClient, ApiService] =
    ZLayer.fromFunction(ApiService.apply _)

  val program: ZIO[ApiService, Throwable, Unit] =
    for {
      result <- ZIO.serviceWithZIO[ApiService](_.fetchUsers)
      _      <- Console.printLine(result)
    } yield ()

  override def run =
    program.provide(dbConfigLayer, httpClientLayer, apiServiceLayer)
}
