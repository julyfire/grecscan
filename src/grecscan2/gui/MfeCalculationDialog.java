/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.Mfe;
import grecscan2.core.Pipeline;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author wb
 */
public class MfeCalculationDialog extends JDialog {
    
    private JFrame parentFrame;
    private JTextField winField;
    private JButton runButton;
    private JProgressBar pro;
    private Mfe pm;
    private Thread calThread;
    private Thread checkThread;
    private boolean pauseCheckThread=false;
    
    public MfeCalculationDialog(MainFrame mf){
        super(mf,"Calcutate MFE curve",true);
        parentFrame=mf;
        
        init();
    }
    
    private void init(){
        Box paraBox=new Box(BoxLayout.Y_AXIS);
        
        JLabel winLabel=new JLabel("Window size  ");
        winField=new JTextField(5);
        winField.setText("20");
        Box winPanel=new Box(BoxLayout.X_AXIS);
        winPanel.add(winLabel);
        winPanel.add(winField);
        winPanel.add(Box.createHorizontalStrut(200));
        paraBox.add(winPanel);
        paraBox.add(Box.createVerticalStrut(5));
        
        paraBox.setBorder(new TitledBorder(new EtchedBorder(),"Options",TitledBorder.LEFT,TitledBorder.TOP));
        
        this.add(paraBox,BorderLayout.CENTER);
        
        Box actionBox=new Box(BoxLayout.X_AXIS);
        runButton=new JButton("Run");
        actionBox.add(runButton);
        actionBox.add(Box.createHorizontalStrut(3));
        
        pro = new JProgressBar(0,100);
        pro.setStringPainted(true);
        actionBox.add(pro);
        
        this.add(actionBox,BorderLayout.SOUTH);
        
        this.pack();
        this.setLocation(parentFrame.getX() + parentFrame.getWidth()/2 - this.getWidth()/2, parentFrame.getY() +parentFrame.getHeight()/2 - this.getHeight()/2);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        runButton.addActionListener(runProcessListener);
//        this.addWindowListener(stopEventListener);
        
        this.setVisible(true);
    }
    
    private ActionListener runProcessListener=new ActionListener() {
        

        @Override
        public void actionPerformed(ActionEvent ae) {
            runButton.setEnabled(false);
            final Pipeline pl=((MainFrame)parentFrame).getPipeline();
            int window=Integer.parseInt(winField.getText());
            
            if(pl.getMfes()!=null){
                if(window==pl.getWindow()){
                    pro.setValue(100);
                    MfeCalculationDialog.this.dispose();
                    return;
                }
                pm=pl.getMfeObject();
            }
            
            
            pl.setWindow(window);
            pl.setProgress(0);
            
//            if(smoothBox.isSelected()){
//                flow.setSw(Double.parseDouble(swField.getText()));
//                flow.setSi(Integer.parseInt(siField.getText()));
//            }
            
            calThread=new Thread(){
                @Override
                public void run(){
                    try {
                        pl.calMfeMatrix();   
                        
                    } catch (Exception ie){
                        
                    }
                    
                    MfeCalculationDialog.this.dispose();

                }
            };
            calThread.start(); 
            
            
            checkThread=new Thread(){
                @Override
                public void run(){
                    try {
                        while(pro.getValue()<100){
                            
                            synchronized (checkThread) {
                                if(pauseCheckThread){
                                    checkThread.wait();
                                }
                            }
                            
                            pro.setValue(pl.getProgress());

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
//                    GRecMfeDialog.this.dispose();
//                    
//                    showGraphPane();
                    
                }
            };
            checkThread.start();
            
        }
    };
    
//    private WindowAdapter stopEventListener=new WindowAdapter() {
//        @Override
//        public void windowClosing(WindowEvent ex){
//            final Pipeline pl=((MainFrame)parentFrame).getPipeline();
//            if(pl.getMfeObject()==null){
//                MfeCalculationDialog.this.dispose();
//                return;
//            } 
//            boolean[] status=pl.getMfeObject().getPauseStatus();
//            status[0]=true;
//            pauseCheckThread=true;
// 
//           
//            
//            int option=JOptionPane.showConfirmDialog(MfeCalculationDialog.this, "Are you sure to stop the calculation?","stop",JOptionPane.YES_NO_OPTION);
//            
//            
//            
//            if(option==JOptionPane.YES_OPTION){
//                status[1]=true; //stop mfe calculation thread
//                pro.setValue(100);
//                
//
//                pl.setMfeObject(pm);
//                
//                MfeCalculationDialog.this.dispose();                
//            }
//            else{
//                synchronized (status) {
//                    status[0]=false;
//                    pauseCheckThread=false;
//                    status.notifyAll();
//                }
//                synchronized (checkThread){
//                    checkThread.notifyAll();
//                }
//            }
//        }
//    };
    
}
