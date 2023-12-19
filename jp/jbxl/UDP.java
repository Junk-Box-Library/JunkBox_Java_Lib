
package jp.jbxl;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.io.*;


/**
 * UDP Berkeley Socketの基本クラス<br>
 * @author  Fumi.Iseki
 */
public class UDP
{
    private final static int BUFSIZE = 512;

    private DatagramSocket sock = null;
    
    private InetAddress ipAddress = null;
    private InetAddress myIPAddress = null;
    private int portNo = -1;
    
    /**
     *  オブジェクトの状態を表すフラグ．false：オブジェクトは正常に動作． true：オブジェクトでエラーが発生．
     */
    public boolean  errFlag = false;

    /**
     * オブジェクトが動作しているマシンのマシン名
     */
    public String   myHostname = null;


    /**
     * UDPオブジェクトのコンストラクタ．ソケットの作成は行わない．
     */
    public UDP()
    {
        init();
    }


    /**
     * UDPオブジェクトのクライアント用コンストラクタ．<br>
     * サーバへ接続するためのソケットを作成する．
     * @param server_host サーバ名
     * @param port ポート番号
     */
    public UDP(String server_host, int port) // クライアント
    {
        init();
        try {
            ipAddress = InetAddress.getByName(server_host);
            portNo = port;
            sock = new DatagramSocket();
        }
        catch  (Exception er) {
            //er.printStackTrace();
            ipAddress = null;
            portNo = -1;
            sock = null;
            errFlag = true;
        }
    }


    /**
     * UDPオブジェクトのサーバ用コンストラクタ．<br>
     * ポートをオープンする．
     * @param port ポート番号．
     */
    public UDP(int port) // サーバー
    {   
        init();
//      Err_Flag = false;

        try {
            sock = new DatagramSocket(port);
        }
        catch  (Exception er) {
            //er.printStackTrace();
            sock = null;
            errFlag = true;
        }
    }
    

    /**
     * UDPオブジェクトを初期化する．
     */
    public void init()
    {
        ipAddress = null;
        portNo = -1;
        sock = null;
        errFlag = false;

        try {
            myIPAddress = InetAddress.getLocalHost();
            myHostname  = myIPAddress.getHostName();
        }
        catch(Exception er) {
            myIPAddress = null;
            myHostname = null;
        }
     }


    /**
     * UDPでメッセージ（文字列）を送信する．
     * @param mesg 送信するメッセージ
     * @throws Exception 送信エラー
     */
    public void  sendMesg(String mesg) throws Exception
    {
        if (ipAddress==null || portNo<0) return;

        errFlag = false;
        byte[] sendto = mesg.getBytes();
//            System.err.println(Tools.byteArray_toHex(sendto));
        try {
            DatagramPacket packet = new DatagramPacket(sendto, sendto.length, ipAddress, portNo);
            sock.send(packet);
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw new Exception("UDP.sendMesg: Send Message Error.");
        }
    }


    /**
       * UDPでメッセージ（文字列）を送信する．メッセージの最後に "\r\n"を付加する．
       * @param mesg 送信するメッセージ
       * @throws Exception 送信エラー
     */
    public void  sendMesgln(String mesg) throws Exception
    {
        if (ipAddress==null || portNo<0) return;
        
        errFlag = false;
        byte[] sendto = (mesg+"\r\n").getBytes();
//      System.err.println(Tools.byteArray_toHex(sendto));
        try {
            DatagramPacket packet = new DatagramPacket(sendto, sendto.length, ipAddress, portNo);
            sock.send(packet);
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw new Exception("UDP.sendMesgln: Send Message Error.");
        }
    }


    /**
       * UDPでデータ（バイナリ）を送信する．
       * @param data 送信するバイナリのバイト列
       * @param len 送信データ数（バイト）
       * @throws Exception 送信エラー
     */
    public void  sendData(byte[] data, int len) throws Exception
    {
        if (ipAddress==null || portNo<0) return;

        errFlag = false;
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, portNo);
            sock.send(packet);
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw new Exception("TCP.sendData: Send Byte Data Error.");
        }
    }


    /**
       * UDPでメッセージ（文字列）を受信する．タイムアウトは 5s．
       * @return 受信したメッセージ
       * @throws Exception 受信エラー．
     */
    public String  recvMesg() throws Exception
    {
        errFlag = false;
        byte[] buffer = new byte[BUFSIZE];
        DatagramPacket packet = null;
        
        try {
            packet = new DatagramPacket(buffer, buffer.length);
            sock.receive(packet);
            ipAddress = packet.getAddress();
            portNo = packet.getPort();
            //System.err.println(ipAddress+":"+portNo);
        }
        catch (Exception er) {
            //er.printStackTrace();
            packet = null;
            ipAddress = null;
            portNo = -1;
            errFlag = true;
            throw new Exception("UDP.recvMesg: Error.");
        }
        if (packet!=null) {
            byte[] retbuf = packet.getData();
            return new String(retbuf, 0, packet.getLength());
        }
        else return null;        
    }


    /**
     * UDPでデータ（バイナリ）を受信する．タイムアウトは 5s．
     * @return 受信データ
     * @throws Exception 受信エラー．
     */
    public byte[]  recvData() throws Exception
    {
        errFlag = false;
        byte[] buffer = new byte[BUFSIZE];
        DatagramPacket packet = null;
        
        try {
            packet = new DatagramPacket(buffer, buffer.length);
            sock.receive(packet);
            ipAddress = packet.getAddress();
            portNo = packet.getPort();
        }
        catch (Exception er) {
            //er.printStackTrace();
            packet = null;
            ipAddress = null;
            portNo = -1;
            errFlag = true;
            throw new Exception("UDP.recvData: Error.");
        }
        if (packet!=null) {
            byte[] retbuf = new byte[packet.getLength()];
            for (int i=0; i<packet.getLength(); i++) {
                retbuf[i] = packet.getData()[i];
            }
            return retbuf;
        }
        else return null;
    }


    /**
     * UDPソケットのクローズと初期化
     */
    public void close()
    {
        try {
            if (sock!=null) sock.close();
            init();
        }
        catch (Exception er) {
            //er.printStackTrace();
            sock = null;
            ipAddress = null;
            portNo = -1;
            errFlag = true;
        }
    }


    protected void finalize()
    {
        this.close();
    }
}

