/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import grecscan2.core.Alignment;
import grecscan2.core.Pipeline;
import grecscan2.core.Recombinant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import test.NucleotidePairwiseDistance.Model;
import wbitoolkit.smooth.Loess;
import wbitoolkit.statistic.Description;

/**
 *
 * @author wb
 */
public class RecFinder2 {
    private int window=100;
    private int step=1;
    private int start=0;
    private int stop=0;
    private int stepNum;
    private Alignment aln;
    private int numOfSeq;
    private ArrayList<Integer> cols;
    private NucleotideFrequency nf;
    private double[][][] pdMatrix;
    private ArrayList<Recombinant> triplets;
    private LinkedList<Recombinant> results;
    private Pipeline pl;
    private int span=100;
    private int times=1000;
    private int loessSpan=200;
    private double areaCutoff=0.005;
    private boolean bonferroniCorrection=true;
    private Model model=NucleotidePairwiseDistance.Model.JC69;
//    private int progress=0;
    
    //temp varibles
    private int[] aPid=new int[2];
    private int[] bPid=new int[2];
    private int[] cPid=new int[2];
    private int[] dPid=new int[2];
    private double[] x;
    

    
    public RecFinder2(Pipeline pl){
        this.pl=pl;
        
        init();
        
    }
    
    public void scan(){
//        buildPdMatrix();
//        buildTriplets();
//        poolSigTriplets();
//        extractRecEvents();
        tripletsScan();

    }
    
    
    
    private void init(){
        this.aln=pl.getAln();
        numOfSeq=aln.getNoOfSeq();
        int[] nogaps=aln.getGapFreeIndex();        
//        this.window=pl.getWindow();
        if(start==0) start=1;
        if(stop==0) stop=nogaps.length;
        stepNum=(stop-start+1-window)/step+1;
        cols=new ArrayList<Integer>();       
        for(int i=0;i<nogaps.length;i++){
            cols.add(nogaps[i]);
        }
        this.nf=new NucleotideFrequency(aln);
    }
    
    private void fillX(){
        x=new double[stepNum];
        for(int i=0;i<x.length;i++)
            x[i]=i;
    }
    
//    private void buildPdMatrix(){
//        pdMatrix=new double[numOfSeq][numOfSeq][];
//        for(int i=0;i<pdMatrix.length;i++){
//            for(int j=i+1;j<pdMatrix.length;j++){
//                double[] pd=new double[mfes.length];
//                for(int k=0;k<pd.length;k++){
//                    pd[k]=Math.abs(mfes[k][i]-mfes[k][j]);
//                }
//                pdMatrix[i][j]=pd;
//                pdMatrix[j][i]=pd;
//            }
//        }
//        
//    }
    
    
    
    private void tripletsScan(){
        long start,end,time;
        start = System.currentTimeMillis();
        
        fillX();
        results=new LinkedList<Recombinant>();
        int progress=0;
        double nProgress=0;
        double sProgress=0;
        double eProgress=0;
        
        int n=numOfSeq;
        int amount=n*(n-1)*(n-2)/2;
        
        System.out.println("begin to scan recombinant from "+amount+" triplets...");
        
        int initSize=1000;  //the size of triplets to be analysis per batch
        triplets=new ArrayList<Recombinant>(initSize);
/***************************************************************/ 
        
        Loess.span=stepNum>5*loessSpan?loessSpan*1.0/stepNum:0.2;
/***************************************************************/         
        
        double[] pd1=new double[stepNum];
        double[] pd2=new double[stepNum];
        double[] pd3=new double[stepNum];
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(j==i) continue;
                fillPdArray(pd1,i,j);
                for(int k=j+1;k<n;k++){
                    if(k==i) continue;                 
                   
                    fillPdArray(pd2,i,k);
                    fillPdArray(pd3,j,k);
                    Recombinant r=triplet(i,j,k,pd1,pd2,pd3);
                    
                    progress+=1;
                    eProgress=progress*100/amount;
                    nProgress=sProgress+(eProgress-sProgress)*0.2;
                    pl.setProgress((int)(nProgress));  
                    
                    if(triplets.size()>=initSize || progress==amount){
                        triplets.trimToSize();  
                        poolSigTriplets(nProgress,eProgress);
                        results.addAll(triplets);
//                        extractRecEvents();
                        triplets=new ArrayList<Recombinant>(initSize);
                        sProgress=eProgress;
                    
                        System.gc();
//                      System.out.println("memory:"+Runtime.getRuntime().totalMemory()+","+Runtime.getRuntime().maxMemory());
                        System.out.println("finished triplets "+progress+"/"+amount);
                    }
                                        
                    if(r==null) continue;

                    triplets.add(r);                   
                    
                }
                
            }
        }
        pd1=null;
        pd2=null;
        pd3=null;
        
        extractRecEvents();
        fillPddArray();
        
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("time cost:"+time/1000.0+"s");
    }
    
//    private void buildTriplets(){
////        long start,end,time;
////        start = System.currentTimeMillis();
//        
//        int n=numOfSeq;
//        int amount=n*(n-1)*(n-2)/2;
//        int initSize=amount>10000?10000:amount;
//        triplets=new ArrayList<Recombinant>(initSize);
//        
//        double[] pd1;
//        for(int i=0;i<n;i++){
//            for(int j=0;j<n;j++){
//                if(j==i) continue;
//                pd1=pdArrayOf(i,j);
//                double[] pd2;
//                for(int k=j+1;k<n;k++){
//                    if(k==i) continue;
//                    pd2=pdArrayOf(i,k);
//                    Recombinant r=triplet(i,j,k,pd1,pd2);
//                    
//                    progress+=1;
//                    pl.setProgress((int)(progress*0.175*100/amount));
//                   
//                    if(r==null) continue;
//                    triplets.add(r);
//                    
//                }
//            }
//        }
//        triplets.trimToSize();
//        
////        end = System.currentTimeMillis();
////        time = end - start;
////        System.out.println("time cost:"+time/1000.0+"s");
//    }
    
    private void poolSigTriplets(double sprogress, double progress){
        refineBreakpoints();       
        
        calculatePValue(sprogress,progress);
        
        ArrayList<Recombinant> recs=new ArrayList<Recombinant>();
        for(Recombinant r:triplets){
            
            ArrayList<Integer> bps=new ArrayList<Integer>();  //breakpoints         
            ArrayList<int[]> fms=new ArrayList<int[]>(); //fragments
            ArrayList<double[]> pvs=new ArrayList<double[]>(); //p-value
            for(int i=0;i<r.getPv().size();i++){
                int[] sb=new int[2];
/***************************************************************/ 
                if(r.getPv().get(i)[0]<0.05 && r.getPv().get(i)[1]<0.05){
/***************************************************************/ 
                    sb[0]=r.getBreakpoints().get(i);
                    sb[1]=r.getBreakpoints().get(i+1)-1;
                    fms.add(sb);
                    pvs.add(r.getPv().get(i));
                    bps.add(sb[0]);
                    bps.add(sb[1]+1);
                }
            }
            if(bps.isEmpty()) continue;
            if(bps.get(0)>0) bps.add(0,0);
            int last=r.getBreakpoints().get(r.getBreakpoints().size()-1);
            if(bps.get(bps.size()-1)!=last) bps.add(last);
            
            if(fms.size()>0){
                r.setBlocks(fms);
                r.setPv(pvs);
                r.setBreakpoints(bps);
                recs.add(r);
            }
        }
        triplets=recs;
    }
    
    private void extractRecEvents(){
        //recSet: store the real recombinant
        LinkedList<Recombinant> recSet=new LinkedList<Recombinant>();
        
        while(results.size()>0){
            Recombinant newrec=maxRec(results);
            results.remove(newrec);      
            Iterator iterator=results.iterator();
            while(iterator.hasNext()){
                Recombinant rec=(Recombinant)iterator.next();
                if(rec.getRec()== newrec.getRec() || rec.getMajor()==newrec.getRec() || rec.getMinor()==newrec.getRec()){
                    iterator.remove();
                }
            }
            recSet.add(newrec);
        }
        results=recSet;
        

    }
    
    private void fillPddArray(){
        double[] pd1=new double[stepNum];
        double[] pd2=new double[stepNum];
        for(Recombinant r:results){
            r.setPdd(pddArray(r,pd1,pd2));
        }
        pd1=null;
        pd2=null;
    }
    
    //find the triplet having maxmal value
    private Recombinant maxRec(LinkedList<Recombinant> recs){
        if(recs.size()==1) return recs.get(0);
        double max=0;
        int p=0;
        for(int i=0;i<recs.size();i++){
            double v=sumDiff(recs.get(i));
//          double v=meanChi2(recs.get(i));
            if(max<v){
                max=v;
                p=i;
            }                   
        }
        return recs.get(p);        
    }
    
//    private double meanChi2(Recombinant r){
//        double mc=0;
//        for(int i=1;i<r.getBreakpoints().size()-1;i++){
//            int a=r.getBreakpoints().get(i-1);
//            int b=r.getBreakpoints().get(i);
//            int c=r.getBreakpoints().get(i+1);
//            int[] left=pid(r,a,b);
//            int[] right=pid(r,b,c);
//            mc+=chi2(left,right);
//        }
//        return mc/(r.getBreakpoints().size()-2);
//    }
    
    private double sumDiff(Recombinant r){
        int pi1=0,pi2=0;
        for(int i=0;i<r.getBreakpoints().size()-1;i++){
            int a=r.getBreakpoints().get(i);
            int b=r.getBreakpoints().get(i+1);
            pid(aPid,r,a,b);
            boolean jump=false;
            for(int[] f:r.getBlocks()){
                if(a==f[0]){
                    jump=true;
                    break;
                }
            }
            if(jump){
                pi1+=aPid[1];
                pi2+=aPid[0];
            }
            else{
                pi1+=aPid[0];
                pi2+=aPid[1];
            }   
        }
        return Math.abs(pi2-pi1);
    }
    
//    private void refineBreakpoints2(){
//        
//        int noRec=20;
//        Iterator it=triplets.iterator();
//        while(it.hasNext()){
//            Recombinant r=(Recombinant)it.next();  
//            ArrayList<Integer> bps=r.getBreakpoints();
//            double[] stat=new double[bps.size()-2];
//
////            if(filterByArea(r)<=2){
////                it.remove();
////                continue;
////            }
//            for(int i=1;i<bps.size()-1;i++){
//                int span=100;
//                int a=bps.get(i-1);
//                int b=bps.get(i);
//                int c=bps.get(i+1);
//                //if fragment is two small, it may be impossible to be a recombination event
////                if(b-a<noRec || c-b<noRec){
////                    continue;
////                }
//                
//                span=b-a>span/2?span:(b-a)*2-2;
//                span=c-b>span/2?span:(c-b)*2-2;
//                
//                int[] left=pid(r,a,b-span/2);
//                int[] right=pid(r,b+span/2,c);
//                double max=0; int imax=b;
//                for(int j=0;j<span;j++){
//                    int x=b-span/2+j;
//                    int[] ld=pid(r,b-span/2,x);
//                    int[] rd=pid(r,x,b+span/2);
//                    ld[0]+=left[0];ld[1]+=left[1];
//                    rd[0]+=right[0];rd[1]+=right[1];
//                    double chi2=chi2(ld,rd);
//                    
//                    //debug
////                    System.out.println((x+1)+"\t"+ld[0]+"\t"+ld[1]+"\t"+rd[0]+"\t"+rd[1]+"\t"+chi2);
//                    
//                    if(max<chi2){
//                        max=chi2;
//                        imax=x;
//                    }
//                }
//                
//                
//                bps.set(i, imax);
//                stat[i-1]=max;
//            }
//            r.setStat(stat);
//                       
//        }
//        pl.setProgress(pl.getProgress()+1);
//
//    }
    
    private void refineBreakpoints(){
        
        int noRec=20;
        Iterator it=triplets.iterator();
        while(it.hasNext()){
            Recombinant r=(Recombinant)it.next();  
            List<Integer> bps=r.getBreakpoints();
//            double[] stat=new double[bps.size()-2];

//            if(filterByArea(r)<=2){
//                it.remove();
//                continue;
//            }
            for(int i=1;i<bps.size()-1;i++){
                int a=bps.get(i-1);
                int b=bps.get(i);
                int c=bps.get(i+1);
                int imax=mm(r,a,c,b);
                
                
                bps.set(i, imax);
//                stat[i-1]=max;
            }
//            r.setStat(stat);
                       
        }
//        pl.setProgress(pl.getProgress()+1);

    }
    
//    private int mm(Recombinant r, int a, int c, int x){
//        int s=x-span;
//        int e=x+span;
//        s=s>a?s:a+1;
//        e=e<c?e:c-1;
//        int[] left=pid(r,a,s);
//        int[] right=pid(r,e,c);
//        double max=0; int imax=s;
//        for(int j=s;j<=e;j++){
//            int[] ld=pid(r,s,j);
//            int[] rd=pid(r,j,e);
//            ld[0]+=left[0];ld[1]+=left[1];
//            rd[0]+=right[0];rd[1]+=right[1];
//            double chi2=chi2(ld,rd);
//
//            if(max<chi2){
//                max=chi2;
//                imax=j;
//            }
//            ld=null;
//            rd=null;
//        }
//        left=null;
//        right=null;
//        
//        if(imax-a<11 || c-imax<11)
//            return imax;
//
//        if(imax-s<10 || e-imax<10){
//            return mm(r,a,c,imax);
//        }
//        
//
//        return imax;
//    }
    private int mm(Recombinant r, int a, int c, int x){
        int s=x-span;
        int e=x+span;
        s=s>a?s:a+1;
        e=e<c?e:c-1;
        pid(aPid,r,a,s);
        pid(bPid,r,e,c);
        double max=0; int imax=s;
        for(int j=s;j<=e;j++){
            pid(cPid,r,s,j);
            pid(dPid,r,j,e);
            cPid[0]+=aPid[0];cPid[1]+=aPid[1];
            dPid[0]+=bPid[0];dPid[1]+=bPid[1];
            double chi2=chi2(cPid,dPid);

            if(max<chi2){
                max=chi2;
                imax=j;
            }
            
        }
        
        
        if(imax-a<11 || c-imax<11)
            return imax;

        if(imax-s<10 || e-imax<10){
            return mm(r,a,c,imax);
        }
        

        return imax;
    }
    
    private int filterByArea(Recombinant r, double[] pdd){
        
//        //debug
//        if(r.getMajor()==6 && r.getMinor()==3 && r.getRec()==2)
//            System.out.println();
        
        double total=0;
        for(double d:pdd){
            total+=Math.abs(d);
        }
        int n=r.getBreakpoints().size();
        int last=cols.size();
        List<Integer> bps=new ArrayList<Integer>();
        for(int i=1;i<n;i++){
            Integer a=Math.abs(r.getBreakpoints().get(i-1));
            Integer b=Math.abs(r.getBreakpoints().get(i));
            double area=0;
            for(int j=a;j<b;j++){
                area+=Math.abs(pdd[j]);
            }
/***************************************************************/            
            if(area/total<areaCutoff){
                bps.add(r.getBreakpoints().get(i));
            }
/***************************************************************/ 
        }
        r.getBreakpoints().removeAll(bps);
        bps=r.getBreakpoints();
        
        for(int i=1;i<bps.size()-1;i++){
            if(bps.get(i)*bps.get(i+1)>0){
                bps.remove(i);
                i--;
            }
        }
        
        for(int i=1;i<bps.size()-1;i++){
            bps.set(i, Math.abs(bps.get(i))+window/2);
        } 
        bps.set(bps.size()-1, last);

        
        
        return bps.size();
    }
    
//    private void initStatistic(){
//        for(Recombinant r:triplets){
//            int n=r.getBreakpoints().size()-1;
//            ArrayList<double[]> pvs=new ArrayList<double[]>(n);
//            for(int i=0;i<n;i++){
//                double[] pv=new double[2];
//                pv[0]=0;
//                pv[1]=0;
//                pvs.add(pv);
//            }
//            r.setPv(pvs);
//            int[][] fpi=new int[n][];
//            for(int j=0;j<n;j++){
//                int a=r.getBreakpoints().get(j);
//                int b=r.getBreakpoints().get(j+1);
//                int[] left=pid(r,a,b);
//                fpi[j]=left;    
//            }
//            r.setFpi(fpi);
//        }
//        
//    }
//    
//    //fragment PI p-value
//    private void calculatePValue(double sprogress, double eprogress){
//        int progress=0;
//        
//        initStatistic();
//        ArrayList<Integer> shuffledCols=(ArrayList<Integer>)cols.clone();
//        for(int i=0;i<times;i++){
//            Collections.shuffle(shuffledCols);
//            for(Recombinant r:triplets){
//                for(int j=0;j<r.getBreakpoints().size()-1;j++){
//                    int a=r.getBreakpoints().get(j);
//                    int b=r.getBreakpoints().get(j+1);
//                    int[] left=pid(r,a,b,shuffledCols);
//                    
//                    if(left[0]<=r.getFpi()[j][0]) r.getPv().get(j)[0]+=1;
//                    if(left[1]>=r.getFpi()[j][1]) r.getPv().get(j)[1]+=1;   
//                    
//                    left=null;
//                }
//            }           
//            progress+=1;
//            pl.setProgress((int)(sprogress+progress*(eprogress-sprogress)/times));
//        }
//        
//        
//        for(Recombinant r:triplets){
//            r.setFpi(null);
//            ArrayList<Integer> bps=r.getBreakpoints();
//            int n=numOfSeq;
//            int g=n*(n-1)*(n-2)/2;
//            for(int i=0;i<r.getPv().size();i++){
//                r.getPv().get(i)[0]/=times;
//                r.getPv().get(i)[1]/=times;
//                //bonferroni correction
//                int l2n=cols.size()/(bps.get(i+1)-bps.get(i));
//                r.getPv().get(i)[0]*=g*l2n;
//                r.getPv().get(i)[1]*=g*l2n;
//            }
//        }
//    }
    
    private void initStatistic(){
        for(Recombinant r:triplets){
            int n=r.getBreakpoints().size()-1;
            ArrayList<double[]> pvs=new ArrayList<double[]>(n);
            for(int i=0;i<n;i++){
                double[] pv=new double[2];
                pv[0]=0;
                pv[1]=0;
                pvs.add(pv);
            }
            r.setPv(pvs);
            int[][] fpi=new int[n][];
            for(int j=0;j<n;j++){
                int a=r.getBreakpoints().get(j);
                int b=r.getBreakpoints().get(j+1);
                pid(aPid,r,a,b);
                int[] left={aPid[0],aPid[1]};
                fpi[j]=left;    
            }
            r.setFpi(fpi);
        }
        
    }
    
    //fragment PI p-value
    private void calculatePValue(double sprogress, double eprogress){
        int progress=0;
        
        initStatistic();
        ArrayList<Integer> shuffledCols=(ArrayList<Integer>)cols.clone();
        for(int i=0;i<times;i++){
            Collections.shuffle(shuffledCols);
            for(Recombinant r:triplets){
                for(int j=0;j<r.getBreakpoints().size()-1;j++){
                    int a=r.getBreakpoints().get(j);
                    int b=r.getBreakpoints().get(j+1);
                    pid(aPid,r,a,b,shuffledCols);
                    
                    if(aPid[0]<=r.getFpi()[j][0]) r.getPv().get(j)[0]+=1;
                    if(aPid[1]>=r.getFpi()[j][1]) r.getPv().get(j)[1]+=1;   
                }
            }           
            progress+=1;
            pl.setProgress((int)(sprogress+progress*(eprogress-sprogress)/times));
        }
        
        
        for(Recombinant r:triplets){
            r.setFpi(null);
            List<Integer> bps=r.getBreakpoints();
            int n=numOfSeq;
            int g=n*(n-1)*(n-2)/2;
            for(int i=0;i<r.getPv().size();i++){
                r.getPv().get(i)[0]/=times;
                r.getPv().get(i)[1]/=times;
/***************************************************************/ 
                //bonferroni correction
                if(bonferroniCorrection){
                    int l2n=cols.size()/(bps.get(i+1)-bps.get(i));
                    r.getPv().get(i)[0]*=g*l2n;
                    r.getPv().get(i)[1]*=g*l2n; 
                }
/***************************************************************/ 
            }
        }
    }
    
//    //statistic: pairwise identity difference
//    private int[] pid(Recombinant t, int from, int to, ArrayList<Integer> cols){
//        int ip1=0, ip2=0;
//        char[] p1=aln.getSeqById(t.getMajor()).getSeq();
//        char[] p2=aln.getSeqById(t.getMinor()).getSeq();
//        char[] r=aln.getSeqById(t.getRec()).getSeq();
//        for(int i=from;i<to;i++){
//            int n=cols.get(i);
//            if(p1[n]==r[n] && p2[n]!=r[n])
//                ip1+=1;
//            else if(p1[n]!=r[n] && p2[n]==r[n])
//                ip2+=1;
//        }
////        ip1/=to-from;
////        ip2/=to-from;
//        return new int[]{ip1,ip2};
//    }
//    private int[] pid(Recombinant t, int from, int to){
//        return pid(t,from,to,cols);
//    }
    
    //statistic: pairwise identity difference
    private void pid(int[] p, Recombinant t, int from, int to, ArrayList<Integer> cols){
        int ip1=0, ip2=0;
        char[] p1=aln.getSeqById(t.getMajor()).getSeq();
        char[] p2=aln.getSeqById(t.getMinor()).getSeq();
        char[] r=aln.getSeqById(t.getRec()).getSeq();
        for(int i=from;i<to;i++){
            int n=cols.get(i);
            if(p1[n]==r[n] && p2[n]!=r[n])
                ip1+=1;
            else if(p1[n]!=r[n] && p2[n]==r[n])
                ip2+=1;
        }
//        ip1/=to-from;
//        ip2/=to-from;
        p[0]=ip1;
        p[1]=ip2;
    }
    private void pid(int[] p, Recombinant t, int from, int to){
        pid(p,t,from,to,cols);
    }
    
   
    //chi-square
    private double chi2(int[] left, int[] right){
        double d=((double)left[0]*right[1]-(double)right[0]*left[1])*((double)left[0]*right[1]-(double)right[0]*left[1]);
        double n=left[0]+left[1]+right[0]+right[1];
        double m=((double)left[0]+left[1])*((double)right[0]+right[1])*((double)left[0]+right[0])*((double)left[1]+right[1]);
        double chi=d*n/m;
        return chi;
    }
    
    private Recombinant triplet(int i, int j, int k, double[] pd1, double[] pd2, double[] pd3){
        Recombinant r=new Recombinant(i);
        double d1=pairwiseDistanceOf(pd1);
        double d2=pairwiseDistanceOf(pd2);
        double dp=pairwiseDistanceOf(pd3);
        if(dp<d1 || dp<d2) return null;
        
        r.setMajor(d1>d2?k:j);
        r.setMinor(d1>d2?j:k);
        double[] pdd=d1>d2?pddArray(pd2,pd1):pddArray(pd1,pd2);
        if(pdd==null) return null;
        //r.setPdd(pdd);
        ArrayList<Integer> cps=crossPointsOf(pdd);
        if(cps.size()<=2) return null;
        r.setBreakpoints(cps);   
        
        if(filterByArea(r,pdd)<=2) return null;
        
        return r;
    }
    
    private double[] pddArray(Recombinant r, double[] pd1, double[] pd2){
        int major=r.getMajor();
        int minor=r.getMinor();
        int rec=r.getRec();
        fillPdArray(pd1,rec,major);
        fillPdArray(pd2,rec,minor);
        double[] pdd=new double[stepNum];
        for(int i=0;i<pdd.length;i++){
            pdd[i]=pd1[i]-pd2[i];
        }
        return Loess.smooth(pdd);
    }
    
    private double[] pddArray(double[] pd1, double[] pd2){
        double[] pdd=new double[stepNum];
        for(int i=0;i<pdd.length;i++){
            pdd[i]=pd1[i]-pd2[i];
        }
        
        
        ArrayList<Integer> cs=crossPointsOf(pdd);
        int size=cs.size();
        if(size==2) 
            return null;
        int max=0;
        if(cs.get(1)>0){
            for(int i=1;i<size-1;i+=2){
                int d=Math.abs(cs.get(i+1))-Math.abs(cs.get(i));
                if(max<d){
                    max=d;
                }
            }
        }
        else{
            for(int i=0;i<size-1;i+=2){
                int d=Math.abs(cs.get(i+1))-Math.abs(cs.get(i));
                if(max<d){
                    max=d;
                }
            }
        }
        //if the maximal fragment < 50bp, discard the triplet
        if(max<50) 
            return null;
        
        
        return Loess.smooth(x,pdd);
    }
    
//    private ArrayList<Integer> crossPointsOf(double[] pdd){
//        ArrayList<Integer> cps=new ArrayList<Integer>(); 
//        cps.add(0);
//        for(int i=1;i<pdd.length-1;i++){
//            //rec is close to group 1
//            if(pdd[i-1]<0 && pdd[i]>=0 && pdd[i+1]>0) {
//                cps.add(i);
//            }
//            //rec is close to group 2
//            else if(pdd[i-1]>0 && pdd[i]<=0 && pdd[i+1]<0) {
//                cps.add(-i);
//            }
//            
//        }
//        cps.add(cps.get(cps.size()-1)<0?pdd.length:(-pdd.length));
//        
//        return cps;
//    }
    
    
    private ArrayList<Integer> crossPointsOf(double[] pdd){
        ArrayList<Integer> cps=new ArrayList<Integer>(); 
        cps.add(0);
        int sign=0;
        for(int i=0;i<pdd.length;i++){
            if(pdd[i]>0 && sign>=0) sign=i;
            else if(pdd[i]<0 && sign<=0) sign=-i;
            else if(pdd[i]>0 && sign<0){
                cps.add(i);
                sign=i;                
            }
            else if(pdd[i]<0 && sign>0){
                cps.add(-sign);
                sign=-i;
            }           
        }
        cps.add(cps.get(cps.size()-1)<0?pdd.length:(-pdd.length));
        return cps;
    }
    
//    private double pairwiseDistanceOf(int a, int b){
//        return Description.mean(pdMatrix[a][b]);
//    }
    
    private double pairwiseDistanceOf(double[] pd){
        return Description.mean(pd);
    }
////////////////////////////////////////////////////////////////////////////////////////////////
    private double pdOf(int s1, int s2, int start, int end){
        nf.count(s1, s2, start, end);
        double d=NucleotidePairwiseDistance.getDistance(model, nf, true);
        return d;
    }

    private void fillPdArray(double[] pd, int a, int b){
        for(int i=0;i<pd.length;i++){
            int start=step*i;
            int end=start+window;
            pd[i]=pdOf(a,b,start,end);
        }
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    /**
     * @return the results
     */
    public LinkedList<Recombinant> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(LinkedList<Recombinant> results) {
        this.results = results;
    }

    /**
     * @return the times
     */
    public int getTimes() {
        return times;
    }

    /**
     * @param times the times to set
     */
    public void setTimes(int times) {
        this.times = times;
    }

    /**
     * @return the loessSpan
     */
    public int getLoessSpan() {
        return loessSpan;
    }

    /**
     * @param loessSpan the loessSpan to set
     */
    public void setLoessSpan(int loessSpan) {
        this.loessSpan = loessSpan;
    }

    /**
     * @return the areaCutoff
     */
    public double getAreaCutoff() {
        return areaCutoff;
    }

    /**
     * @param areaCutoff the areaCutoff to set
     */
    public void setAreaCutoff(double areaCutoff) {
        this.areaCutoff = areaCutoff;
    }

    /**
     * @return the bonferroniCorrection
     */
    public boolean isBonferroniCorrection() {
        return bonferroniCorrection;
    }

    /**
     * @param bonferroniCorrection the bonferroniCorrection to set
     */
    public void setBonferroniCorrection(boolean bonferroniCorrection) {
        this.bonferroniCorrection = bonferroniCorrection;
    }
    
}
