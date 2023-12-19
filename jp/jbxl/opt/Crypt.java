
package jp.jbxl.opt;

import jp.jbxl.*;

/**
 * タイトル: crypt(), encrypt(), setkey() and etc. of UFC-crypt for Java (32bit)
 * 説明: GNUの UFC-crypt(Ultra Fast Crypt)の crypt関数（32bit）を Java用に書き換えた．
 * 
 * @author Fumi.Iseki
 * @version 1.0
 *
 * String crypt(String key, String salt);
 * byte[] encrypt(byte[] buf, int mode);
 * void   setkey(byte[] key);
 *
 * void   setkey_byBase64(String key);
 * void   setkey_byAscii(String key);
 * String encrypt_Ascii(String buf, int mode);
 * String encrypt_Base64(String buf, int mode);
 * byte[] encrypt_bin(byte[] buf, int mode);
 */


public class Crypt
{
    private String Ascii = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private int rots[] = {  /* 16 */
        1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    private int pc1[] = {  /* 56 */
        57, 49, 41, 33, 25, 17,  9,  1, 58, 50, 42, 34, 26, 18,
        10,  2, 59, 51, 43, 35, 27, 19, 11,  3, 60, 52, 44, 36,
        63, 55, 47, 39, 31, 23, 15,  7, 62, 54, 46, 38, 30, 22,
        14,  6, 61, 53, 45, 37, 29, 21, 13,  5, 28, 20, 12,  4
    };

    private int pc2[] = {   /* 48 */
        14, 17, 11, 24,  1,  5,  3, 28, 15,  6, 21, 10,
        23, 19, 12,  4, 26,  8, 16,  7, 27, 20, 13,  2,
        41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
        44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
    };

    private int esel[] = {  /* 48 */
        32,  1,  2,  3,  4,  5,  4,  5,  6,  7,  8,  9,
         8,  9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
        16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
        24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32,  1
    };

    private int perm32[] = {  /* 32 */
        16,  7, 20, 21, 29, 12, 28, 17,  1, 15, 23, 26,  5, 18, 31, 10,
         2,  8, 24, 14, 32, 27,  3,  9, 19, 13, 30,  6, 22, 11,  4, 25
    };

    private int sbox[][][]= {  /* 8, 4, 16 */
        { { 14,  4, 13,  1,  2, 15, 11,  8,  3, 10,  6, 12,  5,  9,  0,  7 },
          {  0, 15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5,  3,  8 },
          {  4,  1, 14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3, 10,  5,  0 },
          { 15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14, 10,  0,  6, 13 }
        },
        { { 15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10 },
          {  3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11,  5 },
          {  0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15 },
          { 13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12,  0,  5, 14,  9 }
        },
        { { 10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2,  8 },
          { 13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11, 15,  1 },
          { 13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5, 10, 14,  7 },
          {  1, 10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3, 11,  5,  2, 12 }
        },
        { {  7, 13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 15 },
          { 13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10, 14,  9 },
          { 10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5,  2,  8,  4 },
          {  3, 15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11, 12,  7,  2, 14 }
        },
        { {  2, 12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14,  9 },
          { 14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9,  8,  6 },
          {  4,  2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6,  3,  0, 14 },
          { 11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9, 10,  4,  5,  3 }
        },
        { { 12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5, 11 },
          { 10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11,  3,  8 },
          {  9, 14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1, 13, 11,  6 },
          {  4,  3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7,  6,  0,  8, 13 }
        },
        { {  4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6,  1 },
          { 13,  0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15,  8,  6 },
          {  1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0,  5,  9,  2 },
          {  6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15, 14,  2,  3, 12 }
        },
        { { 13,  2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7 },
          {  1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2 },
          {  7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8 },
          {  2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11 }
        }
    };

    private int initial_perm[] = {  /* 64 */
        58, 50, 42, 34, 26, 18, 10,  2, 60, 52, 44, 36, 28, 20, 12, 4,
        62, 54, 46, 38, 30, 22, 14,  6, 64, 56, 48, 40, 32, 24, 16, 8,
        57, 49, 41, 33, 25, 17,  9,  1, 59, 51, 43, 35, 27, 19, 11, 3,
        61, 53, 45, 37, 29, 21, 13,  5, 63, 55, 47, 39, 31, 23, 15, 7
    };

    private int final_perm[] = {  /* 64 */
        40,  8, 48, 16, 56, 24, 64, 32, 39,  7, 47, 15, 55, 23, 63, 31,
        38,  6, 46, 14, 54, 22, 62, 30, 37,  5, 45, 13, 53, 21, 61, 29,
        36,  4, 44, 12, 52, 20, 60, 28, 35,  3, 43, 11, 51, 19, 59, 27,
        34,  2, 42, 10, 50, 18, 58, 26, 33,  1, 41,  9, 49, 17, 57, 25
    };

    private int bytemask[]  = {  /* 8 */
        0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
    };

    private int longmask[] = {  /* 32 */
        0x80000000, 0x40000000, 0x20000000, 0x10000000,
        0x08000000, 0x04000000, 0x02000000, 0x01000000,
        0x00800000, 0x00400000, 0x00200000, 0x00100000,
        0x00080000, 0x00040000, 0x00020000, 0x00010000,
        0x00008000, 0x00004000, 0x00002000, 0x00001000,
        0x00000800, 0x00000400, 0x00000200, 0x00000100,
        0x00000080, 0x00000040, 0x00000020, 0x00000010,
        0x00000008, 0x00000004, 0x00000002, 0x00000001
    };


    private int e_inverse[] = new int[64];
    private int ufc_keytab[][] = new int[16][2];

    private int sb[][] = new int[4][8192];
    private int ufc_sb0[] = sb[0];
    private int ufc_sb1[] = sb[1];
    private int ufc_sb2[] = sb[2];
    private int ufc_sb3[] = sb[3];

    private int eperm32tab[][][] = new int[4][256][2];
    private int do_pc1[][][]     = new int[8][2][128];
    private int do_pc2[][]       = new int[8][128];
    private int efp[][][]        = new int[16][64][2];

    private String current_salt = "&&"; /* invalid value */
    private int    current_saltbits = 0;
    private int    direction = 0;


    public Crypt()
    {
        init_des();
    }


/*
 * UNIX crypt function.
 */
    public String crypt(String key, String salt)
    {
        int sz = 8;
        String tsalt = (salt+"..").substring(0, 2);

        setup_salt(tsalt);
        if (key.length()<sz) sz = key.length();
        ufc_mk_keytab(key.substring(0, sz).getBytes());
        int s[] = ufc_doit(0, 0, 0, 0, 25);

        return  output_conversion(s, tsalt);
    }


/*
 * UNIX encrypt function.
 */
    public byte[] encrypt(byte[] buf, int mode)
    {
        int i, l1, l2, r1, r2, mi=64;
        byte[] bk = new byte[64];

        if (buf.length<mi) mi = buf.length;
        for(i=0; i<mi; i++)  bk[i] = buf[i];
        for(i=mi; i<64; i++) bk[i] = 0x00;

        setup_salt("..");
        if(mode!=direction) {
            for(i=0; i<8; i++) {
                int swap;
                swap = ufc_keytab[15-i][0];
                ufc_keytab[15-i][0] = ufc_keytab[i][0];
                ufc_keytab[i][0] = swap;

                swap = ufc_keytab[15-i][1];
                ufc_keytab[15-i][1] = ufc_keytab[i][1];
                ufc_keytab[i][1] = swap;
            }
            direction = mode;
        }

        l1 = l2 = 0;
        r1 = r2 = 0;
        for(i=0; i<24; i++) {
            if (bk[initial_perm[esel[i]-1]-1]==1)    l1 |= BitMask(i);
            if (bk[initial_perm[esel[i]-1+32]-1]==1) r1 |= BitMask(i);
        }
        for(i=24; i<48; i++) {
            if (bk[initial_perm[esel[i]-1]-1]==1)    l2 |= BitMask(i-24);
            if (bk[initial_perm[esel[i]-1+32]-1]==1) r2 |= BitMask(i-24);
        }

        int s[] = ufc_doit(l1, l2, r1, r2, 1);
        for(i=0; i<32; i++) {
            if ((s[0]&longmask[i])!=0) bk[i] = 1;
            else                       bk[i] = 0;
            if ((s[1]&longmask[i])!=0) bk[i+32] = 1;
            else                       bk[i+32] = 0;
        }

        return  bk;
    }


    public String encrypt_Ascii(String buf, int mode)
    {
        byte[] bin;
        String ret;

        if (mode==0) {
            bin = to_bin64(buf.getBytes());
        }
        else {
            byte[] cry = Base64.decode(buf);
            bin = to_bin64(cry);
        }
        byte[] enc = encrypt(bin, mode);
        byte[] cry = from_bin64(enc);
        if (mode==0) ret = Base64.encode(cry);
        else         ret = Tools.byteArray_toString(cry);

        return ret;
    }


    public String encrypt_Base64(String buf, int mode)
    {
        byte[] dec = Base64.decode(buf);
        byte[] enc = encrypt_bin(dec, mode);
        String cry = Base64.encode(enc);
        return cry;
    }


    public byte[] encrypt_bin(byte[] buf, int mode)
    {
        byte[] bin = to_bin64(buf);
        byte[] enc = encrypt(bin, mode);
        byte[] cry = from_bin64(enc);
        return cry;
    }



/*
 * UNIX setkey function.
 */
    public void setkey(byte[] key)
    {
        int i, j, mi=64;
        byte[] ky   = new byte[64];
        byte[] ktab = new byte[8];
        byte c;

        if (key.length<mi) mi = key.length;
        for(i=0; i<mi; i++)  ky[i] = key[i];
        for(i=mi; i<64; i++) ky[i] = 0;

        setup_salt("..");
        for(i=0; i<8; i++) {
            c = 0;
            for(j=0; j<8; j++) c = (byte)((c << 1) | ky[i*8+j]);
            ktab[i] = (byte)(c>>>1);
        }
        ufc_mk_keytab(ktab);
    }


    public void setkey_byBase64(String buf) 
    {
        int  i, mi=8;
        byte[] dec = Base64.decode(buf);
        byte[] key = new byte[8];

        if (dec.length<mi) mi = dec.length;
        for(i=0; i<mi; i++) key[i] = dec[i];
        for(i=mi; i<8; i++) key[i] = 0x00;

        setup_salt("..");
        for(i=0; i<8; i++) key[i] >>>= 1;
        ufc_mk_keytab(key);
    }



    public void setkey_byAscii(String buf)
    {
        int  i, mi=8;
        byte[] dec = buf.getBytes();
        byte[] key = new byte[8];

        if (dec.length<mi) mi = dec.length;
        for(i=0; i<mi; i++) key[i] = dec[i];
        for(i=mi; i<8; i++) key[i] = 0x00;

        setup_salt("..");
        for(i=0; i<8; i++) key[i] >>>= 1;
        ufc_mk_keytab(key);
    }


    private int[] ufc_dofinalperm(int l1, int l2, int r1, int r2) 
    {
        int v1, v2, x;
        int ary[] = new int[2];

        x = (l1 ^ l2) & current_saltbits; l1 ^= x; l2 ^= x;
        x = (r1 ^ r2) & current_saltbits; r1 ^= x; r2 ^= x;

        v1 = v2 = 0; l1 >>>= 3; l2 >>>= 3; r1 >>>= 3; r2 >>>= 3;
        v1 |= efp[15][ r2          & 0x3f][0]; v2 |= efp[15][ r2 & 0x3f][1];
        v1 |= efp[14][(r2 >>>= 6)  & 0x3f][0]; v2 |= efp[14][ r2 & 0x3f][1];
        v1 |= efp[13][(r2 >>>= 10) & 0x3f][0]; v2 |= efp[13][ r2 & 0x3f][1];
        v1 |= efp[12][(r2 >>>= 6)  & 0x3f][0]; v2 |= efp[12][ r2 & 0x3f][1];
        v1 |= efp[11][ r1          & 0x3f][0]; v2 |= efp[11][ r1 & 0x3f][1];
        v1 |= efp[10][(r1 >>>= 6)  & 0x3f][0]; v2 |= efp[10][ r1 & 0x3f][1];
        v1 |= efp[ 9][(r1 >>>= 10) & 0x3f][0]; v2 |= efp[ 9][ r1 & 0x3f][1];
        v1 |= efp[ 8][(r1 >>>= 6)  & 0x3f][0]; v2 |= efp[ 8][ r1 & 0x3f][1];
        v1 |= efp[ 7][ l2          & 0x3f][0]; v2 |= efp[ 7][ l2 & 0x3f][1];
        v1 |= efp[ 6][(l2 >>>= 6)  & 0x3f][0]; v2 |= efp[ 6][ l2 & 0x3f][1];
        v1 |= efp[ 5][(l2 >>>= 10) & 0x3f][0]; v2 |= efp[ 5][ l2 & 0x3f][1];
        v1 |= efp[ 4][(l2 >>>= 6)  & 0x3f][0]; v2 |= efp[ 4][ l2 & 0x3f][1];
        v1 |= efp[ 3][ l1          & 0x3f][0]; v2 |= efp[ 3][ l1 & 0x3f][1];
        v1 |= efp[ 2][(l1 >>>= 6)  & 0x3f][0]; v2 |= efp[ 2][ l1 & 0x3f][1];
        v1 |= efp[ 1][(l1 >>>= 10) & 0x3f][0]; v2 |= efp[ 1][ l1 & 0x3f][1];
        v1 |= efp[ 0][(l1 >>>= 6)  & 0x3f][0]; v2 |= efp[ 0][ l1 & 0x3f][1];

        ary[0] = v1; ary[1] = v2;
        return ary;
    }


    private void setup_salt(String salt)
    {
        int i, j, saltbits;

        if (salt.equals(current_salt)) return;
        current_salt = salt;

        saltbits = 0;
        for(i=0; i < 2; i++) {
            int c = Ascii.indexOf(salt.substring(i, i+1));
            if (c<0) c = 0;
            for(j = 0; j < 6; j++) {
                if (((c>>>j)&0x1)!=0) saltbits |= BitMask(6*i+j);
            }
        }

        shuffle_sb(ufc_sb0, current_saltbits ^ saltbits);
        shuffle_sb(ufc_sb1, current_saltbits ^ saltbits);
        shuffle_sb(ufc_sb2, current_saltbits ^ saltbits);
        shuffle_sb(ufc_sb3, current_saltbits ^ saltbits);

        current_saltbits = saltbits;
    }


    private String output_conversion(int vv[], String salt)
    {
        int i, j, s;
        String outbuf = salt;

        for(i=0; i<5; i++) {
            j = (vv[0]>>>(26-6*i)) & 0x3f;
            outbuf += Ascii.substring(j, j+1);
        }
        s     = (vv[1]&0xf) << 2;
        vv[1] = (vv[1]>>>2) | ((vv[0]&0x3)<<30);

        for(i=5; i<10; i++) {
            j = (vv[1]>>>(56-6*i)) & 0x3f;
            outbuf += Ascii.substring(j, j+1);
        }

        outbuf += Ascii.substring(s, s+1);
        return  outbuf;
    }


    private void shuffle_sb(int k[], int saltbits) 
    {
        int i, x;
        for(i=0; i<4096; i++) {
            x = (k[2*i] ^ k[2*i+1]) & saltbits;
            k[2*i]   ^= x;
            k[2*i+1] ^= x;
        }
    }


    private int s_lookup(int i, int s)
    {
        return  sbox[i][((s>>>4)&0x2)|(s&0x1)][(s>>>1)&0xf];
    }


    private int BitMask(int i) 
    {
        int s;
        if (i<12) s = 16;
        else      s = 0;
        return ((1<<(11-i%12+3)) << s);
    }


    private void init_des()
    {
        int i, j, k;
        int comes_from_bit;
        int bit, sg;
        int mask1, mask2;

        for(bit=0; bit<56; bit++) {
            comes_from_bit = pc1[bit] - 1;
            mask1 = bytemask[comes_from_bit%8+1];
            mask2 = longmask[bit%28+4];
            for(j=0; j<128; j++) {
                if((j&mask1)!=0) do_pc1[comes_from_bit/8][bit/28][j] |= mask2;
            }
        }

        for(bit=0; bit<48; bit++) {
            comes_from_bit = pc2[bit] - 1;
            mask1 = bytemask[comes_from_bit%7 + 1];
            mask2 = BitMask(bit%24);
            for(j=0; j<128; j++) {
                if((j&mask1)!=0) do_pc2[comes_from_bit/7][j] |= mask2;
            }
        }

        for(i=0; i<4; i++) {
            for(j=0; j<256; j++) {
                for(k=0; k<2; k++) {
                    eperm32tab[i][j][k] = 0;
                }
            }
        }

        for(bit=0; bit<48; bit++) {
            int comes_from;
            comes_from = perm32[esel[bit]-1]-1;
            mask1      = bytemask[comes_from%8];

            for(j=0; j<256; j++) {
                if ((j&mask1)!=0) eperm32tab[comes_from/8][j][bit/24] |= BitMask(bit % 24);
            }
        }

        for(sg=0; sg<4; sg++) {
            int j1, j2;
            int s1, s2;

            for(j1=0; j1<64; j1++) {
                s1 = s_lookup(2*sg, j1);
                for(j2=0; j2<64; j2++) {
                    int to_permute, inx;

                    s2         = s_lookup(2*sg+1, j2);
                    to_permute = ((s1<<4)|s2) << (24-8*sg);

                    inx = ((j1<<6)|j2) << 1;
                    sb[sg][inx  ]  = eperm32tab[0][(to_permute>>>24) & 0xff][0];
                    sb[sg][inx+1]  = eperm32tab[0][(to_permute>>>24) & 0xff][1];
                    sb[sg][inx  ] |= eperm32tab[1][(to_permute>>>16) & 0xff][0];
                    sb[sg][inx+1] |= eperm32tab[1][(to_permute>>>16) & 0xff][1];
                    sb[sg][inx  ] |= eperm32tab[2][(to_permute>>> 8) & 0xff][0];
                    sb[sg][inx+1] |= eperm32tab[2][(to_permute>>> 8) & 0xff][1];
                    sb[sg][inx  ] |= eperm32tab[3][(to_permute)      & 0xff][0];
                    sb[sg][inx+1] |= eperm32tab[3][(to_permute)      & 0xff][1];
                }
            }
        }

        for(bit=0; bit<48; bit++) {
            e_inverse[esel[bit]-1   ] = bit;
            e_inverse[esel[bit]-1+32] = bit + 48;
        }

        for(i=0; i<16; i++) {
            for(j=0; j<64; j++) {
                for(k=0; k<2; k++) {
                    efp[i][j][k] = 0;
                }
            }
        }

        for(bit=0; bit<64; bit++) {
            int o_bit, o_long;
            int word_value;
            int comes_from_f_bit, comes_from_e_bit;
            int comes_from_word, bit_within_word;

            /* See where bit i belongs in the two 32 bit long's */
            o_long = bit / 32;
            o_bit  = bit % 32;

            comes_from_f_bit = final_perm[bit] - 1;
            comes_from_e_bit = e_inverse[comes_from_f_bit];
            comes_from_word  = comes_from_e_bit/6;
            bit_within_word  = comes_from_e_bit%6;

            mask1 = longmask[bit_within_word+26];
            mask2 = longmask[o_bit];

            for(word_value=0; word_value<64; word_value++) {
                if ((word_value & mask1)!=0)
                    efp[comes_from_word][word_value][o_long] |= mask2;
            }
        }
    }


    private void ufc_mk_keytab(byte[] key)
    {
        int v1, v2;
        int i, v, mi=8;
        byte cc[] = new byte[8];

        if (key.length<mi) mi = key.length;
        for(i=0; i<mi; i++) cc[i] = key[i];
        for(i=mi; i<8; i++) cc[i] = 0x00;

        v1 = v2 = 0;
        for(i=0; i<8; i++) {
            v1 |= do_pc1[i][0][cc[i]&0x7f];
            v2 |= do_pc1[i][1][cc[i]&0x7f];
        }

        for(i=0; i <16; i++) {
            v1 = (v1<<rots[i]) | (v1>>>(28-rots[i]));
            v  = do_pc2[0][(v1>>>21)&0x7f];
            v |= do_pc2[1][(v1>>>14)&0x7f];
            v |= do_pc2[2][(v1>>> 7)&0x7f];
            v |= do_pc2[3][(v1     )&0x7f];
            ufc_keytab[i][0] = v;

            v2 = (v2<<rots[i]) | (v2>>>(28-rots[i]));
            v  = do_pc2[4][(v2>>>21)&0x7f];
            v |= do_pc2[5][(v2>>>14)&0x7f];
            v |= do_pc2[6][(v2>>> 7)&0x7f];
            v |= do_pc2[7][(v2     )&0x7f];
            ufc_keytab[i][1] = v;
       }

        direction = 0;
    }


    private int[]  ufc_doit(int l1, int l2, int r1, int r2, int itr)
    {
        int i, s;

        while(itr>0) {
            for(i=0; i<8; i++) {
                s = ufc_keytab[2*i][0] ^ r1;
                l1 ^= ufc_sb1[(s&0xffff)/4];
                l2 ^= ufc_sb1[(s&0xffff)/4+1];
                l1 ^= ufc_sb0[(s>>>=16)/4];
                l2 ^= ufc_sb0[(s/4)+1];

                s = ufc_keytab[2*i][1] ^ r2;
                l1 ^= ufc_sb3[(s&0xffff)/4];
                l2 ^= ufc_sb3[(s&0xffff)/4+1];
                l1 ^= ufc_sb2[(s>>>=16)/4];
                l2 ^= ufc_sb2[(s/4)+1];

                s = ufc_keytab[2*i+1][0] ^ l1;
                r1 ^= ufc_sb1[(s&0xffff)/4];
                r2 ^= ufc_sb1[(s&0xffff)/4+1];
                r1 ^= ufc_sb0[(s>>>=16)/4];
                r2 ^= ufc_sb0[(s/4)+1];

                s = ufc_keytab[2*i+1][1] ^ l2;
                r1 ^= ufc_sb3[(s&0xffff)/4];
                r2 ^= ufc_sb3[(s&0xffff)/4+1];
                r1 ^= ufc_sb2[(s>>>=16)/4];
                r2 ^= ufc_sb2[(s/4)+1];
            }
            s=l1; l1=r1; r1=s; s=l2; l2=r2; r2=s; itr--;
        }
        return  ufc_dofinalperm(l1, l2, r1, r2);
    }


    public static byte[] to_bin64(byte[] buf)
    {
        int i, j, mi=8, mask;
        byte[] byt = new byte[8];
        byte[] bin = new byte[64];

        if (buf.length<mi) mi = buf.length;
        for(i=0; i<mi; i++) byt[i] = buf[i];
        for(i=mi; i<8; i++) byt[i] = 0x00;

        for(i=0; i<8; i++) {
            mask = 0x80;
            for(j=0; j<8; j++) {
                if ((byt[i]&mask)!=0) bin[i*8+j] = 0x01;
                mask >>>= 1;
            }
        }
        return bin;
    }


    public static byte[] from_bin64(byte[] buf)
    {
        int i, j, mi=64;
        byte[] bin = new byte[64];
        byte[] byt = new byte[8];
        byte c;

        if (buf.length<mi) mi = buf.length;
        for(i=0;  i<mi; i++) bin[i] = buf[i];
        for(i=mi; i<64; i++) bin[i] = 0x00;

        for(i=0; i<8; i++) {
            c = 0x00;
            for(j=0; j<8; j++) c = (byte)((c << 1) | bin[i*8+j]);
            byt[i] = (byte)c;
        }
        return byt;
    }


    public static String bin64_toString(byte[] byt)
    {
        int  i, mi=64;
        String ret = "";

        if (byt.length<mi) mi = byt.length;
        for(i=0; i<mi; i++) ret += Byte.toString(byt[i]);
        return ret;
    }
}

