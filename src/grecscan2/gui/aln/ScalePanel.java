/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 *
 * @author wb
 */
class ScalePanel extends JPanel implements MouseMotionListener, MouseListener{
    private final AlnViewport av;
    private final AlignmentPanel ap;

    ScalePanel(AlnViewport av, AlignmentPanel ap) {
        this.av = av;
        this.ap = ap;
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void paintComponent(Graphics g){
        drawScale(g, av.getStartRes(), av.getEndRes(), getWidth(), getHeight());
    }

    // scalewidth will normally be screenwidth,
    public void drawScale(Graphics g, int startx, int endx, int width, int height){
        Graphics2D gg = (Graphics2D) g;
        gg.setFont(av.getFont());

        if (av.antiAlias){
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Fill in the background
        gg.setColor(new Color(238,238,238));
        gg.fillRect(0, 0, width, height);
        gg.setColor(Color.black);
        
    
        // Draw the scale numbers
        gg.setColor(Color.black);
        
        

        FontMetrics fm = gg.getFontMetrics(av.getFont());
        int y = av.charHeight - fm.getDescent();

        String label;
        for(int i=startx;i<=endx;i++){
            int v=i+1;
            
            if((v%10)==0){
                label=String.valueOf(v);
                
                gg.drawString(label, (i-startx+0.5f-label.length()*1f/2)*av.charWidth,y);
                gg.drawLine((int)((i-startx+0.5)*av.charWidth), y, (int)((i-startx+0.5)*av.charWidth), y+fm.getDescent()*2);
            }
            else{
                gg.drawLine((int)((i-startx+0.5)*av.charWidth), y+fm.getDescent(), (int)((i-startx+0.5)*av.charWidth), y+fm.getDescent()*2);
            }
        }
        gg.drawLine(0, y+fm.getDescent()*2, (endx-startx+1)*av.charWidth, y+fm.getDescent()*2);
        
        

    }

    @Override
    public void mouseDragged(MouseEvent me) {
        
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        
    }

    @Override
    public void mousePressed(MouseEvent me) {
        int x = (me.getX() / av.getCharWidth()) + av.getStartRes();
        final int res;

        if (x >= av.getAlignment().getWidth()){
            return;
        }

        res = x;

        System.out.println(x);
        
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }
    
}
