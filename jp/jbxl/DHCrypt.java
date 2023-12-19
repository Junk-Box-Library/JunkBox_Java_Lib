package jp.jbxl;

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;


/**
 * Diffie-Hellman鍵交換法 クライアント
 * @author  Fumi.Iseki
 * @version 1.0
 */
public class  DHCrypt
{

    private KeyPairGenerator myKeyPairGen;
    private KeyFactory       myKeyFac;
    private KeyPair          myKeyPair;
    private PublicKey        serverPubKey;
    private KeyAgreement     myKeyAgree;
    private byte[]           mySharedSecret = null;

    /**
     * このオブジェクトの Subject Public Key Info (DER形式) <br>
     * dhClient() 実行後でないと，有効な値を得られない．
     */
    public  byte[]           myPubKey = null;
    
    /**
     * Base64でエンコードされた myPubKey <br>
     * dhClient() 実行後でないと，有効な値を得られない．
     */
    public  String           myPubKeyEnc = null;


    /**
     * コンストラクタ．状態を初期化する．
     */
    public  DHCrypt()
    {
        try {
            myKeyFac     = KeyFactory.getInstance("DiffieHellman");
            myKeyPairGen = KeyPairGenerator.getInstance("DiffieHellman");
            myKeyAgree   = KeyAgreement.getInstance("DiffieHellman");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * サーバの SPKI(DER形式)から自分の SPKI(DER形式)を生成して返す．また，共通鍵 mySharedSecret も計算する．
     * 
     * @param serverKeyEnc サーバの Subject Public Key Info (DER)
     * @return このオブジェクトの Subject Public Key Info (DER)
     */
    public byte[] dhClient(byte[] serverKeyEnc)
    {
        myPubKey = null;
        myPubKeyEnc = null;
        mySharedSecret = null;
          
        try {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverKeyEnc);
            serverPubKey = myKeyFac.generatePublic(x509KeySpec);
            DHParameterSpec dhParamSpec = ((DHPublicKey)serverPubKey).getParams();

            myKeyPairGen.initialize(dhParamSpec);
            myKeyPair = myKeyPairGen.generateKeyPair();
            myKeyAgree.init(myKeyPair.getPrivate());
            myKeyAgree.doPhase(serverPubKey, true);

            byte[] tmpSharedSecret = new byte[256];  // for 2048 bit key 
            int myLen = myKeyAgree.generateSecret(tmpSharedSecret, 0);
            mySharedSecret = new byte[myLen];
            for (int i=0; i<myLen; i++) mySharedSecret[i] = tmpSharedSecret[i];
            tmpSharedSecret = null;
              
            myPubKey = myKeyPair.getPublic().getEncoded();
            myPubKeyEnc = Base64.encode(myPubKey);
            //System.err.println("SCRT = "+Tools.byteArray_toHex(mySharedSecret));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return myPubKey;
    }     
      
      

    /**
     * DES(ECB) での暗号化．テスト用．<br>
     * 要動作確認
     * 
     * @param data 暗号化するバイト列．
     * @return 暗号化されたバイト列．
     */
    public byte[]  dhCrypt(byte[] data) 
    {
        byte[] cipher = null;

        try {
            myKeyAgree.doPhase(serverPubKey, true);
            SecretKey myDesKey = myKeyAgree.generateSecret("DES");

            Cipher myCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            myCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            cipher = myCipher.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return cipher;
    }

}


