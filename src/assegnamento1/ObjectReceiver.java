package assegnamento1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Random;


public class ObjectReceiver
{
  private static final int SIZE = 1024;
  private final MulticastSocket mSocket;
  private final int clientId;
    private final int[] arrayCounters;
  private final ObjectSender sender;
  private static final double LP = 0.9;
  private final Random random = new Random();


  public ObjectReceiver(MulticastSocket socket, int clientId, int numNodes, ObjectSender sender) {
    this.mSocket = socket;
    this.clientId = clientId;
    this.arrayCounters = new int[numNodes]; // gli elementi sono inizializzati a 0
    this.sender = sender;
  }

  public void receive()
  {
    byte[] buf = new byte[SIZE];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);

    try {
      mSocket.receive(packet);
      ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
      Object o = ois.readObject();


      if (o instanceof Message m) {
        int senderId = m.getUserID();
        String content = m.getMex();

        if (senderId == clientId) {return;} // Ignora i messaggi inviati da se stesso

        //potrebbe non essermi arrivato questo messaggio...
        if (random.nextDouble() > LP) {
          System.out.println("Ho perso un messaggio da "+ senderId);
          return; //esce dalla lettura del messaggio
        }

        if (content.startsWith("error;")) {
          // Il formato atteso è "error;num_id_mittenteOriginale;N_messaggio_perso; N_messaggio_arrivato"
          String[] parts = content.split(";");
          int errorSenderId = Integer.parseInt(parts[1]);
          int missedMessage = Integer.parseInt(parts[2]);
          int currentCounter = Integer.parseInt(parts[3]);

          if (clientId == errorSenderId) { //non sono arrivati i miei messaggi
            for (int i = missedMessage; i <= currentCounter; i++) {
              Message resendMsg = new Message(clientId, i, "requested message");
              System.out.println("REINVIO messaggio: " + i );
              sender.send(resendMsg);
            }
          }

        } else {
          int receivedCounter = m.getMessageID();
          int expectedCounter = arrayCounters[senderId - 1] + 1;
          if (receivedCounter == expectedCounter) {
            arrayCounters[senderId - 1]++;
            System.out.format("Receiver got: [%d] from USER: %d, content: %s%n", receivedCounter, senderId, m.getMex());
          }
          else if (receivedCounter > expectedCounter) {
            requestMissingMessages(senderId, expectedCounter, receivedCounter);
          }
        }
      }
    }
    catch (SocketException se) {
      if (mSocket.isClosed()) {
        // Il socket è stato chiuso
        return;
      }
      se.printStackTrace();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

}

  private void requestMissingMessages(int senderId, int expectedCounter, int receivedCounter) {
    System.out.println("Richiedo i messaggi mancanti dal Client "
            + senderId + " per i contatori da " + expectedCounter + " a " + receivedCounter);

    String errorContent = "error;" + senderId + ";" + expectedCounter + ";" + receivedCounter;
    Message errorMsg = new Message(clientId, 0, errorContent);
    sender.send(errorMsg);
  }

  }

