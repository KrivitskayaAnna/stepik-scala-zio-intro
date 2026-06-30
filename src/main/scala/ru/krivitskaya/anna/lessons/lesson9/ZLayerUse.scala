package ru.krivitskaya.anna.lessons.lesson9

import zio._

case class User(userId: Int, name: String, age: Int)

class UserRepo {
  def getUserById(userId: Int): UIO[Option[User]] = ZIO.some(User(1, "aboba", 25))
}

case class Order(orderId: Int, orderItems: List[String], orderTotalSum: Double, userId: Int)

class OrderRepo {
  def getOrdersByUserId(userId: Int): UIO[List[Order]] =
    ZIO.succeed(
      List(
        Order(1, List("first", "second"), 123.4, 1),
        Order(2, List("third"), 5.6, 1)
      )
    )
}

case class OrderService(userRepo: UserRepo, orderRepo: OrderRepo) {
  def findOrderByUserId(userId: Int): IO[Option[Nothing], (User, List[Order])] =
    for {
      optionUser <- userRepo.getUserById(userId)
      user       <- ZIO.fromOption(optionUser)
      orders     <- orderRepo.getOrdersByUserId(userId)
    } yield (user, orders)
}

object ZLayerUse extends ZIOAppDefault {
  val userRepoLayer: ULayer[UserRepo] = ZLayer.succeed(new UserRepo)

  val orderRepoLayer: ULayer[OrderRepo] = ZLayer.succeed(new OrderRepo)

  val orderServiceLayer: URLayer[UserRepo with OrderRepo, OrderService] =
    ZLayer.fromFunction(OrderService.apply _)

  val accessOrderEffect: ZIO[OrderService, Option[Nothing], (User, List[Order])] =
    ZIO.serviceWithZIO[OrderService](orderService => orderService.findOrderByUserId(userId = 1))

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    accessOrderEffect.provide(orderServiceLayer, userRepoLayer, orderRepoLayer).debug
}
