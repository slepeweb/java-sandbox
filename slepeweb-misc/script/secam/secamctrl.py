import socket, sys, logging
from thread import *

GO = "go"
STOP = "stop"
OK = "ok"
Q = "q"
DQ = "dq"
NR = "-1"
NEXT = "next"
GETQ = "getq"
CLRQ = "clrq"
CLOSE = "close"
STAT = "status"
HOST = ''   # Symbolic name, meaning all available interfaces
PORT = 8888 # Arbitrary non-privileged port    

logging.basicConfig(filename="/home/pi/secamctrl.log", format="%(asctime)s (%(filename)s) [%(levelname)s] %(message)s", level=logging.DEBUG)

class SecamControllerClient:
    def send_message(self, msg):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        try:
            sock.connect((HOST, PORT))
            sock.sendall(msg)
            return sock.recv(1024)
        finally:
            try:
                sock.close()
            except:
                logging.warn("Failed to close the socket")
            
        return NR;

class SecamController:
    
    def __init__(self):
        self.server = None
        self.status = GO
        self.queue = []
        self.counter = 0
     
    def start(self):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        logging.debug('Socket created')
        
        #Bind socket to local host and port
        try:
            self.server.bind((HOST, PORT))
        except socket.error as msg:
            logging.error('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
            sys.exit()
             
        logging.debug('Socket bind complete')
         
        #Start listening on socket
        self.server.listen(5)
        logging.debug('Socket now listening')
         
        #now keep talking with the client
        try:
            while True:
                #wait to accept a connection - blocking call
                conn, addr = self.server.accept()
                start_new_thread(self.service, (conn,))
        except KeyboardInterrupt:
            logging.info("Keyboard interrupt")
        finally:
            self.server.close()
                 
    def service(self, conn):
        while True:
            # Blocking call: Wait for a) message from client, or b) closed connection
            dirty = conn.recv(1024)
            if not dirty: 
                break;
            
            data = dirty.strip(" \t\n\r")
            
            if data == STAT:
                conn.sendall(self.get_status())
            elif data == GETQ:
                r = "|".join(self.queue) if len(self.queue) > 0 else NR
                conn.sendall(r)
            elif data == CLRQ:
                self.clear_all_messages()
                conn.sendall(OK)
            else:
                parts = data.split("|")
            
                if len(parts) == 2:
                    if parts[0] == Q:                    
                        response = self.queue_message(parts[1])           
                    else:
                        response = NR
                else:
                    response = NR
                    
                conn.sendall(response)
         
        #came out of loop
        conn.close()

    def queue_message(self, msg):
        self.counter += 1
        s = ",".join([str(self.counter), msg])
        self.queue.append(s)
        logging.info("Message added to queue [%s]" % s)
        return s
        
    def get_message(self):
        if len(self.queue) > 0:
            return self.queue[0]
        return None
        
    def dequeue_message(self, msg):
        if msg in self.queue:
            self.queue.remove(msg)
            return True
        return False

    def clear_all_messages(self):
        self.queue = []
        
    def get_queue(self):
        return self.queue
    
    def set_status(self, s):
        if s in [STOP, GO]:
            self.status = s
        
    def get_status(self):
        return self.status

if __name__ == "__main__":
    SecamController()