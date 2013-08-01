/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.io;

import grecscan2.core.Sequence;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author wb
 */
public abstract class SeqIO {
    private int noOfSeq;
    private int maxLength;
    private List<Sequence> seqs;
    
    public abstract void read(File file) throws FileNotFoundException, IOException;
    
    public abstract void write(File file, Sequence seq) throws IOException;

    /**
     * @return the noOfSeq
     */
    public int getNoOfSeq() {
        return noOfSeq;
    }

    /**
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @return the seqs
     */
    public List<Sequence> getSeqs() {
        return seqs;
    }

    /**
     * @param noOfSeq the noOfSeq to set
     */
    public void setNoOfSeq(int noOfSeq) {
        this.noOfSeq = noOfSeq;
    }

    /**
     * @param maxLength the maxLength to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @param seqs the seqs to set
     */
    public void setSeqs(List<Sequence> seqs) {
        this.seqs = seqs;
    }
}
