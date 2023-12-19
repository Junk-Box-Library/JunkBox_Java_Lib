package jp.jbxl; 


import java.net.*;
import java.io.*;


/**
 * TCP Berkeley Socketの基本クラス<br>
 * クライアントソケットとして単体での使用も可能．
 * @author  Fumi.Iseki
 */
public class TCP_Socket
{
    private final static int  BUFSIZE = 1024;

    private InputStream       inpStrm;        	// 入力ストリーム
    private OutputStream      outStrm;        	// 出力ストリーム
    private DataInputStream   dataInpStrm;    	// 入力バイトストリーム
    private DataOutputStream  dataOutStrm;    	// 出力バイトストリーム
    private InputStreamReader inpReader;      	// 入力バイトストリームのストリング化
    private BufferedReader    bufReader;      	// 入力ストリングのバッファー化
    private String  encoding = "UTF-8";			// 取り扱う文字セット（charset）

    protected Socket sock = null;             	// クライアントソケット

    /**
     * オブジェクトの状態を表すフラグ．true：オブジェクトは正常に動作． false：オブジェクトでエラーが発生．
     */
    public boolean errFlag = false;
    
    /**
     * オブジェクトが動作しているマシンの IPアドレス
     */
    public InetAddress myIPAddress = null;
    
    /**
     * オブジェクトが動作しているマシンのマシン名
     */
    public String      myHostname = null;


    /**
     * 受信したデータの長さ
     */
    public int         recvLen = 0;



    /**
     * TCPソケットオブジェクトのコンストラクタ<br>
     * サーバへの connect は行わない．<br>
     */
    public TCP_Socket()
    {
        init();
    }



    /**
     * TCP_Socketオブジェクトのコンストラクタ<br>
     * サーバへの connectも行う
     * @param server_host サーバ名
     * @param port ポート番号
     */
    public TCP_Socket(String server_host, int port)
    {
        init();
        //
        try {
            connect(server_host, port);
        }
        catch  (Exception er) {
            //er.printStackTrace();
            sock = null;
            errFlag = true;
        }
    }



    /**
     * TCP_Socketオブジェクトのコンストラクタ<br>
     * サーバへの connectも行う
     * @param server_host サーバ名
     * @param port ポート番号
     * @param coding 取り扱う文字セット名(charset)  (ex. "UTF-8"
     */
    public TCP_Socket(String server_host, int port, String coding)
    {
        init();
        encoding = coding;
        //
        try {
            connect(server_host, port);
        }
        catch  (Exception er) {
            //er.printStackTrace();
            sock = null;
            errFlag = true;
        }
    }



    /**
     * 外部で作成した Socketを使用して TCP_Socketオブジェクトを作成する．<br>
     * TCP_Serverクラス からの使用を想定．<br>
     * @param sockno 外部で生成したTCPソケット．
     */
    public TCP_Socket(Socket sockno)
    {
        init();
        errFlag = false;
        sock = sockno;

        try {
            createStream(sock);
        }
        catch  (Exception er) {
            //er.printStackTrace();
            sock = null;
            errFlag = true;
        }
    }



    /**
     * 外部で作成した Socketを使用して TCP_Socketオブジェクトを作成する．<br>
     * TCP_Serverクラス からの使用を想定．<br>
     * @param sockno 外部で生成したTCPソケット．
     * @param coding 取り扱う文字セット名(charset)  (ex. "UTF-8"
     */
    public TCP_Socket(Socket sockno, String coding)
    {
        init();
        errFlag = false;
        sock = sockno;
        encoding = coding;

        try {
            createStream(sock);
        }
        catch  (Exception er) {
            //er.printStackTrace();
            sock = null;
            errFlag = true;
        }
    }



    /**
     * サーバポートへのコネクトを行う．
     * @param server_host サーバ名
     * @param port ポート番号
     * @throws Exception 接続エラー
     */
    public void connect(String server_host, int port) throws Exception
    {
        errFlag = false;
        
        try {
            sock = new Socket(server_host, port);
            createStream(sock);
        }
        catch (Exception er) {
            //er.printStackTrace();
            sock = null;
            errFlag = true;
            throw new Exception("TCP_Socket: Connection Faled.");
        }
    }



    private void  createStream(Socket socket) throws Exception
    { 
        inpStrm = socket.getInputStream();
        outStrm = socket.getOutputStream();
        dataInpStrm = new DataInputStream(inpStrm);
        dataOutStrm = new DataOutputStream(outStrm);
        inpReader = new InputStreamReader(inpStrm, encoding);
        bufReader = new BufferedReader(inpReader);
    }



    /**
     * TCP_Socketオブジェクトを初期化する．
     */
    public void init()
    {
        sock     = null;
        errFlag  = false;
        recvLen  = 0;
        encoding = "UTF-8";
        
        try {
            myIPAddress = InetAddress.getLocalHost();
            myHostname  = myIPAddress.getHostName();
        }
        catch(Exception er) {
            myIPAddress = null;
            myHostname  = null;
        }
    }



    /**
     * TCP_Socketでメッセージ（文字列）を送信する．
     * @param mesg 送信するメッセージ
     * @throws Exception 送信エラー
     */
    public void  sendMesg(String mesg) throws Exception
    {
        errFlag = false;
        byte[] sendto = mesg.getBytes();

        try {
            dataOutStrm.write(sendto, 0, sendto.length);
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw new Exception("TCP_Socket.sendMesg: Send Message Error.");
        }
    }



    /**
     * TCP_Socketでメッセージ（文字列）を送信する．メッセージの最後に "\r\n"を付加する．
     * @param mesg 送信するメッセージ
     * @throws Exception 送信エラー
     */
    public void  sendMesgln(String mesg) throws Exception
    {
        errFlag = false;
        byte[] sendto = (mesg+"\r\n").getBytes();

        try {
            dataOutStrm.write(sendto, 0, sendto.length);
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw new Exception("TCP_Socket.sendMesgln: Send Message Error.");
        }
    }



    /**
     * TCP_Socketでデータ（バイナリ）を送信する．
     * @param data 送信するバイナリのバイト列
     * @param len  送信データ数（バイト）
     * @throws Exception 送信エラー
     */
    public void  sendData(byte[] data, int len) throws Exception
    {
        errFlag = false;
        try {
            dataOutStrm.write(data, 0, len);
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw new Exception("TCP_Socket.sendData: Send Byte Data Error.");
        }
    }



    /**
     * TCP_Socketでメッセージ（文字列）を受信する．タイムアウトは 5s．<br>
     * 正常に受信した場合，recvLen に受信したデータのバイト数が入る．
     * @return 受信したメッセージ
     * @throws Exception タイムアウト．または受信エラー．
     */
    public String  recvMesg() throws Exception
    {
        return recvMesg(5000);
    }



    /**
     * TCP_Socketでメッセージ（文字列）を受信する．タイムアウトを msで指定可能．<br>
     * 正常に受信した場合，recvLen に受信したデータのバイト数が入る．
     * @param timeout タイムアウト ms
     * @return 受信したメッセージ
     * @throws Exception タイムアウト．または受信エラー．
     */
    public String  recvMesg(int timeout) throws Exception 
    {
        String mesg = null;
        recvLen = 0;
        errFlag = false;

        try {
            sock.setSoTimeout(timeout);
            mesg = bufReader.readLine();
            sock.setSoTimeout(0);
            if (mesg!=null) recvLen = mesg.length();
        }
        catch (InterruptedIOException er) {
            errFlag = true;
            throw new Exception("TCP_Socket.recvMesg: Timeout.");
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw er;
        }

        return mesg;
    }



    /**
     * TCP_Socketでデータ（バイナリ）を受信する．タイムアウトは 5s．<br>
     * 正常に受信した場合，recvLen に受信したデータのバイト数が入る．
     * @return 受信データ
     * @throws Exception タイムアウト．または受信エラー．
     */
    public byte[]  recvData() throws Exception 
    {
        return recvData(5000);
    }



    /**
     * TCP_Socketでデータ（バイナリ）を受信する．タイムアウトを msで指定可能．<br>
     * 正常に受信した場合，recvLen に受信したデータのバイト数が入る．
     * @param timeout タイムアウト ms
     * @return 受信データ
     * @throws Exception タイムアウト．または受信エラー．
     */
    public byte[]  recvData(int timeout) throws Exception 
    {
        recvLen = 0;
        errFlag = false;

        byte[] buffer = new byte[BUFSIZE];
        byte[] retbuf = null;
        int len = 0;

        try {
            sock.setSoTimeout(timeout);
            len = dataInpStrm.read(buffer);
            sock.setSoTimeout(0);
        }
        catch (EOFException er) {
            // Normal END
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw new Exception("TCP_Socket.recvData: Timeout.");
        }

        if (len>0) {
            recvLen = len;
            retbuf = buffer;
        }
        return retbuf;
    }



    /**
     * TCPソケットのクローズと初期化
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
            errFlag = true;
            recvLen = 0;
        }
    }


    protected void finalize()
    {
        this.close();
    }
}
