/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.rec;

import grecscan2.core.Alignment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import wbitoolkit.smooth.Loess;

/**
 *
 * @author wb
 */
public class RecDetect {
    
    private Alignment aln;
    private char[][] cols;
    private File infile;
    private int window=60;
    private int step=1;
    private int start=0;
    private int stop=0;
    private int stepNum;
    private int times=1000;

    
    public RecDetect(File file) throws FileNotFoundException, IOException{
        infile=file;
        init();
    }
    
    //load data and parameters
    private void init() throws FileNotFoundException, IOException{
//        aln=new Alignment();
//        aln.loadAlignment(infile);
//        cols=aln.getAln();
//        if(start==0) start=1;
//        if(stop==0) stop=aln.getLength();
//        stepNum=(stop-start+1-window)/step+1;
    }
    
    //scan alignment
    public void scan(){
        
        for(int i=0;i<aln.getNoOfSeq();i++)
             for(int j=i+1;j<aln.getNoOfSeq();j++)
                 for(int k=j+1;k<aln.getNoOfSeq();k++){
                     buildTriplet(i,j,k);
                 
             
        }
    }
    
    //Triplet analysis
    private Triplet buildTriplet(int a, int b, int c){
        ArrayList<char[]> infoAln=new ArrayList<char[]>();
        for(int i=0;i<stop;i++){
            if((cols[i][a]==cols[i][b] && cols[i][b]==cols[i][c] && cols[i][c]==cols[i][a])
                    || (cols[i][a]!=cols[i][b] && cols[i][b]!=cols[i][c] && cols[i][c]!=cols[i][a])){
                continue;
            }
            infoAln.add(new char[]{cols[i][a],cols[i][b],cols[i][c]});
        }
        int[] ts=new int[]{a,b,c};
        int[] s=assignRec(infoAln);
        Triplet t=new Triplet(ts[s[0]],ts[s[2]],ts[s[1]]);
        
        if(findBreakpoints(t)==0) return null;
        else return t;
    }
    
    //find breakpoints
    private int findBreakpoints(Triplet t){
        slidingPid(t);
        int bp=0;
//        double[] sspid=Loess.smooth(t.spid);
        
        //remove zero value
        ArrayList<Double> noo=new ArrayList<Double>();
        ArrayList<Integer> pos=new ArrayList<Integer>();
        for(int i=0;i<t.spid.length;i++){
            if(t.spid[i]!=0){
                noo.add(t.spid[i]);
                pos.add(i);
            }
        }
        
        //cross-over points
        ArrayList<Integer> cps=new ArrayList<Integer>();
        for(int i=0;i<noo.size()-1;i++){
            if(noo.get(i)>0 && noo.get(i+1)<0){
                cps.add(pos.get(i+1));
            }
            if(noo.get(i)<0 && noo.get(i+1)>0){
                cps.add(-pos.get(i));
            }
        }
        
        System.out.print(t.rec+": ");
        for(Integer cp:cps){
            System.out.print(cp+"\t");
        }
        System.out.println();
        
        if(t.rec==2){
            for(Double no:t.spid){
                System.out.println(no);
            }
        }
        
        
        return bp;
    }
    
    
    
    //sliding window scan
    private void slidingPid(Triplet t){
        double[] spid=new double[stepNum];
        for(int i=0;i<stepNum;i++){
            int s=start-1+step*i;
            spid[i]=pid(t,s,s+window)[2];
        }
        t.spid=spid;
    }
    
    //statistic: pairwise identity difference
    private double[] pid(Triplet t, int from, int to){
        double ip1=0, ip2=0;
        for(int i=from;i<to;i++){
            if(cols[i][t.p1]==cols[i][t.rec] && cols[i][t.p2]!=cols[i][t.rec])
                ip1+=1;
            if(cols[i][t.p1]!=cols[i][t.rec] && cols[i][t.p2]==cols[i][t.rec])
                ip2+=1;
        }
        ip1/=to-from;
        ip2/=to-from;
        return new double[]{ip1,ip2, ip1-ip2};
    }
    
    //assign rec and parents
    private int[] assignRec(ArrayList<char[]> cols){
        HashMap<Integer,Double> imap=new HashMap<Integer,Double>();
        imap.put(0, pI(1,2,cols));
        imap.put(1, pI(0,2,cols));
        imap.put(2, pI(0,1,cols));
        ArrayList<Map.Entry<Integer, Double>> infoIds = new ArrayList<Map.Entry<Integer, Double>>(imap.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<Integer,Double>>() { 
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) { 
                return o1.getValue()<o2.getValue()?-1:1; 
            } 
        }); 
        int[] s=new int[3];
        for(int i=0;i<infoIds.size();i++){
            s[i]=infoIds.get(i).getKey();
        }
        return s;
   }
    
    
    //pairwise identity
    private double pI(int s1, int s2, ArrayList<char[]> cols){
        double pi=0;
        for(char[] c:cols){
            if(c[s1]==c[s2]){
                pi+=1;
            }
        }
        return pi/cols.size();
    }
    
    
    class Triplet{
        int rec;
        int p1;
        int p2;
        double[] spid;
        public Triplet(int rec, int p1, int p2){
            this.rec=rec;
            this.p1=p1;
            this.p2=p2;
        }
    }
    
    
    
    
    
    
}
