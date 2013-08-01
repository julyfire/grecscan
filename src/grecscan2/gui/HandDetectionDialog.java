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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author wb
 */
public class HandDetectionDialog extends JDialog {
    private JFrame parentFrame;
    
    private JList seqList;
    private JCheckBox queryCheckBox;
    private JComboBox queryField;
    private JTree groupTree;
    private JButton newButton;
    private JButton deleteButton;
    private JButton runButton;
    private JButton moveToTree;
    private JButton moveToList;
    private Pipeline flow;
    private JTextField windowField;
    private JTextField spanField;
    private JTextField areaField;
    private JTextField threadField;
    private JTextField permutationField;
    private JCheckBox correctionCheckbox;
    private JButton defaultButton;
    private JobStatus jobStatus;
    private Thread mfeThread;
    private JProgressBar progress;
    private JLabel infoLabel;
    private JButton stopButton;
    private Thread checkMfeThread;
    private Thread resetThread;
    private Thread checkRecThread;
    private Thread recThread;
    private Thread recThread2;
    private String querySeq;
    private DropTarget queryTarget;
    private DropTarget treeTarget;
    private DropTarget seqListTarget;
    private JTextField searchField;
    
    public HandDetectionDialog(JFrame owner){
        super(owner,"Custom Recombination Detection",true);
        parentFrame=owner;
        this.flow=((MainFrame)parentFrame).getPipeline();
        init();
    }
    
    public void init(){
        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
        JPanel paraPanel=createParametersPanel();
        JPanel seqHandlePanel=createSeqHandlePanel(); 
        mainPanel.add(paraPanel);
        mainPanel.add(Box.createHorizontalStrut(10));
        mainPanel.add(seqHandlePanel);

        this.setPadding(10);
        this.add(mainPanel);
        this.setSize(850,400);
        this.setLocation(parentFrame.getX() + parentFrame.getWidth()/2 - this.getWidth()/2, parentFrame.getY() +parentFrame.getHeight()/2 - this.getHeight()/2);
        this.setResizable(false);
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
    
    private JPanel createParametersPanel(){
        
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
        
        Box buttonBox=Box.createHorizontalBox();
        defaultButton=new JButton("Default");
        defaultButton.addActionListener(new RecActionListener());
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(defaultButton);
        
        paraPanel.add(windowBox);
        paraPanel.add(Box.createVerticalStrut(20));
        paraPanel.add(spanBox);
        paraPanel.add(Box.createVerticalStrut(20));
        paraPanel.add(areaBox);
        paraPanel.add(Box.createVerticalStrut(20));
        paraPanel.add(threadBox);
        paraPanel.add(Box.createVerticalStrut(20));
        paraPanel.add(permutationBox);
        paraPanel.add(Box.createVerticalStrut(20));
        paraPanel.add(correctionBox);
        paraPanel.add(Box.createVerticalStrut(20));
        paraPanel.add(buttonBox);
        paraPanel.add(Box.createVerticalStrut(20));
        
        
        return paraPanel;
    }
    
    private JPanel createSeqHandlePanel(){
        JPanel seqHandlePanel=new JPanel();
        seqHandlePanel.setLayout(new BoxLayout(seqHandlePanel,BoxLayout.X_AXIS));
        
        JPanel seqPanel =new JPanel(new BorderLayout());
        seqPanel.setPreferredSize(new Dimension(150,250));
        seqPanel.setBorder(BorderFactory.createLineBorder(new Color(122,138,153)));
        seqList=createSeqList(flow.getTitles());      
        seqPanel.add(new JScrollPane(seqList));
        searchField=new JTextField();
        searchField.addActionListener(new RecActionListener());
        searchField.addKeyListener(new RecActionListener());
        seqPanel.add(searchField,BorderLayout.SOUTH);
        
        Box moveButtonBox=new Box(BoxLayout.Y_AXIS);
        moveToTree=new JButton(">");     
        moveToList=new JButton("<");    
//        moveButtonBox.add(Box.createVerticalStrut(20));
        moveButtonBox.add(moveToTree);
        moveButtonBox.add(Box.createVerticalStrut(10));
        moveButtonBox.add(moveToList);
        
        JPanel groupPanel=new JPanel();
        groupPanel.setPreferredSize(new Dimension(250,250));
        groupPanel.setLayout(new BorderLayout(0,5));
        JPanel queryPanel=new JPanel();
        JPanel subjectPanel=new JPanel(new BorderLayout());
        JPanel actionPanel=new JPanel();
        groupPanel.add(queryPanel,BorderLayout.NORTH);
        groupPanel.add(subjectPanel);
        groupPanel.add(actionPanel,BorderLayout.SOUTH); 
        
        queryCheckBox=new JCheckBox("Select a query sequence", false);
        queryField=createSeqComboBox(flow.getTitles());
        queryPanel.setLayout(new BoxLayout(queryPanel,BoxLayout.Y_AXIS));
        queryPanel.add(queryCheckBox);
        queryPanel.add(queryField);
        
        JLabel groupLabel=new JLabel("Select groups between which recombination may occur");
        groupTree=createSeqTree();
        groupTree.setBorder(BorderFactory.createLineBorder(new Color(122,138,153)));
        subjectPanel.add(groupLabel,BorderLayout.NORTH);
        subjectPanel.add(new JScrollPane(groupTree));
        
        newButton=new JButton("New group");
        
        deleteButton=new JButton("Delete group");
        
        runButton=new JButton("Run");
        
        actionPanel.add(newButton);
        actionPanel.add(deleteButton);
        actionPanel.add(runButton);
        
        
        seqHandlePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        seqHandlePanel.add(seqPanel);
        seqHandlePanel.add(moveButtonBox);
        seqHandlePanel.add(groupPanel);
        
        seqList.addListSelectionListener(new RecActionListener());
        queryCheckBox.addItemListener(new RecActionListener());
        queryField.addItemListener(new RecActionListener());
        
        moveToTree.addActionListener(new RecActionListener());
        moveToList.addActionListener(new RecActionListener());
        newButton.addActionListener(new RecActionListener());
        deleteButton.addActionListener(new RecActionListener());
        runButton.addActionListener(new RecActionListener());
        
        return seqHandlePanel;
    }
    
    private JList createSeqList(String[] seqs){
        JList list=new JList();
        DefaultListModel listModel=new DefaultListModel();
        for(String seq:seqs)
            listModel.addElement(seq);
        list.setModel(listModel);
        list.setDragEnabled(true);
        seqListTarget=new DropTarget(list,DnDConstants.ACTION_COPY,new RecActionListener());
        list.setDropTarget(seqListTarget);
        
        return list;
    }
    
    private JComboBox createSeqComboBox(String[] seqs){
        JComboBox comboBox=new JComboBox();
        DefaultComboBoxModel model=new DefaultComboBoxModel();
        model.addElement(" ");
        for(String seq:seqs)
            model.addElement(seq);   
        comboBox.setModel(model);
        comboBox.setEnabled(false);
        queryTarget=new DropTarget(comboBox,DnDConstants.ACTION_COPY,new RecActionListener());
        comboBox.setDropTarget(queryTarget);
        return comboBox;
    }
    private JTree createSeqTree(){
        JTree tree=new JTree();
        DefaultMutableTreeNode root=new DefaultMutableTreeNode();
        DefaultTreeModel model=new DefaultTreeModel(root);
        tree.setModel(model);
        tree.setEditable(true);//if the tree can be edited
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        treeTarget=new DropTarget(tree,DnDConstants.ACTION_COPY,new RecActionListener());
        tree.setDropTarget(treeTarget);
        return tree; 
    }
    
    private List getGroup(){
        
        DefaultTreeModel treeModel=(DefaultTreeModel) groupTree.getModel();
        DefaultMutableTreeNode treeRoot=(DefaultMutableTreeNode) treeModel.getRoot();
        int groupNum=treeRoot.getChildCount();
        List groups=new LinkedList();
        for(int i=0;i<groupNum;i++){
            DefaultMutableTreeNode group=(DefaultMutableTreeNode) treeRoot.getChildAt(i);
            
            int groupSize=group.getChildCount();
            if(groupSize>0){
                String[] inGroup=new String[groupSize];
                for(int j=0;j<groupSize;j++){
                    String seqName=(String)((DefaultMutableTreeNode)group.getChildAt(j)).getUserObject();
                    inGroup[j]=seqName;
                    flow.getAln().getSeqByName(seqName).setSelected(true);
                }
                groups.add(inGroup);
            }
            
        }
        return groups;
    }
    
    
    class RecActionListener implements ActionListener,ListSelectionListener,ItemListener,DropTargetListener,KeyListener{
        DefaultListModel listModel;
        DefaultTreeModel treeModel;
        DefaultComboBoxModel combModel;
        DefaultMutableTreeNode treeRoot;
        DefaultMutableTreeNode selectedNode;
        
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==newButton){
                newGroup();
            }
            else if(e.getSource()==deleteButton){
                deleteGroup();
            }
            else if(e.getSource()==moveToTree){
                moveToTree();               
            }
            else if(e.getSource()==moveToList){
                moveToList();
            }
            else if(e.getSource()==runButton){
                List<String[]> groups=getGroup();
                if(groups.isEmpty()){
                    JOptionPane.showMessageDialog(HandDetectionDialog.this, "At least one group must be specified!","Warning",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                querySeq=(String)queryField.getSelectedItem();
                flow.setGroups(groups);
                flow.updateSelectedSeqsIndex();
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
                stopButton.addActionListener(new RecActionListener());               
                progressBox.add(infoLabel);
                progressBox.add(progress);
                progressBox.add(stopButton);
                final JPanel statusBar=((MainFrame)parentFrame).getStatusBar();
                statusBar.removeAll();
                statusBar.add(progressBox);
                statusBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                statusBar.validate();
                
                new Thread(){
                    @Override
                    public void run(){
                        long start,end,time;  
                        start = System.currentTimeMillis();
                        createJob();
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
            else if(e.getSource()==searchField){
                searchSeq();
            }
        }
        
        

        @Override
        public void itemStateChanged(ItemEvent ie) {
            if(ie.getSource()==queryCheckBox){
                if(queryCheckBox.isSelected()){
                    queryField.setEnabled(true);
                }
                else{
                    queryField.setEnabled(false);
                    queryField.setSelectedItem(" ");
                }
            }
            else if(ie.getSource()==queryField){
                if(ie.getStateChange()==ItemEvent.SELECTED){
                    listModel=(DefaultListModel) seqList.getModel();
                    combModel=(DefaultComboBoxModel) queryField.getModel();
                    listModel.removeAllElements();
                    for(int i=0;i<combModel.getSize();i++){
                        if(combModel.getElementAt(i).equals(combModel.getSelectedItem()))
                            continue;
                        if(combModel.getElementAt(i).equals(" "))
                            continue;
                        listModel.addElement(combModel.getElementAt(i));
                    }    
                }  
            }
        }


        @Override
        public void valueChanged(ListSelectionEvent lse) {
            
        }
        
        private void newGroup(){
            DefaultMutableTreeNode newGroup=new DefaultMutableTreeNode("new group");
            treeModel=(DefaultTreeModel) groupTree.getModel();
            treeRoot=(DefaultMutableTreeNode) treeModel.getRoot();
            treeModel.insertNodeInto(newGroup, treeRoot, treeRoot.getChildCount());
            groupTree.startEditingAtPath(new TreePath(newGroup.getPath()));
           
        }
        
        private void deleteGroup(){
            listModel=(DefaultListModel) seqList.getModel();
            treeModel=(DefaultTreeModel) groupTree.getModel();
            combModel=(DefaultComboBoxModel) queryField.getModel();
            selectedNode=(DefaultMutableTreeNode)groupTree.getLastSelectedPathComponent();
            if(selectedNode!=null){
                
                if(selectedNode.getAllowsChildren()==false){
                    listModel.addElement(selectedNode.getUserObject());
                }
                else{
                    int childNum=selectedNode.getChildCount();
                    if(childNum>0)
                        for(int i=0;i<childNum;i++)
                            listModel.addElement(((DefaultMutableTreeNode)selectedNode.getChildAt(i)).getUserObject());  
                }
                treeModel.removeNodeFromParent(selectedNode);
                
                updateQueryModle();
                  
            }
        }
        
        private void moveToTree(){
            treeModel=(DefaultTreeModel) groupTree.getModel();
            DefaultMutableTreeNode root=((DefaultMutableTreeNode) treeModel.getRoot());
            int groupNum=root.getChildCount();
            if(groupNum==0){
                newGroup();
            }
            else if(groupNum==1){
                groupTree.setSelectionRow(0);
            }
            listModel=(DefaultListModel) seqList.getModel();
            combModel=(DefaultComboBoxModel) queryField.getModel();
            Object[] seqs=seqList.getSelectedValues();
            selectedNode=(DefaultMutableTreeNode)groupTree.getLastSelectedPathComponent();
            if(selectedNode==null || selectedNode.getAllowsChildren()==false) return;
            DefaultMutableTreeNode newNode=new DefaultMutableTreeNode();
            for(Object seq:seqs){
                listModel.removeElement(seq);
                newNode=new DefaultMutableTreeNode(seq);
                newNode.setAllowsChildren(false);
                treeModel.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());     
            }
            TreeNode[] nodes=treeModel.getPathToRoot(newNode);
            TreePath path=new TreePath(nodes);
            groupTree.scrollPathToVisible(path);
            
            updateQueryModle();
        }
        
        private void moveToList(){
            listModel=(DefaultListModel) seqList.getModel();
            treeModel=(DefaultTreeModel) groupTree.getModel();
            combModel=(DefaultComboBoxModel) queryField.getModel();
            if(groupTree.getSelectionPaths()==null) return;
            for(TreePath path:groupTree.getSelectionPaths()){
                selectedNode=(DefaultMutableTreeNode)path.getLastPathComponent();
                if(selectedNode!=null && selectedNode.getAllowsChildren()==false){
                    treeModel.removeNodeFromParent(selectedNode);
                    listModel.addElement(selectedNode.getUserObject());
                }
            }
            updateQueryModle();
        }
        
        private void searchSeq(){
            seqList.clearSelection();
            String field=searchField.getText();
            if(field==null || field.equals("")) return;
            listModel=(DefaultListModel) seqList.getModel();
            Pattern p=Pattern.compile(field,Pattern.CASE_INSENSITIVE);
            Matcher m;
            ArrayList<Integer> hits=new ArrayList();
            for(int i=0;i<listModel.getSize();i++){
                String label=(String)listModel.elementAt(i);
                m=p.matcher(label);
                if(m.find()){
                    hits.add(i);
                }
            }
            if(hits.size()==0) return;
            int[] sels=new int[hits.size()];
            for(int i=0;i<hits.size();i++){
                sels[i]=hits.get(i);
            }
            seqList.setSelectedIndices(sels);
            seqList.ensureIndexIsVisible(sels[0]);
        }
        
        private void updateQueryModle(){
//            combModel.removeAllElements();
            Object s=combModel.getSelectedItem();
            for(int i=0;i<combModel.getSize();i++){
                if(combModel.getElementAt(i).equals(s))
                    continue;
                if(combModel.getElementAt(i).equals(" "))
                    continue;
                combModel.removeElementAt(i);
                i--;
            }
            
            for(int i=0;i<listModel.size();i++)
                combModel.addElement(listModel.get(i));

        }

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {}

        @Override
        public void dragOver(DropTargetDragEvent dtde) {}

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {}

        @Override
        public void dragExit(DropTargetEvent dte) {}

        @Override
        public void drop(DropTargetDropEvent dtde) {
            
            Object source=dtde.getSource();
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable tr=dtde.getTransferable();
            DataFlavor[] flavors=tr.getTransferDataFlavors();
            for(int i=0;i<flavors.length;i++){
                DataFlavor d=flavors[i];
                if(d.equals(DataFlavor.stringFlavor)){
                    
                    try {
                        String seq = (String) tr.getTransferData(d);
//                        System.out.println(seq);
                        if(source==queryTarget){
                            DefaultComboBoxModel model=(DefaultComboBoxModel)queryField.getModel();
                            for(int j=1;j<model.getSize();j++){
                                if(model.getElementAt(j).equals(seq)){
                                    queryCheckBox.setSelected(true);
                                    queryField.setSelectedIndex(j);
                                    break;
                                }
                            }
                        }
                        else if(source==treeTarget){
                            
                            moveToTree();
                        }
                        else if(source==seqListTarget){
                            moveToList();
                        }
                    } catch (UnsupportedFlavorException ex) {
                        Logger.getLogger(HandDetectionDialog.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(HandDetectionDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }
            
        }

        @Override
        public void keyTyped(KeyEvent ke) {}

        @Override
        public void keyPressed(KeyEvent ke) {}

        @Override
        public void keyReleased(KeyEvent ke) {
            searchSeq();
        }

       

    }
    
    private void createJob(){
        final Pipeline flow=((MainFrame)parentFrame).getPipeline();
        flow.setWindow(20);
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
        
        
        recThread=new Thread(){
            @Override
            public void run(){
                dispose();
                try {
                    resetThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AutoRun.class.getName()).log(Level.SEVERE, null, ex);
                }
                infoLabel.setText("Scan recombinants...");
                if(querySeq.equals(" ")){
                    flow.scanRecombinant();
                }
                else{
                    int id=flow.getAln().getSeqByName(querySeq).getId();
                    flow.scanRecombinant(id);
                }
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

    }
    
    public void startJob(){
        mfeThread.start();
        checkMfeThread.start();
        resetThread.start();
        checkRecThread.start();
        recThread.start();
    }

}

