# Compile and Execute
## Navigate to directory containing all files
make
java TCPServer "your port number here"
java TCPClient "IP address of server" "port number of server"

# Description
# NetworksProject
Client can send messages to the server in the form of space delimited equations.
Equations support (),+,-,*,/,^.
Eg. ( 1 + 4 ) ^ 7 / 2

Client can type 'quit' or give no input to close connection to the server.

The server will reply with the answer to the equation if it is well-formed.
Eg. FROM SERVER: 3.00