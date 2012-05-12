import scala.actors.Actor
import scala.actors.Actor._
import java.net._
import java.io._
import scala.collection.mutable.{Map, ListBuffer}
import java.util.Random

case class IdleWorker(worker : Worker)
case class IncomingConnection(socket : Socket)

// hardcode some headers to make it look like HTTP
val headers = "HTTP/1.1 200 OK\nIncomingConnection: close\nServer: NeuroSca\nContent-Type: text/plain\n\n"

// URL routes
val versionURL = """GET /version(.*)""".r
val classifyURL = """GET /multiply/(\d)/(\d)(.*)""".r

class Worker(val id : Int, val dispatcher : Dispatcher) extends Actor {
  def act() {
    loop {
      react {
        case IncomingConnection(socket) =>
          // master sent a socket with a connection, handle it
          handleIncomingConnection(socket)
          socket.close()
          // tell the dispatcher the worker is done
          dispatcher ! IdleWorker(this)
      }
    }
  }
  
  def handleIncomingConnection(socket : Socket) = {
    val ostream = socket.getOutputStream
    val writer = new java.io.OutputStreamWriter(ostream)
    val istream = socket.getInputStream
    val reader = new java.io.LineNumberReader(new java.io.InputStreamReader(istream))
    val line = reader.readLine()
    
    if (line != null) {
      processRequest(line, writer)
    }
    // write the output to the socket
    writer.flush()
  }

  def processRequest(address : String, writer : Writer) = {    
    // pattern matching the URL
    address match {
      case classifyURL(a, b, rest) =>
        val result : Int = (a toInt)*(b toInt)
        writer.write(headers + result.toString)
      case versionURL(rest) =>
        writer.write(headers + "v0.1")
      case _ =>
        writer.write(headers + "ready")
    }
  }
}

class Dispatcher(numWorkers : Int) extends Actor
{
  val busyWorkers = Map[Int, Worker]()
  val idleWorkers = new ListBuffer[Worker]
  val random = new Random()

  // initialize workers, add the to the pool
  for (i <- 1 to numWorkers) {
    val w = new Worker(i, this)
    w.start()
    idleWorkers += w
  }

  def act() {
    loop {
      react {
        case connection: IncomingConnection =>
          if (idleWorkers.length > 0) {
            // we have an idle worker, let's use it
            val w = idleWorkers.remove(0)
            busyWorkers += {w.id -> w}
            w ! connection   
          } else {
            // no idle worker, need to message a busy one
            val w = busyWorkers.get(random.nextInt(busyWorkers.size)).get
            w ! connection
          }
        case IdleWorker(worker) =>
          // worker announced it's idle, let's add it back to the idle workers pool
          busyWorkers -= worker.id
          idleWorkers += worker
      }
    }  
  }
}

class Master() {
  def run() = {
    val socket = new java.net.ServerSocket(8080)
    val dispatcher = new Dispatcher(1024)
    
    dispatcher.start()
    println("ready on localhost:8080")
    while (true) {
      val connection = socket.accept()
      // send the socket to the dispatcher
      dispatcher ! IncomingConnection(connection)
    }
  }
}

new Master().run()
