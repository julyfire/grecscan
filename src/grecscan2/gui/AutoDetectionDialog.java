/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.JobStatus;
import grecscan2.core.Pipeline;
import grecscan2.core.Recombinant;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author wb
 */
public class AutoDetectionDialog extends JDialog {

    private JFrame parentFrame;
    private JTextField windowField;
    private JTextField spanField;
    private JTextField areaField;
    private JTextField threadField;
    private JTextField permutationField;
    private JCheckBox correctionCheckbox;
    private JButton runButton;
    private JButton defaultButton;
    private JButton helpButton;
    private JButton stopButton;
    
    private JProgressBar progress;
    private JLabel infoLabel;
    private Thread mfeThread;
    private Thread recThread;
    private Thread resetThread;
    private Thread checkMfeThread;
    private Thread checkRecThread;
    private JobStatus jobStatus;
    

    
    public AutoDetectionDialog(JFrame owner){
        super(owner,"Detection Dialog",true);
        parentFrame=owner;
        init();
    }
    
    private void init(){
        JPanel paraPanel=new JPanel();
        paraPanel.setLayout(new BoxLayout(paraPanel,BoxLayout.Y_AXIS));
        paraPanel.setBorder(new TitledBorder(new EtchedBorder(),"Options",TitledBorder.LEFT,TitledBorder.TOP));
        
        Box windowBox=Box.createHorizontalBox();
        JLabel windowLabel=new JLabel("MFE window size");
        windowField=new JTextField(10);
        windowField.setText("20");
        windowField.setMaximumSize(new Dimension(30,25));
        windowBox.add(windowLabel);
        windowBox.add(Box.createHorizontalGlue());
        windowBox.add(windowField);
        
        Box spanBox=Box.createHorizontalBox();
        JLabel spanLabel=new JLabel("Smooth span size");
        spanField=new JTextField(10);
        spanField.setText("300");
        spanField.setMaximumSize(new Dimension(30,25));
        spanBox.add(spanLabel);
        spanBox.add(Box.createHorizontalGlue());
        spanBox.add(spanField);
        
        Box areaBox=Box.createHorizontalBox();
        JLabel areaLabel=new JLabel("Area cutoff");
        areaField=new JTextField(10);
        areaField.setText("0.01");
        areaField.setMaximumSize(new Dimension(30,25));
        areaBox.add(areaLabel);
        areaBox.add(Box.createHorizontalGlue());
        areaBox.add(areaField);
        
        Box threadBox=Box.createHorizontalBox();
        JLabel threadLabel=new JLabel("Number of threads");
        threadField=new JTextField(10);
        threadField.setText("3");
        threadField.setMaximumSize(new Dimension(30,25));
        threadBox.add(threadLabel);
        threadBox.add(Box.createHorizontalGlue());
        threadBox.add(threadField);
        
        Box permutationBox=Box.createHorizontalBox();
        JLabel permutationLabel=new JLabel("Permutation times");
        permutationField=new JTextField(10);
        permutationField.setText("1000");
        permutationField.setMaximumSize(new Dimension(30,25));
        permutationBox.add(permutationLabel);
        permutationBox.add(Box.createHorizontalGlue());
        permutationBox.add(permutationField);
        
        Box correctionBox=Box.createHorizontalBox();
        JLabel correctionLabel=new JLabel("Bonferroni correction");
        correctionCheckbox=new JCheckBox();
        correctionCheckbox.setSelected(true);
        correctionBox.add(correctionLabel);
        correctionBox.add(Box.createHorizontalGlue());
        correctionBox.add(correctionCheckbox);
        
        paraPanel.add(windowBox);
        paraPanel.add(Box.createVerticalStrut(10));
        paraPanel.add(spanBox);
        paraPanel.add(Box.createVerticalStrut(10));
        paraPanel.add(areaBox);
        paraPanel.add(Box.createVerticalStrut(10));
        paraPanel.add(threadBox);
        paraPanel.add(Box.createVerticalStrut(10));
        paraPanel.add(permutationBox);
        paraPanel.add(Box.createVerticalStrut(10));
        paraPanel.add(correctionBox);
        
        Box buttonBox=Box.createHorizontalBox();
        runButton=new JButton("Run");
        defaultButton=new JButton("Default");
        helpButton=new JButton("Help");
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(runButton);
        buttonBox.add(defaultButton);
        buttonBox.add(helpButton);
        
        JPanel wrapPanel=new JPanel();
        wrapPanel.setLayout(new BorderLayout());
        wrapPanel.add(paraPanel);
        wrapPanel.add(buttonBox,BorderLayout.SOUTH);
        this.setPadding(10);
        this.add(wrapPanel);
        
        this.setSize(320, 320);
        this.setLocation(parentFrame.getX() + parentFrame.getWidth()/2 - this.getWidth()/2, parentFrame.getY() +parentFrame.getHeight()/2 - this.getHeight()/2);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        runButton.addActionListener(buttonListener);
        defaultButton.addActionListener(buttonListener);
        helpButton.addActionListener(buttonListener);
        
        
        this.setVisible(true);
    }
    
    private void setPadding(int padding){
        this.setLayout(new BorderLayout());
        JPanel top=new JPanel();
        top.setPreferredSize(new Dimension(800,padding));
        JPanel right=new JPanel();
        right.setPreferredSize(new Dimension(padding,400));
        JPanel down=new JPanel();
        down.setPreferredSize(new Dimension(800,padding));
        JPanel left=new JPanel();
        left.setPreferredSize(new Dimension(padding,400));
        
        this.add(left, BorderLayout.WEST);
        this.add(right, BorderLayout.EAST);
        this.add(top, BorderLayout.NORTH);
        this.add(down, BorderLayout.SOUTH);
    }
    
    private ActionListener buttonListener=new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            MainFrame mf=(MainFrame)parentFrame;
            final JPanel statusBar=mf.getStatusBar();
            
            if(ae.getSource()==runButton){
                dispose();
                
                Pipeline flow=mf.getPipeline();
                
                flow.setWindow(Integer.parseInt(windowField.getText()));
                flow.setLoessSpan(Integer.parseInt(spanField.getText()));
                flow.setAreaCutoff(Double.parseDouble(areaField.getText()));
                flow.setPermutationTimes(Integer.parseInt(permutationField.getText()));
                flow.setThreadNum(Integer.parseInt(threadField.getText()));
                flow.setBonferroniCorrection(correctionCheckbox.isSelected());
               
                //make progress bar
                Box progressBox=Box.createHorizontalBox();
                progress = new JProgressBar(1, 100);
                progress.setPreferredSize(new Dimension(300,25));
                progress.setStringPainted(true);
                progress.setBackground(Color.white);                
                infoLabel=new JLabel("Start detection...");
                stopButton=new JButton("Stop");
                stopButton.addActionListener(buttonListener);               
                progressBox.add(infoLabel);
                progressBox.add(progress);
                progressBox.add(stopButton);
                statusBar.removeAll();
                statusBar.add(progressBox);
                statusBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                statusBar.validate();
                
                new Thread(){
                    @Override
                    public void run(){
                        long start,end,time;  
                        start = System.currentTimeMillis();
                        startJob();
                        try {
                            recThread.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AutoDetectionDialog.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        end = System.currentTimeMillis();
                        time = end - start;
                        ((MainFrame)parentFrame).resetStatusBar();
                        statusBar.add(new JLabel("Finishied detection. (total time: "+(time/1000.0)+" seconds)"));
                        statusBar.validate();
                        System.out.println("Detection is finishied!");
                        System.out.println("take "+(time/1000.0)+"s");
                    }
                }.start();
                
        
            }
            else if(ae.getSource()==stopButton){
                jobStatus.pause=true;
                jobStatus.stop=false;
                int option=JOptionPane.showConfirmDialog(AutoDetectionDialog.this, "Are you sure to stop the calculation?","stop",JOptionPane.YES_NO_OPTION);            
            
                if(option==JOptionPane.YES_OPTION){
                    synchronized (jobStatus) {
                        jobStatus.pause=false;
                        jobStatus.stop=true; 
                        jobStatus.notifyAll();
                    }
                }
                else{
                    synchronized (jobStatus) {
                        jobStatus.pause=false;
                        jobStatus.stop=false;
                        jobStatus.notifyAll();
                    }

                }
        
            }
            else if(ae.getSource()==defaultButton){
                windowField.setText("20");
                spanField.setText("300");
                areaField.setText("0.01");
                threadField.setText("3");
                permutationField.setText("1000");
                correctionCheckbox.setSelected(true);
            }
            else if(ae.getSource()==helpButton){
                
            }
        }
        
    };
    
    public void startJob(){
        
        
        final Pipeline flow=((MainFrame)parentFrame).getPipeline();
        jobStatus=new JobStatus();
        flow.setJobStatus(jobStatus);
        
        mfeThread=new Thread(){
                @Override
                public void run(){
                    infoLabel.setText("calculating MFE...");
                    if(flow.getMfeObject()==null){
                        try {
                            flow.calMfeMatrix();
                        } catch (Exception ex) {
                            System.err.println("MFE calculation threads interrupted exception!");
                        }
                    }     
                }
        };
        mfeThread.start(); 
        
        checkMfeThread=new Thread(){
            @Override
            public void run(){
                while(progress.getValue()<100){
                    synchronized(jobStatus){
                        if(jobStatus.pause){
                            try {
                                jobStatus.wait();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(AutoDetectionDialog.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(jobStatus.stop){
                            System.out.println("check mfe thread stop");
                            return;
                        }
                    }
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
                    synchronized(jobStatus){
                        if(jobStatus.pause){
                            try {
                                jobStatus.wait();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(AutoDetectionDialog.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(jobStatus.stop){
                            System.out.println("check rec thread stop");
                            return;
                        }
                    }
                    progress.setValue(flow.getProgress());
                    infoLabel.setText("Scan recombinants..."+flow.getDetailProgress()+"/"+flow.getAln().getNoOfSeq());
                }
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
                infoLabel.setText("Scan recombinants...");
                flow.scanRecombinant();
                try {
                    checkRecThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AutoRun.class.getName()).log(Level.SEVERE, null, ex);
                }
                JPanel statusBar=((MainFrame)parentFrame).getStatusBar();
                List<Recombinant> results=flow.getRecs();
                if(results.size()>0){
                    flow.summary();
                    ((MainFrame)parentFrame).showResults();
                }
                else{
                    ((MainFrame)parentFrame).resetStatusBar();
                    JLabel info=new JLabel();
                    statusBar.add(info);
                    if(jobStatus.stop){
                        info.setText("Detection is stoped by user!");
                        flow.setProgress(0);
                        flow.setMfeObject(null);
                        flow.setRecDetectionObject(null);
                        JOptionPane.showMessageDialog(parentFrame, "The detection has been terminated!","Oops!",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else{
                        info.setText("Detection is finishied!");
                        JOptionPane.showMessageDialog(parentFrame, "No recombination event found!","Oops!",JOptionPane.INFORMATION_MESSAGE);
                    }
                                      
                }
            }
        };
        recThread.start();      
    }
}
