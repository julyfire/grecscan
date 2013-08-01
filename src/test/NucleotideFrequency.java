/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import grecscan2.core.Alignment;
import grecscan2.core.Sequence;

/**
 *
 * @author wb
 */
public class NucleotideFrequency {
    private int A1, A2, C1, C2, G1, G2, T1, T2, total1, total2, missing1, missing2;
    private int AA, AC, AG, AT, CA, CC, CG, CT, GA, GC, GG, GT, TA, TC, TG, TT, size, ambiguous, uninformative;
    private int length;

    //private double x;
    private int w, sumWeight, rand, s, boot;
    private NucleotideFrequency npmboot;
    
    private Alignment aln;


    /**
     *  Constructs an empty <code>NucleotidePairwiseMeter</code> with all parameters set to 0.
     */
    public NucleotideFrequency(Alignment aln) {
        this.aln=aln;
	init();
    }
    
    private void init(){
        length = 0;
	A1 = 0; C1 = 0; G1 = 0; T1 = 0;
	A2 = 0; C2 = 0; G2 = 0; T2 = 0;
	total1 = 0; missing1 = 0;
	total2 = 0; missing2 = 0;
	AA = 0; AC = 0; AG = 0; AT = 0;
	CA = 0; CC = 0; CG = 0; CT = 0;
	GA = 0; GC = 0; GG = 0; GT = 0;
	TA = 0; TC = 0; TG = 0; TT = 0;
	size = 0; ambiguous = 0; uninformative = 0;
    }

    
    public void count(int s1, int s2, int start, int end) {
        init();
	length = end-start;
        int[] gfi=aln.getGapFreeIndex();
	for(int s=start;s<end;s++) {
	    w = 1;
            char c1=aln.getResAt(s1, gfi[s]);
            char c2=aln.getResAt(s2, gfi[s]);
	    if ( ! (ignore(c1) || ignore(c2)) ) {
		size += w;
		total1 += w;
		total2 += w;
		switch (c1) {
		case 'A': 
		    A1 += w;
		    switch (c2) {
		    case 'A' : AA += w; A2 += w; break;
		    case 'C' : AC += w; C2 += w; break;
		    case 'G' : AG += w; G2 += w; break;
		    case 'T' : AT += w; T2 += w; break;
		    }
		    break;
		case 'C':
		    C1 += w;
		    switch (c2) {
		    case 'A' : CA += w; A2 += w; break;
		    case 'C' : CC += w; C2 += w; break;
		    case 'G' : CG += w; G2 += w; break;
		    case 'T' : CT += w; T2 += w; break;
		    }
		    break;
		case 'G':
		    G1 += w;
		    switch (c2) {
		    case 'A' : GA += w; A2 += w; break;
		    case 'C' : GC += w; C2 += w; break;
		    case 'G' : GG += w; G2 += w; break;
		    case 'T' : GT += w; T2 += w; break;
		    }
		    break;
		case 'T':
		    T1 += w;
		    switch (c2) {
		    case 'A' : TA += w; A2 += w; break;
		    case 'C' : TC += w; C2 += w; break;
		    case 'G' : TG += w; G2 += w; break;
		    case 'T' : TT += w; T2 += w; break;
		    }
		    break;
		}
	    }
	    else {
		if ( ignore(c1) && ignore(c2) ) {
		    w = 1;
		    uninformative += w;
		    missing1 += w;
		    missing2 += w;
		}
		else {
		    ambiguous += w;
		    if ( ignore(c1) ) {
			missing1 += w;
			total2 += w;
			switch (c2) {
			case 'A' : A2 += w; break;
			case 'C' : C2 += w; break;
			case 'G' : G2 += w; break;
			case 'T' : T2 += w; break;
			}
		    }
		    else {
			missing2 += w;
			total1 += w;
			switch (c1) {
			case 'A' : A1 += w; break;
			case 'C' : C1 += w; break;
			case 'G' : G1 += w; break;
			case 'T' : T1 += w; break;
			}
		    }
		}
	    }
	}
    }
    
    private boolean ignore(char c){
        if(c=='A' || c=='T' || c=='C' || c=='G' || c=='U')
            return false;
        return true;
    }


    /**
     *  Returns the sum of the character weights of the non-missing character states counted by this <code>Meter</code> in the first <code>NucleotideSequence</code>. 
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of non-missing character states.
     *  @return the sum of the character weights of the non-missing character states in the first <code>NucleotideSequence</code>
     */
    public int totalFirst() {
	return total1;
    }
    
    /**
     *  Returns the sum of the character weights of the non-missing character states counted by this <code>Meter</code> in the second <code>NucleotideSequence</code>. 
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of non-missing character states.
     *  @return the sum of the character weights of the non-missing character states in the second <code>NucleotideSequence</code>
     */
    public int totalSecond() {
	return total2;
    }
    
    /**
     *  Returns the sum of the character weights of the non-missing character states counted by this <code>Meter</code> in the two <code>NucleotideSequence</code>. 
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of non-missing character states.
     *  @return the sum of the character weights of the non-missing character states in the two <code>NucleotideSequence</code>
     */
    public int total() {
	return total1 + total2;
    }
    
    /**
     *  Returns the sum of the character weights of the missing character states counted by this <code>Meter</code> in the first <code>NucleotideSequence</code>. 
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of missing character states.
     *  @return the sum of the character weights of the missing character states in the first <code>NucleotideSequence</code>
     */
    public int missingFirst() {
	return missing1;
    }
    
    /**
     *  Returns the sum of the character weights of the missing character states counted by this <code>Meter</code> in the second <code>NucleotideSequence</code>. 
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of missing character states.
     *  @return the sum of the character weights of the missing character states in the second <code>NucleotideSequence</code>
     */
    public int missingSecond() {
	return missing2;
    }
    
    /**
     *  Returns the sum of the character weights of the missing character states counted by this <code>Meter</code> in the two <code>NucleotideSequence</code>. 
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of missing character states.
     *  @return the sum of the character weights of the missing character states in the two <code>NucleotideSequence</code>
     */
    public int missing() {
	return missing1 + missing2;
    }
    
     /**
     *  Returns the sum of the character weights with non-ambiguous pairwise comparisons, i.e. corresponding to two non-missing character states.
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of non-ambiguous pairwise comparisons.
     *  @return the sum of the character weights with non-ambiguous pairwise comparisons
     */
    public int size() {
	return size;
    }
    
     /**
     *  Returns the sum of the character weights with ambiguous pairwise comparisons, i.e. one missing character state aligned next to known character state (for example: <code>A?</code> or <code>-G</code>).
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of ambiguous pairwise comparisons.
     *  @return the sum of the character weights with ambiguous pairwise comparisons
     */
    public int ambiguous() {
	return ambiguous;
    }
    
     /**
     *  Returns the sum of the character weights with uninformative pairwise comparisons.
     *  For example: <code>??</code> or <code>--</code>.
     *  Since all character weights are 1 by default in <code>NucleotideSequence</code>, this corresponds in this case to the total number of unknown pairwise comparisons.
     *  @return the sum of the character weights with uninformative pairwise comparisons
     */
    public int uninformative() {
	return uninformative;
    }
    
    /**
     *  Returns the length of the two <code>NucleotideSequence</code> corresponding to this <code>NucleotidePairwiseMeter</code>.
     *  @return 0 if this <code>NucleotidePairwiseMeter</code> is empty; the length of the two <code>NucleotideSequence</code> otherwise
     */
    public int length() {
	return length;
    }
    
   
     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>A</code> aligned next to state <code>A</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>AA</code> sum of weight
     */
    public int getAA() {
	return AA;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>A</code> aligned next to state <code>C</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>AC</code> sum of weight
     */
    public int getAC() {
	return AC;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>A</code> aligned next to state <code>G</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>AG</code> sum of weight
     */
    public int getAG() {
	return AG;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>A</code> aligned next to state <code>T</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>AT</code> sum of weight
     */
    public int getAT() {
	return AT;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>C</code> aligned next to state <code>A</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>CA</code> sum of weight
     */
    public int getCA() {
	return CA;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>C</code> aligned next to state <code>C</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>CC</code> sum of weight
     */
    public int getCC() {
	return CC;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>C</code> aligned next to state <code>G</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>CG</code> sum of weight
     */
    public int getCG() {
	return CG;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>C</code> aligned next to state <code>T</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>CT</code> sum of weight
     */
    public int getCT() {
	return CT;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>G</code> aligned next to state <code>A</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>GA</code> sum of weight
     */
    public int getGA() {
	return GA;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>G</code> aligned next to state <code>C</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>GC</code> sum of weight
     */
    public int getGC() {
	return GC;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>G</code> aligned next to state <code>G</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>GG</code> sum of weight
     */
    public int getGG() {
	return GG;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>G</code> aligned next to state <code>T</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>GT</code> sum of weight
     */
    public int getGT() {
	return GT;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>T</code> aligned next to state <code>A</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>TA</code> sum of weight
     */
    public int getTA() {
	return TA;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>T</code> aligned next to state <code>C</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>TC</code> sum of weight
     */
    public int getTC() {
	return TC;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>T</code> aligned next to state <code>G</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>TG</code> sum of weight
     */
    public int getTG() {
	return TG;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the first <code>NucleotideSequence</code> has state <code>T</code> aligned next to state <code>T</code> in the second <code>NucleotideSequence</code>.
     *  @return the <code>TT</code> sum of weight
     */
    public int getTT() {
	return TT;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>A</code> is appearing in the first <code>NucleotideSequence</code>.
     *  @return the <code>A</code> sum of weight in the first <code>NucleotideSequence</code>
     */
    public int getAfirst() {
	return A1;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>A</code> is appearing in the second <code>NucleotideSequence</code>.
     *  @return the <code>A</code> sum of weight in the second <code>NucleotideSequence</code>
     */
    public int getAsecond() {
	return A2;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>A</code> is appearing in the two <code>NucleotideSequence</code>.
     *  @return the <code>A</code> sum of weight in the two <code>NucleotideSequence</code>
     */
    public int getA() {
	return A1 + A2;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>C</code> is appearing in the first <code>NucleotideSequence</code>.
     *  @return the <code>C</code> sum of weight in the first <code>NucleotideSequence</code>
     */
    public int getCfirst() {
	return C1;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>C</code> is appearing in the second <code>NucleotideSequence</code>.
     *  @return the <code>C</code> sum of weight in the second <code>NucleotideSequence</code>
     */
    public int getCsecond() {
	return C2;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>C</code> is appearing in the two <code>NucleotideSequence</code>.
     *  @return the <code>C</code> sum of weight in the two <code>NucleotideSequence</code>
     */
    public int getC() {
	return C1 + C2;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>G</code> is appearing in the first <code>NucleotideSequence</code>.
     *  @return the <code>G</code> sum of weight in the first <code>NucleotideSequence</code>
     */
    public int getGfirst() {
	return G1;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>G</code> is appearing in the second <code>NucleotideSequence</code>.
     *  @return the <code>G</code> sum of weight in the second <code>NucleotideSequence</code>
     */
    public int getGsecond() {
	return G2;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>G</code> is appearing in the two <code>NucleotideSequence</code>.
     *  @return the <code>G</code> sum of weight in the two <code>NucleotideSequence</code>
     */
    public int getG() {
	return G1 + G2;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>T</code> is appearing in the first <code>NucleotideSequence</code>.
     *  @return the <code>T</code> sum of weight in the first <code>NucleotideSequence</code>
     */
    public int getTfirst() {
	return T1;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>T</code> is appearing in the second <code>NucleotideSequence</code>.
     *  @return the <code>T</code> sum of weight in the second <code>NucleotideSequence</code>
     */
    public int getTsecond() {
	return T2;
    }

     /**
     *  Returns the number of times (more precisely the sum of character weights) the character state <code>T</code> is appearing in the two <code>NucleotideSequence</code>.
     *  @return the <code>T</code> sum of weight in the two <code>NucleotideSequence</code>
     */
    public int getT() {
	return T1 + T2;
    }

     

}
