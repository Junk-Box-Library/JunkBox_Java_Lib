/*
 * 作成日: 2004/04/25
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.jbxl;

import java.io.*;



/**
 * INet Protocol を処理するためのTCPサブクラス 
 * @author Fumi.Iseki
 */
 
public class INet_TCP extends TCP_Socket
{
    /**
     * 相手との接続状態を表す．true:接続中． false:接続していない．
     */
    public boolean isConnect = false;

    /**
     * ファイル識別子．様々な用途に使用（予定）<br>
     * 例）ファイル転送中にエラーを起こしたファイルの番号．
     */
    public int  file_Indicater = 0;

    
    /**
     * コンストラクタ．初期化のみ行なう．
     */
    public  INet_TCP()
    {
        super();
        isConnect = false;
    }



    /**
     * コンストラクタ．サーバへの connectも行う．
     * @param server_host サーバ名
     * @param port ポート番号
     */
    public  INet_TCP(String server_host, int port)
    {
        super(server_host, port);
        isConnect = !errFlag;
    }



    /**
     * サーバポートへのコネクトを行う．
     * @param server_host サーバ名
     * @param port ポート番号
     * @throws Exception 接続エラー
     */
    public void connect(String server_host, int port) throws Exception
    {
        try {
            super.connect(server_host, port);
        }
        catch(Exception er) {
            throw er;
        }

        if (errFlag==false) isConnect = true;
    }



    /**
     * サーバから "OK" の返答を待つ．
     * @return 0:正常終了（OKの返答あり）. その他:サーバからのエラーNo.
     * @throws Exception タイムアウト．または受信エラー．
     */
    public int  recvOK() throws Exception 
    {
        int ret_no = 0;
        String ret = null;

        try {
            ret = recvMesg();
            if (!ret.equals("OK")) {
                if ((Tools.cawk(ret, ' ', 1)).equals("ERR")) {
                    ret_no = Integer.parseInt(Tools.cawk(ret, ' ', 2));
                }
                else {
                    throw new Exception("INet_TCP.Recv_OK: Unknown Error.");
                }
            }
        }
        catch(Exception er) {
            throw  er;
        }

        return ret_no;
    }



    /**
     * サーバにコマンドを送って，"OK" の返答を待つ．
     * @param command サーバへ送るコマンド．
     * @return 0:    正常終了． その他:サーバからのエラーメッセージNo.
     * @throws Exception 送信エラー．
     */
    public int  sendCommandRecvOK(String command) throws Exception
    {
        int ret_no = 0;

        try {
            sendMesgln(command);
            ret_no = recvOK();
        }
        catch(Exception er) {
            throw  er;
        }

        return ret_no;
    }



    /**
      * サーバからの "OK"の後のメッセージ（"END"まで）を受けとる．
     * @param mesg サーバへ送るコマンド
     * @return 受信したメッセージ（行）の数 
     * @throws Exception タイムアウト．または受信エラー．
     */
    public int recvMesgUntilEND(String[] mesg) throws Exception
    {
        String buf;
        int ret_no = 0;

        try {
            buf = recvMesg();
            while (!buf.equals("END") && errFlag==false && ret_no<mesg.length) {
                mesg[ret_no++] = new String(buf);
                buf = recvMesg();
            }
            if (!buf.equals("END")) {     // 読み込めなかったメッセージを読み飛ばす．
                buf = recvMesg();
                while (!buf.equals("END")) buf = recvMesg();
            }
        }
        catch(Exception er) {
            throw er;
        }

        return ret_no;
    }


    
    /**
     * ソケットをクローズする
     */
    public void  close()
    {
        if (isConnect==true) {
            super.close();
            isConnect = false;
        }
    }

    
    
    /**
     * 複数のファイルをサーバへ転送し，サーバからの返答を待つ<br>
     * 転送状況を示すプログレスバーは使用しない
     * 
     * @param files ファイルポインタの入った配列．
     * @return 0:正常終了．その他：エラー
     * @throws Exception 通信エラー
     */
    public int  sendFileRecvOK(File[] files) throws Exception
    {
        return sendFileRecvOK(files, false);
    }

    

    /**
     * 複数のファイルをサーバへ転送し，サーバからの返答を待つ<br>
     * 転送状況を示すプログレスバーを使用を使用するかどうか指定可能
     *
     * @param files ファイルポインタの入った配列．
     * @param t プログレスバーを表示するかどうか
     * @return 0:正常終了． その他：エラー
     * @throws Exception 通信エラー
     */
    public int  sendFileRecvOK(File[] files, boolean t) throws Exception
    {
        ProgBarDialog pbd;
        int len = files.length;

        if (t) {
            int tsize = 0;
            for (int i=0; i<len; i++) tsize += (int)files[i].length();
            pbd = new ProgBarDialog("ファイル転送", tsize);
            pbd.showup_Center();
        }
        else {
            pbd = null;
        }

        int n;
        for (int i=0; i<len; i++) {
            n = sendFileRecvOK(files[i], pbd);
            if (n!=0) {
                file_Indicater = i;
                if (t) pbd.setVisible(false);
                return n;
            }
        }

        if (t) pbd.setVisible(false);
        return 0;
    }



    /**
     * ファイルを１つサーバへ転送し，サーバからの返答を待つ
     * 転送状況を示すプログレスバーは使用しない
     * 
     * @param file ファイル
     * @return 0:正常終了．9:ファイル名エラー．その他：呼び出し関数でのエラー．
     * @throws Exception 通信エラー
     */
    public int  sendFileRecvOK(File file) throws Exception
    {
        return this.sendFileRecvOK(file, (ProgBarDialog)null);
    }



    /**
     * ファイルを１つサーバへ転送し，サーバからの返答を待つ
     * 転送状況を示すプログレスバーを使用を使用するかどうか指定可能
     * 
     * @param file ファイル
     * @param pbd プログレスバーを表示するかどうか
     * @return 0:正常終了．9:ファイル名エラー．その他：呼び出し関数でのエラー．
     * @throws Exception 通信エラー
     */
    public int  sendFileRecvOK(File file, ProgBarDialog pbd) throws Exception
    {
        int  ret;

        if (file.canRead() && file.isFile()) {
            String fname = file.getName();
            String fsize = Integer.toString((int)file.length());

            try {
                // 転送要求 
                ret = sendCommandRecvOK("SRPT "+fsize+" "+Tools.space2_(fname));
                if (ret!=0) return ret;

                // ファイル転送
                ret = sendFile(file, pbd);
                if (ret!=0) return ret;

                // 転送確認 
                ret = recvOK();
                if (ret!=0) return ret;
            }
            catch (Exception er) {
                throw new Exception("INet_TCP.Send_File_Recv_OK: 転送エラー!!");
            }

        }
        else return 9;

        return 0;
    }

    
    
    /**
     * ファイルを１つサーバへ転送する．
     * 転送状況を示すプログレスバーは使用しない．
     * 
     * @param file 転送するファイル
     * @return 0:正常終了．1：ファイルオープンエラー．2:データ転送エラー．3:通信エラー
     * @throws Exception ファイル転送エラー
     */
    public int  sendFile(File file) throws Exception
    {
        return this.sendFile(file, (ProgBarDialog)null);
    }


    
    /**
     * ファイルを１つサーバへ転送する．
     * 転送状況を示すプログレスバーを使用を使用するかどうか指定可能．
     * 
     * @param file 転送するファイル
     * @param pbd 表示するプログレスバーを指定．nullの場合は表示しない．
     * @return 0:正常終了． 1：ファイルオープンエラー． 2:データ転送エラー． 3:通信エラー
     * @throws Exception ファイル転送エラー
     */
    public int  sendFile(File file, ProgBarDialog pbd) throws Exception
    {
        int err_no = 0;
        byte[] buf = new byte[1024];
        FileInputStream  iFile = null;

        try {
            iFile = new FileInputStream(file.getAbsolutePath());
        }
        catch(Exception er) {
            er.printStackTrace();
            return 1;
        }

        if (pbd!=null) {
            pbd.mesgText.setText("転送中．\n"+file.getName());
            pbd.mesgText.print(pbd.mesgText.getGraphics());
        }

        int n, sz=0;
        boolean cnt = true;

        while(cnt) {
            try {
                n = iFile.read(buf);
                if (n>0) {
                    sendData(buf, n);
                    if (errFlag==true) {
                        err_no = 2;
                        cnt = false;
                    }
                    if (pbd!=null) {
                        sz += n*100;
                        pbd.prgBar.setValue(pbd.prgBar.getValue() + n);
                        if (sz>pbd.prgBar.getMaximum()) {
                            //System.err.println("XXXXXXXXX "+ sz+">"+pbd.prgBar.getMaximum()+"    "+pbd.prgBar.getValue()); 
                            pbd.prgBar.paint(pbd.prgBar.getGraphics()); // 1%上昇したら再描画
                            sz = 0;
                        }
                    }
                }
                else {
                    cnt = false;
                }
            }
            catch(Exception er) {
                err_no = 3;
                cnt = false;
            }
        }

        
        try {
            iFile.close();
        }
        catch(Exception er) {
            throw new Exception("INet_TCP.Send_File: ファイル転送エラー!!");
        }

        return err_no;
    }

}

