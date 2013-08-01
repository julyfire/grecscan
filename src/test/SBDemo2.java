/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 *
 * @author wb
 */
public class SBDemo2 extends JPanel implements AdjustmentListener {

    private JPanel seqPanelHolder=new JPanel();
    private JScrollBar hscroll=new JScrollBar();
    private JScrollBar vscroll=new JScrollBar();
    
    private int width = 10000;
    private int height = 1000;
    private int uw=5;
    private int uh=5;
    private int startx;
    private int endx;
    private int starty;
    private int endy;
    
    int hextent = 0;
    int vextent = 0;
    
    public SBDemo2(){
       
        seqPanelHolder.setLayout(new BorderLayout());
        
        seqPanelHolder.setPreferredSize(new Dimension(800,400));

        this.setLayout(new BorderLayout());
        this.add(seqPanelHolder);
        this.add(vscroll, BorderLayout.EAST);
        this.add(hscroll,BorderLayout.SOUTH);
        hscroll.setOrientation(JScrollBar.HORIZONTAL);
        
        setScrollValues(0, 0);
        
        hscroll.addAdjustmentListener(this);
        vscroll.addAdjustmentListener(this);
    }

     private void setScrollValues(int x, int y) {
        
        int vWidth=seqPanelHolder.getWidth();
        int vHeight=seqPanelHolder.getHeight();

        endx=x+vWidth/uw;
        endy=y+vHeight/uh;
        
        hextent = vWidth / uw;
        vextent = vHeight / uh;

        if (hextent > width){
            hextent = width;
        }

        if (vextent > height){
            vextent = height;
        }

        if ((hextent + x) > width){
            x = width - hextent;
        }

        if ((vextent + y) > height){
            y = height - vextent;
        }

        if (y < 0){
            y = 0;
        }

        if (x < 0){
            x = 0;
        }

        hscroll.setValues(x, hextent, 0, width);
        vscroll.setValues(y, vextent, 0, height);
    }
    
    @Override
    public void adjustmentValueChanged(AdjustmentEvent evt) {
        int oldX = startx;
        int oldY = starty;
        
        int vWidth=seqPanelHolder.getWidth();
        int vHeight=seqPanelHolder.getHeight();

        if (evt.getSource() == hscroll){
            int x = hscroll.getValue();
            startx=x;
            endx=x+vWidth/uw;
        }

        if (evt.getSource() == vscroll){
            int offy = vscroll.getValue();
            starty=offy;
            endy=offy+vHeight/uh;     
        }


        int scrollX = startx - oldX;
        int scrollY = starty - oldY;

    
        // Make sure we're not trying to draw a panel
        // larger than the visible window
        if (scrollX > endx - startx){
            scrollX = endx - startx;
        }
        else if (scrollX < startx - endx){
            scrollX = startx - endx;
        }

        if (scrollX != 0 || scrollY != 0){
//            idPanel.idCanvas.fastPaint(scrollY);
//            seqPanel.seqCanvas.fastPaint(scrollX, scrollY);
//            scalePanel.repaint();
            repaint();

        }
    }
    
    public void paintComponent(Graphics g){ 
        setScrollValues(startx, starty);
        System.out.println(startx+","+starty);
    }
    
}
