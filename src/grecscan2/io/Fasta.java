/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.io;

import grecscan2.core.Sequence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author wb
 */
public class Fasta extends SeqIO{
    
    
    @Override
    public void read(File file) throws FileNotFoundException, IOException{
        ArrayList seqs=new ArrayList<Sequence>();
        int maxLength=0;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        String title = "";
        StringBuilder seq = new StringBuilder();
        title = br.readLine();
        if(title.substring(0, 1).equals(">") == false){
            throw new java.io.IOException("Bad FASTA format on first line\n");
        }
        title=title.substring(1, title.length());
//        title = checkTitle(title);

        int i=0;
        while((line = br.readLine()) != null){
            if(line.startsWith(">")==true){
                //add last sequence
                addSequence(seqs, i, title, seq.toString());
                
                i++;
                //check if the sequences are the same length
                if(maxLength<seq.length())
                    maxLength=seq.length();
                //the title of current sequence
                title=line.substring(1, line.length());
//                title = checkTitle(title);
                seq=new StringBuilder();
            }
            else{
                //current sequence
                seq.append(line.toUpperCase());
            }           
        }
        //add the last sequence
        addSequence(seqs,i,title,seq.toString());
        if(maxLength<seq.length()){
            maxLength=seq.length();
        }
        
        br.close();
        seqs.trimToSize();
        this.setMaxLength(maxLength);
        this.setNoOfSeq(seqs.size());
        this.setSeqs(seqs);
    }
    
    private void addSequence(ArrayList seqs, int id, String title, String seq){
        int s=title.indexOf(" ");
                String name=s>0?title.substring(0, s):title;
                String desc=s>0?title.substring(s):"";
                Sequence aseq=new Sequence(id,name,seq);
                aseq.setDescription(desc);
                seqs.add(aseq);
    }
    
    
    private String checkTitle(String t) {
        Pattern p=Pattern.compile("\\.|\\&|\\%|\\\\|\\/|\\*|\\[|\\]");
        Matcher m=p.matcher(t);
        t=m.replaceAll("");

        return t;
    }

    @Override
    public void write(File file, Sequence seq) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
