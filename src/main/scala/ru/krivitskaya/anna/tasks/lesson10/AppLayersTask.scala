package ru.krivitskaya.anna.tasks.lesson10

import zio._

case class Config(url: String, timeout: Int)

case class DatabaseConnection(config: Config) {
  def query(sql: String): UIO[String] =
    ZIO.succeed(s"Result from db ${config.url} for sql $sql")
}

object DatabaseConnection {
  val live: URLayer[Config, DatabaseConnection] = ???
}

case class Cache(config: Config) {
  def get(key: String): UIO[Option[String]] =
    ZIO.some(s"Result from cache ${config.url} for key $key")
}

object Cache {
  val live: URLayer[Config, Cache] = ???
}

case class UserService(db: DatabaseConnection, cache: Cache) {
  def findUser(id: Int): UIO[String] =
    for {
      cached <- cache.get(s"user:$id")
      result <- cached match {
        case Some(v) => ZIO.succeed(v)
        case None    => db.query(s"SELECT * FROM users WHERE id = $id")
      }
    } yield result
}

object UserService {
  val live: URLayer[DatabaseConnection with Cache, UserService] = ???
}

object AppLayersTask extends ZIOAppDefault {
  val dbConfigLayer: ULayer[Config] =
    ZLayer.succeed(Config("jdbc:postgresql://localhost:5432/mydb", 10000))

  val cacheConfigLayer: ULayer[Config] =
    ZLayer.succeed(Config("redis://admin:foobar@redis.server:6380", 100))

  // Реализуйте ручную сборку через операторы >>> и ++
  val manualLayer: ULayer[UserService] = ???

  // Реализуйте автоматическую сборку, насколько это возможно, через ZLayer.make
  val semiAutoLayer: ULayer[UserService] = ???

  override def run =
    for {
      _ <- Console.printLine("=== Manual ===")
      _ <- ZIO.serviceWithZIO[UserService](_.findUser(42).debug).provide(manualLayer.debug)
      _ <- Console.printLine("=== Semi auto ===")
      _ <- ZIO.serviceWithZIO[UserService](_.findUser(42).debug).provide(semiAutoLayer.debug)
    } yield ()
}
