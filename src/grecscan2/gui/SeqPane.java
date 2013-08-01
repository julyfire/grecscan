/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 *
 * @author wb
 */
public class SeqPane extends JPanel{
    
    private JPanel idPanelHolder;
    private JPanel seqPanelHolder;
    private JPanel scalePanelHolder;
    private JPanel annotationPanel;
    private JPanel hscrollHolder;
    private JScrollBar vscroll;
    private JScrollBar hscroll;
    
    public SeqPane(){
        this.setBackground(Color.white);
        this.setPreferredSize(new Dimension(800,400));
        init();
    }
    
    private void init(){
        
    }
}
