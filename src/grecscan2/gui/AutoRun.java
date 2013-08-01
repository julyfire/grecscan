/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.Pipeline;
import grecscan2.core.Recombinant;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author wb
 */
class AutoRun extends JDialog {
    private final JProgressBar progress;
    private final JFrame parentFrame;
    private JLabel info;
    private Thread mfeThread;
    private Thread recThread;
    private Thread resetThread;
    private Thread checkMfeThread;
    private Thread checkRecThread;
    
    public AutoRun(JFrame owner){
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
        this.setModal(false);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e){
                startProgress();
            }

        });
        
        this.setVisible(true);
    }
    
    public void startProgress(){
        final Pipeline flow=((MainFrame)parentFrame).getPipeline();
        flow.setWindow(20);
        mfeThread=new Thread(){
                @Override
                public void run(){
                    info.setText("calculating MFE...");
                    if(flow.getMfes()==null){
                        try {
                            flow.calMfeMatrix();
                        } catch (Exception ex) {
                            System.out.println("MFE calculation threads interrupted exception!");
                        }
                    }     
                    
                }
        };
        mfeThread.start(); 
        
        checkMfeThread=new Thread(){
            @Override
            public void run(){
                while(progress.getValue()<100){
                    progress.setValue(flow.getProgress());
                }
            }
        };
        checkMfeThread.start();
        
        resetThread=new Thread(){
            @Override
            public void run(){
                try {
                    mfeThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AutoRun.class.getName()).log(Level.SEVERE, null, ex);
                }
                flow.setProgress(0);
                progress.setValue(0);
            }
        };
        resetThread.start();
        
        checkRecThread=new Thread(){
            @Override
            public void run(){
                try {
                    resetThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AutoRun.class.getName()).log(Level.SEVERE, null, ex);
                }
                while(progress.getValue()<100){
                    progress.setValue(flow.getProgress());
                }
                dispose();
            }
        };
        checkRecThread.start();
        
        recThread=new Thread(){
            @Override
            public void run(){
                
                try {
                    resetThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AutoRun.class.getName()).log(Level.SEVERE, null, ex);
                }
                info.setText("detecting recombination sequences...");
                flow.scanRecombinant();
                try {
                    checkRecThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AutoRun.class.getName()).log(Level.SEVERE, null, ex);
                }
                List<Recombinant> results=flow.getRecs();
                if(results.size()>0){
                    flow.summary();
                    JFrame rrw=new RecResultWindow(flow.getRecs(), parentFrame);
                    rrw.setLocation(parentFrame.getX(), parentFrame.getY()+parentFrame.getHeight());
//                    ((MainFrame)parentFrame).setRecPlotFrame(rrw);
                }
                else{
                    JOptionPane.showMessageDialog(parentFrame, "No recombination event found!","Oops!",JOptionPane.INFORMATION_MESSAGE);
                }

            }
        };
        recThread.start();   
        
    }
}
