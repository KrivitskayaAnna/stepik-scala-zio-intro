package ru.krivitskaya.anna.lessons.lesson3

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FutureEffects extends App {
  val atomic = new AtomicInteger()

  val f1 = Future {
    println(s"Отправляю запрос ${atomic.incrementAndGet()} в БД")
    42
  }

  val f2 = Future {
    println(s"Отправляю запрос ${atomic.incrementAndGet()} в БД")
    42
  }

  val f = Future {
    println(s"Отправляю запрос ${atomic.incrementAndGet()} в БД")
    42
  }

  //called just once
  val f3 = f
  val f4 = f

  Thread.sleep(1000)
}
