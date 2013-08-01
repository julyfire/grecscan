/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2;

import grecscan2.core.Pipeline;
import grecscan2.gui.MainFrame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.UIManager;

/**
 *
 * @author wb
 */
public class GRecScan2 {
    
    private double version=3.0;
    private boolean cmd=false;
    private Pipeline pl;
    private MainFrame mf;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        GRecScan2 gs=new GRecScan2();
        gs.pl=new Pipeline();
        
        
        gs.readArguments(args);
        
        if(gs.cmd){   
            gs.printLogo();
//            gs.pl.loadAlignment();
//            gs.pl.findRecFromAlignment();
            new RunInCMD(gs.pl).startDetection();

            return;
        }
        
        gs.mf=new MainFrame();
        gs.mf.init();
        
        if(gs.pl!=null){
            gs.mf.addPipeline(gs.pl);
        }
        
        
    }
    
    private void printLogo(){
        System.out.print(
		 "\n    =======================================================================\n"
                + "    |         GRecScan "+version+" --A recombination detection program            |\n"
                + "    =======================================================================\n"
                + "    Please report bugs to helloweibo@gmail.com\n\n");
    }
    
    private void exit_with_help(){
        printLogo();
        System.out.print(		 
                "Usage: java -jar GRecScan2 [options] input_file [output_directory]\n"
		+"options:\n"
                +"\t-w window:       window size when sliding scan, default 20\n"
                +"\t-s smooth:       smooth span size, defalut 300\n"
                +"\t-a area:         area cutoff when refine breakpoints, defalut 0.01\n"
                +"\t-t thread:       number of thread when scan, default 3\n"
                +"\t-p permutation:  permutation times when statistical test, default 1000\n"
                +"\t-c correction:   no Bonferroni correction when statistical test\n"
                +"\t-C cmd:          run as commond line mode\n"
                +"\t-h help\n\n"+
                 "input_file:        the input file, should be fasta format at this version\n"
                +"output_directory:  the output directory, default is current directory\n\n"
	);
	System.exit(0);
    }

    private void exit_with_error(){
        System.out.println("use -h to see detail help");
        System.exit(1);
    }

    private void readArguments(String[] argv){
        int i;
        if(argv.length==0) {
            pl=null;
            return;
        }

        for(i=0;i<argv.length;i++){
            
            if (argv[i].charAt(0) != '-')	break;
            if(++i>=argv.length && !(argv[i-1].equals("-c") || argv[i-1].equals("-h"))){
                System.out.println("arguments error!");
                exit_with_error();
            }
            switch(argv[i-1].charAt(1)){
                case 'w': pl.setWindow(Integer.parseInt(argv[i])); break;
                case 's': pl.setLoessSpan(Integer.parseInt(argv[i])); break;
                case 'a': pl.setAreaCutoff(Double.parseDouble(argv[i])); break;
                case 't': pl.setThreadNum(Integer.parseInt(argv[i])); break;
                case 'p': pl.setPermutationTimes(Integer.parseInt(argv[i])); break;
                case 'c': pl.setBonferroniCorrection(false); break;    
                case 'C': cmd=true; i--; break;
                case 'h': exit_with_help();
		default:
			System.err.println("unknown option");
			exit_with_error();
            }
	}

        if(pl.getWindow()<0){
            System.err.println("window should not be less than 0");
            exit_with_error();
        }
        if(pl.getLoessSpan()<0){
            System.err.println("loess smooth span size should not be less than 0");
            exit_with_error();
        }
        if(pl.getAreaCutoff()>1 || pl.getAreaCutoff()<0){
            System.err.println("area cutoff should be in 0~1");
            exit_with_error();
        }
        

        // determine filenames, at least two filenames
        if(i>=argv.length){
            System.err.println("lack input file!");
            exit_with_error();
        }
        pl.setInfile(new File(argv[i]));
        if(i<argv.length-1)
            pl.setDir(new File(argv[i+1]));
    }

    
    
}
