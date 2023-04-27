import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.PriorityQueue;
import java.util.Stack;

class TCPServer {
  public static void main(String argv[]) throws Exception {
    // Create log.txt for logging
    new File("log.txt");

    ServerSocket welcomeSocket = null;
    Socket connectionSocket = null;

    try {
        // Create a new ServerSocket that listens to port 6789 for incoming client connections.
      welcomeSocket = new ServerSocket(6789);
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true) {

      try {
          // Wait for a client to connect and accept the connection.
        connectionSocket = welcomeSocket.accept();
      } catch (IOException e) {
        e.printStackTrace();
      }

      // new thread for a client
      new ClientThread(connectionSocket).start();
    }
  }
}

class ClientThread extends Thread {
  protected Socket socket;

  public ClientThread(Socket clientSocket) {
    this.socket = clientSocket;
  }

  public void run() {
    BufferedReader inFromClient = null;
    DataOutputStream outToClient = null;
    Instant start = Instant.now();

    try {
         // Create a BufferedReader and a DataOutputStream to communicate with the client.
      inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      outToClient = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      return;
    }

    String clientSentence;
    String reply;
    String name = "";

    while (true) {
      try {
        // Read a line of input from the client.
        clientSentence = inFromClient.readLine();
           // Close the socket if the client disconnected or sent "QUIT" command.
        if ((clientSentence == null) || clientSentence.equalsIgnoreCase("QUIT")) {
          socket.close();

          // LOG NAME AND TIME AND HOW LONG CONNECTED
          // ////////////////////////////////////////////////
          Instant end = Instant.now();
          logEnd(name, start, end);
          System.out.println(start);

          return;
        } 
        else if(name.isEmpty()){
          // If this is the first line of input, assume it is the client's name and log the start of the session to log.txt.
          name = clientSentence;
          // LOG ENTRY TIME /////////////////////////////
          logStart(name, start);
        }
        else {
          // CALL EQUATION PARSER

          Double ans = equationParser(clientSentence);
          if(ans == null)
              reply = "Invalid Format for Equation, ex. 1 + ( 2 - 3 ) * 10 / 2 ^ 2";
          else
              reply = String.format("The answer is: %.2f", ans);

          reply = reply + "\n";
          outToClient.writeBytes(reply);
        }
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }
  }
   // Log the start of a session to log.txt.

  public void logStart(String name, Instant start){
      try {
        String output = name + " login: " + start + "\n";

        FileWriter writer = new FileWriter("log.txt", true);
        writer.write(output);
        writer.close();

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }
 // Log the end of a session to log.txt.
  public void logEnd(String name, Instant start, Instant end){
    try {
      Duration timeElapsed = Duration.between(start, end);
      String output = name + " logout: " + end + " Duration: " + timeElapsed.toSeconds() +"s\n";

      FileWriter writer = new FileWriter("log.txt", true);
      writer.write(output);
      writer.close();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // Shunting Algorithm
  public Double equationParser(String input) {

    try {
      //input String into an array of Strings, using a space as the delimiter
      String[] equation = input.split(" ");
      //operator_stack is a Stack that will be used to keep track of the operators as they are processed
      Stack<String> operator_stack = new Stack<String>();
      //output_queue is a PriorityQueue that will be used to keep track of the operands as they are processed.
      PriorityQueue<Double> output_queue = new PriorityQueue<Double>();

      for (String item : equation) {
        //checks if the current item is an operator 
        //checks the precedence of item against the operator at top of  operator_stack. 
         // If the current operator has higher precedence or same & left
        // pops  top operator off  stack and performs  operation on  top two operands in output_queue
        if (item.matches("\\^|\\*|/|\\+|-")) {
          while ((!operator_stack.empty() && !operator_stack.peek().equals("("))
              && (isHigherPrecedence(item, operator_stack.peek())
                  || samePrecedenceLeftAssociative(item, operator_stack.peek()))) {

            String op = operator_stack.pop();
            Double num1 = output_queue.remove();
            Double num2 = output_queue.remove();
            output_queue.add(performOperation(op, num2, num1));
          }
          operator_stack.push(item);
        //case where the current item is an open parenthesis.
          // pushes the open parenthesis onto the operator_stack
        } else if (item.equals("(")) {
          operator_stack.push(item);
          // case where the current item is a close parenthesis.
          // pops operators off the operator_stack and performs operations on  top two operands in output_queue until it reaches the open parenthesis @ top of  operator_stack.
         // Once it has processed all operators between the parentheses  pops the open parenthesis off the operator_stack.
        } else if (item.equals(")")) {
          while ((!operator_stack.peek().equals("("))) {
            if (!operator_stack.empty()) {
              String op = operator_stack.pop();
              Double num1 = output_queue.remove();
              Double num2 = output_queue.remove();
              output_queue.add(performOperation(op, num2, num1));
            }
          }

          if (operator_stack.peek().equals("(")) {
            operator_stack.pop();

          } else
            return null;
        //if item is a #, add to output queue
        } else
          output_queue.add(Double.parseDouble(item));
      }
      //after iterating over all items, the code pops the remaining operators and 
      //evaluates them. Final results is the only item left in the queue.

      while (!operator_stack.empty()) {
        if (operator_stack.peek().equals("("))
          return null;
        String op = operator_stack.pop();
        Double num1 = output_queue.remove();
        Double num2 = output_queue.remove();
        output_queue.add(performOperation(op, num2, num1));
      }

      return output_queue.remove();

    } catch (Exception e) {
      return null;
    }
  }
//helper method that evaluates two operands with the operator 
  public Double performOperation(String op, Double num1, Double num2) {

    if (op.equals("^"))
      return Math.pow(num1, num2);
    else if (op.equals("*"))
      return num1 * num2;
    else if (op.equals("/"))
      return num1 / num2;
    else if (op.equals("+"))
      return num1 + num2;
    else if (op.equals("-"))
      return num1 - num2;

    return -1.0;

  }
//used to check if one operator has higher precedence than the other
  public boolean isHigherPrecedence(String op1, String op2) {
    if (getPrecedence(op1) > getPrecedence(op2))
      return true;

    return false;
  }
//helper method that checks if 2 operators have same precedence and left-associativr
  public boolean samePrecedenceLeftAssociative(String op1, String op2) {
    if (getPrecedence(op1) == getPrecedence(op2) && op1.matches("/|\\^"))
      return true;

    return false;
  }
//returns precedence of operator
  public int getPrecedence(String op) {

    if (op.equals(")"))
      return 4;

    if (op.equals("^"))
      return 3;

    if (op.equals("*") || op.equals("/"))
      return 2;

    if (op.equals("+") || op.equals("-"))
      return 1;

    return -1;
  }
}
