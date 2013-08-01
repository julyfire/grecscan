/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import grecscan2.core.Alignment;
import grecscan2.core.Sequence;
import wbitoolkit.phylo.DistanceMatrix;

/**
 *
 * @author wb
 */
public class PDistance extends DistanceMatrix{

    private int[] columnIndex;
    
    @Override
    public double distanceOf(Object a, Object b) {
        double dis=0;
        dis=pDistanceOf((Sequence)a, (Sequence)b);
        return dis;
    }
    
    public PDistance(){
        
    }
    
    public PDistance(int[] colIndex){
        columnIndex=colIndex;
    }
    
    public PDistance(Alignment aln, int[] columnIndex){        
        super.setData(aln.getSeqs().toArray());
        this.columnIndex=columnIndex;
    }
    
    public PDistance(Alignment aln, int[] rowIndex, int[] columnIndex){
        Object[] data=new Object[rowIndex.length];
        for(int i=0;i<rowIndex.length;i++){
            data[i]=aln.getSeqs().get(rowIndex[i]);
        }
        super.setData(data);
        this.columnIndex=columnIndex;
    }
    
    /**
     * P distance of two sequences
     * @param a
     * @param b
     * @return 
     */
    public double pDistanceOf(Sequence a, Sequence b){
        double dis=0;
        char[] ca=a.getSeq();
        char[] cb=b.getSeq();
        dis=pDistanceOf(ca,cb);
        return dis;
    }
    
    public double pDistanceOf(String a, String b){
        double dis=0;
        char[] ca=a.toCharArray();
        char[] cb=b.toCharArray();
        dis=pDistanceOf(ca,cb);
        return dis;
    }
    
    public double pDistanceOf(char[] a, char[] b){
        double dis=0;
        for(int i=0;i<columnIndex.length;i++){
            if(a[columnIndex[i]]!=b[columnIndex[i]]){
                dis+=1;
            }
        }
        dis/=columnIndex.length;
        return dis;
    }
    
}
