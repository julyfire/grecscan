/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import grecscan2.gui.aln.PDistance;
import grecscan2.io.AlignIO;
import grecscan2.io.XMLRecIO;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.jdom.Document;
import org.jdom.Element;
import test.RecFinder2;
import test.RecFinder3;
import test.SizeOf;
import test.SizeOfObject;
import wbitoolkit.phylo.BootstrapTree;
import wbitoolkit.phylo.NJTree;
import wbitoolkit.phylo.Tree;
import wbitoolkit.phylo.TreeIO;
import wbitoolkit.phylo.TreePartition;
import wbitoolkit.statistic.Bootstrap;
import wbitoolkit.tools.UsefulMethods;

/**
 *
 * @author wb
 */
public class Pipeline{

    private File infile;
    private String[] titles;
    private Alignment aln;
    private Tree tree;
    private List groups;
    private List clusters;
    private Mfe m;
    private int window=20;
    private int step=1;
    private int start;
    private int stop;
    private double[][] mfes;
    private File dir;
    private List selectedSeqsIndex;
    private RecDetection rf;
    private List<Recombinant> recs;
    private int progress;
    private int detailProgress;
    private JobStatus jobStatus;
    private int threadNum=3;
    private int times=1000;
    private int loessSpan=300;
    private double areaCutoff=0.01;
    private boolean bonferroniCorrection=true;
    private double groupCutoff=0.25;
    private int treeMethod=0;
    private int treeModel=0;
    private int bootstrap=100;

    public Pipeline(){
        
    }
    
    //step 1
    public void loadAlignment() throws FileNotFoundException, IOException{
        AlignIO aio=new AlignIO();
        aio.read(infile);
        aln=aio.getAlignment();
        titles=aln.getNames();
        updateSelectedSeqsIndex();
        System.out.println("loaded alignment "+infile.getPath());
    }
    
    //cmd pipeline
    public void findRecFromAlignment() throws FileNotFoundException, InterruptedException, IOException{
        jobStatus=new JobStatus();
        selectAllSeqs();
        updateSelectedSeqsIndex();
        System.out.println("detecting...");
        printProgress();
        calMfeMatrix();
        scanRecombinant();
        System.out.println("finished!");
        summary();
//        reportResults();
        XMLRecIO xio=new XMLRecIO(this);       
        File file=new File(UsefulMethods.getBaseFileName(infile.getAbsolutePath())+".xml");
//        xio.saveXML(xio.xmlResults(),file);
    }
    
    private void printProgress(){
        new Thread(){
            public void run(){
                System.out.println(progress);
            }
        }.start();
    }
    
    public void buildTree(){
//        setTree(buildTree(aln.getGapFreeIndex()));
        setTree(bootstrapTree());
    }
    
    public void partitionTree(){
        TreePartition tp=new TreePartition(tree);
        tp.setCutoff(groupCutoff);
        tp.grouping();
        setGroups(tp.getGroups());
        setClusters(tp.getClusters());
    }
    
    
    //step 2
    public void calMfeMatrix() throws InterruptedException{
        progress=0;
        m=new Mfe(this);
        m.setAlignment(aln);
        m.setWindow(window);
        m.setStep(step);
        m.setThreadNum(threadNum);
        m.setJobStatus(jobStatus);
        
//        long start,end,time;
//        start = System.currentTimeMillis();        
        m.calMfe();
//        end = System.currentTimeMillis();
//        time = end - start;
//        System.out.println(time/1000.0);
        
        mfes=m.getMfes();
        progress=100;
    }
    
    public void scanRecombinant(){
        progress=0;
        setRecDetectionObject(new RecDetection(this));
        getRecDetectionObject().setAreaCutoff(areaCutoff);
        getRecDetectionObject().setLoessSpan(loessSpan);
        getRecDetectionObject().setBonferroniCorrection(bonferroniCorrection);
        getRecDetectionObject().setThreadNum(threadNum);
        getRecDetectionObject().setTimes(times);
        getRecDetectionObject().setJobStatus(jobStatus);
        getRecDetectionObject().scan();
        recs=getRecDetectionObject().getResults();
        progress=100;
    }
    
    public void scanRecombinant(int i){
        progress=0;
        setRecDetectionObject(new RecDetection(this));
        getRecDetectionObject().setAreaCutoff(areaCutoff);
        getRecDetectionObject().setLoessSpan(loessSpan);
        getRecDetectionObject().setBonferroniCorrection(bonferroniCorrection);
        getRecDetectionObject().setThreadNum(threadNum);
        getRecDetectionObject().setTimes(times);
        getRecDetectionObject().setJobStatus(jobStatus);
        getRecDetectionObject().oneSeqScan(i);
        recs=getRecDetectionObject().getResults();
        progress=100;
    }
    
    
    
    public void summary(){
        int[] pos=aln.getGapFreeIndex();
        for(Recombinant r:recs){
            r.setMajorSeq(aln.getSeqById(r.getMajor()));
            r.setMinorSeq(aln.getSeqById(r.getMinor()));
            r.setRecSeq(aln.getSeqById(r.getRec()));
            int n=r.getBlocks().size();
            ArrayList<int[]> full=new ArrayList<int[]>(n);
            for(int[] b:r.getBlocks()){
                full.add(new int[]{pos[b[0]]+1,pos[b[1]]+1});
                b[0]+=1;b[1]+=1;
            }
            if(r.getBlocks().get(n-1)[1]==r.getBreakpoints().get(r.getBreakpoints().size()-1)){
                full.get(n-1)[1]=aln.getLength();
            }
            r.setFullBlock(full);
            double[] site=new double[r.getPdd().length];
            for(int i=0;i<site.length;i++){
                site[i]=i+window/2;
            }
            r.setPos(site);
            int[][] colI=partitionAlignmentByRec(r);
            PDistance pd1=new PDistance(colI[0]);
            PDistance pd2=new PDistance(colI[1]);
            r.setPdisToMajor(pd1.pDistanceOf(r.getMajorSeq(), r.getRecSeq()));
            r.setPdisToMinor(pd2.pDistanceOf(r.getMinorSeq(), r.getRecSeq()));
            
        }
    }
    
    public int getGroupIdBySeq(Sequence seq){
        List<String[]> gs=groups;
        int id=-1,i=0;
        String seqName=seq.getName();
        for(String[] group:gs){
            for(String s:group){
                if(s.equals(seqName)){
                    id=i;
                    break;
                }
            }
            i++;
        }
        return id;
    }
    
    
    private Set getRecFragmentAlignSeqList(Recombinant rec){
        int g1i=getGroupIdBySeq(rec.getMajorSeq());
        int g2i=getGroupIdBySeq(rec.getMinorSeq());
        int greci=getGroupIdBySeq(rec.getRecSeq());
        
        Set seqSet=new HashSet();
        if(g1i!=-1){
            String[] g1=(String[])(groups.get(g1i));
            for(String g:g1){
                seqSet.add(aln.getSeqByName(g).getId());
            }
        }
        if(g2i!=-1){
            String[] g2=(String[])(groups.get(g2i));
            for(String g:g2){
                seqSet.add(aln.getSeqByName(g).getId());
            }
        }
        if(greci!=-1){
            String[] grec=(String[])(groups.get(greci));
            for(String g:grec){
                seqSet.add(aln.getSeqByName(g).getId());
            }    
        }
        return seqSet;
    }

    
    private int[][] partitionAlignmentByRec(Recombinant rec){
        int[] cs=aln.getGapFreeIndex();
        int[] s1i;//fragment close to major
        int[] s2i;//fragment close to minor
        ArrayList s1=new ArrayList(); 
        ArrayList s2=new ArrayList(); 
        List<Integer> bp=rec.getBreakpoints();
        for(int i=0;i<bp.size()-1;i++){
            if(i%2==0){
                for(int j=bp.get(i);j<bp.get(i+1);j++){
                    s1.add(cs[j]);
                }
            }
            else{
                for(int j=bp.get(i);j<bp.get(i+1);j++){
                    s2.add(cs[j]);
                }
            }
                
        }
        if(rec.getBlocks().get(0)[0]==1){
            s1i=new int[s2.size()];
            for(int i=0;i<s1i.length;i++){
                s1i[i]=(Integer)s2.get(i);
            }
            s2i=new int[s1.size()];
            for(int i=0;i<s2i.length;i++){
                s2i[i]=(Integer)s1.get(i);
            }
        }
        else{
            s1i=new int[s1.size()];
            for(int i=0;i<s1i.length;i++){
                s1i[i]=(Integer)s1.get(i);
            }
            s2i=new int[s2.size()];
            for(int i=0;i<s2i.length;i++){
                s2i[i]=(Integer)s2.get(i);
            }
        }
        return new int[][]{s1i,s2i};
    }
    

    
    public void makeRecTree(Recombinant rec){
        int[][] colI=partitionAlignmentByRec(rec);
        
        Tree t1=buildTree(colI[0]);
        Tree t2=buildTree(colI[1]);
        TreeIO tio=new TreeIO();
        rec.setMajorTree(tio.writeTree(t1));
        rec.setMinorTree(tio.writeTree(t2));
        
    }
    
    public void makeRecAlignment(Recombinant rec){
        int[][] colI=partitionAlignmentByRec(rec);
        int a=rec.getMajor();
        int b=rec.getMinor();
        int r=rec.getRec();
        Set<Integer> seqs;
        if(groups==null){
            seqs=new HashSet();
            seqs.add(a);
            seqs.add(b);
            seqs.add(r);
        }
        else{
            seqs=getRecFragmentAlignSeqList(rec);
        }
        
        HashMap up=new HashMap();
        HashMap down=new HashMap();
        ArrayList<Integer> mid=new ArrayList();
        ArrayList<Integer> left=new ArrayList();
        PDistance pd1=new PDistance(colI[0]);
        PDistance pd2=new PDistance(colI[1]);
        for(Integer s:seqs){
            if(s==a || s==b || s==r) continue;
            double d1ai=pd1.pDistanceOf(aln.getSeqById(s), aln.getSeqById(a));
            double d1bi=pd1.pDistanceOf(aln.getSeqById(s), aln.getSeqById(b));
            double d1ri=pd1.pDistanceOf(aln.getSeqById(s), aln.getSeqById(r));
            double d2ai=pd2.pDistanceOf(aln.getSeqById(s), aln.getSeqById(a));
            double d2bi=pd2.pDistanceOf(aln.getSeqById(s), aln.getSeqById(b));
            double d2ri=pd2.pDistanceOf(aln.getSeqById(s), aln.getSeqById(r)); 
            if(d1ai<d1bi && d2ai<d2bi){
                up.put(s,d1ri);
            }
            else if(d1ai>=d1bi && d2ai>=d2bi){
                down.put(s, d2ri);
            }
            else{
                if(d1ri<d1ai && d2ri<d2bi){
                    mid.add(s);
                }
                else{
                    left.add(s);
                }
            }
        }
        for(int i=0;i<aln.getNoOfSeq();i++){
            if(seqs.contains(i)) continue;
            left.add(i);
        }
        List<Map.Entry> l1=UsefulMethods.sortMap(up, false, false);
        List<Map.Entry> l2=UsefulMethods.sortMap(down, false, true);
        int[] index=new int[aln.getNoOfSeq()];
        int i=0;
        for(Map.Entry en:l1){
            index[i++]=(Integer)en.getKey();
        }
        index[i++]=a;
        index[i++]=r;
        index[i++]=b;
        for(Map.Entry en:l2){
            index[i++]=(Integer)en.getKey();
        }        
        rec.setAlnStart(i);
        for(Integer m:mid){
            index[i++]=m;
        }
        rec.setAlnEnd(i-1);
        for(Integer ls:left){
            index[i++]=ls;
        }
        rec.setAlnIndex(index);
    }
    
    
    public void reportResults() throws IOException{
         //print results
        summary();
        
        File out=new File(infile.getAbsolutePath().replaceAll("[.][^.]+$", ".out"));
        BufferedWriter outfile=new BufferedWriter(new FileWriter(out));
        
        for(Recombinant r:recs){
            
            outfile.write("============================\n");
            outfile.write("Recombination: "+r.getRecSeq().getName()+"\n");
            outfile.write("Major:         "+r.getMajorSeq().getName()+"\n");
            outfile.write("Minor:         "+r.getMinorSeq().getName()+"\n\n");
            outfile.write("recombination fragments: \n");
            for(int i=0;i<r.getBlocks().size();i++){
                outfile.write(r.getBlocks().get(i)[0]+"-"+r.getBlocks().get(i)[1]+"\t(");
                outfile.write(r.getFullBlock().get(i)[0]+"-"+r.getFullBlock().get(i)[1]+" in alignment)\t");
                outfile.write("P-value:"+r.getPv().get(i)[0]+","+r.getPv().get(i)[1]+"\n");
            }
            double[] pdd=r.getPdd();
            outfile.write("\npos\tpdd\n");
            for(int i=0;i<pdd.length;i++){
                outfile.write((i+window/2)+"\t"+pdd[i]+"\n");
            }
            
        }
        
        outfile.close();
        
        System.out.println("See results in "+out.getPath());
    }
    
    private Tree buildTree(int[] sitesIndex){
        double[][] dm=new PDistance(aln,sitesIndex).getMatrix();
        
//        for(int i=0;i<dm.length;i++){
//            for(int j=i+1;j<dm.length;j++)
//                System.out.print(dm[i][j]+"\t");
//            System.out.println();
//        }
        
        NJTree nt=new NJTree(dm,titles);
        nt.buildTree();
        Tree t=nt.getTree();
        t.setRootByMidpoint();
        return t;
    }
    
    private Tree buildTree(int[] seqsIndex, int[] sitesIndex){

        double[][] dm=new PDistance(aln,seqsIndex,sitesIndex).getMatrix();
        String[] leaftitle=new String[seqsIndex.length];
        for(int i=0;i<leaftitle.length;i++){
            leaftitle[i]=titles[seqsIndex[i]];
        }
        NJTree nt=new NJTree(dm,leaftitle);
        nt.buildTree();
        Tree t=nt.getTree();
        t.setRootByMidpoint();
        return t;
    }
    
    private Tree bootstrapTree(){
        int[] col=aln.getGapFreeIndex();
        Integer[] data=new Integer[col.length];
        for(int i=0;i<col.length;i++){
            data[i]=col[i];
        }
        bt.setData(data);
        bt.setTimes(bootstrap);
        bt.setLabels(titles);
        bt.test(Bootstrap.H1.NOT_EQUAL);
        double[] sp=bt.getSp();
        Tree tree=((BootstrapTree)bt).getBootstrapTree();
        tree.setRootByMidpoint();
        return tree;
    }
    
    public BootstrapTree bt=new BootstrapTree(){

        @Override
        public double[][] getMatrix(Object[] sample) {
            int[] colIndex=new int[sample.length];
            for(int i=0;i<sample.length;i++){
                colIndex[i]=(Integer)(sample[i]);
            }

            double[][] m=new PDistance(aln,colIndex).getMatrix();

            return m;
        }
        
    };
    
    

    /**
     * @return the infile
     */
    public File getInfile() {
        return infile;
    }

    /**
     * @param infile the infile to set
     */
    public void setInfile(File infile) {
        this.infile = infile;
    }

    /**
     * @return the titles
     */
    public String[] getTitles() {
        return titles;
    }

    /**
     * @param titles the titles to set
     */
    public void setTitles(String[] titles) {
        this.titles = titles;
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
     * @return the mfes
     */
    public double[][] getMfes() {
        return mfes;
    }

    /**
     * @param mfes the mfes to set
     */
    public void setMfes(double[][] mfes) {
        this.mfes = mfes;
    }

    /**
     * @return the dir
     */
    public File getDir() {
        return dir;
    }

    /**
     * @param dir the dir to set
     */
    public void setDir(File dir) {
        this.dir = dir;
    }

    /**
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * @param progress the progress to set
     */
    public void setProgress(int progress) {
        this.progress = progress;
    }

    /**
     * @return the m
     */
    public Mfe getMfeObject() {
        return m;
    }

    /**
     * @param m the m to set
     */
    public void setMfeObject(Mfe m) {
        this.m = m;
    }

    /**
     * @return the recs
     */
    public List<Recombinant> getRecs() {
        return recs;
    }

    /**
     * @param recs the recs to set
     */
    public void setRecs(List<Recombinant> recs) {
        this.recs = recs;
    }

    /**
     * @return the aln
     */
    public Alignment getAln() {
        return aln;
    }

    /**
     * @param aln the aln to set
     */
    public void setAln(Alignment aln) {
        this.aln = aln;
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
     * @return the times
     */
    public int getPermutationTimes() {
        return times;
    }

    /**
     * @param times the times to set
     */
    public void setPermutationTimes(int times) {
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

    /**
     * @return the tree
     */
    public Tree getTree() {
        return tree;
    }

    /**
     * @param tree the tree to set
     */
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    /**
     * @return the detailProgress
     */
    public int getDetailProgress() {
        return detailProgress;
    }

    /**
     * @param detailProgress the detailProgress to set
     */
    public void setDetailProgress(int detailProgress) {
        this.detailProgress = detailProgress;
    }

    /**
     * @return the groups
     */
    public List getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List groups) {
        this.groups = groups;
        List<String[]> gs=groups;
        int i=0;
        for(String[] g:gs){
            for(String s:g){
                aln.getSeqByName(s).setGroup(i);
            }
            i++;
        }
    }


    /**
     * @return the clusters
     */
    public List getClusters() {
        return clusters;
    }

    /**
     * @param clusters the clusters to set
     */
    public void setClusters(List clusters) {
        this.clusters = clusters;
    }

    /**
     * @return the rf
     */
    public RecDetection getRecDetectionObject() {
        return rf;
    }

    /**
     * @param rf the rf to set
     */
    public void setRecDetectionObject(RecDetection rf) {
        this.rf = rf;
    }

    /**
     * @return the selectedSeqsIndex
     */
    public List getSelectedSeqsIndex() {
        return selectedSeqsIndex;
    }

    /**
     * @param selectedSeqsIndex the selectedSeqsIndex to set
     */
    public void setSelectedSeqsIndex(List selectedSeqsIndex) {
        this.selectedSeqsIndex = selectedSeqsIndex;
    }

    public void updateSelectedSeqsIndex(){
        selectedSeqsIndex=new LinkedList();
        for(int i=0;i<aln.getHeight();i++){
            if(aln.getSeqById(i).isSelected()){
                selectedSeqsIndex.add(i);
            }  
        }
    }
    
    public void unsetSelectedSeqs(){
        for(int i=0;i<aln.getHeight();i++){
            aln.getSeqById(i).setSelected(false);
        }
    }
    
    public void selectAllSeqs(){
        for(int i=0;i<aln.getHeight();i++){
            aln.getSeqById(i).setSelected(true);
        }
    }

    /**
     * @return the groupCutoff
     */
    public double getGroupCutoff() {
        return groupCutoff;
    }

    /**
     * @param groupCutoff the groupCutoff to set
     */
    public void setGroupCutoff(double groupCutoff) {
        this.groupCutoff = groupCutoff;
    }

    /**
     * @return the treeMethod
     */
    public int getTreeMethod() {
        return treeMethod;
    }

    /**
     * @param treeMethod the treeMethod to set
     */
    public void setTreeMethod(int treeMethod) {
        this.treeMethod = treeMethod;
    }

    /**
     * @return the treeModel
     */
    public int getTreeModel() {
        return treeModel;
    }

    /**
     * @param treeModel the treeModel to set
     */
    public void setTreeModel(int treeModel) {
        this.treeModel = treeModel;
    }

    /**
     * @return the bootstrap
     */
    public int getBootstrap() {
        return bootstrap;
    }

    /**
     * @param bootstrap the bootstrap to set
     */
    public void setBootstrap(int bootstrap) {
        this.bootstrap = bootstrap;
    }

  
}
