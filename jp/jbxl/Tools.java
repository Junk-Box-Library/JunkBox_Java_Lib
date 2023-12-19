package jp.jbxl;

//import java.io.*;
import java.util.*;


/**
 * 便利なツールボックスクラス
 * @author Fumi.Iseki
 * @version 1.0
 * 
 */


public class Tools
{
    private static String Ascii   = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static String HexChar = "0123456789ABCDEF";


    /**
     * ランダムな文字をｎ個生成する．
     * @param n 生成する文字の数
     * @return 生成された（おそらくは）ランダムな文字列
     */
    public static String randstr(int n)
    {
        int  i, r, sz=Ascii.length();
        Random rnd = new Random();
        String ret = "";

        for (i=0; i<n; i++) {
            r = (rnd.nextInt()>>>1)%sz;
            ret += Ascii.substring(r, r+1);
        }
        return ret;
    }



    /**
     * バイトをストリングに変換する
     * @param buf 変換するバイト列
     * @return 変換された文字列
     */
    public static String byteArray2String(byte[] buf)
    {
        return new String(buf);
    }



    /**
     * バイト列 buf を HexのStringへ変換する．
     * @param buf 変換するバイト列
     * @return Hex表示の文字列
     */      
    public static String byteArray2Hex(byte[] buf)
    {
        String ret = "";
        int i, high, low;

        for (i=0; i<buf.length; i++) {
              high = ((buf[i] & 0xf0) >>> 4);
              low  = (buf[i] & 0x0f);
              ret += HexChar.substring(high, high+1) + HexChar.substring(low, low+1);
              if ((i+1)!=buf.length) {
                 if ((i+1)%64==0) ret += '\n';
                 else             ret += ':';
              }
        }
        return ret;
    }

    
    
    /**
     * バイト列 buf（長さn）を HexのStringへ変換する．
     * @param buf 変換するバイト列
     * @param n 変換対象となる，バイトの先頭からの長さ（バイト）
     * @return Hex表示の文字列
     */
    public static String byteArray2Hex(byte[] buf, int n)
    {
        String ret = "";
        int i, high, low;

        for (i=0; i<n; i++) {
            high = ((buf[i] & 0xf0) >>> 4);
            low  = (buf[i] & 0x0f);
            ret += HexChar.substring(high, high+1)+HexChar.substring(low, low+1);
            if ((i+1)!=buf.length) {
                if ((i+1)%64==0) ret += '\n';
                else             ret += ':';
            }
        }
        return ret;
    }



    /**
     * cc を区切り文字として n 番目の項目を戻す． nは 1から数える．
     * 
     * @param mesg 操作対象の文字列．
     * @param cc 区切り文字．
     * @param n 項目の番号．1から数える．
     * @return 指定された項目の文字列，該当項目がない場合は null を返す．
     */
    public static String  awk(String mesg, char cc, int n)
    {
        int i=0, j;
        char[] wrk = (mesg+cc).toCharArray();
        String ret = "";

        if (n<1) return null;

        for (j=0; j<n-1; j++) {
            while (wrk[i]!=cc && i<wrk.length-1) i++;
            if (wrk[i]==cc) i++;
        }
        if (i>=wrk.length) return null;
        
        while (wrk[i]!=cc && i<wrk.length-1) {
            ret = ret + wrk[i++];
        }
        
        return ret;
    }



    /**
     * cc を区切り文字として n 番目の項目を戻す．ただし，連続した ccは一個の区切りとみなす．<br>
     * nは 1から数える．<br>
     * 
     * @param mesg 操作対象の文字列．
     * @param cc 区切り文字．
     * @param n 項目の番号．1から数える．
     * @return 指定された項目の文字列，該当項目がない場合は null を返す．
     */
    public static String  cawk(String mesg, char cc, int n)
    {
        int i=0, j;
        char[] wrk = (mesg+cc).toCharArray();
        String ret = "";

        if (n<1) return null;

        for (j=0; j<n-1; j++) {
            while (wrk[i]!=cc && i<wrk.length-1) i++;
            while (wrk[i]==cc && i<wrk.length-1) i++;
        }
        if (i>=wrk.length) return null;

        while (wrk[i]!=cc && i<wrk.length-1) {
            ret = ret + wrk[i++];
        }
        return ret;
    }



    /**
     * cc を区切り文字として ret[] に可能な限りの項目を入れる．戻り値は入力された項目数．
     * 連続した ccは一個の区切りとみなす．
     * 
     * @param mesg 操作対象の文字列．
     * @param cc 区切り文字．
     * @param ret 取り出した項目が入力されて返される．
     * @return 取り出した項目数．ret[]の配列の大きさ．
     */
    public static  int  get_allitem(String mesg, char cc, String[] ret)
    {
        int i=0, j=0;
        char[] wrk = (mesg+cc).toCharArray();
        String str;

        while (wrk[i]==cc && i<wrk.length-1) i++;
        while(i<wrk.length-1 && j<ret.length) {
            str = "";
            while (wrk[i]!=cc && i<wrk.length-1) str += wrk[i++];
            while (wrk[i]==cc && i<wrk.length-1) i++;
            ret[j++] = str;
        }

        return j;
    }



    /**
     * 文字列中の空白を _ に換える
     * 
     * @param mesg 操作対象の文字列．
     * @return " " を "_" に変換された文字列．
     */
    public static  String  space2_ (String mesg)
    {
        char[] str = mesg.toCharArray();
        String ret = "";
        for (int i=0; i<str.length; i++) {
            if (str[i]==' ') ret += '_';
            else             ret += str[i];
        }

        return ret;
    }

}
