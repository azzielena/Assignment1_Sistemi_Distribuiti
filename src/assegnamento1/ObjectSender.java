package assegnamento1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class ObjectSender
{
  private final String ADDRESS;
  private final int DPORT;
  private final MulticastSocket mSocket;

  public ObjectSender(MulticastSocket socket, String multicastAddress, int multicastPort){
    this.ADDRESS = multicastAddress;
    this.DPORT = multicastPort;
    this.mSocket = socket;
  }

  public void send(Message message)
  {
    try
    {
      InetAddress inetA       = InetAddress.getByName(ADDRESS);
      //System.out.format("Sender sends [%d] USER : %d \n", message.getMessageID(), message.getUserID());
      byte[] b = toByteArray(message);
      DatagramPacket packet = new DatagramPacket(b, b.length, inetA, DPORT);
      mSocket.send(packet);

    }
    catch (IOException e)
    { e.printStackTrace();}
  }

  private byte[] toByteArray(final Object o) throws IOException
  {
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    ObjectOutputStream    s = new ObjectOutputStream(b);

    s.writeObject(o);
    s.flush();
    s.close();
    //b.close();

    return b.toByteArray();
  }

}
