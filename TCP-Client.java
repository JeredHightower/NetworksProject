import java.io.*;
import java.net.*;

class TCPClient {

  public static void main(String argv[]) throws Exception {
    String sentence = "";
    String modifiedSentence = "";

    String ip = "";
    int port = 6827;

    if(argv.length != 2){
      System.out.println("Bad arguments");
      return;
    }

    try {
      ip = argv[0];
      port = Integer.parseInt(argv[1]);
    } catch (Exception e) {
      System.out.println("Bad port");
    }
    //creating bufferedreader obj to read input from the user
    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    // ASK FOR NAME HERE //////////////////////////////////
    System.out.print("What is your name?: ");
    String name = inFromUser.readLine();

    if(name.isBlank())
      return;

    // ASK 'JOIN SERVER? (y/n)' ///////////////////////////
    System.out.print("Join Server? (y/n): ");
    String answer = inFromUser.readLine().toLowerCase();
    while(!answer.equals("y")){
      if(answer.toLowerCase().equals("n"))
        return;
      
      System.out.print("Join Server? (y/n): ");
      answer = inFromUser.readLine().toLowerCase();
    }

    // Establish Connection with server 
    Socket clientSocket = null;
    for (int i = 0; i < 3; i++) {
      try {
        clientSocket = new Socket(ip, port);
        System.out.println("Connection established");
        break;
      } catch (Exception e) {
        System.out.println("Connect failed, waiting and trying again");
        //if connection fails, wait for 2 seconds and retry
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

    //creat dataoutputstream obj for Message Sent Stream
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
//if user types QUIT, messages stop being sent
      if (sentence.equalsIgnoreCase("QUIT"))
        break;

      // Message received
      modifiedSentence = inFromServer.readLine();

      System.out.println("FROM SERVER: " + modifiedSentence);
    }
  //close connection 
    clientSocket.close();

  }
}
