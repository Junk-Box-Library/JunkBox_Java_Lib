package jp.jbxl;


import java.net.*;


/**
 * TCP Berkeley Socketのサーバクラス<br>
 * @author Fumi.Iseki
 * @version 1.0
 */

public class TCP_Server
{
    private ServerSocket serverSock = null; // サーバーソケット

    /**
     * <code>errFlag</code> true：オブジェクトは正常に動作． false：オブジェクトでエラーが発生．
     */
    public boolean errFlag = false;

    public int connectMax = 50;


      
    /**
     * コンストラクタ．初期化のみ行なう．<br>
     * ソケットは作成しない．
     */
    public TCP_Server()
    {
        init();
    }

      

    /**
     * オブジェクトを生成し，TCPサーバソケットを作成する．<br>
     * ポート番号portにソケットを作成する．同時最大接続数はデフォルトで50.<br>
     * 接続待ち(accept)行なわない．
     * @param port  ポート番号．
     */
    public TCP_Server(int port)
    {
        init();

        try {
            serverSock = new ServerSocket(port, connectMax);
        }
        catch  (Exception er) {
            //er.printStackTrace();
            serverSock = null;
            errFlag = true;
        }
    }


    
    /**
     * オブジェクトを生成し，TCPサーバソケットを作成する． <br>
     * ポート番号 portに同時最大接続数 cmaxのソケットを作成する．<br>
     * 接続待ち(accept)は行なわない．<br>
     * @param port ポート番号．
     * @param cmax 同時最大接続数．
     */
    public TCP_Server(int port, int cmax)
    {
        init();
        
        try {
            serverSock = new ServerSocket(port, cmax);
        }
        catch  (Exception er) {
            //er.printStackTrace();
            serverSock = null;
            errFlag = true;
        }
    }



    /**
     * すでにあるオブジェクトに対して，新しいTCPサーバソケットを作成（オープン）する<br>
     * ポート番号portにソケットを作成する．同時最大接続数は50.<br>
     * 接続待ち(accept)は行なわない．
     * @param port ポート番号
     * @throws Exception ソケットのオープンエラー
     */
    public void open(int port) throws Exception
    {
        try {
            if (serverSock!=null) {
                serverSock.close();
                serverSock = null;
            }
            serverSock = new ServerSocket(port, connectMax);
        }
        catch (Exception er) {
            //er.printStackTrace();
            serverSock = null;            
            errFlag = true;
            throw new Exception("TCP_Server.open: Open Server Socket Faled.");
        }
    }

    

    /**
     * すでにあるオブジェクトに対して，新しいTCPサーバソケットを作成（オープン）する<br>
     * ポート番号 portに同時最大接続数 cmaxのソケットを作成する．<br>
     * 接続待ち(accept)は行なわない．
     * @param port ポート番号
     * @param cmax 同時最大接続数．
     * @throws Exception ソケットのオープンエラー
     */
    public void open(int port, int cmax) throws Exception
    {
        try {
            if (serverSock!=null) {
                serverSock.close();
                serverSock = null;
            }
            serverSock = new ServerSocket(port, cmax);
        }
        catch (Exception er) {
            //er.printStackTrace();
            serverSock = null;            
            errFlag = true;
            throw new Exception("TCP_Server.open: Open Server Socket Faled.");
        }
    }

    

    /**
     * ソケットへの接続待ち状態に入り，接続があった場合，その接続に対する通信ソケット用のTCPオブジェクトを返す．
     * 
     * @return 通信用の TCPオブジェクト
     * @throws Exception 接続待ち失敗
     */
    public TCP_Socket accept() throws Exception
    { 
        errFlag = false;
        TCP_Socket tcp = null;

        try {
            Socket sock = serverSock.accept();
            tcp = new TCP_Socket(sock);
        }
        catch (Exception er) {
            //er.printStackTrace();
            errFlag = true;
            throw new Exception("TCP_Server.accept: Accept Faled.");
        }
        return tcp;
    }

    

    /**
     * TCP_Serverオブジェクトの初期化
     *
     */
    public void init()
    {
        serverSock = null;
        errFlag = false;
    }



    /**
     * TCP_Serverソケットのクローズ
     *
     */
    public void close()
    {
        //
        try {
            if (serverSock!=null) serverSock.close();
            init();
        }
        catch (Exception er) {
            //er.printStackTrace();
            serverSock = null;              
            errFlag = true;
        }
    }
      


    protected void finalize()
    {
        this.close();
    }
}
