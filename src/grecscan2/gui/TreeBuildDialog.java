/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.JobStatus;
import grecscan2.core.Pipeline;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import wbitoolkit.tools.UsefulMethods;

/**
 *
 * @author wb
 */
public class TreeBuildDialog extends JDialog {
    
    private static String[] methods=new String[]{
        "Neighbor-Joining"
    };
    private static String[] models=new String[]{
        "P-distance",
        "JC69",
        "K2P"
    };
    
    private final JFrame parentFrame;
    private JComboBox methodComboBox;
    private JComboBox modelComboBox;
    private JTextField btField;
    private JButton runButton;
    private JButton defaultButton;
    private JButton helpButton;
    private JLabel statusPane;
    private JobStatus jobStatus;
    private PhyloTreeWindow treeFrame;
    private Thread groupThread;
    
    
    public TreeBuildDialog(JFrame owner){
        super(owner,"Phylogenetic Tree Building Dialog",false);
        parentFrame=owner;
        init();
    }

    private void init() {
        JPanel paraPanel=new JPanel();
        paraPanel.setLayout(new BoxLayout(paraPanel,BoxLayout.Y_AXIS));
        paraPanel.setBorder(new TitledBorder(new EtchedBorder(),"Options",TitledBorder.LEFT,TitledBorder.TOP));
        
        Box methodBox=Box.createHorizontalBox();
        JLabel methodLabel=new JLabel("Method");        
        methodComboBox=new JComboBox(methods);
        methodComboBox.setMaximumSize(new Dimension(30,25));
        methodComboBox.setMinimumSize(new Dimension(30,25));
        methodBox.add(methodLabel);
        methodBox.add(Box.createHorizontalGlue());
        methodBox.add(methodComboBox);
        
        Box modelBox=Box.createHorizontalBox();
        JLabel modelLabel=new JLabel("Model");
        modelComboBox=new JComboBox(models);
        modelComboBox.setMaximumSize(new Dimension(30,25));
        modelComboBox.setMinimumSize(new Dimension(30,25));
        modelBox.add(modelLabel);
        modelBox.add(Box.createHorizontalGlue());
        modelBox.add(modelComboBox);
        
        Box btBox=Box.createHorizontalBox();
        JLabel btLabel=new JLabel("Bootstrap");
        btField=new JTextField(10);
        btField.setMaximumSize(new Dimension(30,25));
        btField.setMinimumSize(new Dimension(30,25));
        btField.setText("100");
        btBox.add(btLabel);
        btBox.add(Box.createHorizontalGlue());
        btBox.add(btField);

        paraPanel.add(methodBox);
        paraPanel.add(Box.createVerticalStrut(10));
        paraPanel.add(modelBox);
        paraPanel.add(Box.createVerticalStrut(10));
        paraPanel.add(btBox);

        
        Box buttonBox=Box.createHorizontalBox();
        runButton=new JButton("Run");
        defaultButton=new JButton("Default");
//        helpButton=new JButton("Help");
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(runButton);
        buttonBox.add(defaultButton);
//        buttonBox.add(helpButton);
        
        JPanel sidePanel=new JPanel();
        sidePanel.setLayout(new BorderLayout());
        statusPane=new JLabel();
        sidePanel.add(statusPane);
        sidePanel.add(buttonBox,BorderLayout.EAST);
        
        JPanel wrapPanel=new JPanel();
        wrapPanel.setLayout(new BorderLayout());
        wrapPanel.add(paraPanel);
        wrapPanel.add(sidePanel,BorderLayout.SOUTH);
        this.setPadding(10);
        this.add(wrapPanel);
        
        this.setSize(320, 200);
        this.setLocation(parentFrame.getX() + parentFrame.getWidth()/2 - this.getWidth()/2, parentFrame.getY() +parentFrame.getHeight()/2 - this.getHeight()/2);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        runButton.addActionListener(buttonListener);
        defaultButton.addActionListener(buttonListener);
//        helpButton.addActionListener(buttonListener);
        jobStatus=new JobStatus();
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we) {
                jobStatus.stop=true;
            }       
        });
        
        
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
//        this.add(down, BorderLayout.SOUTH);
    }
    
    private ActionListener buttonListener=new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            final MainFrame mf=(MainFrame)parentFrame;
            final Pipeline pl=mf.getPipeline();
            if(ae.getSource()==runButton){   
                runButton.setEnabled(false);
                final Thread buildTree=new Thread(){
                    @Override
                    public void run(){                       
                        pl.buildTree();                                         
                    }
                };
                final Thread checkBuildTree=new Thread(){
                    @Override
                    public void run(){
                        while(buildTree.isAlive()){
//                            synchronized (jobStatus) {
                                if(getJobStatus().stop){
                                    pl.bt.setStop(true);
                                    System.out.println("\ncheckBuildTree thread stoped!");
                                    return;
                                }
//                            }
                            statusPane.setText(pl.bt.getProgress());
                        }
                    }
                };
                groupThread=new Thread(){
                    @Override
                    public void run(){
                        pl.setTreeMethod(methodComboBox.getSelectedIndex());
                        pl.setTreeModel(modelComboBox.getSelectedIndex());
                        pl.setBootstrap(Integer.parseInt(btField.getText()));
                        buildTree.start();
                        checkBuildTree.start();
                        try {
                            checkBuildTree.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TreeBuildDialog.class.getName()).log(Level.SEVERE, null, ex);
                        }
//                        synchronized (jobStatus) {
                            if(getJobStatus().stop){
                                System.out.println("group thread stoped!");
                                return;
                            }
//                        }
                        statusPane.setText("Partition tree");
                        pl.partitionTree();
                        dispose();
                        
                        setTreeFrame(new PhyloTreeWindow(mf,pl.getTree()));
                        getTreeFrame().setClusters(pl.getClusters());
                        getTreeFrame().addGroupMark();
                        getTreeFrame().repaint();
                        getTreeFrame().setVisible(true);
                        jobStatus.finished=true;
                        mf.resetStatusBar();
                        mf.setTreeFrame(getTreeFrame());
                        mf.getStatusBar().add(new JLabel("Sequence grouping finished!"));
                        mf.validate();
                    }
                };
                groupThread.start();
            }
            else if(ae.getSource()==defaultButton){
                methodComboBox.setSelectedIndex(0);
                modelComboBox.setSelectedIndex(0);
                btField.setText(100+"");
            }
            else if(ae.getSource()==helpButton){
                
            }
        }
        
    };

    /**
     * @return the jobStatus
     */
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * @param jobStatus the jobStatus to set
     */
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * @return the treeFrame
     */
    public PhyloTreeWindow getTreeFrame() {
        return treeFrame;
    }

    /**
     * @param treeFrame the treeFrame to set
     */
    public void setTreeFrame(PhyloTreeWindow treeFrame) {
        this.treeFrame = treeFrame;
    }
}
