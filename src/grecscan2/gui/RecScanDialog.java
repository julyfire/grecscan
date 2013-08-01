/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author wb
 */
public class RecScanDialog extends JDialog{
    
    private JProgressBar progress;
    private JFrame parentFrame;
    private JLabel info;
    private Thread mfeThread;
    private Thread recThread;
    private Thread resetThread;
    private Thread checkMfeThread;
    private Thread checkRecThread;
    
    public RecScanDialog(MainFrame mf){
        super(mf,"Calcutate MFE curve",true);
        parentFrame=mf;
        
        init(mf);
    }

    private void init(JFrame owner) {
        parentFrame=owner;
        
        progress = new JProgressBar(1, 100);
        progress.setPreferredSize(new Dimension(300,50));
        progress.setStringPainted(true);
        progress.setBackground(Color.white); 
        
        info=new JLabel("Start detection...");
        this.add(progress, BorderLayout.SOUTH); 
        this.add(info);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.setUndecorated(true);
        this.pack();
        this.setLocation(parentFrame.getX() + parentFrame.getWidth()/2 - this.getWidth()/2, parentFrame.getY() +parentFrame.getHeight()/2 - this.getHeight()/2);
    }
    
}
