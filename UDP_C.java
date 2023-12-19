
import jp.jbxl.*;

class UDP_C
{
    public static void  main(String args[])
    {
        UDP udp = new UDP("202.26.159.139", 8000);
        String recv = "";

        try {
            udp.sendMesgln("OK");
            recv = udp.recvMesg();
        }
        catch (Exception er) {
            //er.printStackTrace();
            recv = "接続失敗\n";
        }

        System.out.print(recv);
        udp.close();
    }
}
