/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.Recombinant;
import grecscan2.core.Sequence;
import java.awt.Color;
import grecscan2.gui.aln.ColorSchemeI;

/**
 *
 * @author wb
 */
public class RecColorScheme implements ColorSchemeI{
    
    private Recombinant rec;
    private Color red=new Color(229,51,25);
    private Color green=new Color(32,131,32);
    private Color blue=new Color(25,127,229);
    private Color lightBlue=new Color(148,167,207);
    private Color lightRed=new Color(244,152,140);
    private Color lightGreen=new Color(127,164,127);
    
    public RecColorScheme(Recombinant rec){
        this.rec=rec;
    }

    @Override
    public Color colorOf(char c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Color colorOf(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Color colorOf(char c, Sequence seq, int offset) {
        int recIndex=rec.getIndexOf(rec.getRecSeq());
        int seqIndex=rec.getIndexOf(seq);
        if(seqIndex==-1) return Color.white;
        int start=rec.getAlnStart();
        int end=rec.getAlnEnd();
        char seqChar=seq.getSeq()[offset];
        char recChar=rec.getRecSeq().getSeq()[offset];
//        char majorChar=rec.getMajorSeq().getSeq().charAt(offset);
//        char minorChar=rec.getMinorSeq().getSeq().charAt(offset);
        
        if(seqChar==recChar){
            if(seqIndex<recIndex-1){
                return lightRed;
            }
            else if(seqIndex==recIndex-1){
                return red;
            }
            else if(seqIndex==recIndex+1){
                return green;
            }
            else if(seqIndex>recIndex+1 && seqIndex<start){
                return lightGreen;
            }
            else if(seqIndex==recIndex){
                Color rc=null;
                for(int[] b:rec.getFullBlock()){
                    if(offset>=b[0]-1 && offset<b[1]){
                        rc=green;
                        break;
                    }
                }
                if(rc==null){
                    rc=red;
                }
                return rc;
            }
            else if(seqIndex>=start && seqIndex<=end){
                Color rc=null;
                for(int[] b:rec.getFullBlock()){
                    if(offset>=b[0]-1 && offset<b[1]){
                        rc=lightGreen;
                        break;
                    }
                }
                if(rc==null){
                    rc=lightRed;
                }
                return rc;
            }
            else{
                return Color.white;
            }
        }
        else{
            return Color.white;
        }
        
    }

    @Override
    public Color colorOf(Sequence seq) {
        int recIndex=rec.getIndexOf(rec.getRecSeq());
        int seqIndex=rec.getIndexOf(seq);
        if(seqIndex==-1) return Color.white;
        int start=rec.getAlnStart();
        int end=rec.getAlnEnd();
        
        if(seqIndex<recIndex-1){
            return lightRed;
        }
        else if(seqIndex==recIndex-1){
            return red;
        }
        else if(seqIndex==recIndex){
            return blue;
        }
        else if(seqIndex==recIndex+1){
            return green;
        }
        else if(seqIndex>recIndex+1 && seqIndex<start){
            return lightGreen;
        }
        else if(seqIndex>=start && seqIndex<=end){
            return lightBlue;
        }
        else{
            return Color.white;
        }
    }
    
}
