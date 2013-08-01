/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wb
 */
public class Alignment {
    
    private int length;
    private int noOfSeq;
    private char gap='-';
    private ArrayList<Sequence> seqs;
    private String[] names;
    private int[] seqIndex;
    private int[] columnIndex;
    private int[] gapFreeIndex; //the index of columns which have no gap
    private ArrayList[] gapMap;
    private HashMap<String,Sequence> nameMap;
    
    /**
     * explore gap index and non-gap index
     */
    public void gapProfile(){
        gapMap=new ArrayList[noOfSeq];
        boolean[] nogaps=new boolean[length];
        for(int i=0;i<length;i++){
            nogaps[i]=true;
        }
        int gapNum=0;
        for(int i=0;i<noOfSeq;i++){
            ArrayList gaps=new ArrayList();
            char[] seq=seqs.get(i).getSeq();
            for(int j=0;j<length;j++){
                if(seq[j]<'A' || seq[j]>'Z'){
                    gaps.add(j);
                    if(nogaps[j]){
                        nogaps[j]=false;
                        gapNum+=1;
                    }
                }
            }
            gaps.trimToSize();
            gapMap[i]=gaps;
        }
        gapFreeIndex=new int[length-gapNum];
        int j=0;
        for(int i=0;i<length;i++){
            if(nogaps[i]){
                gapFreeIndex[j++]=i;
            }
        }
    }
    
    public int getSitePosAt(int seqIndex, int colIndex){
        int id=getSeqByIndex(seqIndex).getId();
        ArrayList gap=gapMap[id];
        if(gap.contains(colIndex))
            return -1;
        int gapNum=0;
        for(Object i:gap){
            int g=(Integer)i;
            if(g<=colIndex){
                gapNum+=1;
            }
        }
        return colIndex-gapNum+1;
    }
    
    public String getGapFreeSeq(Sequence seq){
        char[] ss=seq.getSeq();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<gapFreeIndex.length;i++){
            sb.append(ss[gapFreeIndex[i]]);
        }
        return sb.toString();
    }
    
    public char getResAt(int seqIndex, int resIndex){
        return getSeqByIndex(seqIndex).getSeq()[resIndex];
    }
    
    public Sequence getSeqByName(String name){
        return nameMap.get(name);
    }
    
    public Sequence getSeqById(int id){
        if(id<0 || id>=seqs.size())
            return null;
        return seqs.get(id);
    }
    
    public Sequence getSeqByIndex(int i){
        if(i<0 || i>=seqs.size())
            return null;
        return seqs.get(seqIndex[i]);
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }
    
    /**
     * @return the length
     */
    public int getWidth() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setWidth(int length) {
        this.length = length;
    }
    

    /**
     * @return the noOfSeq
     */
    public int getNoOfSeq() {
        return noOfSeq;
    }

    /**
     * @param noOfSeq the noOfSeq to set
     */
    public void setNoOfSeq(int noOfSeq) {
        this.noOfSeq = noOfSeq;
    }
    
    /**
     * @return the noOfSeq
     */
    public int getHeight() {
        return noOfSeq;
    }

    /**
     * @param noOfSeq the noOfSeq to set
     */
    public void setHeight(int noOfSeq) {
        this.noOfSeq = noOfSeq;
    }

    /**
     * @return the names
     */
    public String[] getNames() {
        return names;
    }

    /**
     * @param names the names to set
     */
    public void setNames(String[] names) {
        this.names=names;
    }


    /**
     * @return the seqs
     */
    public ArrayList<Sequence> getSeqs() {
        return seqs;
    }

    /**
     * @param seqs the seqs to set
     */
    public void setSeqs(ArrayList<Sequence> seqs) {
        this.seqs = seqs;
    }


    /**
     * @return the gapFreeIndex
     */
    public int[] getGapFreeIndex() {
        return gapFreeIndex;
    }

    /**
     * @return the index
     */
    public int[] getIndex() {
        return seqIndex;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int[] index) {
        this.seqIndex = index;
    }

    /**
     * @return the nameMap
     */
    public HashMap<String,Sequence> getNameMap() {
        return nameMap;
    }

    /**
     * @param nameMap the nameMap to set
     */
    public void setNameMap(HashMap<String,Sequence> nameMap) {
        this.nameMap = nameMap;
    }

    /**
     * @return the columnIndex
     */
    public int[] getColumnIndex() {
        return columnIndex;
    }

    /**
     * @param columnIndex the columnIndex to set
     */
    public void setColumnIndex(int[] columnIndex) {
        this.columnIndex = columnIndex;
    }

    
    
    
    
}
