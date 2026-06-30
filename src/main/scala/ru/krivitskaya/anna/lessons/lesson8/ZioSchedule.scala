package ru.krivitskaya.anna.lessons.lesson8

import zio._

object ZioSchedule extends ZIOAppDefault {
  val successEffect =
    ZIO.succeed(10).tap(p => ZIO.logInfo(s"Success: $p")) //*> ZIO.sleep(1.seconds)

  val failEffect =
    ZIO.fail("Uh oh!").tapError(p => ZIO.logError(p)) *> ZIO.sleep(1.seconds)

  val recurSchedule = Schedule.recurs(3)

  val fixedSchedule = Schedule.fixed(2.seconds)

  val spacedSchedule = Schedule.spaced(2.seconds)

  val foreverSchedule = Schedule.forever

  // Экспоненциальный backoff
  val exponentialSchedule = Schedule.exponential(1.seconds)

  val cappedExponential = Schedule.exponential(1.second) || Schedule.spaced(30.seconds)

  val jitteredExponential = Schedule.exponential(1.second).jittered

  // Композиция: && и ||
  val timeWithN = Schedule.fixed(2.seconds) && Schedule.recurs(3)

  val timeOrN = Schedule.fixed(2.seconds) || Schedule.recurs(3)

  // Временные расписания
  val everyMinuteAt1     = Schedule.secondOfMinute(1)

  val everyHourAt30      = Schedule.minuteOfHour(30)

  val everyDayAtMidnight = Schedule.hourOfDay(0)

  val everyMonday        = Schedule.dayOfWeek(1)

  // Практические паттерны
  val retryPolicy = Schedule.exponential(100.millis).jittered && Schedule.recurs(5)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    successEffect.repeat(exponentialSchedule).debug
}
