/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.io;

import grecscan2.core.Pipeline;
import grecscan2.core.Recombinant;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
//import org.jdom.Attribute;
//import org.jdom.Document;
//import org.jdom.Element;
//import org.jdom.JDOMException;
//import org.jdom.input.SAXBuilder;
//import org.jdom.output.XMLOutputter;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import wbitoolkit.phylo.TreeIO;

/**
 *
 * @author wb
 */
public class XMLRecIO {
    
    private Pipeline pl;
    
    public XMLRecIO(Pipeline pl){
        this.pl=pl;
    }
    
//    public Document xmlResults(){
//        Element root = new Element("GRecScan");
//        Document doc = new Document(root);
//        
//        Element alignment = new Element("Alignment");
//        alignment.addContent(pl.getInfile().getAbsolutePath());
//        Attribute width=new Attribute("width",pl.getAln().getWidth()+"");
//        Attribute height=new Attribute("height",pl.getAln().getHeight()+"");
//        alignment.addAttribute(width);
//        alignment.addAttribute(height);
//        root.addContent(alignment);
//        
//        Element tree=new Element("Tree");
//        if(pl.getTree()!=null){
//            tree.addContent(new TreeIO().writeTree(pl.getTree()));
//        }
//        root.addContent(tree);
//        
//        Element groups=new Element("Groups");
//        if(pl.getGroups()!=null){
//        List<String[]> gs=pl.getGroups();
//        for(String[] g:gs){
//            Element group=new Element("Group");
//            for(String e:g){
//                Element seq=new Element("Sequence");
//                seq.addContent(e);
//                group.addContent(seq);
//            }
//            groups.addContent(group);
//        }
//        }
//        root.addContent(groups);
//        
////        Element parameters=new Element("Parameters");
////        Element mfeWindow=new Element("MfeWindow");
////        mfeWindow.addContent(pl.getWindow()+"");
////        parameters.addContent(mfeWindow);
////        Element smoothSpan=new Element("SmoothSpan");
////        smoothSpan.addContent(pl.getLoessSpan()+"");
////        parameters.addContent(smoothSpan);
////        Element areaCutoff=new Element("AreaCutoff");
////        areaCutoff.addContent(pl.getAreaCutoff()+"");
////        parameters.addContent(areaCutoff);
////        Element permutation=new Element("PermutationTimes");
////        permutation.addContent(pl.getPermutationTimes()+"");
////        parameters.addContent(permutation);
////        root.addContent(parameters);
//        
//        Element recombinants=new Element("Recombinants");
//        if(pl.getRecs()!=null){
//        List<Recombinant> recs=pl.getRecs();
//        for(Recombinant rec:recs){
//            Element recombinant=new Element("Recombinant");
//            Element recSeq=new Element("RecSeq");
//            recSeq.addAttribute(new Attribute("id",rec.getRec()+""));
//            recSeq.addContent(rec.getRecSeq().getName());
//            recombinant.addContent(recSeq);
//            Element majorSeq=new Element("MajorSeq");
//            majorSeq.addAttribute(new Attribute("id",rec.getMajor()+""));
//            majorSeq.addContent(rec.getMajorSeq().getName());
//            recombinant.addContent(majorSeq);
//            Element minorSeq=new Element("MinorSeq");
//            minorSeq.addAttribute(new Attribute("id",rec.getMinor()+""));
//            minorSeq.addContent(rec.getMinorSeq().getName());
//            recombinant.addContent(minorSeq);
//            Element majorSim=new Element("MajorSimilarity");
//            majorSim.addContent(rec.getPdisToMajor()+"");
//            recombinant.addContent(majorSim);
//            Element minorSim=new Element("MinorSimilarity");
//            minorSim.addContent(rec.getPdisToMinor()+"");
//            recombinant.addContent(minorSim);
//            Element majorTree=new Element("MajorTree");
//            majorTree.addContent(rec.getMajorTree());
//            recombinant.addContent(majorTree);
//            Element minorTree=new Element("MinorTree");
//            minorTree.addContent(rec.getMinorTree());
//            recombinant.addContent(minorTree);
//            Element breakpoints=new Element("Breakpoints");
//            List<Integer> bps=rec.getBreakpoints();
//            int n=bps.size();
//            for(int i=0;i<n-1;i++){
//                Element breakpoint=new Element("Breakpoint");
//                breakpoint.addContent((bps.get(i)+1)+"");
//                breakpoints.addContent(breakpoint);
//            }
//            Element breakpoint=new Element("Breakpoint");
//            breakpoint.addContent(bps.get(n-1)+"");
//            breakpoints.addContent(breakpoint);
//            recombinant.addContent(breakpoints);
//            Element recCurve=new Element("RecCurve");
//            double[] pos=rec.getPos();
//            double[] pdd=rec.getPdd();
//            for(int i=0;i<pdd.length;i++){
//                Element point=new Element("Point");
//                point.addAttribute("x",pos[i]+"");
//                point.addAttribute("y",pdd[i]+"");
//                recCurve.addContent(point);
//            }
//            recombinant.addContent(recCurve);
//            Element fragments=new Element("RecFragments");
//            List<int[]> frags=rec.getFullBlock();
//            List<int[]> noGapFrags=rec.getBlocks();
//            List<double[]> probs=rec.getPv();
//            for(int i=0;i<frags.size();i++){
//                Element fragment=new Element("Fragment");
//                Element start=new Element("Start");
//                start.addAttribute(new Attribute("noGapPos",noGapFrags.get(i)[0]+""));
//                start.addContent(frags.get(i)[0]+"");
//                fragment.addContent(start);
//                Element end=new Element("End");
//                end.addAttribute(new Attribute("noGapPos",noGapFrags.get(i)[1]+""));
//                end.addContent(frags.get(i)[1]+"");
//                fragment.addContent(end);
//                Element majorProb=new Element("MajorProb");
//                majorProb.addContent(probs.get(i)[0]+"");
//                fragment.addContent(majorProb);
//                Element minorProb=new Element("MinorProb");
//                minorProb.addContent(probs.get(i)[1]+"");
//                fragment.addContent(minorProb);
//                fragments.addContent(fragment);
//            }
//            recombinant.addContent(fragments);
//            recombinants.addContent(recombinant);
//        }
//        }
//        root.addContent(recombinants);
//        
//        return doc;
//    }
    
    public void openXML(File file) throws FileNotFoundException, DocumentException{
        final SAXReader reader = new SAXReader();
	reader.setDefaultHandler(new ElementHandler(){
            List gs;
            List g;
            List rs;
            Recombinant r;
            List bps;
            List ps;
            List fs;
            fragment f;

            @Override
            public void onEnd(ElementPath ep) {
                Element e = ep.getCurrent();  
                String name=e.getName();
                if(name.equals("Alignment")){
                    String alnFile=e.getText();
                    File f=new File(alnFile);
                    pl.setInfile(new File(alnFile));
                    if(f.exists()==false){                      
                        Document parsedDocument = ep.getCurrent().getDocument();
                        throw new AbortParsingException(parsedDocument, "stop parsing after '"+ep.getPath()+"' node");
                    }
                       
                    try {
                        pl.loadAlignment();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(XMLRecIO.class.getName()).log(Level.SEVERE, null, ex);
                        return;
                    } catch (IOException ex) {
                        Logger.getLogger(XMLRecIO.class.getName()).log(Level.SEVERE, null, ex);
                        return;
                    }
                }
                else if(name.equals("Tree")){
                    String tree=e.getText();
                    if(tree.length()>0){
                        pl.setTree(new TreeIO().readTree(tree));
                    }
                }
                else if(name.equals("Sequence")){
                    g.add(e.getText());
                }
                else if(name.equals("Group")){
                    gs.add(g.toArray(new String[]{}));
                }
                else if(name.equals("Groups")){
                    if(gs.size()>0){
                        pl.setGroups(gs);
                    }
                }
                else if(name.equals("RecSeq")){
                    int id=Integer.parseInt(e.attributeValue("id"));
                    r.setRec(id);
                    r.setRecSeq(pl.getAln().getSeqById(id));
                }
                else if(name.equals("MajorSeq")){
                    int id=Integer.parseInt(e.attributeValue("id"));
                    r.setMajor(id);
                    r.setMajorSeq(pl.getAln().getSeqById(id));
                }
                else if(name.equals("MinorSeq")){
                    int id=Integer.parseInt(e.attributeValue("id"));
                    r.setMinor(id);
                    r.setMinorSeq(pl.getAln().getSeqById(id));
                }
                else if(name.equals("MajorSimilarity")){
                    r.setPdisToMajor(Double.parseDouble(e.getText()));
                }
                else if(name.equals("MinorSimilarity")){
                    r.setPdisToMinor(Double.parseDouble(e.getText()));
                }
                else if(name.equals("MajorTree")){
                    String tree=e.getText();
                    if(tree.equals("")==false){
                        r.setMajorTree(tree);
                    }
                }
                else if(name.equals("MinorTree")){
                    String tree=e.getText();
                    if(tree.equals("")==false){
                        r.setMinorTree(tree);
                    }
                }
                else if(name.equals("Breakpoint")){
                    bps.add(Integer.parseInt(e.getText())-1);
                }
                else if(name.equals("Breakpoints")){
                    bps.set(bps.size()-1, (Integer)(bps.get(bps.size()-1))+1);
                    r.setBreakpoints(bps);
                }
                else if(name.equals("Point")){
                    ps.add(new double[]{Double.parseDouble(e.attributeValue("x")),
                        Double.parseDouble(e.attributeValue("y"))});
                }
                else if(name.equals("RecCurve")){
                    double[] x=new double[ps.size()];
                    double[] y=new double[ps.size()];
                    int i=0;
                    for(Object p:ps){
                        double[] pa=(double[])p;
                        x[i]=pa[0];
                        y[i]=pa[1];
                        i++;
                    }
                    r.setPos(x);
                    r.setPdd(y);
                }
                else if(name.equals("Start")){
                    f.start=Integer.parseInt(e.attributeValue("noGapPos"));
                    f.fStart=Integer.parseInt(e.getText());
                }
                else if(name.equals("End")){
                    f.end=Integer.parseInt(e.attributeValue("noGapPos"));
                    f.fEnd=Integer.parseInt(e.getText());
                }
                else if(name.equals("MajorProb")){
                    f.p1=Double.parseDouble(e.getText());
                }
                else if(name.equals("MinorProb")){
                    f.p2=Double.parseDouble(e.getText());
                }
                else if(name.equals("Fragment")){
                    fs.add(f);
                }
                else if(name.equals("RecFragments")){
                    ArrayList blocks=new ArrayList(fs.size());
                    ArrayList fullBlocks=new ArrayList(fs.size());
                    ArrayList pvs=new ArrayList(fs.size());
                    for(Object fo:fs){
                        fragment f=(fragment)fo;
                        blocks.add(new int[]{f.start,f.end});
                        fullBlocks.add(new int[]{f.fStart,f.fEnd});
                        pvs.add(new double[]{f.p1,f.p2});
                    }
                    r.setBlocks(blocks);
                    r.setFullBlock(fullBlocks);
                    r.setPv(pvs);
                }
                else if(name.equals("Recombinant")){
                    rs.add(r);
                }
                else if(name.equals("Recombinants")){
                    pl.setRecs(rs);
                }
                
		e.detach();
            }
            @Override
            public void onStart(ElementPath ep) {
                Element e = ep.getCurrent();  
                String name=e.getName();
                if(name.equals("Groups")){
                    gs=new LinkedList();                    
                }
                else if(name.equals("Group")){
                    g=new LinkedList();
                }
                else if(name.equals("Recombinants")){
                    rs=new LinkedList();
                }
                else if(name.equals("Recombinant")){
                    r=new Recombinant();
                }
                else if(name.equals("Breakpoints")){
                    bps=new LinkedList();
                }
                else if(name.equals("RecCurve")){
                    ps=new LinkedList();
                }
                else if(name.equals("RecFragments")){
                    fs=new LinkedList();
                }
                else if(name.equals("Fragment")){
                    f=new fragment();
                }
                e.detach();
            }
	});
	reader.read(new BufferedInputStream(new FileInputStream(file)));
    }
    
//    public void openXML(File file) throws JDOMException, FileNotFoundException, IOException{
//        SAXBuilder bSAX = new SAXBuilder(false);
//        Document doc = bSAX.build(file);
//        Element root=doc.getRootElement();
//        String alnFile=root.getChild("Alignment").getText();
//        pl.setInfile(new File(alnFile));
//        pl.loadAlignment();
//        String tree=root.getChild("Tree").getText();
//        if(tree.length()>0)
//            pl.setTree(new TreeIO().readTree(tree));
//        List<Element> groups=root.getChild("Groups").getChildren("Group");
//        if(groups.size()>0){
//            List groupList=new LinkedList();
//            for(Element group:groups){
//                List<Element> seqs=group.getChildren("Sequence");
//                String[] aGroup=new String[seqs.size()];
//                int i=0;
//                for(Element seq:seqs){
//                    aGroup[i++]=seq.getText();
//                }
//                groupList.add(aGroup);
//            }
//            pl.setGroups(groupList);
//        }
//        List<Element> recombinants=root.getChild("Recombinants").getChildren("Recombinant");
//        if(recombinants.size()>0){
//            LinkedList recList=new LinkedList();
//            for(Element recombinant:recombinants){
//                Recombinant rec=new Recombinant();
//                int recId=Integer.parseInt(recombinant.getChild("RecSeq").getAttributeValue("id"));
//                rec.setRec(recId);
//                rec.setRecSeq(pl.getAln().getSeqById(recId));
//                int majorId=Integer.parseInt(recombinant.getChild("MajorSeq").getAttributeValue("id"));
//                rec.setMajor(majorId);
//                rec.setMajorSeq(pl.getAln().getSeqById(majorId));
//                int minorId=Integer.parseInt(recombinant.getChild("MinorSeq").getAttributeValue("id"));
//                rec.setMinor(minorId);
//                rec.setMinorSeq(pl.getAln().getSeqById(minorId));
//                rec.setPdisToMajor(Double.parseDouble(recombinant.getChild("MajorSimilarity").getText()));
//                rec.setPdisToMinor(Double.parseDouble(recombinant.getChild("MinorSimilarity").getText()));
//                List<Element> breakpoints=recombinant.getChild("Breakpoints").getChildren();
//                ArrayList<Integer> bps=new ArrayList(breakpoints.size());
//                for(Element breakpoint:breakpoints){
//                    bps.add(Integer.parseInt(breakpoint.getText())-1);
//                }
//                bps.set(bps.size()-1, bps.get(bps.size()-1)+1);
//                rec.setBreakpoints(bps);
//                List<Element> points=recombinant.getChild("RecCurve").getChildren();
//                double[] pos=new double[points.size()];
//                double[] pdd=new double[points.size()];
//                int i=0;
//                for(Element point:points){
//                    pos[i]=Double.parseDouble(point.getAttributeValue("x"));
//                    pdd[i]=Double.parseDouble(point.getAttributeValue("y"));
//                    i++;
//                }
//                rec.setPos(pos);
//                rec.setPdd(pdd);
//                List<Element> frags=recombinant.getChild("RecFragments").getChildren("Fragment");
//                ArrayList blocks=new ArrayList(frags.size());
//                ArrayList fullBlocks=new ArrayList(frags.size());
//                ArrayList pvs=new ArrayList(frags.size());
//                for(Element frag:frags){
//                    int b1=Integer.parseInt(frag.getChild("Start").getAttributeValue("noGapPos"));
//                    int b2=Integer.parseInt(frag.getChild("End").getAttributeValue("noGapPos"));
//                    blocks.add(new int[]{b1,b2});
//                    b1=Integer.parseInt(frag.getChild("Start").getText());
//                    b2=Integer.parseInt(frag.getChild("End").getText());
//                    fullBlocks.add(new int[]{b1,b2});
//                    double p1=Double.parseDouble(frag.getChild("MajorProb").getText());
//                    double p2=Double.parseDouble(frag.getChild("MinorProb").getText());
//                    pvs.add(new double[]{p1,p2});
//                }
//                rec.setBlocks(blocks);
//                rec.setFullBlock(fullBlocks);
//                rec.setPv(pvs);
//                
//                recList.add(rec);
//            }
//            pl.setRecs(recList);
//        }
//        
//    }
    
//    public void saveXML(Document doc, File file){
//        XMLOutputter out = new XMLOutputter();
//        try{
//            out.setEncoding("utf-8");
//            out.setIndent(true);
//            out.setNewlines(true);
//            FileWriter outXML = new FileWriter(file);
// 
//            out.output(doc, outXML);
// 
//            outXML.close();
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(null, e.toString(), "fail to save the file", 0);
//        }
//
//    }
    
    public void saveXML(File file){
        Document doc = DocumentHelper.createDocument();  
        Element root=doc.addElement("GRecScan");
        
        XMLWriter out = null;  
        try {
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("utf-8");
            out = new XMLWriter(new FileWriter(file), outputFormat);           
            out.startDocument();                
            out.writeOpen(root);
            
            Element alignment = DocumentHelper.createElement("Alignment");  
            alignment.addAttribute("width", pl.getAln().getWidth()+""); 
            alignment.addAttribute("height", pl.getAln().getHeight()+"");
            alignment.addText(pl.getInfile().getAbsolutePath());  
            alignment.setParent(root);
            out.write(alignment);
            
            Element tree= DocumentHelper.createElement("Tree"); 
            if(pl.getTree()!=null){
                tree.addText(new TreeIO().writeTree(pl.getTree()));  
            }
            tree.setParent(root);
            out.write(tree);
            
            Element groups=DocumentHelper.createElement("Groups");
            groups.setParent(root);
            out.writeOpen(groups);
            if(pl.getGroups()!=null){
                List<String[]> gs=pl.getGroups();
                for(String[] g:gs){
                    Element group=DocumentHelper.createElement("Group");
                    group.setParent(groups);
                    out.writeOpen(group);
                    for(String e:g){
                        Element sequence=DocumentHelper.createElement("Sequence");
                        sequence.addText(e);
                        sequence.setParent(group);
                        out.write(sequence);
                    }
                    out.writeClose(group);
                }
            }
            out.writeClose(groups);
            
            Element recombinants=DocumentHelper.createElement("Recombinants");
            recombinants.setParent(root);
            out.writeOpen(recombinants);
            if(pl.getRecs()!=null){
                List<Recombinant> recs=pl.getRecs();
                for(Recombinant rec:recs){
                    Element recombinant=DocumentHelper.createElement("Recombinant");
                    recombinant.setParent(recombinants);
                    out.writeOpen(recombinant);
                    Element recSeq=DocumentHelper.createElement("RecSeq");
                    recSeq.addAttribute("id",rec.getRec()+"");
                    recSeq.addText(rec.getRecSeq().getName());
                    recSeq.setParent(recombinant);
                    out.write(recSeq);
                    Element majorSeq=DocumentHelper.createElement("MajorSeq");
                    majorSeq.addAttribute("id",rec.getMajor()+"");
                    majorSeq.addText(rec.getMajorSeq().getName());
                    majorSeq.setParent(recombinant);
                    out.write(majorSeq);
                    Element minorSeq=DocumentHelper.createElement("MinorSeq");
                    minorSeq.addAttribute("id",rec.getMinor()+"");
                    minorSeq.addText(rec.getMinorSeq().getName());
                    minorSeq.setParent(recombinant);
                    out.write(minorSeq);
                    Element majorSim=DocumentHelper.createElement("MajorSimilarity");
                    majorSim.addText(rec.getPdisToMajor()+"");
                    majorSim.setParent(recombinant);
                    out.write(majorSim);
                    Element minorSim=DocumentHelper.createElement("MinorSimilarity");
                    minorSim.addText(rec.getPdisToMinor()+"");
                    minorSim.setParent(recombinant);
                    out.write(minorSim);
                    Element majorTree=DocumentHelper.createElement("MajorTree");
                    if(rec.getMajorTree()!=null){
                        majorTree.addText(rec.getMajorTree());
                    }
                    majorTree.setParent(recombinant);
                    out.write(majorTree);
                    Element minorTree=DocumentHelper.createElement("MinorTree");
                    if(rec.getMinorTree()!=null){
                        minorTree.addText(rec.getMinorTree());
                    }
                    minorTree.setParent(recombinant);
                    out.write(minorTree);
                    Element breakpoints=DocumentHelper.createElement("Breakpoints");
                    breakpoints.setParent(recombinant);
                    out.writeOpen(breakpoints);
                    List<Integer> bps=rec.getBreakpoints();
                    int n=bps.size();
                    for(int i=0;i<n-1;i++){
                        Element breakpoint=DocumentHelper.createElement("Breakpoint");
                        breakpoint.addText((bps.get(i)+1)+"");
                        breakpoint.setParent(breakpoints);
                        out.write(breakpoint);
                    }
                    Element breakpoint=DocumentHelper.createElement("Breakpoint");
                    breakpoint.addText(bps.get(n-1)+"");
                    breakpoint.setParent(breakpoints);
                    out.write(breakpoint);
                    out.writeClose(breakpoints);
                    Element recCurve=DocumentHelper.createElement("RecCurve");
                    recCurve.setParent(recombinant);
                    out.writeOpen(recCurve);
                    double[] pos=rec.getPos();
                    double[] pdd=rec.getPdd();
                    for(int i=0;i<pdd.length;i++){
                        Element point=DocumentHelper.createElement("Point");
                        point.addAttribute("x",pos[i]+"");
                        point.addAttribute("y",pdd[i]+"");
                        point.setParent(recCurve);
                        out.write(point);
                    }
                    out.writeClose(recCurve);
                    Element fragments=DocumentHelper.createElement("RecFragments");
                    fragments.setParent(recombinant);
                    out.writeOpen(fragments);
                    List<int[]> frags=rec.getFullBlock();
                    List<int[]> noGapFrags=rec.getBlocks();
                    List<double[]> probs=rec.getPv();
                    for(int i=0;i<frags.size();i++){
                        Element fragment=DocumentHelper.createElement("Fragment");
                        fragment.setParent(fragments);
                        out.writeOpen(fragment);
                        Element start=DocumentHelper.createElement("Start");
                        start.addAttribute("noGapPos",noGapFrags.get(i)[0]+"");
                        start.addText(frags.get(i)[0]+"");
                        start.setParent(fragment);
                        out.write(start);
                        Element end=DocumentHelper.createElement("End");
                        end.addAttribute("noGapPos",noGapFrags.get(i)[1]+"");
                        end.addText(frags.get(i)[1]+"");
                        end.setParent(fragment);
                        out.write(end);
                        Element majorProb=DocumentHelper.createElement("MajorProb");
                        majorProb.addText(probs.get(i)[0]+"");
                        majorProb.setParent(fragment);
                        out.write(majorProb);
                        Element minorProb=DocumentHelper.createElement("MinorProb");
                        minorProb.addText(probs.get(i)[1]+"");
                        minorProb.setParent(fragment);
                        out.write(minorProb);
                        out.writeClose(fragment);
                    }
                    out.writeClose(fragments);
                    out.writeClose(recombinant);
                }
                out.writeClose(recombinants);
            }        
            out.writeClose(root);
            out.endDocument(); 
            out.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "fail to save the file", 0);
            Logger.getLogger(XMLRecIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XMLRecIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    class fragment{
        int start;
        int end;
        int fStart;
        int fEnd;
        double p1;
        double p2;
    }
}
