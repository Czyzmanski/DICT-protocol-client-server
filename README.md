# DICT-protocol-client-server
My implementation of simplified DICT protocol written with the use of socket programming in Java.
Project consists of a desktop client written in JavaFx, central server and servers responsible for each language. Client, after receiving word to translate, abbreviation of the target language and port number on which client expects to receive response, connects to central server which then delegates request to appropriate language server. The last mentioned responds to the client which is waiting on the port given by the client's user.
