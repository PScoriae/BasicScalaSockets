import java.io.{DataInputStream, DataOutputStream}
import java.net.{ServerSocket, Socket}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn.readLine

object Server extends App {

  // function for concurrent processing
  def processSocket(x: Socket): Unit = {
    val clientSocket2: Future[Socket] = Future { server.accept() }
    clientSocket2.foreach(x => processSocket(x))

    // initialise streams
    val inputStream = new DataInputStream(x.getInputStream())
    val outputStream = new DataOutputStream(x.getOutputStream())

    // get user input
    val userInput = inputStream.readLine()
    println(s"User's input: $userInput")

    // try to fetch book from books Map
    var bookPrice: String = ""
    if (hasIsbn(userInput)) {
      bookPrice = books(userInput)
      println(s"Returned price of $userInput: $bookPrice")
    } else {
      bookPrice = "ISBN not found"
      println(s"ISBN $userInput not in books database")
    }

    // handle user input
    try {
      outputStream.writeBytes(bookPrice)
    } catch {
      // catching errors
      case e: Exception => {
        outputStream.writeBytes("An error occured.")
        println("An error occured.")
      }
    }

    x.close()
    println("socket closed\n")
  }

  def hasIsbn(isbn: String): Boolean = {
    return (books.contains(isbn))
  }

  // define book pricing
  val books = Map(
    "9780921303897" -> "RM125",
    "9780193483723" -> "RM30",
    "9781302948230" -> "RM175",
    "9781234523111" -> "RM12.90"
  )

  // start new server
  val port = 6969
  val server = new ServerSocket(port)
  println(s"Server is running on port $port!")

  var flag = true
  // leave server running until user interrupt
  while (flag) {

    val clientSocket: Future[Socket] = Future { server.accept() }

    clientSocket.foreach(x => processSocket(x))
    // prevent code from hitting server.close() until user interrupts
    readLine("Press enter to quit\n\n")
    server.close()
    flag = false
  }

}
