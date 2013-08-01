/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 *
 * @author wb
 */
public class SBDemo extends JPanel implements AdjustmentListener, MouseMotionListener {
    String msg = "";
    Scrollbar vertSB, horzSB;
    public SBDemo(){
        init();
    }
    public void init() {
        int width = 800;
        int height = 500;
        this.setPreferredSize(new Dimension(width,height));
        this.setLayout(new BorderLayout());
        vertSB = new Scrollbar(Scrollbar.VERTICAL,
                100, 100, 0, height);
        horzSB = new Scrollbar(Scrollbar.HORIZONTAL,
                100, 100, 0, width);
        add(vertSB, BorderLayout.EAST);
        add(horzSB, BorderLayout.SOUTH);
        // register to receive adjustment events
        vertSB.addAdjustmentListener(this);
        horzSB.addAdjustmentListener(this);
        addMouseMotionListener(this);
    }
    @Override
    public void adjustmentValueChanged(AdjustmentEvent ae) {
        repaint();
    }
    // Update scroll bars to reflect mouse dragging.
    @Override
    public void mouseDragged(MouseEvent me) {
        int x = me.getX();
        int y = me.getY();
        vertSB.setValue(y);
        horzSB.setValue(x);
        repaint();
    }
    // Necessary for MouseMotionListener
    @Override
    public void mouseMoved(MouseEvent me) {
//        int x = me.getX();
//        int y = me.getY();
//        vertSB.setValue(y);
//        horzSB.setValue(x);
//        repaint();
    }
    // Display current value of scroll bars.
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        msg = "Vertical: " + vertSB.getValue();
        msg += ", Horizontal: " + horzSB.getValue();
        g.drawString(msg, 6, 160);
        // show current mouse drag position
        g.drawString("*", horzSB.getValue(),
        vertSB.getValue());
    }
}
