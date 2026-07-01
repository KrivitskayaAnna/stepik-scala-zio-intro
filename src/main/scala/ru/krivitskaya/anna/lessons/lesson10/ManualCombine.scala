package ru.krivitskaya.anna.lessons.lesson10

import zio._

case class DbConnectionParams(host: String, port: Int, databaseName: String)

case class UserRepo(db: DbConnectionParams) {
  def getUser(id: Int): UIO[String] =
    ZIO.succeed(s"User from ${db.databaseName}")
}

object UserRepo {
  val live: URLayer[DbConnectionParams, UserRepo] = ZLayer.fromFunction(UserRepo.apply _)
}

case class OrderRepo(db: DbConnectionParams) {
  def getOrder(id: Int): UIO[String] =
    ZIO.succeed(s"Order from ${db.databaseName}")
}

object OrderRepo {
  val live: URLayer[DbConnectionParams, OrderRepo] = ZLayer.fromFunction(OrderRepo.apply _)
}

case class AppService(userRepo: UserRepo, orderRepo: OrderRepo) {
  def process(userId: Int): UIO[String] =
    for {
      user  <- userRepo.getUser(userId)
      order <- orderRepo.getOrder(userId)
    } yield s"$user, $order"
}

object AppService {
  val live: URLayer[UserRepo with OrderRepo, AppService] = ZLayer.fromFunction(AppService.apply _)
}

object ManualCombine extends ZIOAppDefault {
  val firstDb: ULayer[DbConnectionParams] = ZLayer.succeed(DbConnectionParams("host1", 5432, "db1"))

  val secondDb: ULayer[DbConnectionParams] =
    ZLayer.succeed(DbConnectionParams("host2", 6543, "db2"))

  val semiAutoLayers: ULayer[AppService] = ZLayer.make[AppService](
    firstDb >>> UserRepo.live,
    secondDb >>> OrderRepo.live,
    AppService.live
  )

  val userRepoCompleted: ULayer[UserRepo] = firstDb >>> UserRepo.live

  val orderRepoCompleted: ULayer[OrderRepo] = secondDb >>> OrderRepo.live

  val manualLayers: ULayer[AppService] =
    (userRepoCompleted ++ orderRepoCompleted) >>> AppService.live

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    ZIO.serviceWithZIO[AppService](_.process(1).debug).provide(manualLayers.debug)
}
