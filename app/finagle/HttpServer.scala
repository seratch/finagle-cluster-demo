package finagle

import java.net.InetSocketAddress
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.Http
import com.twitter.finagle.Service
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http._

case class HttpServer(name: String, port: Int) {

  val address = new InetSocketAddress(port)
  val finagle = ServerBuilder()
    .codec(Http())
    .bindTo(address)
    .name(name)
    .build(new Service[HttpRequest, HttpResponse] {
      def apply(req: HttpRequest) = {
        Option(req.getHeader("X-MESSAGE-ID")).map {
          messageId =>
            ServerSubject.notifyObservers(ServerEvent(messageId.toLong, name, System.currentTimeMillis))
        }
        Future(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
      }
    })

  var isRunning = true

  def status(): String = if (isRunning) "<span class=\"label label-info\">Running</span>" else "<span class=\"label\">Stopped</span>"

  def stop() = {
    isRunning = false
    finagle.close()
  }

}
