package ru.krivitskaya.anna.lessons.lesson3

case class User(id: Int, fio: String)

case class Database() {
  def findById(id: Int): User = ???
}

case class Metrics() {
  def increment(metricName: String): Unit = ???
}

case class UserService(database: Database, metrics: Metrics) {
  def getUser(id: Int): User = {
    println(s"Fetching user $id")
    val user = database.findById(id)
    metrics.increment("user.fetch")
    user
  }
}