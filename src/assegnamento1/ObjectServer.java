package assegnamento1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ObjectServer {
  private static final int SPORT = 4444;
  private static final String MULTICAST_ADDRESS = "230.0.0.1";
  private static final int MULTICAST_PORT = 4446;
  private static final int NUM_CLIENTS = 3;

  // Lista per salvare i gestori dei client
  private final List<ClientHandler> handlers = new ArrayList<>();

  public void startServer() {
    try (ServerSocket server = new ServerSocket(SPORT)) {
      System.out.println("\n\nServer in ascolto sulla porta " + SPORT);

      // Accetta 3 client
      while (handlers.size() < NUM_CLIENTS) {
        Socket client = server.accept();
        System.out.println("\n\nConnessione stabilita con: " + client.getInetAddress());
        int id = handlers.size() + 1;
        ClientHandler handler = new ClientHandler(client, id);
        handlers.add(handler);
      }
      // Invia il messaggio "start" a ciascun client
      for (ClientHandler handler : handlers) {
        handler.sendStart();
      }

      // Attende il messaggio "complete" da ogni client (lettura bloccante)
      for (ClientHandler handler : handlers) {
        try {
          Message m = (Message) handler.in.readObject();  // chiamata bloccante
          if ("complete".equals(m.getMex())) {
            System.out.println("Ricevuto messaggio di completamento da client " + handler.id);
          }
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
      }

      // Invia il messaggio "terminate" a tutti i client
      for (ClientHandler handler : handlers) {
        handler.sendTerminate();
      }

      System.out.println("Gruppo terminato.");
      handlers.clear();
      System.exit(0);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class ClientHandler {
    private final Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final int id;

    public ClientHandler(Socket client, int id) {
      this.client = client;
      this.id = id;
      try {
        // Inizializza l'output stream prima di quello di input
        this.out = new ObjectOutputStream(client.getOutputStream());
        this.in = new ObjectInputStream(client.getInputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void sendStart() {
      try {
        // Formato: "start;indirizzoMulticast;portaMulticast"
        String startMsg = "start;" + MULTICAST_ADDRESS + ";" + MULTICAST_PORT;
        out.writeObject(new Message(id, NUM_CLIENTS, startMsg));
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void sendTerminate() {
      try {
        out.writeObject(new Message(id, 0, "terminate"));
        out.flush();
        client.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(final String[] args) {
    new ObjectServer().startServer();
  }
}
