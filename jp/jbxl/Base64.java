package jp.jbxl;


/**
 * Base64 の符号化，復号化を行なう．
 * 
 * @author Fumi.Iseki
 * @version 1.0
 */

public class Base64
{
    private static String ascii = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    //                             0123456789012345678901234567890123456789012345678901234567890123

    /**
     * コンストラクタは使用しない
     * @deprecated 
     */
    private Base64()
    {
    }
      

    /**
     * バイトデータ dec を Base64の Stringに変換する．
     * @param dec Base64 に変換するバイト列
     * @return Base64 に変換された文字列
     */
    public static String  encode(byte[] dec)
    {
        int i, j, k;
        int sz = (dec.length+2)/3*4;
        int wk, mk;
        String enc = "";

        mk = 0x80;
        for (i=0; i<sz; i++) {
            wk = 0;
            if (dec.length*8>i*6) {
                for (j=0; j<6; j++) {
                    k = (i*6 + j)/8;
                    if (k<dec.length) {
                        if ((dec[k]&mk)!=0) wk += 1;
                    }
                    wk <<= 1;
                    mk >>>= 1;
                    if (mk==0x00) mk = 0x80;
                }
                wk >>>= 1;
                enc += ascii.charAt(wk);
            }
            else {
                enc += "=";
            }
        }
        return enc;
    }


    /**
     * Base64でエンコードされた String buf をバイト列に戻す．
     * @param buf 復号化する Base64 文字列
     * @return 復号化されたバイト列
     */
    public static byte[]  decode(String buf)
    {
        int i, j, k=0;
        int ix, wk, mk, lt=0;

        while (lt<buf.length() && buf.charAt(lt)!='=') {
            lt++;
            if (lt==buf.length()) break;
        }

        int sz = lt/4*3 + (lt%4)*3/4;
        byte[] dec = new byte[sz];

        for (i=0; i<sz; i++) dec[i] = 0x00;

        wk = 0;
        for (i=0; i<lt; i++) {
            ix = ascii.indexOf(buf.substring(i, i+1));
            mk = 0x20;

            for (j=0; j<6; j++) {
                k = i*6 + j;
                if ((ix&mk)!=0) wk += 1;
                wk <<= 1;
                if (((k+1)%8)==0) {
                    wk >>>= 1;
                    dec[k/8] = (byte)wk;
                    wk = 0;
                }
                mk >>>= 1;
            }
        }

        return  dec;
    }

}

