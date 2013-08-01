/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import grecscan2.core.Sequence;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author wb
 */
public class SeqCanvas  extends JComponent{
    private final AlnViewport av;
    
    final SequenceRenderer sr;
    
    BufferedImage img;

    Graphics2D gg;

    int imgWidth;

    int imgHeight;
    
    boolean fastpainting = false;
    boolean fastPaint = false;


    SeqCanvas(AlignmentPanel ap) {
        this.av = ap.av;
        sr = new SequenceRenderer(av);
        setLayout(new BorderLayout());
        this.setSize(new Dimension(530,200));
        setBackground(Color.white);
    }
    
    /**
    * Definitions of startx and endx (hopefully): SMJS This is what I'm working
    * towards! startx is the first residue (starting at 0) to display. endx is
    * the last residue to display (starting at 0). starty is the first sequence
    * to display (starting at 0). endy is the last sequence to display (starting
    * at 0). NOTE 1: The av limits are set in setFont in this class and in the
    * adjustment listener in SeqPanel when the scrollbars move.
    */

    // Set this to false to force a full panel paint
    @Override
    public void paintComponent(Graphics g){
        BufferedImage lcimg = img; // take reference since other threads may null
        // img and call later.
        super.paintComponent(g);

        // this draws the whole of the alignment
        imgWidth = getWidth();
        imgHeight = getHeight();
        
//        System.out.println(imgWidth+","+imgHeight);

        imgWidth -= (imgWidth % av.charWidth);
        imgHeight -= (imgHeight % av.charHeight);

        if ((imgWidth < 1) || (imgHeight < 1)){
            return;
        }

        if (lcimg == null || imgWidth != lcimg.getWidth()
            || imgHeight != lcimg.getHeight()){
            try{
                lcimg = img = new BufferedImage(imgWidth, imgHeight,
                    BufferedImage.TYPE_INT_RGB);
                gg = (Graphics2D) img.getGraphics();
                gg.setFont(av.getFont());
            } catch (OutOfMemoryError er){
                System.gc();
                System.err.println("SeqCanvas OutOfMemory Redraw Error.\n" + er);
//        new OOMWarning("Creating alignment image for display", er);

                return;
            }
        }

        if (av.antiAlias){
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        gg.setColor(Color.white);
        gg.fillRect(0, 0, imgWidth, imgHeight);

        int offset=1;
        drawPanel(gg, av.startRes, av.endRes, av.startSeq, av.endSeq, offset);
    
        g.drawImage(lcimg, 0, 0, this);

    }
    
    void drawPanel(Graphics g1, int startRes, int endRes, int startSeq, int endSeq, int offset){

        int screenY = 0;
        int blockStart = startRes;
        int blockEnd = endRes;

        

        if (screenY <= (endRes - startRes)){
            blockEnd = blockStart + (endRes - startRes) - screenY;
            g1.translate(screenY * av.charWidth, 0);
            draw(g1, blockStart, blockEnd, startSeq, endSeq, offset);

            g1.translate(-screenY * av.charWidth, 0);
        }
    

    }
    
    
    void draw(Graphics g, int startRes, int endRes, int startSeq, int endSeq, int offset){
        g.setFont(av.getFont());
        sr.prepare(g, av.renderGaps);

        Sequence nextSeq;

        // / First draw the sequences
        // ///////////////////////////
        for (int i = startSeq; i <=endSeq; i++){
            nextSeq = av.getAlignment().getSeqByIndex(i);
            int sy=offset + ((i - startSeq) * av.charHeight);
            sr.drawSequence(nextSeq, startRes, endRes, sy);   
        }

        if (av.getSelectionGroup() != null){
            drawGroupsBoundaries(g, startRes, endRes, startSeq, endSeq, offset);
        }
    }

    void drawGroupsBoundaries(Graphics g1, int startRes, int endRes, int startSeq, int endSeq, int offset){
        Graphics2D g = (Graphics2D) g1;
        //
        // ///////////////////////////////////
        // Now outline any areas if necessary
        // ///////////////////////////////////
        SequenceGroup group = av.getSelectionGroup();

        int sx = -1;
        int sy = -1;
        int ex = -1;
        int groupIndex = -1;
        int visWidth = (endRes - startRes + 1) * av.charWidth;

        if (group != null){
//            do{
                int oldY = -1;
                int i = 0;
                boolean inGroup = false;
                int top = -1; //top border
                int bottom = -1; //bottom border

                for (i = startSeq; i <= endSeq; i++){
                    sx = (group.getStartRes() - startRes) * av.charWidth;
                    sy = offset + ((i - startSeq) * av.charHeight);
                    ex = (((group.getEndRes() + 1) - group.getStartRes()) * av.charWidth) - 1;

                    if (sx + ex < 0 || sx > visWidth){
                        continue;
                    }

                    if ((sx <= (endRes - startRes) * av.charWidth) 
                            && group.getSequences().contains(av.getAlignment().getSeqByIndex(i))){
                        
                        //if this seq is the last selected, then find the bottom border
                        if ((bottom == -1)
                                && !group.getSequences().contains(
                                        av.getAlignment().getSeqByIndex(i + 1))){
                            bottom = sy + av.charHeight;

                        }
                        
                        if(top != -1 && bottom != -1 && group == av.getSelectionGroup()){
                            Paint op=g.getPaint();
                            g.setPaint(new Color(0,0,0,50));
                            g.fillRect(0, top, visWidth, bottom-top);
                            g.setPaint(op);
                        }

                        if (!inGroup){
                            //if this seq is the first selected, then find the top border
                            if (((top == -1) && (i == 0))
                                    || !group.getSequences().contains(av.getAlignment().getSeqByIndex(i - 1))){
                                top = sy;

                            }

                            oldY = sy;
                            inGroup = true;

                            if (group == av.getSelectionGroup()){
//                                g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
//                                BasicStroke.JOIN_ROUND, 3f, new float[]{ 5f, 3f }, 0f));
                                g.setColor(Color.yellow);
                                
                                if(top != -1 && bottom != -1){
                                    Paint op=g.getPaint();
                                    g.setPaint(new Color(0,0,0,50));
                                    g.fillRect(0, top, visWidth, bottom-top);
                                    g.setPaint(op);
                                }
                                
                            }
                            else{
                                g.setStroke(new BasicStroke());
                                g.setColor(group.getOutlineColour());
                            }
                        }
                    }
                    else{
                        if (inGroup){
                            
                            
                            
                            //if the left border is visable, draw it
                            if (sx >= 0 && sx < visWidth){
                                g.drawLine(sx, oldY, sx, sy);
                            }
                            //if the right border is visable, draw it
                            if (sx + ex < visWidth){
                                g.drawLine(sx + ex, oldY, sx + ex, sy);
                            }

                            if (sx < 0){
                                ex += sx;
                                sx = 0;
                            }

                            if (sx + ex > visWidth){
                                ex = visWidth;
                            }

                            else if (sx + ex >= (endRes - startRes + 1) * av.charWidth){
                                ex = (endRes - startRes + 1) * av.charWidth;
                            }

                            if (top != -1){
                                g.drawLine(sx, top, sx + ex, top);
                                top = -1;
                            }

                            if (bottom != -1){
                                g.drawLine(sx, bottom, sx + ex, bottom);
                                bottom = -1;
                            }

                            inGroup = false;
                        }
                    }
                    
                }

                if (inGroup){
                    sy = offset + ((i - startSeq) * av.charHeight);
                    if (sx >= 0 && sx < visWidth){
                        g.drawLine(sx, oldY, sx, sy);
                    }

                    if (sx + ex < visWidth){
                        g.drawLine(sx + ex, oldY, sx + ex, sy);
                    }

                    if (sx < 0){
                        ex += sx;
                        sx = 0;
                    }

                    if (sx + ex > visWidth){
                        ex = visWidth;
                    }
                    else if (sx + ex >= (endRes - startRes + 1) * av.charWidth){
                        ex = (endRes - startRes + 1) * av.charWidth;
                    }

                    if (top != -1){
                        g.drawLine(sx, top, sx + ex, top);
                        top = -1;
                    }

                    if (bottom != -1){
                        g.drawLine(sx, bottom - 1, sx + ex, bottom - 1);
                        bottom = -1;
                    }

                    inGroup = false;
                }

                groupIndex++;

                g.setStroke(new BasicStroke());

//                if (groupIndex >= av.alignment.getGroups().size()){
//                    break;
//                }
//
//                group = (SequenceGroup) av.alignment.getGroups().elementAt(groupIndex);

//            } while (groupIndex < av.alignment.getGroups().size());

        }

    }
  
}
