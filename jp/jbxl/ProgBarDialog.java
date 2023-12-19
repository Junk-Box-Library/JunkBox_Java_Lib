/*
 * Created on 2004/12/04
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jp.jbxl;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;


/**
 * プログレスバー<br>
 * <br>
 * テキストモード字の使用例<br>
 *   pbd.mesgText.setText("転送中．\n"+file.getName());<br>
 *   pbd.mesgText.print(pbd.mesgText.getGraphics());<br>
 *<br>
 * グラフィックモード時の使用例<br>
 *   pbd.prgBar.setValue(pbd.prgBar.getValue() + n);<br>
 *   pbd.prgBar.paint(pbd.prgBar.getGraphics()); <br>
 *<br>
 * @author Fumi
 */
public class ProgBarDialog extends JDialog
{
    /**
     * <code>serialVersionUID</code> のコメント
     */
    private static final long serialVersionUID = -6049446785650861737L;

    private javax.swing.JPanel jContentPane = null;
    private int pmax=100, pmin=0;
    
    /**
     * プログレスバー本体
     */
    public  JProgressBar prgBar = null;
    
    /**
     * プログレスバーのテキストモード時の表示部．
     */
    public  JTextArea mesgText  = null;
    

    /**
     * デフォルトコンストラクタ
     */
    public ProgBarDialog()
    {
        super();
        initialize();
    }


    /**
     * コンストラクタ．
     * @param frame 呼び出した（親）フレームを指定．（通常は this を指定）
     * @param title タイトル
     * @param min バーの目盛の最小値
     * @param max バーの目盛の最大値
     */
    public ProgBarDialog(Frame frame, String title, int min, int max)
    {
        super(frame, title, false);
        try {
            //prgBar = new JProgressBar(min, max);
            pmax = max;
            pmin = min;
            initialize();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    
    /**
     * コンストラクタ．
     * @param title タイトル
     * @param min バーの目盛の最小値
     * @param max バーの目盛の最大値
     */
    public ProgBarDialog(String title, int min, int max)
    {
        this(null, title, min, max);
    }

    
    /**
     * コンストラクタ．バーの目盛の最小値は 0となる．
     * @param title タイトル
     * @param max バーの目盛の最大値
     */
    public ProgBarDialog(String title, int max)
    {
        this(null, title, 0, max);
    }


    /**
     * ダイアログを画面の中央に表示する．
     */
    public void  showup_Center() 
    {
        Dimension ScrnSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dlogSize = this.getSize();
        this.setLocation((ScrnSize.width-dlogSize.width)/2, (ScrnSize.height-dlogSize.height)/2);
        this.setVisible(true);
    }
        
    
    private void initialize()
    {
        this.setResizable(false);  // Generated
        this.setTitle("プログレスバー");
        this.setSize(299, 56);
        this.setContentPane(getJContentPane());
        this.setVisible(false);
    }

    
    private javax.swing.JPanel getJContentPane()
    {
        if(jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getPrgBar(), null);
            jContentPane.add(getMesg_Text(), null);
        }
        return jContentPane;
    }


    private JProgressBar getPrgBar()
    {
        if (prgBar == null) {
            prgBar = new JProgressBar(pmin, pmax);
            prgBar.setBounds(7, 25, 279, 21);
            prgBar.setStringPainted(true);  // Generated
            prgBar.setVisible(true);  // Generated
        }
        return prgBar;
    }

    
    private JTextArea getMesg_Text()
    {
        if (mesgText == null) {
            mesgText = new JTextArea();
            mesgText.setBounds(8, 6, 276, 18);
            mesgText.setBackground(java.awt.SystemColor.activeCaptionBorder);
        }
        return mesgText;
    }
 
}  //  @jve:decl-index=0:visual-constraint="10,10"
