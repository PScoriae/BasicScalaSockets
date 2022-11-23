import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import scala.io.StdIn.readLine
import java.net.ConnectException

object Client extends App {
  // main app logic
  def clientProcess() {
    // initialise connection settings
    val address = "localhost"
    val port = 6969

    println("Attempting to connect to server...")
    val socket = new Socket(address, port)
    println("Connected to server!\n")

    // initialise streams
    val inputStream = new DataInputStream(socket.getInputStream())
    val outputStream = new DataOutputStream(socket.getOutputStream())

    // get user input
    println(
      "Please enter the ISBN of the book and I will fetch its price from the server."
    )
    println("You can exit at any time by typing 'exit'")

    val userInput = readLine("\n")
    // exit application if they want
    if (userInput == "exit") return println("Goodbye.")

    // send userInput to server
    outputStream.writeBytes(s"$userInput\n")

    // get response from server
    val res = inputStream.readLine()

    // handle server response for client
    // ISBN not found
    if (res == "ISBN not found")
      println(s"Server could not find book with ISBN: $userInput!\n")
    // some runtime error like networking from catch block in server
    else if (res == "An error occured.") println("An error occured.\n")
    // server is able to get data from books map
    else println(s"The server says the price of $userInput is: $res\n")

    // ask user if they would like to repeat
    loopFunction(clientProcess, "Would you like to fetch prices again?")
  }

  // function to attempt connection to server
  def attemptServerConn() {
    try clientProcess()
    catch {
      case ce: ConnectException => {
        println("Could not connect to server!")
        loopFunction(
          attemptServerConn,
          "Would you like to try connecting again?"
        )
      }

      case e: Exception => println("Some error occured.")
    }
  }

  // function that asks user if they would like to repeat a certain function passed as an argument
  def loopFunction(repeatFunction: () => Unit, userPrompt: String) {
    val repeat = readLine(s"$userPrompt (Y/n)\n").toLowerCase()
    if (List("y", "").contains(repeat)) return repeatFunction()
    else if (List("n", "no", "exit").contains(repeat))
      return println("\nGoodbye.")
    else {
      println("I'm not sure what you mean.")
      return loopFunction(repeatFunction, userPrompt)
    }
  }

  // start client logic
  attemptServerConn()
}
