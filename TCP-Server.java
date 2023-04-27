import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.PriorityQueue;
import java.util.Stack;

class TCPServer {
  public static void main(String argv[]) throws Exception {
    // Create log.txt
    new File("log.txt");

    ServerSocket welcomeSocket = null;
    Socket connectionSocket = null;

    try {
      welcomeSocket = new ServerSocket(6789);
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true) {

      try {
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
        clientSentence = inFromClient.readLine();

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

  // NEED TO ADD EQUATION PARSER
  public Double equationParser(String input) {

    try {
      String[] equation = input.split(" ");
      Stack<String> operator_stack = new Stack<String>();
      PriorityQueue<Double> output_queue = new PriorityQueue<Double>();

      for (String item : equation) {
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

        } else if (item.equals("(")) {
          operator_stack.push(item);

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

        } else
          output_queue.add(Double.parseDouble(item));
      }

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

  public boolean isHigherPrecedence(String op1, String op2) {
    if (getPrecedence(op1) > getPrecedence(op2))
      return true;

    return false;
  }

  public boolean samePrecedenceLeftAssociative(String op1, String op2) {
    if (getPrecedence(op1) == getPrecedence(op2) && op1.matches("/|\\^"))
      return true;

    return false;
  }

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