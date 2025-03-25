package assegnamento1;

import java.io.Serializable;


public final class Message implements Serializable
{
  private int userID;
  private int messageID;
  private String mex;

  public Message(final int u, final int m, final String r)
  {
    this.userID    = u;
    this.messageID = m;
    this.mex = r;
  }

  public int getUserID()
  {
    return this.userID;
  }
  public int getMessageID()
  {
    return this.messageID;
  }
  public String getMex() {
    return this.mex;
  }


  public void setMessageID(final int c)
  {
    this.messageID = c;
  }
  public void setMex(final String c)
  {
    this.mex = c;
  }
}
