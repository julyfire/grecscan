/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.Pipeline;
import grecscan2.core.Recombinant;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import grecscan2.gui.aln.AlignmentPanel;
import grecscan2.gui.aln.AlnViewport;
import grecscan2.gui.aln.FileDrop;
import grecscan2.io.AbortParsingException;
import grecscan2.io.XMLRecIO;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.dom4j.DocumentException;
import org.jdom.JDOMException;
import wbitoolkit.tools.UsefulMethods;

/**
 *
 * @author wb
 */
public class MainFrame extends JFrame implements ActionListener{
    private String title="GRecScan";
    
    private Pipeline pl;
    File lastFile; // the file opened last
    
    private JMenu file=new JMenu("File");
    private JMenu help=new JMenu("Help");
    private JMenu analysis=new JMenu("Analysis");
    
    private JMenu openItem;
    private JMenuItem openFastaItem;
    private JMenuItem openTreeItem;
    private JMenuItem openGrsItem;
    private JMenuItem openXMLItem;
    private JMenuItem saveItem;
    private JMenuItem exitItem;
    private JMenuItem mfeItem;
    private JMenuItem groupItem;
    private JMenuItem clusterItem;
    private JMenuItem typeItem;
    private JMenu detectRecItem;
    private JMenuItem handItem;
    private JMenuItem autoItem;
    private JMenuItem helpItem;
    private JMenuItem aboutItem;
    
    private JFileChooser fileChooser;
    private ExtensionFileFilter filter;
    private String filename;
    
    private JPanel statusBar;
    private JSplitPane wrapPanel;
    private JSplitPane mainPanel;
    private JPanel sidePanel;
    private JPanel workPanel;
    
    private AlnViewport av;
    private AlignmentPanel ap;
    
    private JPanel recPlotFrame;
    private JFrame recTreeFrame;
    private JFrame treeFrame;
    private RecResultPanel rrw;
    private RecTreePanel rtw;
    
    
    public void init(){
        this.setTitle(title);
               
        workPanel=new JPanel();
        workPanel.setLayout(new BorderLayout());
        workPanel.setBackground(Color.white);
        workPanel.setPreferredSize(new Dimension(800,300));
        fillWorkPanel();
        this.add(workPanel);
        
        statusBar=new JPanel();
        statusBar.setPreferredSize(new Dimension(800,25));
        statusBar.setLayout(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        this.add(statusBar,BorderLayout.SOUTH);
        
        new FileDrop(this,getDropFileInListener());
        
        this.setJMenuBar(makeMenuBar());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.pack();
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);//in the center of screen
//        displayAtCenter(this);       
        this.setVisible(true);
        
//        addComponentListener(new ComponentAdapter(){
//            @Override
//	    public void componentMoved(ComponentEvent e){
//                if(recPlotFrame!=null){
//                    recPlotFrame.setLocation(getX(), getY()+getHeight());
//                    recPlotFrame.setSize(getWidth(), recPlotFrame.getHeight());
//                }
//                if(recTreeFrame!=null){
//                    recTreeFrame.setLocation(getX()+getWidth(),getY());
//                    recTreeFrame.setSize(recTreeFrame.getWidth(), getHeight()+recPlotFrame.getHeight());
//                }
//	    }
//
//	});
    }
    
    private void fillWorkPanel(){
        mainPanel=new JSplitPane();
        mainPanel.setPreferredSize(new Dimension(800,300));
        mainPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mainPanel.setContinuousLayout(false);
        mainPanel.setOneTouchExpandable(true);
        mainPanel.setTopComponent(new JPanel());
        mainPanel.setBottomComponent(new JPanel());
        mainPanel.setDividerSize(10);
        mainPanel.setResizeWeight(1);
        
        sidePanel=new JPanel();
        sidePanel.setLayout(new BorderLayout());
        
        wrapPanel=new JSplitPane();
        wrapPanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        wrapPanel.setContinuousLayout(false);
        wrapPanel.setOneTouchExpandable(true);
        wrapPanel.setLeftComponent(mainPanel);
        wrapPanel.setRightComponent(sidePanel);
        wrapPanel.setDividerSize(10);
        wrapPanel.setResizeWeight(1);
        
        workPanel.add(wrapPanel);
    }
    
    public void addPipeline(Pipeline pl) throws FileNotFoundException, IOException{
        File fileObj=pl.getInfile();
        filename=UsefulMethods.getBaseFileName(fileObj.getAbsolutePath());
        this.setTitle("GRecScan--"+fileObj.getPath());
        setPipeline(pl);
        this.pl.loadAlignment();           
        resetMainFrame();
    }
    
    
    
    private JMenuBar makeMenuBar(){
        JMenuBar menubar=new JMenuBar();
        openItem=new JMenu("Open");
        openFastaItem=new JMenuItem("FASTA file");
        openFastaItem.addActionListener(this);
        openTreeItem=new JMenuItem("NEWICK tree file");
        openTreeItem.addActionListener(this);
        openGrsItem=new JMenuItem("Project file");
        openGrsItem.addActionListener(this);
        openXMLItem=new JMenuItem("XML result file");
        openXMLItem.addActionListener(this);
        openItem.add(openFastaItem);
        openItem.add(openXMLItem);
//        openItem.add(openTreeItem);
//        openItem.add(openGrsItem);
        
        saveItem=new JMenuItem("Save");
        saveItem.setIcon(new ImageIcon(""));
        saveItem.addActionListener(this);
        
        exitItem=new JMenuItem("Exit");
        exitItem.setIcon(new ImageIcon(""));
        exitItem.addActionListener(this);
        
//        mfeItem=new JMenuItem("Calculate MFE distance matrix");
//        mfeItem.addActionListener(this);
        
        groupItem=new JMenuItem("Grouping");
        groupItem.addActionListener(this);
        
        detectRecItem=new JMenu("Recombination detection");
        handItem=new JMenuItem("Detection by hand");
        handItem.addActionListener(this);
        autoItem=new JMenuItem("Detection automated");
        autoItem.addActionListener(this);
        detectRecItem.add(handItem);
        detectRecItem.add(autoItem);
        
        
        aboutItem=new JMenuItem("About");
        aboutItem.setIcon(new ImageIcon(""));
        aboutItem.addActionListener(this);
        
        file.add(openItem);
        file.add(saveItem);
        file.add(exitItem);
//        analysis.add(mfeItem);
        analysis.add(groupItem);
        analysis.add(detectRecItem);
        analysis.setEnabled(false);
        help.add(aboutItem);
        
        menubar.add(file);
        menubar.add(analysis);
        menubar.add(help);
        
        return menubar;
    }
    
    private void resetWorkPanel(){        
        workPanel.removeAll();
        fillWorkPanel();
//        this.pack();
    }
    
    public void resetStatusBar(){
        statusBar.removeAll(); 
        statusBar.setPreferredSize(new Dimension(800,25));
        statusBar.setLayout(new BorderLayout());
        statusBar.setCursor(Cursor.getDefaultCursor());
//        this.pack();
    }

    private void resetMainFrame(){
        analysis.setEnabled(true);
//        mfeItem.setEnabled(true);
        setAlnViewport(new AlnViewport(getPipeline().getAln()));
        setAlignmentPanel(new AlignmentPanel(getAlnViewport()));
        ap.setPreferredSize(new Dimension(800,300));
        mainPanel.setTopComponent(ap);
        
        int seqNum=pl.getAln().getHeight();
        int seqLength=pl.getAln().getWidth();
        statusBar.add(new JLabel("Alignment loaded. number of sequences: "+seqNum+", length of Alignment: "+seqLength));
//        statusBar.validate();
        workPanel.setPreferredSize(new Dimension(800,300));
//        this.pack();
        this.validate();
    }
    
    public void showResults(){     
        rrw=new RecResultPanel(pl.getRecs(), this); 
        workPanel.setPreferredSize(new Dimension(800,600));
//        this.pack();
        mainPanel.setBottomComponent(rrw);        
        mainPanel.setDividerLocation(0.5);
        resetStatusBar();        
        this.validate();
    }
    
    public void showRecTree(Recombinant rec){
        
        workPanel.setPreferredSize(new Dimension(1000,600));
//        this.pack();
        pl.makeRecTree(rec);
        rtw=new RecTreePanel(rec);
        wrapPanel.setRightComponent(rtw);
        wrapPanel.setDividerLocation(0.6);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==openFastaItem){
            
            fileChooser=new JFileChooser();
            ExtensionFileFilter filter=new ExtensionFileFilter();
            filter.addExtension("fasta");
            filter.addExtension("fas");
            filter.setDescription("FASTA file(*.fasta,*.fas)");
            fileChooser.addChoosableFileFilter(filter);
//            fileChooser.setAcceptAllFileFilterUsed(true);
            
            if(pl!=null){
                lastFile=getPipeline().getInfile();
            }
            if(lastFile!=null){
                fileChooser.setCurrentDirectory(new File(lastFile.getAbsoluteFile().getParent()));
            }
            else
                fileChooser.setCurrentDirectory(new File("."));
            
            int result=fileChooser.showOpenDialog(this);
            if(result==JFileChooser.APPROVE_OPTION){
                File fileObj=fileChooser.getSelectedFile();
                filename=UsefulMethods.getBaseFileName(fileObj.getAbsolutePath());
                
                if(pl!=null){                    
                    int yo=JOptionPane.showConfirmDialog(this, "Do you want to open a new session?", "save or not", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if(yo==JOptionPane.YES_OPTION){
                        this.resetWorkPanel();
                        this.resetStatusBar();                       
                    }
                    else{
                        return;
                    }
                }
                
                if(treeFrame!=null)
                    treeFrame.dispose();
                pl=new Pipeline();
                treeFrame=null;
                pl.setInfile(fileObj);
                try {
                    pl.loadAlignment();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
                
                
                this.setTitle("GRecScan--"+fileObj.getPath());
                resetMainFrame();
                
                                            
            }
        }
        else if(e.getSource()==openXMLItem){
            fileChooser=new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", new String[] { "xml" });
            fileChooser.addChoosableFileFilter(filter);          
//            fileChooser.setAcceptAllFileFilterUsed(true);
            
            if(pl!=null){
                lastFile=getPipeline().getInfile();
            }
            if(lastFile!=null){
                fileChooser.setCurrentDirectory(new File(lastFile.getAbsoluteFile().getParent()));
            }
            else
                fileChooser.setCurrentDirectory(new File("."));
            
            int result=fileChooser.showOpenDialog(this);
            if(result==JFileChooser.APPROVE_OPTION){
                File fileObj=fileChooser.getSelectedFile();
                filename=UsefulMethods.getBaseFileName(fileObj.getAbsolutePath());
                
                if(pl!=null){                    
                    int yo=JOptionPane.showConfirmDialog(this, "Do you want to open a new session?", "save or not", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if(yo==JOptionPane.YES_OPTION){                      
                        this.resetWorkPanel();
                        this.resetStatusBar();
                    }
                    else{
                        return;
                    }
                }
                if(treeFrame!=null)
                    treeFrame.dispose();
                pl=new Pipeline();
                treeFrame=null;
                XMLRecIO rio=new XMLRecIO(pl);
                    
                try {
                    rio.openXML(fileObj);
                } catch (AbortParsingException ex) {
                }catch (DocumentException ex) {
                    if (ex.getNestedException() != null && ex.getNestedException() instanceof AbortParsingException){
                        JOptionPane.showMessageDialog(this, "Cannot find Alignment File\n"+pl.getInfile().getAbsolutePath()
                            + "\nor the file has broken.\nCheck the path in the XML file.", "Error", JOptionPane.ERROR_MESSAGE);
                        pl=null;
                        return;
                    }
                    else{
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
               
                this.setTitle("GRecScan--"+fileObj.getPath());
                resetMainFrame();
                if(pl.getRecs()!=null){
                    showResults();
                }
            }
                
        }
//        else if(e.getSource()==mfeItem){
//            
////            new MfeCalculationDialog(this);
////           
////            getStatusBar().add(new JLabel("MFE calculation are finished!"));
////            statusBar.validate();
//        }
        else if(e.getSource()==handItem){
            if(pl.getGroups()!=null || pl.getRecDetectionObject()!=null){
                int save = JOptionPane.showConfirmDialog(null, "Some results have existed, do you want to save it?", "save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if(save==0){
                    saveFile();
                }
            }
            if(rrw!=null)
                mainPanel.remove(rrw);
            if(rtw!=null)
                workPanel.remove(rtw);
            validate();
            pl.unsetSelectedSeqs();
            new HandDetectionDialog(this);
        }
        else if(e.getSource()==autoItem){ 
            if(pl.getGroups()!=null || pl.getRecDetectionObject()!=null){
                int save = JOptionPane.showConfirmDialog(null, "Some results have existed, do you want to save it?", "save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if(save==0){
                    saveFile();
                }
            }
            if(rrw!=null)
                mainPanel.remove(rrw);
            if(rtw!=null)
                workPanel.remove(rtw);
            validate();
            pl.selectAllSeqs();
            pl.updateSelectedSeqsIndex();
            new AutoDetectionDialog(this);          
        }
        else if(e.getSource()==saveItem){
            saveFile();
        }
        else if(e.getSource()==groupItem){
            new Thread(){
                @Override
                public void run(){
                    System.out.print("Build NJ tree...");
                    if(pl.getTree()!=null){
                        int g=JOptionPane.showConfirmDialog(null, "The Phylogenetic clusters data has existed, do you want to compute it again?", "yes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if(g!=0){
                            if(treeFrame==null){
                                treeFrame=new PhyloTreeWindow(MainFrame.this,pl.getTree());
                                pl.partitionTree();
                                ((PhyloTreeWindow)treeFrame).setClusters(pl.getClusters());
                                ((PhyloTreeWindow)treeFrame).addGroupMark();
                                ((PhyloTreeWindow)treeFrame).setVisible(true);
                                ((PhyloTreeWindow)treeFrame).repaint();
                            }
                            treeFrame.setVisible(true);
                            return;
                        }
                    }
                    pl.setTree(null);
                    TreeBuildDialog tb=new TreeBuildDialog(MainFrame.this);
                    System.out.println();
                }
            }.start();
            
        }
        else if(e.getSource()==aboutItem){
            JDialog ad=new JDialog(this,"About",false); 
            
            ad.setLayout(new BorderLayout());
            JTextArea content=new JTextArea();
            content.setEditable(false);
            content.setText(""
                    + "GRecScan v2.0\n"
                    + "A program for recombination detection\n\n"
                    + "please report bug to helloweibo@gmail.com");
            ad.add(content);
            ad.setSize(300,200);
            ad.setLocation(getX() + getWidth()/2 - ad.getWidth()/2, getY() +getHeight()/2 - ad.getHeight()/2);
            ad.setResizable(false);
            ad.setVisible(true);
        }
        
    }
    
    private void saveFile(){
        JFileChooser fileChooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", new String[] { "xml" });
        fileChooser.addChoosableFileFilter(filter);
        
        File file = new File(filename+".xml");
        fileChooser.setSelectedFile(file);
        
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            String fileName = file.toString();
            if (!fileName.endsWith("xml")) {
                fileName = fileName + ".xml";
                file = new File(fileName);
            }
            if (file.exists()) {
                int save = JOptionPane.showConfirmDialog(null, "The file has existed, do you want to overwrite it?", "save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (save == 0)
                    fileChooser.approveSelection();
                else 
                    return;              
            }
            
            XMLRecIO xio=new XMLRecIO(pl);
//                xio.saveXML(xio.xmlResults(),file);
            xio.saveXML(file);
        }
 
    }
    
    

    /**
     * @return the av
     */
    public AlnViewport getAlnViewport() {
        return av;
    }

    /**
     * @param av the av to set
     */
    public void setAlnViewport(AlnViewport av) {
        this.av = av;
    }

    /**
     * @return the ap
     */
    public AlignmentPanel getAlignmentPanel() {
        return ap;
    }

    /**
     * @param ap the ap to set
     */
    public void setAlignmentPanel(AlignmentPanel ap) {
        this.ap = ap;
    }

    /**
     * @return the pl
     */
    public Pipeline getPipeline() {
        return pl;
    }

    /**
     * @param pl the pl to set
     */
    public void setPipeline(Pipeline pl) {
        this.pl = pl;
    }

    /**
     * @return the recPlotFrame
     */
    public JPanel getRecPlotFrame() {
        return recPlotFrame;
    }

    /**
     * @param recPlotFrame the recPlotFrame to set
     */
    public void setRecPlotFrame(JPanel recPlotFrame) {
        this.recPlotFrame = recPlotFrame;
    }

    /**
     * @return the recTreeFrame
     */
    public JFrame getRecTreeFrame() {
        return recTreeFrame;
    }

    /**
     * @param recTreeFrame the recTreeFrame to set
     */
    public void setRecTreeFrame(JFrame recTreeFrame) {
        this.recTreeFrame = recTreeFrame;
    }

    /**
     * @return the statusBar
     */
    protected JPanel getStatusBar() {
        return statusBar;
    }

    /**
     * @param statusBar the statusBar to set
     */
    protected void setStatusBar(JPanel statusBar) {
        this.statusBar = statusBar;
    }

    /**
     * @return the treeFrame
     */
    public JFrame getTreeFrame() {
        return treeFrame;
    }

    /**
     * @param treeFrame the treeFrame to set
     */
    public void setTreeFrame(JFrame treeFrame) {
        this.treeFrame = treeFrame;
    }

    private FileDrop.Listener getDropFileInListener() {
        return new FileDrop.Listener(){

            @Override
            public void filesDropped(File[] files) {
                for(File file:files){
                    System.out.println(file.getAbsolutePath());
                }
            }            
        };
    }

    
    private class ExtensionFileFilter extends FileFilter{

        private String description;
        private ArrayList<String> extensions=new ArrayList<String>();
        
        
        
        public void addExtension(String extension){
            if(!extension.startsWith(".")){
                extension="."+extension;
                extensions.add(extension.toLowerCase());
            }
        }
        
        public String getExtension(){
            return extensions.get(0);
        }
        
        public void setDescription(String aDescription){
            description=aDescription;
        }
        
        @Override
        public String getDescription(){
            return description;
        }

        @Override
        public boolean accept(File f) {
            if(f.isDirectory()) return true;
            String name=f.getName().toLowerCase();
            for(String extension : extensions){
                if(name.endsWith(extension))
                    return true;   
            }
            return false;
        }
    }
       
}
