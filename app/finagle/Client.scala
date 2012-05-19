package finagle

import models.MessageStore
import com.twitter.finagle.http.Http
import com.twitter.finagle.Service
import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.builder.{Cluster, ClientBuilder}
import java.net.SocketAddress
import play.api.Logger

object Client {

  val log = Logger("Client")

  var lastMessageId: Int = 0

  def nextMessageId = {
    var next = lastMessageId
    this.synchronized {
      lastMessageId += 1
      next = lastMessageId
    }
    next
  }

  var client: Service[HttpRequest, HttpResponse] = null
  var clientThread: Thread = null

  def start(serverSetCluster: Cluster[SocketAddress], sleep:Long) = {
    client = ClientBuilder()
      .cluster(serverSetCluster)
      .hostConnectionLimit(1) // TODO testing
      .codec(Http())
      .build()
    clientThread = new Thread(clientExecutor(sleep))
    clientThread.start()
  }

  def stop() = {
    if (clientThread != null && !clientThread.isInterrupted) {
      clientThread.interrupt()
    }
  }

  def clientExecutor(sleep: Long) = new Runnable {
    def run() {
      while (true) {
        try {
          var messageId = nextMessageId
          val req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
          req.setHeader("X-MESSAGE-ID", messageId)
          client.apply(req) onSuccess {
            res => log.debug("Succceded: " + messageId)
          } onFailure {
            error =>
              MessageStore.setAsFailure(messageId, error.getMessage)
              ClientSubject.notifyObservers(ClientEvent(error.getMessage))
              log.warn("Failed to send message (" + messageId + ") becuase of " + error)
          }
          Thread.sleep(sleep)
        } catch {
          case interrupt: InterruptedException => Thread.interrupted()
          case e => log.error("Client error!", e)
        }
      }
    }
  }

}
