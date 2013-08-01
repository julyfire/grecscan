/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import grecscan2.core.Sequence;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author wb
 */
class IdCanvas extends JPanel{
    private final AlnViewport av;
    private int imgHeight;
    BufferedImage image;
    Graphics2D gg;
    private Font idfont;
    private FontMetrics fm;


    public IdCanvas(AlnViewport av) {
        setLayout(new BorderLayout());
        this.av = av;
    }
    
    @Override
    public void paintComponent(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        int oldHeight = imgHeight;

        imgHeight = getHeight();
        imgHeight -= (imgHeight % av.charHeight);

        if (imgHeight < 1){
            return;
        }   

        if (oldHeight != imgHeight || image.getWidth(this) != getWidth()){
            image = new BufferedImage(getWidth(), imgHeight, BufferedImage.TYPE_INT_RGB);
        }

        gg = (Graphics2D) image.getGraphics();

        // Fill in the background
        gg.setColor(Color.white);
        gg.fillRect(0, 0, getWidth(), imgHeight);

        drawIds(av.startSeq, av.endSeq);

        g.drawImage(image, 5, 1, this);
    }

    private void drawIds(int starty, int endy) {
        
        idfont = av.getFont();

        gg.setFont(idfont);
        fm = gg.getFontMetrics();

        if (av.antiAlias){
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        Color currentColor = Color.white;
        Color currentTextColor = Color.black;

        // Now draw the id strings
        int xPos = 0;

        Sequence sequence;
        // Now draw the id strings
        for (int i = starty; i <= endy; i++){
            sequence = av.getAlignment().getSeqByIndex(i);

            if (sequence == null){
                continue;
            }
            
            if ((av.getSelectionGroup() != null)
                && av.getSelectionGroup().getSequences().contains(sequence)){
                currentColor = Color.lightGray;
                currentTextColor = Color.black;
            }
            else{
//                currentColor=Color.white;
                currentColor=av.getColorScheme().colorOf(sequence);
                currentTextColor=Color.black;
            }
//            else{
//                currentColor = av.getSequenceColour(sequence);
//                currentTextColor = Color.black;
//            }

            gg.setColor(currentColor);

            gg.fillRect(0, (i - starty) * av.charHeight, getWidth(),
                    av.charHeight);

            gg.setColor(currentTextColor);

            String string = sequence.getName();

            gg.drawString(string, xPos,
                    (((i - starty) * av.charHeight) + av.charHeight)
                        - (av.charHeight / 5));


        }
    
    }
}
