/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wb
 */
public class Recombinant {
    
    private int rec;
    private int major;
    private int minor;
    private Sequence recSeq;
    private Sequence majorSeq;
    private Sequence minorSeq;
    private List<Integer> breakpoints;
    private double[] pdd;
    private double[] pos;
    private double[] stat;
    private double[] sig;
    private ArrayList<double[]> pv; //fragment p-value: rec-p1, rec-p2
    private int[][] fpi; //fragment pairwise identity: rec-p1, rec-p2
    private ArrayList<int[]> blocks; //recombination block: start, end
    private ArrayList<int[]> fullBlock; //include gap
    
    private int[] alnIndex; // seqs index in alignment
    private int alnStart; //first sequence index related with rec in alignment
    private int alnEnd; //last sequence index related with rec in alignment  
    private String majorTree;
    private String minorTree;
    private double pdisToMajor;
    private double pdisToMinor;
    
    public Recombinant(){
        
    }
    public Recombinant(int rec){
        this.rec=rec;
    }
    

    @Override
    public String toString(){
        if(recSeq!=null){
            return recSeq.getName();
        }
        else{
            return this.toString();
        }
    }
    
    /**
     * @return the rec
     */
    public int getRec() {
        return rec;
    }

    /**
     * @param rec the rec to set
     */
    public void setRec(int rec) {
        this.rec = rec;
    }

    /**
     * @return the donor
     */
    public int getMajor() {
        return major;
    }

    /**
     * @param donor the donor to set
     */
    public void setMajor(int donor) {
        this.major = donor;
    }

    /**
     * @return the acceptor
     */
    public int getMinor() {
        return minor;
    }

    /**
     * @param acceptor the acceptor to set
     */
    public void setMinor(int acceptor) {
        this.minor = acceptor;
    }

    /**
     * @return the pdd
     */
    public double[] getPdd() {
        return pdd;
    }

    /**
     * @param pdd the pdd to set
     */
    public void setPdd(double[] pdd) {
        this.pdd = pdd;
    }

    /**
     * @return the breakpoints
     */
    public List<Integer> getBreakpoints() {
        return breakpoints;
    }

    /**
     * @param breakpoints the breakpoints to set
     */
    public void setBreakpoints(List<Integer> breakpoints) {
        this.breakpoints = breakpoints;
    }

    /**
     * @return the stat
     */
    public double[] getStat() {
        return stat;
    }

    /**
     * @param stat the stat to set
     */
    public void setStat(double[] stat) {
        this.stat = stat;
    }

    /**
     * @return the sig
     */
    public double[] getSig() {
        return sig;
    }

    /**
     * @param sig the sig to set
     */
    public void setSig(double[] sig) {
        this.sig = sig;
    }

    /**
     * @return the pv
     */
    public ArrayList<double[]> getPv() {
        return pv;
    }

    /**
     * @param pv the pv to set
     */
    public void setPv(ArrayList<double[]> pv) {
        this.pv = pv;
    }

    /**
     * @return the fpi
     */
    public int[][] getFpi() {
        return fpi;
    }

    /**
     * @param fpi the fpi to set
     */
    public void setFpi(int[][] fpi) {
        this.fpi = fpi;
    }

    /**
     * @return the blocks
     */
    public ArrayList<int[]> getBlocks() {
        return blocks;
    }

    /**
     * @param blocks the blocks to set
     */
    public void setBlocks(ArrayList<int[]> blocks) {
        this.blocks = blocks;
    }

    /**
     * @return the recSeq
     */
    public Sequence getRecSeq() {
        return recSeq;
    }

    /**
     * @param recSeq the recSeq to set
     */
    public void setRecSeq(Sequence recSeq) {
        this.recSeq = recSeq;
    }

    /**
     * @return the majorSeq
     */
    public Sequence getMajorSeq() {
        return majorSeq;
    }

    /**
     * @param majorSeq the majorSeq to set
     */
    public void setMajorSeq(Sequence majorSeq) {
        this.majorSeq = majorSeq;
    }

    /**
     * @return the minorSeq
     */
    public Sequence getMinorSeq() {
        return minorSeq;
    }

    /**
     * @param minorSeq the minorSeq to set
     */
    public void setMinorSeq(Sequence minorSeq) {
        this.minorSeq = minorSeq;
    }

  

    /**
     * @return the fullBlock
     */
    public ArrayList<int[]> getFullBlock() {
        return fullBlock;
    }

    /**
     * @param fullBlock the fullBlock to set
     */
    public void setFullBlock(ArrayList<int[]> fullBlock) {
        this.fullBlock = fullBlock;
    }

    /**
     * @return the pos
     */
    public double[] getPos() {
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(double[] pos) {
        this.pos = pos;
    }

    /**
     * @return the alnIndex
     */
    public int[] getAlnIndex() {
        return alnIndex;
    }

    /**
     * @param alnIndex the alnIndex to set
     */
    public void setAlnIndex(int[] alnIndex) {
        this.alnIndex = alnIndex;
    }

    /**
     * @return the majorTree
     */
    public String getMajorTree() {
        return majorTree;
    }

    /**
     * @param majorTree the majorTree to set
     */
    public void setMajorTree(String majorTree) {
        this.majorTree = majorTree;
    }

    /**
     * @return the minorTree
     */
    public String getMinorTree() {
        return minorTree;
    }

    /**
     * @param minorTree the minorTree to set
     */
    public void setMinorTree(String minorTree) {
        this.minorTree = minorTree;
    }

    public int getIndexOf(Sequence seq){
        int index=-1;
        int id=seq.getId();
        for(int i=0;i<alnIndex.length;i++){
            if(id==alnIndex[i]){
                index=i;
                break;
            }
        }
        return index;
    }

    /**
     * @return the alnEnd
     */
    public int getAlnEnd() {
        return alnEnd;
    }

    /**
     * @param alnEnd the alnEnd to set
     */
    public void setAlnEnd(int alnEnd) {
        this.alnEnd = alnEnd;
    }

    /**
     * @return the pdisToMajor
     */
    public double getPdisToMajor() {
        return pdisToMajor;
    }

    /**
     * @param pdisToMajor the pdisToMajor to set
     */
    public void setPdisToMajor(double pdisToMajor) {
        this.pdisToMajor = pdisToMajor;
    }

    /**
     * @return the pdisToMinor
     */
    public double getPdisToMinor() {
        return pdisToMinor;
    }

    /**
     * @param pdisToMinor the pdisToMinor to set
     */
    public void setPdisToMinor(double pdisToMinor) {
        this.pdisToMinor = pdisToMinor;
    }

    /**
     * @return the alnStart
     */
    public int getAlnStart() {
        return alnStart;
    }

    /**
     * @param alnStart the alnStart to set
     */
    public void setAlnStart(int alnStart) {
        this.alnStart = alnStart;
    }

}
