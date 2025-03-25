package assegnamento1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.ThreadLocalRandom;

public class ObjectClient {
    private static final int SPORT = 4444;
    private static final String SHOST = "localhost";
    private int NUM_NODES;
    private int id;
    private MulticastSocket mSocket;
    private int multicastPort;
    private String multicastAddress;
    private ObjectSender multicastSender;
    private ObjectReceiver multicastReceiver;
    private int countMyMessage = 0;


    public void start() throws IOException, InterruptedException {
        Socket client = new Socket(SHOST, SPORT);
        ObjectOutputStream os = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(client.getInputStream());
        try  {
            System.out.format("\n\nClient connesso al Server, in attesa del messaggio di start...");
            Object o = is.readObject();
            Message msg = (Message) o;
            String startMsg = msg.getMex();
            this.id = msg.getUserID();
            this.NUM_NODES = msg.getMessageID();
            System.out.format("\n\nreceived USERID: %d from Server\n", id);
            // formato "start;indirizzo;porta"
            String[] parts = startMsg.split(";");
            if (parts.length == 3 && parts[0].equals("start")) {
                multicastAddress = parts[1];
                multicastPort = Integer.parseInt(parts[2]);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //AVVIO COMUNICAZIONE MULTICAST
        InetAddress inetA       = InetAddress.getByName(multicastAddress);
        InetSocketAddress group = new InetSocketAddress(inetA, multicastPort);
        NetworkInterface netI   = NetworkInterface.getByInetAddress(inetA);
        MulticastSocket mSocket       = new MulticastSocket(multicastPort);

        mSocket.joinGroup(group, netI);

        multicastSender = new ObjectSender(mSocket, multicastAddress, multicastPort);
        multicastReceiver = new ObjectReceiver(mSocket,id, NUM_NODES, multicastSender);

        // Avvia un thread per il ricevitore multicast
        new Thread(() -> {
            while (true) {
                multicastReceiver.receive();
            }
        }).start();

        // invio di 100 messaggi multicast
        while (countMyMessage < 100) {
            countMyMessage++;
            String content = "new message";
            Message m = new Message(id, countMyMessage, content);
            System.out.println("Client " + id + " sends: " + content + " (counter: " + countMyMessage + ")");
            multicastSender.send(m);

            // Genera un ritardo casuale tra 2s e 5s prima di inviare il messaggio successivo
            int delay = ThreadLocalRandom.current().nextInt(2000, 5001);
            Thread.sleep(delay);
        }

        System.out.println("Client " + id + " ha inviato 100 messaggi.");

        // notifica al server (via TCP) il completamento.
        Message completeMsg = new Message(id, 101, "complete");
        os.writeObject(completeMsg);
        os.flush();
        System.out.println("Client " + id + " ha notificato al server il completamento.");

        //NOTIFICA COMPLETAMENTO AL SERVER
        try {
            Message termResp = (Message) is.readObject();
            if ("terminate".equals(termResp.getMex())) {
                System.out.println("Ricevuto messaggio di terminazione dal server. Termino il client.");
                mSocket.leaveGroup(new InetSocketAddress(InetAddress.getByName(multicastAddress), multicastPort), mSocket.getNetworkInterface());
                mSocket.close();
                System.exit(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(final String[] args) throws IOException, InterruptedException {
        new ObjectClient().start();
    }
}
