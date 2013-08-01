/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import grecscan2.core.Sequence;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

/**
 *
 * @author wb
 */
class SeqPanel extends JPanel implements MouseListener,
        MouseMotionListener, MouseWheelListener{

    public SeqCanvas seqCanvas;

    public AlignmentPanel ap;

    protected int lastres;

    protected int startseq;

    protected AlnViewport av;
    
    SeqPanel(AlnViewport av, AlignmentPanel ap) {
        this.av = av;
        setBackground(Color.white);

        seqCanvas = new SeqCanvas(ap);
        setLayout(new BorderLayout());
        add(seqCanvas, BorderLayout.CENTER);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        this.ap = ap;
    }
    
    public int findRes(MouseEvent evt){
        int res = 0;
        int x = evt.getX();
        if (x>seqCanvas.getWidth()+seqCanvas.getWidth()){
            // make sure we calculate relative to visible alignment, rather than right-hand gutter
            x = seqCanvas.getX()+seqCanvas.getWidth();
        }
        res = (x / av.getCharWidth()) + av.getStartRes();

        return res;

    }

    public int findSeq(MouseEvent evt){
        int seq = 0;
        int y = evt.getY();

        seq = Math.min((y / av.getCharHeight()) + av.getStartSeq(),
              av.getAlignment().getHeight() - 1);
        seq=y/av.getCharHeight()+av.getStartSeq();
        if(seq>=av.getAlignment().getHeight())
            return -1;
        return seq;
    }
    
    
    
    
    
    @Override
    public void mouseClicked(MouseEvent me) {
        System.out.println("Mouse clicked");
        int seqIndex=findSeq(me);
        if(seqIndex==-1) return;
        Sequence seq=av.getAlignment().getSeqByIndex(findSeq(me));
        int resIndex=findRes(me);
        System.out.println(seq.getName()+","+av.getAlignment().getResAt(seqIndex, resIndex));
    }

    @Override
    public void mousePressed(MouseEvent me) {
        
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

    @Override
    public void mouseDragged(MouseEvent me) {
        
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        int seqIndex=findSeq(me);
        if(seqIndex==-1) {
            this.setToolTipText(null);
            return;
        }
        Sequence seq=av.getAlignment().getSeqByIndex(findSeq(me));
        int resIndex=findRes(me);
        int pos=av.getAlignment().getSitePosAt(seqIndex, resIndex);
        if(pos==-1){
            this.setToolTipText(null);
            return;
        }
        this.setToolTipText("<html><div bgcolor=\"#eeff99\">"+(seqIndex+1)+": "+seq.getName()+" "+(resIndex+1)+"("+ pos +")</div></html>");
        
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        mwe.consume();
    
        if (mwe.getWheelRotation() > 0){
            ap.scrollUp(false);
        }
        else{
            ap.scrollUp(true);
        }
    }
    
}
