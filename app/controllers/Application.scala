package controllers

import play.api._
import play.api.mvc._

import play.api.libs.Comet
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import java.net._

import akka.util.duration._
import com.twitter.common.zookeeper._
import com.twitter.common.quantity.{Time, Amount}
import com.twitter.finagle.zookeeper._

import finagle._
import models._

object Application extends Controller {

  val log = Logger(classOf[Application])

  val zkClient = new ZooKeeperClient(Amount.of(100, Time.MILLISECONDS), new InetSocketAddress(2181))
  val serverSet = new ServerSetImpl(zkClient, "/finagles")
  val serverSetCluster = new ZookeeperServerSetCluster(serverSet)

  val localhostServers = new collection.mutable.HashMap[String, HttpServer]

  object MessageObserver extends ServerObserver {
    override def notify(event: ServerEvent): Unit = {
      MessageStore.setAsSuccess(event.id, event.server)
    }
  }

  ServerSubject.addObserver(MessageObserver)

  def index = Action {
    Ok(views.html.index())
  }

  def startServer(port: Int) = Action {
    try {
      val name = "localhost:" + port
      val server = HttpServer(name, port)
      log.info("Starting " + server + "...")
      localhostServers.update(name, server)
      serverSetCluster.join(server.address)
    } catch {
      case e =>
    }
    Ok("Started!")
  }

  def stopServer(port: Int) = Action {
    localhostServers.get("localhost:" + port).map {
      server =>
        log.info("Stopping " + server + "...")
        server.stop()
    }
    Ok("Stopped!")
  }

  def serverStatus(port: Int) = Action {
    Ok(localhostServers.get("localhost:" + port).map(_.status).getOrElse("<span class=\"label\">Stopped</span>"))
  }

  def startClient(sleep: Long) = Action {
    stopClient()
    Client.start(serverSetCluster, sleep)
    Ok("Started!")
  }

  def stopClient() = Action {
    Client.stop()
    Ok("Stopped!")
  }

  // ---------------------------------
  // comet

  val _monitoringServers = Enumerator.fromCallback {
    () =>
      val serverObserver = new ServerObserver
      ServerSubject.addObserver(serverObserver)
      Thread.sleep(500L)
      ServerSubject.removeObserver(serverObserver)
      val result = "<table class=\"table\">" + serverObserver.events.groupBy {
        event => event.server
      }.map {
        case
          (server, events) => "<tr><td>" + server + "</td>" + events.map {
          event => "<td>" + event.id + "</td>"
        }.mkString
      }.mkString + "</table>"
      Promise.timeout(Some(result), 0 milliseconds)
  }

  def monitoringServers = Action {
    Ok.stream(_monitoringServers &> Comet(callback = "parent.monitoringServersCallback"))
  }

  val _monitoringClient = Enumerator.fromCallback {
    () =>
      val clientObserver = new ClientObserver
      ClientSubject.addObserver(clientObserver)
      Thread.sleep(2000)
      val result = "<table class=\"table\">" + clientObserver.events.map {
        event =>
          "<tr><td>" + event.message + "</td></tr>"
      }.mkString + "</table>"
      ClientSubject.removeObserver(clientObserver)
      Promise.timeout(Some(result), 0 milliseconds)
  }

  def monitoringClient = Action {
    Ok.stream(_monitoringClient &> Comet(callback = "parent.monitoringClientCallback"))
  }

  lazy val _monitoringMessages = Enumerator.fromCallback {
    () =>
      val result =
        try {
          val allCount = MessageStore.all
          val failureCount = MessageStore.failures
          val successRate = (allCount - failureCount).toDouble / allCount.toDouble * 100
          "<table class=\"table\">" +
            "<tr><td>All</td><td>" + allCount + "</td>" +
            "<tr><td>Failures</td><td>" + failureCount + "</td>" +
            "<tr><td>Rate</td><td>" + "%.3f".format(successRate) + "%</td>" +
            "</table>"
        } catch {
          case e =>
            log.error("Failed to count messages.", e)
            "--- Oops! ---"
        }
      Promise.timeout(Some(result), 100 milliseconds)
  }

  def monitoringMessages = Action {
    Ok.stream(_monitoringMessages &> Comet(callback = "parent.monitoringMessagesCallback"))
  }

}
