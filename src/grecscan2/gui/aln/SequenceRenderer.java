/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import grecscan2.core.Sequence;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 *
 * @author wb
 */
class SequenceRenderer {
    AlnViewport av;

    FontMetrics fm;

    boolean renderGaps = true;

  

    Color resBoxColour;

    Graphics graphics;

    boolean monospacedFont;

    boolean forOverview = false;

    /**
    * Creates a new SequenceRenderer object.
    * 
    * @param av
    *          DOCUMENT ME!
    */
    public SequenceRenderer(AlnViewport av){
        this.av = av;
    }

    /**
    * DOCUMENT ME!
    * 
    * @param b
    *          DOCUMENT ME!
    */
    public void prepare(Graphics g, boolean renderGaps){
        graphics = g;
        fm = g.getFontMetrics();

        // If EPS graphics, stringWidth will be a double, not an int
        double dwidth = fm.getStringBounds("M", g).getWidth();

        monospacedFont = (dwidth == fm.getStringBounds("|", g).getWidth() && (float) av.charWidth == dwidth);

        this.renderGaps = renderGaps;
    }


    /**
    * DOCUMENT ME!
    * 
    * @param cs
    *          DOCUMENT ME!
    * @param seq
    *          DOCUMENT ME!
    * @param i
    *          DOCUMENT ME!
    */
    void getBoxColour(ColorSchemeI cs, Sequence seq, int i){
        if(cs!=null){
            resBoxColour=cs.colorOf(seq.getSeq()[i], seq, i);
        }
        else{
            resBoxColour = Color.white;
        }
        
    
    }

   
    public void drawSequence(Sequence seq, int start, int end, int y1){

        drawBoxes(seq, start, end, y1);

        if (av.validCharWidth){
            drawText(seq, start, end, y1);
        }
    }

    public synchronized void drawBoxes(Sequence seq, int start, int end, int y1){
        if (seq == null) return; // fix for racecondition
        
        int i = start;
        int length = seq.getLength();

        int curStart = -1;
        int curWidth = av.charWidth;

        Color tempColour = null;

        while (i <= end){
            resBoxColour = Color.white;

      
            getBoxColour(av.getColorScheme(), seq, i);

            if (resBoxColour != tempColour){
                if (tempColour != null){
                    graphics.fillRect(av.charWidth * (curStart - start), y1, curWidth, av.charHeight);
                }

                graphics.setColor(resBoxColour);

                curStart = i;
                curWidth = av.charWidth;
                tempColour = resBoxColour;
            }
            else{
                curWidth += av.charWidth;
            }

            i++;
        }

        graphics.fillRect(av.charWidth * (curStart - start), y1, curWidth, av.charHeight);

    }


    public void drawText(Sequence seq, int start, int end, int y1){
        y1 += av.charHeight - av.charHeight / 5; // height/5 replaces pady
        int charOffset = 0;
        char s;

        if (end + 1 >= seq.getLength()){
            end = seq.getLength() - 1;
        }
        graphics.setColor(av.textColour);

   
        if (av.renderGaps){
            char[] ss=seq.getSeq();
            StringBuilder sb=new StringBuilder();
            for(int i=start;i<end+1;i++){
                sb.append(ss[i]);
            }
            graphics.drawString(sb.toString(), 0, y1);
        }
     
    
    }

 
}
