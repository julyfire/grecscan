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
import java.util.LinkedList;
import javax.swing.JPanel;

/**
 *
 * @author wb
 */
class IdPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener{
    private final AlnViewport av;
    private final AlignmentPanel alignPanel;
    private final IdCanvas idCanvas;
    private boolean mouseDragging;
    private int lastid=-1;

    IdPanel(AlnViewport av, AlignmentPanel parent) {
        this.setBackground(Color.white);
        this.av = av;
        alignPanel = parent;
        idCanvas = new IdCanvas(av);
        setLayout(new BorderLayout());
        add(idCanvas, BorderLayout.CENTER);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (me.getClickCount() == 2) return;

        int seq = alignPanel.seqPanel.findSeq(me);
        if(seq<0) return;
        
        //if choose one item, create a new group to put the item
        if ((av.getSelectionGroup() == null)
            || ((!me.isControlDown() && !me.isShiftDown()) && av
                    .getSelectionGroup() != null)){
            av.setSelectionGroup(new SequenceGroup());
            av.getSelectionGroup().setStartRes(0);
            av.getSelectionGroup().setEndRes(av.getAlignment().getWidth() - 1);
        }

        //if choose a block of items, add all selected items
        if (me.isShiftDown() && (lastid != -1)){
            selectSeqs(lastid, seq);
        }
        //add selected item
        else{
            selectSeq(seq);
        }
        
        alignPanel.repaint();
        
        

//        System.out.println(av.selectionGroup.getGroupName());
//        for(Object s:av.selectionGroup.getSequences()){
//            System.out.println(((Seq)s).getName());
//        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        mouseDragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        mouseDragging = true;

        int seq = alignPanel.seqPanel.findSeq(me);
        if(seq<0) return;
        av.getSelectionGroup().deleteAll();

        selectSeqs(lastid, seq);

        alignPanel.repaint();
        
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        SeqPanel sp = alignPanel.seqPanel;
        int seq = sp.findSeq(me);
        if(seq==-1) {
            this.setToolTipText(null);
            return;
        }
        Sequence sequence = av.getAlignment().getSeqByIndex(seq);
        this.setToolTipText((seq+1)+": "+sequence.getName()+" "+sequence.getDescription());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        mwe.consume();
    
        if (mwe.getWheelRotation() > 0){
            alignPanel.scrollUp(false);
        }
        else{
            alignPanel.scrollUp(true);
        }
    }

    public void selectSeq(int seq){
        lastid = seq;

        Sequence pickedSeq = av.getAlignment().getSeqByIndex(seq);
        av.getSelectionGroup().addOrRemove(pickedSeq);
    }

    public void selectSeqs(int start, int end){
        if (av.getSelectionGroup() == null){
            return;
        }

        if (end >= av.getAlignment().getHeight()){
            end = av.getAlignment().getHeight() - 1;
        }

        if (end < start){
            int tmp = start;
            start = end;
            end = tmp;
        }
        
        for (int i = start; i <= end; i++){
            av.getSelectionGroup().addSequence(av.getAlignment().getSeqByIndex(i));
        }
    }
   
    
}
