package ru.krivitskaya.anna.solutions.lesson2

trait MyZIO[-R, +E, +A]

trait Database
trait Logging
trait FullEnv extends Database with Logging

trait AppError
case class DbError(msg: String)  extends AppError
case class NetError(msg: String) extends AppError

object MyZIOVariance extends App {
  val r1: MyZIO[Database, String, Int] = ???

  val r2: MyZIO[FullEnv, String, Int] = ???

  val r3: MyZIO[Database with Logging, String, Int] = ???

  //works: val r4: MyZIO[Database with Logging, String, Int] = r1

  //fails: val r5: MyZIO[Database, String, Int] = r3

  //fails: val r6: MyZIO[Database with Logging, String, Int] = r2

  val e1: MyZIO[Nothing, DbError, Int] = ???

  val e2: MyZIO[Nothing, NetError, Int] = ???

  val e3: MyZIO[Nothing, AppError, Int] = ???

  //works: val e4: MyZIO[Nothing, AppError, Int] = e2

  //fails: val e5: MyZIO[Nothing, NetError, Int] = e3
}
