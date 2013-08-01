/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.Alignment;
import grecscan2.core.Pipeline;
import grecscan2.core.Recombinant;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import grecscan2.gui.aln.AlignmentPanel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author wb
 */
public class RecResultPanel extends JPanel implements ActionListener{
    private JPanel workPanel;
    private JPanel topPanel;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JComboBox reclistBox;
    private JButton seqButton;
    private JButton treeButton;
    private JPanel recInfo;
    private Recombinant rec;
    private TreeBuildDialog tb;
    private List<Recombinant> results;
    private JFrame parentFrame;
    
    public RecResultPanel(List<Recombinant> results, JFrame parentFrame){
        this.parentFrame=parentFrame;
        this.results=results;
        rec=results.get(0);
        init();
    }
    
    private void init(){
        topPanel=new JPanel();
        topPanel.setLayout(new BorderLayout());
        fillTopPanel();
        leftPanel=new JPanel();
        leftPanel.setLayout(new BorderLayout());
        fillLeftPanel();
        centerPanel=new JPanel();
        centerPanel.setLayout(new BorderLayout());
        fillCenterPanel();
        
        workPanel=new JPanel();
        workPanel.setLayout(new BorderLayout());
        workPanel.add(topPanel, BorderLayout.NORTH);
        workPanel.add(leftPanel, BorderLayout.WEST);
        workPanel.add(centerPanel, BorderLayout.CENTER);
        workPanel.setPreferredSize(new Dimension(800,400));
        workPanel.setBorder(BorderFactory.createEtchedBorder());
        
        this.setLayout(new BorderLayout());
        setPadding(10);
        this.add(workPanel);

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

    private void fillTopPanel() {
        Box box=Box.createHorizontalBox();
//        box.setBorder(BorderFactory.createEtchedBorder());
        reclistBox=new JComboBox((Recombinant[])results.toArray(new Recombinant[0]));
//        reclistBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//        reclistBox.setUI(new MetalComboBoxUI() {
//            @Override
//            public void configureArrowButton() {
//                super.configureArrowButton();
//                if (arrowButton != null) {
//                    arrowButton.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
//                }
//            }
//            @Override
//            protected JButton createArrowButton() {
//                JButton arrow = new JButton();
//                arrow.setText("Choose a recombinant ");
//                return arrow;
//            }
//        });
//        ((JLabel)reclistBox.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
        
        reclistBox.addItemListener(new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent ie) {
                rec=(Recombinant)reclistBox.getSelectedItem();
                updateResult();
            }
        });
        seqButton=new JButton("view alignment");
        treeButton=new JButton("view tree");

        box.add(reclistBox);
        box.add(Box.createHorizontalGlue());
        box.add(seqButton);
        box.add(treeButton);
        
        seqButton.addActionListener(this);
        treeButton.addActionListener(this);
        
        topPanel.add(box);
    }
    
    private void updateResult(){
        leftPanel.removeAll();
        fillLeftPanel();
        leftPanel.validate();
        centerPanel.removeAll();
        fillCenterPanel();
        centerPanel.validate();
    }

    private void fillLeftPanel() {
        recInfo=new RecInfoPane();        
        recInfo.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(320,360));
        leftPanel.add(new JScrollPane(recInfo));
    }
    
    

    private void fillCenterPanel() {
        JPanel plot=new RecPlot(rec).getPlotPanel();
        centerPanel.add(plot);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource()==seqButton){
            new Thread(){
                public void run(){
                    MainFrame mf=(MainFrame)parentFrame;
                    AlignmentPanel ap=mf.getAlignmentPanel();
                    Pipeline pl=mf.getPipeline();
                    Alignment aln=pl.getAln();
                    if(pl.getGroups()==null && tb==null){
                        int g=JOptionPane.showConfirmDialog(null, "No grouping information available.\nDo you want to group the sequences firstly?", "group or not", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if(g==0){    
                            tb=new TreeBuildDialog(parentFrame);  
                        }
                    }
                    mf.getPipeline().makeRecAlignment(rec);
                    aln.setIndex(rec.getAlnIndex());
                    mf.getAlnViewport().setColorScheme(new RecColorScheme(rec));
                    ap.repaint();
                }                        
            }.start();
        }
        else if(ae.getSource()==treeButton){
            new Thread(){
                public void run(){
                    MainFrame pf=(MainFrame)parentFrame;
                    pf.showRecTree(rec);
                }
            }.start();
//            if(pf.getRecTreeFrame()!=null){
//                pf.getRecTreeFrame().dispose();               
//            }
//            pf.getPipeline().makeRecTree(rec);
//            JFrame rtw=new RecTreeWindow(rec, parentFrame);           
//            rtw.setLocation(parentFrame.getX()+parentFrame.getWidth(), parentFrame.getY());
//            pf.setRecTreeFrame(rtw);
//            pf.getRecTreeFrame().requestFocus();
        }
    }
    
    
    
    class RecInfoPane extends JPanel{
        
        int width=600;
        int height=600;
        int padding=15;
        BufferedImage img; 
        
        public RecInfoPane(){
            draw();
            this.setPreferredSize(new Dimension(width,height));
        }
        
        
        private void draw(){
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gg = (Graphics2D) img.getGraphics();
            
            //setBackground color
            gg.setColor(Color.WHITE);
            gg.fillRect(0, 0, width, height);
            
            Font font=new Font("Courier", Font.PLAIN, 12);
            gg.setFont(font);
            FontMetrics fm=gg.getFontMetrics();
            float lh=gg.getFontMetrics().getHeight();
            float x=0;
            float y=lh;          
            gg.setPaint(new Color(25,127,229));
            String str="recombinant:    "+rec.getRecSeq().getName()+"\n";
            width=fm.stringWidth(str);
            gg.drawString(str, x, y);
            y+=lh*1.5f;
            gg.setPaint(new Color(229,51,25));
            str="major sequence: "+rec.getMajorSeq().getName()+" ("+(Math.round((1-rec.getPdisToMajor())*1000)/10.0)+"%)\n";
            width=Math.max(fm.stringWidth(str), width);
            gg.drawString(str, x, y);
            y+=lh*1.5f;
            gg.setPaint(new Color(25,204,25));
            str="minor sequence: "+rec.getMinorSeq().getName()+" ("+(Math.round((1-rec.getPdisToMinor())*1000)/10.0)+"%)\n";
            width=Math.max(fm.stringWidth(str), width);
            gg.drawString(str, x, y);
            y+=lh*2.2f;
            gg.setPaint(Color.gray);
            str="recombination fragments:";
            width=Math.max(fm.stringWidth(str), width);
            gg.drawString(str, x, y);
            for(int i=0;i<rec.getBlocks().size();i++){
                 y+=lh*2f;
                 str=rec.getBlocks().get(i)[0]+"-"+rec.getBlocks().get(i)[1]+" ("+rec.getFullBlock().get(i)[0]+"-"+rec.getFullBlock().get(i)[1]+" in alignment)\n";
                 width=Math.max(fm.stringWidth(str), width);
                 gg.drawString(str,x,y);
                 y+=lh*1.5f;
                 str="P-value of comparing with major: "+rec.getPv().get(i)[0]+"\n";
                 width=Math.max(fm.stringWidth(str), width);
                 gg.drawString(str,x,y);
                 y+=lh*1.5f;
                 str="P-value of comparing with minor: "+rec.getPv().get(i)[1]+"\n";
                 width=Math.max(fm.stringWidth(str), width);
                 gg.drawString(str,x,y);
            }
            
            width+=2*padding;
            height=(int) (y)+2*padding;
            
        }
        
        
        @Override
         public void paintComponent(Graphics g) {
             super.paintComponent(g);
             Graphics2D g2 = (Graphics2D) g;
             g2.setBackground(Color.white);
             g2.drawImage(img, padding, padding, this);
         }    
        
    }
}
