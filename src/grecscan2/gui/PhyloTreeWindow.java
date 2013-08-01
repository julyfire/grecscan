/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.Pipeline;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import wbitoolkit.phylo.RectangularTreeAnnotation;
import wbitoolkit.phylo.Tree;
import wbitoolkit.phylo.TreeAnnotation.AnnotationType;
import wbitoolkit.phylo.TreeGUI;
import wbitoolkit.phylo.TreeNode;

/**
 *
 * @author wb
 */
public class PhyloTreeWindow extends JFrame{
    private JFrame parentFrame;
    private TreeGUI tg;
    private Tree tree;
    private List<TreeNode> clusters;
    private JSlider slider;
    private JTextField cutoffField;
    
    public PhyloTreeWindow(JFrame owner,Tree tree){
        super("Detection Dialog");
        parentFrame=owner;
        this.tree=tree;
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        tg=new TreeGUI(tree);
        this.add(tg.getPanel());
        this.add(controlPanel(),BorderLayout.SOUTH);
        this.setSize(600, 600);
        this.setLocation(parentFrame.getX() + parentFrame.getWidth()/2 - this.getWidth()/2, parentFrame.getY() +parentFrame.getHeight()/2 - this.getHeight()/2);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        this.setVisible(true);
    }
    
    private JPanel controlPanel(){
        JPanel btPanel=new JPanel();
        btPanel.setLayout(new BoxLayout(btPanel,BoxLayout.X_AXIS));
        btPanel.add(new JLabel("Grouping distance cutoff"));
        slider=new JSlider();
        slider.setPaintLabels(false);
        slider.setPaintTicks(false);
        slider.setValue(25);
        slider.addChangeListener(cutoffChangeListener());
        btPanel.add(slider);
        cutoffField=new JTextField("25");
        cutoffField.setMaximumSize(new Dimension(50,30));
        cutoffField.setPreferredSize(new Dimension(50,30));
        cutoffField.setMinimumSize(new Dimension(50,30));
        cutoffField.addActionListener(cutoffInputListener());
        btPanel.add(cutoffField);
        return btPanel;
    }
    
    private ChangeListener cutoffChangeListener(){
        return new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent ce) {
                int value=slider.getValue();
                cutoffField.setText(value+"");
                Pipeline pl=((MainFrame)parentFrame).getPipeline();
                pl.setGroupCutoff(value/100d);
                pl.partitionTree();
                tg.clearAnnotation();
                clusters=pl.getClusters();
                addGroupMark();
                repaint();
            }
            
        };
    }
    
    private ActionListener cutoffInputListener(){
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                int value=Integer.parseInt(cutoffField.getText());
                slider.setValue(value);
                Pipeline pl=((MainFrame)parentFrame).getPipeline();
                pl.setGroupCutoff(value/100d);
                pl.partitionTree();
                tg.clearAnnotation();
                clusters=pl.getClusters();
                addGroupMark();
                repaint();
            }
            
        };
    }
    
    public void addGroupMark(){
        Color blue=new Color(25,127,229);
        Color red=new Color(229,51,25);
        Color green=new Color(25,204,25);
        int i=0;
        for(TreeNode c:clusters){
            if(i%2==0)
            tg.addAnnotation(new RectangularTreeAnnotation(tg.getTree2D(), c, blue, AnnotationType.CLUSTER));
            else
                tg.addAnnotation(new RectangularTreeAnnotation(tg.getTree2D(), c, red, AnnotationType.CLUSTER));
            i++;
        }
        
    }

    /**
     * @return the clusters
     */
    public List<TreeNode> getClusters() {
        return clusters;
    }

    /**
     * @param clusters the clusters to set
     */
    public void setClusters(List<TreeNode> clusters) {
        this.clusters = clusters;
    }
}
