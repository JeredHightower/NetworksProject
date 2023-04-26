import java.io.*;
import java.net.*;

class TCPClient {

  public static void main(String argv[]) throws Exception {
    String sentence = "";
    String modifiedSentence = "";

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    // ASK FOR NAME HERE //////////////////////////////////
    System.out.print("What is your name?: ");
    String name = inFromUser.readLine();

    // ASK 'JOIN SERVER? (y/n)' ///////////////////////////
    System.out.print("Join Server? (y/n): ");
    String answer = inFromUser.readLine().toLowerCase();
    while(!answer.equals("y")){
      if(answer.toLowerCase().equals("n"))
        return;
      
      System.out.print("Join Server? (y/n): ");
      answer = inFromUser.readLine().toLowerCase();
    }

    // Establish Connection
    Socket clientSocket = null;
    for (int i = 0; i < 3; i++) {
      try {
        clientSocket = new Socket("127.0.0.1", 6789);
        System.out.println("Connection established");
        break;
      } catch (Exception e) {
        System.out.println("Connect failed, waiting and trying again");
        try {
          Thread.sleep(2000);// 2 seconds
        } catch (InterruptedException ie) {
          ie.printStackTrace();
        }
      }
    }

    if (clientSocket == null) {
      System.out.println("Connection timed out");
      return;
    }

    // Message Sent Stream
    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

    // Message Received Stream
    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    // SEND NAME HERE /////////////////////////////////////////
    outToServer.writeBytes(name + '\n');

    // Send many Messages
    while (!(sentence.equalsIgnoreCase("QUIT"))) {
      // Message to sent
      sentence = inFromUser.readLine();

      outToServer.writeBytes(sentence + '\n');

      if (sentence.equalsIgnoreCase("QUIT"))
        break;

      // Message received
      modifiedSentence = inFromServer.readLine();

      System.out.println("FROM SERVER: " + modifiedSentence);
    }

    clientSocket.close();

  }
}
