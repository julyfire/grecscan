/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import wbitoolkit.rna.Fold;

/**
 *
 * @author wb
 */
public class Mfe {
    
    private int window=20;
    private int step=1;
    private int start=0;
    private int stop=0;
    private int stepNum;
    private int threadNum=3; //seems RNAfold does not support multithread
    private int unit;
    private double[][] mfes;
    private UniFold uf;
    private Alignment aln;
    private int amount;
    private AtomicInteger progress;
    private Pipeline pl;
    private Thread[] ts;
    private JobStatus jobStatus;
//    private boolean[] mfeThreadStatus = {false, false}; //{pause, stop}
    
    public Mfe(){
        
    }
    
    public Mfe(Pipeline pl){
        this.pl=pl;
    }
    
    private void init(){
        progress=new AtomicInteger(0);
        if(start==0) start=1;
        if(stop==0) stop=aln.getGapFreeIndex().length;
        stepNum=(stop-start+1-window)/step+1;
        mfes=new double[stepNum][aln.getHeight()];
        amount=stepNum*aln.getHeight();
        ts=new Thread[threadNum];
        unit=stepNum/threadNum;
        if(unit<1){
            unit=1;
            threadNum=1;
        }
        UniFold.initFold(window);
    }
    
    public double[][] getMfes(){
        return mfes;
    }
    
    public void calMfe() throws InterruptedException{
//        long start,end,time;  
//        start = System.currentTimeMillis();
        
        init();
        
        CountDownLatch threadSignal = new CountDownLatch(threadNum);
        int i;
        for(i=0;i<threadNum-1;i++){
            int b=i*unit;
            int e=(i+1)*unit-1;
            ts[i]=new Thread(new CalMfe(b,e,threadSignal));
            ts[i].start();
        }
        ts[i]=new Thread(new CalMfe(i*unit,stepNum-1,threadSignal));
        ts[i].start();
        threadSignal.await();
        
//        end = System.currentTimeMillis();
//        time = end - start;
//        System.out.println(time);
    }

    /**
     * @return the window
     */
    public int getWindow() {
        return window;
    }

    /**
     * @param window the window to set
     */
    public void setWindow(int window) {
        this.window = window;
    }

    /**
     * @return the step
     */
    public int getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(int step) {
        this.step = step;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the stop
     */
    public int getStop() {
        return stop;
    }

    /**
     * @param stop the stop to set
     */
    public void setStop(int stop) {
        this.stop = stop;
    }

    /**
     * @return the threadNum
     */
    public int getThreadNum() {
        return threadNum;
    }

    /**
     * @param threadNum the threadNum to set
     */
    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }


    /**
     * @param seqs the seqs to set
     */
    public void setAlignment(Alignment aln) {
        this.aln = aln;
    }


    /**
     * @return the jobStatus
     */
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * @param jobStatus the jobStatus to set
     */
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
    
    class CalMfe implements Runnable{
        int startStep;
        int stopStep;
        UniFold uf;
        CountDownLatch cdl;
        public CalMfe(int start, int stop, CountDownLatch cdl){
            startStep=start;
            stopStep=stop;
            this.cdl=cdl;
            uf=new UniFold(window);
        }
        @Override
        public void run() {
            for(int i=0;i<aln.getNoOfSeq();i++){
                String seq=aln.getGapFreeSeq(aln.getSeqById(i));
                int s=start-1+step*startStep;
                for(int j=startStep;j<=stopStep;j++){
                    
                    synchronized (jobStatus) {
                        if(jobStatus.pause){
                            try {
                                jobStatus.wait();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        if(jobStatus.stop){
                            cdl.countDown();
                            System.out.println("mfe thread stop");
                            return;
                        }
                    }
                    
                    String subSeq=subSeq=seq.substring(s, s+window);

//                    double mfe=new MfeFold().cal(subSeq);
//                    double mfe=new Fold().fold(subSeq);
                    double mfe=uf.fold(subSeq);
                    mfes[j][i]=mfe;
                    s++;
                    
                    progress.incrementAndGet();
                    pl.setProgress(progress.get()*100/amount);
                }
            }
            cdl.countDown();
        }
        
    }
    
    
}
