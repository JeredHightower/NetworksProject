import java.io.*;
import java.net.*;

class TCPServer {

  // NEED TO ADD EQUATION PARSER

  public static void main(String argv[]) throws Exception {
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

    try {
      inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      outToClient = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      return;
    }

    String clientSentence;
    String capitalizedSentence;

    // GET NAME HERE //////////////////////////////////
    // LOG NAME AND TIME /////////////////////////////

    
    while (true) {
      try {
        clientSentence = inFromClient.readLine();
        
        if ((clientSentence == null) || clientSentence.equalsIgnoreCase("QUIT")) {
          socket.close();

          // LOG NAME AND TIME AND HOW LONG ////////////////////////////////////////////////

          return;
        } else {
          // CHANGE SERVER RESPONSE TO MATH, CALL EQUATION PARSER //////////////////////////////
          capitalizedSentence = clientSentence.toUpperCase() + '\n';
          outToClient.writeBytes(capitalizedSentence);
        }
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }
  }
}