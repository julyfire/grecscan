/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.Recombinant;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import wbitoolkit.phylo.RectangularTreeAnnotation;
import wbitoolkit.phylo.Tree;
import wbitoolkit.phylo.TreeAnnotation.AnnotationType;
import wbitoolkit.phylo.TreeGUI;
import wbitoolkit.phylo.TreeIO;
import wbitoolkit.phylo.TreeNode;

/**
 *
 * @author wb
 */
public class RecTreePanel extends JPanel{
    private JPanel leftTreePanel;
    private JPanel rightTreePanel;
    private JLabel leftTreeLabel;
    private JLabel rightTreeLabel;
    private JSplitPane treePanel;
    private Recombinant rec;
    private Tree t1;
    private Tree t2;
    private String leftString;
    private String rightString;
    private TreeGUI tg1;
    private TreeGUI tg2;
    
    public RecTreePanel(Recombinant rec){
        this.rec=rec;
        init();
    }
    
    private void init(){    
        makeTreePanel();
        this.setLayout(new BorderLayout());
        this.add(treePanel);
        this.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                treePanel.setDividerLocation(0.5);
            }
        });
    }
    
    private void makeTreePanel(){
        buildRecTree();
        setTreeString();
        addMark();
        
        leftTreePanel=new JPanel();
        leftTreePanel.setLayout(new BorderLayout());
        leftTreeLabel=new JLabel(leftString);
        leftTreePanel.add(leftTreeLabel, BorderLayout.NORTH);
        leftTreePanel.add(tg1.getPanel());
        
        rightTreePanel=new JPanel();
        rightTreePanel.setLayout(new BorderLayout());
        rightTreeLabel=new JLabel(rightString);
        rightTreePanel.add(rightTreeLabel, BorderLayout.NORTH);
        rightTreePanel.add(tg2.getPanel());        
        
        treePanel=new JSplitPane();
        treePanel.setPreferredSize(new Dimension(800,600));
        treePanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        treePanel.setLeftComponent(new JScrollPane(leftTreePanel));
        treePanel.setRightComponent(new JScrollPane(rightTreePanel));
        treePanel.setDividerSize(5);
        

    }
    
    public void fairTree(){
        treePanel.setDividerLocation(0.5);
        this.validate();
    }
    
    private void buildRecTree(){
        TreeIO tio=new TreeIO();
        t1=tio.readTree(rec.getMajorTree());
        t2=tio.readTree(rec.getMinorTree());
        tg1=new TreeGUI(t1);
        tg2=new TreeGUI(t2);
    }
    
    private void setTreeString(){
        ArrayList<int[]> blocks=rec.getBlocks();
        String b1=blocks.get(0)[0]+"-"+blocks.get(0)[1];
        String str1="NJ tree of alignment ";

        for(int i=1;i<blocks.size();i++){
            b1+=", "+blocks.get(i)[0]+"-"+blocks.get(i)[1];
        }
        
        leftString=str1+"reomved range "+b1;
        rightString=str1+"in the range "+b1;
    }
    
    private void addMark(){
        Color blue=new Color(25,127,229);
        Color red=new Color(229,51,25);
        Color green=new Color(25,204,25);
        
        TreeNode recNode= tg1.getTree().getLeafByName(rec.getRecSeq().getName());
        TreeNode majorNode=tg1.getTree().getLeafByName(rec.getMajorSeq().getName());
        TreeNode minorNode=tg1.getTree().getLeafByName(rec.getMinorSeq().getName());
        tg1.addAnnotation(new RectangularTreeAnnotation(tg1.getTree2D(), recNode, blue, AnnotationType.BRANCH));
        tg1.addAnnotation(new RectangularTreeAnnotation(tg1.getTree2D(), recNode, blue, AnnotationType.LABEL));
        tg1.addAnnotation(new RectangularTreeAnnotation(tg1.getTree2D(), majorNode, red, AnnotationType.BRANCH));
        tg1.addAnnotation(new RectangularTreeAnnotation(tg1.getTree2D(), majorNode, red, AnnotationType.LABEL));
        tg1.addAnnotation(new RectangularTreeAnnotation(tg1.getTree2D(), minorNode, green, AnnotationType.BRANCH));
        tg1.addAnnotation(new RectangularTreeAnnotation(tg1.getTree2D(), minorNode, green, AnnotationType.LABEL));
        
        TreeNode recNode2= tg2.getTree().getLeafByName(rec.getRecSeq().getName());
        TreeNode majorNode2=tg2.getTree().getLeafByName(rec.getMajorSeq().getName());
        TreeNode minorNode2=tg2.getTree().getLeafByName(rec.getMinorSeq().getName());
        tg2.addAnnotation(new RectangularTreeAnnotation(tg2.getTree2D(), recNode2, blue, AnnotationType.BRANCH));
        tg2.addAnnotation(new RectangularTreeAnnotation(tg2.getTree2D(), recNode2, blue, AnnotationType.LABEL));
        tg2.addAnnotation(new RectangularTreeAnnotation(tg2.getTree2D(), majorNode2, red, AnnotationType.BRANCH));
        tg2.addAnnotation(new RectangularTreeAnnotation(tg2.getTree2D(), majorNode2, red, AnnotationType.LABEL));
        tg2.addAnnotation(new RectangularTreeAnnotation(tg2.getTree2D(), minorNode2, green, AnnotationType.BRANCH));
        tg2.addAnnotation(new RectangularTreeAnnotation(tg2.getTree2D(), minorNode2, green, AnnotationType.LABEL));
    }
}
