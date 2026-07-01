package ru.krivitskaya.anna.lessons.lesson10

import zio._

case class ConnectionParams(url: String, timeout: Int)

case class ConnectionService(connectionParams: ConnectionParams) {
  def sendRequest[T](requestUri: String, requestBody: T): Task[String] =
    ZIO.attempt(s"answer for request with uri $requestUri and body $requestBody")
}

case class AuthRequestBody(user: String, pwd: String)

object ZlayerCombine extends ZIOAppDefault {
  val connectionParamsLayer: ULayer[ConnectionParams] =
    ZLayer.succeed(ConnectionParams("http://host:port", 10000))

  val connectionServiceLayer: URLayer[ConnectionParams, ConnectionService] =
    ZLayer.fromFunction(ConnectionService.apply _)

  val authRequestBodyLayer: ULayer[AuthRequestBody] =
    ZLayer.succeed(AuthRequestBody("test_user", "test_pwd"))

  // вертикальная композиция:
  //  ZLayer[RIn, E, ROut] >>> ZLayer[ROut, E1, ROut2] = ZLayer[RIn, E1, ROut2]
  // горизонтальная композиция:
  //  ZLayer[RIn, E, ROut] ++ ZLayer[RIn2, E1, ROut2] = ZLayer[RIn with RIn2, E1, ROut1 with ROut2]
  // вертикальная композиция с возвратом всех:
  //  ZLayer[RIn, E, ROut] >+> ZLayer[ROut, E1, ROut2] = ZLayer[RIn, E1, ROut with ROut2]
  val manualLayers: ULayer[AuthRequestBody with ConnectionService] =
    authRequestBodyLayer ++ (connectionParamsLayer >>> connectionServiceLayer)

  val automatedLayers: ULayer[AuthRequestBody with ConnectionService] =
    ZLayer.make[AuthRequestBody with ConnectionService](
      connectionServiceLayer,
      authRequestBodyLayer,
      connectionParamsLayer
    )

  override def run =
    (for {
      authRequestBody <- ZIO.service[AuthRequestBody]
      authResponse <- ZIO.serviceWithZIO[ConnectionService](
        _.sendRequest("/login", authRequestBody)
      )
      _ <- ZIO.logInfo(s"authResponse: $authResponse")
    } yield ())
      .provideSome[AuthRequestBody](connectionParamsLayer, connectionServiceLayer)
      .provide(authRequestBodyLayer)
}
