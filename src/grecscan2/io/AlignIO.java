/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.io;

import grecscan2.core.Alignment;
import grecscan2.core.Sequence;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wb
 */
public class AlignIO extends SeqIO {
    
    private Alignment ali;

    @Override
    public void read(File file) throws FileNotFoundException, IOException {
        loadAlignment(getSeqIO(file));
    }

    @Override
    public void write(File file, Sequence seq) throws IOException {
        
    }
    
    private SeqIO getSeqIO(File file) throws FileNotFoundException, IOException{
        Fasta sio=new Fasta();
        sio.read(file);
        return sio;
    }
    
    private void loadAlignment(SeqIO sio){
        ali=new Alignment();
        Fasta fa=(Fasta)sio;
        int length=fa.getMaxLength();
        int noOfSeq=fa.getNoOfSeq();
        int[] index=new int[noOfSeq];
        String[] names=new String[noOfSeq];
        HashMap nameMap=new HashMap<String,Sequence>();
        ArrayList seqs=new ArrayList<Sequence>(noOfSeq);
        for(int i=0;i<noOfSeq;i++){
            index[i]=i;
            Sequence seq=fa.getSeqs().get(i);
            names[i]=seq.getName();
            nameMap.put(seq.getName(), seq);
            char[] aseq=new char[length];
            char[] oseq=seq.getSeq();
            int olength=seq.getLength();
            int j=0;
            for(;j<olength;j++){
                aseq[j]=oseq[j];
            }
            for(;j<length;j++){
                aseq[j]=' ';
            }
            seq.setSeq(aseq);
            seqs.add(seq);
        }
        ali.setLength(length);
        ali.setNoOfSeq(noOfSeq);
        ali.setIndex(index);
        ali.setNames(names);
        ali.setNameMap(nameMap);
        ali.setSeqs(seqs);
        ali.gapProfile();
    }

    /**
     * @return the ali
     */
    public Alignment getAlignment() {
        return ali;
    }

    /**
     * @param ali the ali to set
     */
    public void setAlignment(Alignment ali) {
        this.ali = ali;
    }
}
