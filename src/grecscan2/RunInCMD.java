/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2;

import grecscan2.core.JobStatus;
import grecscan2.core.Pipeline;
import grecscan2.core.Recombinant;
import grecscan2.io.XMLRecIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import wbitoolkit.tools.UsefulMethods;

/**
 *
 * @author wb
 */
public class RunInCMD {
    private Pipeline pl;
    private JobStatus jobStatus;
    private Thread mfeThread;
    private Thread checkMfeThread;
    private Thread resetThread;
    private Thread checkRecThread;
    private Thread recThread;
    
    public RunInCMD(Pipeline pl){
        this.pl=pl;
    }
    
    public void startDetection() throws FileNotFoundException, IOException{
        pl.loadAlignment();
        pl.selectAllSeqs();
        pl.updateSelectedSeqsIndex();
        new Thread(){
            @Override
            public void run(){
                long start,end,time;  
                start = System.currentTimeMillis();
                startJob();
                try {
                    recThread.join();
                } catch (InterruptedException ex) {
                    
                }
                end = System.currentTimeMillis();
                time = end - start;
                System.out.println("Finishied detection. (total time: "+(time/1000.0)+" seconds)");
            }
        }.start();
    }
    
    public void startJob(){
        
        jobStatus=new JobStatus();
        pl.setJobStatus(jobStatus);
        
        mfeThread=new Thread(){
                @Override
                public void run(){
                    
                    if(pl.getMfeObject()==null){
                        try {
                            pl.calMfeMatrix();
                        } catch (Exception ex) {
                            System.err.println("MFE calculation threads interrupted exception!");
                        }
                    }     
                }
        };
        mfeThread.start(); 
        
        checkMfeThread=new Thread(){
            @Override
            public void run(){
                System.out.print("calculating MFE...");
                while(pl.getProgress()<100){
                    System.out.print(pl.getProgress()+"%"+UsefulMethods.backspace(pl.getProgress()+"%"));
                }
                pl.setProgress(100);
                System.out.println(pl.getProgress()+"%"+UsefulMethods.backspace(pl.getProgress()+"%"));
            }
        };
        checkMfeThread.start();
        
        resetThread=new Thread(){
            @Override
            public void run(){
                try {
                    mfeThread.join();
                    checkMfeThread.join();
                } catch (InterruptedException ex) {
                    
                }
                System.out.println("MFE calculation finished!");
                System.out.print("Scan recombinants...");
                pl.setProgress(0);
            }
        };
        resetThread.start();
        
        checkRecThread=new Thread(){
            @Override
            public void run(){
                try {
                    resetThread.join();
                } catch (InterruptedException ex) {
                    
                }
                
                while(pl.getProgress()<100){                                  
                    System.out.print(pl.getProgress()+"%"+UsefulMethods.backspace(pl.getProgress()+"%"));
//                    System.out.println("Scan recombinants..."+pl.getDetailProgress()+"/"+pl.getAln().getNoOfSeq());
                }
                pl.setProgress(100);
                System.out.println(pl.getProgress()+"%"+UsefulMethods.backspace(pl.getProgress()+"%"));
            }
        };
        checkRecThread.start();
        
        recThread=new Thread(){
            @Override
            public void run(){
                
                try {
                    resetThread.join();
                } catch (InterruptedException ex) {
                    
                }
                
                pl.scanRecombinant();
                try {
                    checkRecThread.join();
                } catch (InterruptedException ex) {
                    
                }
                List<Recombinant> results=pl.getRecs();
                if(results.size()>0){
                    pl.summary();
                }
                else{
                    System.out.println("No recombination event found!");                                     
                }
                XMLRecIO xio=new XMLRecIO(pl);                     
                File file=new File(UsefulMethods.getBaseFileName(pl.getInfile().getAbsolutePath())+".xml");
//                xio.saveXML(xio.xmlResults(),file);
                xio.saveXML(file);
            }
        };
        recThread.start();      
    }
}
