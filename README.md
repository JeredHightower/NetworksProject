# NetworksProject
This is a simple client-server application that allows a user to send equations to a server, which will parse and evaluate them, then return the result back to the client. The server is able to handle multiple clients concurrently.

## Installation

1. Open the zip file and navigate to its directory
3. Run `make` to compile the java files.

## Usage

### Starting the Server

1. In a terminal or command prompt, navigate to the directory containing the compiled TCPServer class.
2. Start the server by running the command: 

   `java TCPServer <port>`

   Replace `<port>` with the desired port number.

### Starting a Client

1. In a separate terminal or command prompt, navigate to the directory containing the compiled TCPClient class.
2. Start a client by running the command:

   `java TCPClient <ip> <port>`

   Replace `<ip>` with the IP address of the server, and `<port>` with the port number specified when starting the server.

3. When prompted, enter your name and whether or not you would like to join the server.

### Sending Equations
Equations support (),+,-,*,/,^.
Equations must be space delimited
1. Once connected to the server, you can start sending equations.
2. Type an equation and press Enter to send it to the server.
3. The server will evaluate the equation and send back the result.

### Quitting

1. To exit the program, type "QUIT" and press Enter.
2. The client will close the connection to the server and exit.
3. The server will log the client's name, start time, end time, and duration of the session to a file named "log.txt".