
import jp.jbxl.*;

class UDP_S
{
    public static void  main(String args[])
    {
        UDP udp = new UDP(8000);
        String recv = "";

        while(true){
            try {
                recv = udp.recvMesg();
                udp.sendMesgln("OK");
            }
            catch (Exception er) {
                //er.printStackTrace();
                recv = "接続失敗\n";
                System.out.print(recv);
                break;
            }
            System.out.print(recv);
        }
        udp.close();
    }
}
