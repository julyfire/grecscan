/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author wb
 */
public class NucleotideEvolutionaryModel {
    private byte type;    // type of model (i.e. nucleotide, codon)
    private byte name;

    private NucleotideEvolutionaryModel mod;


    /*
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the uncorrected distance between <code>NucleotideSequence</code>.
     public final static NucleotideEvolutionaryModel p = new NucleotideEvolutionaryModel((byte) 0 , (byte) 0);
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the transition-type (<code>A</code><><code>G</code>, <code>C</code><><code>T</code>) uncorrected distance between <code>NucleotideSequence</code>.
     public final static NucleotideEvolutionaryModel ts = new NucleotideEvolutionaryModel((byte) 0 , (byte) 1);
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the transvertion-type (all substitutions except <code>A</code><><code>G</code> and <code>C</code><><code>T</code>) uncorrected distance between <code>NucleotideSequence</code>.
     public final static NucleotideEvolutionaryModel tv = new NucleotideEvolutionaryModel((byte) 0 , (byte) 2);
    */

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Jukes and Cantor (1969) evolutionary model of <code>NucleotideSequence</code> (equal base frequencies, one substitution type).
     *  <br><br>
     *  Jukes, T.H. and Cantor C.R. (1969) <i>Evolution of Protein Molecules</i>, <i>in</i> Mammalian Protein Metabolism, vol. III, ed. H. N. Munro, Academic Press, 21-132.
     */
    public final static NucleotideEvolutionaryModel JC69 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 3);

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Felsenstein (1981) evolutionary model of <code>NucleotideSequence</code> (unequal base frequencies, one substitution type).
     *  <br><br>
     *  Felsenstein, J. (1981) <i>Evolutionary trees from DNA sequences: a maximum likelihood approach</i>, Journal of Molecular Evolution, 17:368-376.
     */
    public final static NucleotideEvolutionaryModel F81 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 4);

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Equal-Input (EI) evolutionary model of <code>NucleotideSequence</code> (unequal base frequencies, one substitution type; Tajima and Nei, 1982).
     *  <br><br>
     *  Tajima, F. and Nei, M. (1982) <i>Biases of the estimates of DNA divergence obtained by the restriction enzyme technique</i>, Journal of Molecular Evolution, 18:115-120
     */
    public final static NucleotideEvolutionaryModel TN82_EI = new NucleotideEvolutionaryModel((byte) 0 , (byte) 5);

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Kimura (1980) evolutionary model of <code>NucleotideSequence</code> (equal base frequencies, transversion and transition substitution parameters).
     *  <br><br>
     *  Kimura, M. (1980) <i>A simple method for estimating evolutionary rate of base substitutions through comparative studies of nucleotide sequences</i>, Journal of Molecular Evolution, 16:111-120.
     */
    public final static NucleotideEvolutionaryModel K80 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 6);

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Felsenstein (1984) evolutionary model of <code>NucleotideSequence</code> (unequal base frequencies, transversion and transition substitution parameters).
     *  <br><br>
     *  Felsenstein, J. (1984) <i>The statistical approach to inferring phylogeny and what it tells us about parsimony and compatibility</i>, <i>in</i> Cladistics: Perspectives on the Reconstruction of Evolutionary History, ed. T. Duncan & T. F. Stuessy, Columbia University Press: New York, 169-191
     */
    public final static NucleotideEvolutionaryModel F84 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 7);

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Hasegawa, Kishino and Yano (1985) evolutionary model of <code>NucleotideSequence</code> (unequal base frequencies, transversion and transition substitution parameters).
     *  <br><br>
     *  Hasegawa, M., Kishino, H. and Yano, T. (1985) <i>Dating the human-ape splitting by a molecular clock of mitochondrial DNA</i>, Journal of Molecular Evolution, 22:160-174
     */
    public final static NucleotideEvolutionaryModel HKY85 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 8);

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the 3ST evolutionary model of <code>NucleotideSequence</code> (equal base frequencies, three substitution types; Kimura, 1981).
     *  <br><br>
     *  Kimura, M. (1981) <i>Estimation od evolutionary distances between homologous nucleotide sequences</i>, Proceedings of the National Academy of Science USA, 78(1):454-458.
     */
    public final static NucleotideEvolutionaryModel K81_3ST = new NucleotideEvolutionaryModel((byte) 0 , (byte) 9);

    /*
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the 2FC evolutionary model of <code>NucleotideSequence</code> (Kimura, 1981).
     *  <br><br>
     *  Kimura, M. (1981) <i>Estimation od evolutionary distances between homologous nucleotide sequences</i>, Proceedings of the National Academy of Science USA, 78(1):454-458.
     public final static NucleotideEvolutionaryModel K81_2FC = new NucleotideEvolutionaryModel((byte) 0 , (byte) 10);
    */

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Tamura and Nei (1993) evolutionary model of <code>NucleotideSequence</code> (unequal base frequencies, three substitution types).
     *  <br><br>
     *  Tamura, K. and Nei, M. (1993) <i>Estimation of the number of nucleotide substitutions in the control region of mitochondrial DNA in human and chimpanzees</i>, Molecular Biology and Evolution, 10:512-526
     */
    public final static NucleotideEvolutionaryModel TN93 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 11);

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Tamura (1992) evolutionary model of <code>NucleotideSequence</code> (two base frequencies, two substitution types).
     *  <br><br>
     *  Tamura, K. (1992) <i>Estimation of the number of nucleotide substitutions when there are strong transition-transvertion and G+C content biases</i>, Molecular Biology and Evolution, 9:678-687
     */
    public final static NucleotideEvolutionaryModel T92 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 12);

    /*
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Rzhetsky and Nei (1995) evolutionary model of <code>NucleotideSequence</code> (eight substitution types).
     *  <br><br>
     *  Rzhetsky, A. and Nei, M. (1995) <i>Tests of applicability of several substitution models for DNA sequence data</i>, Molecular Biology and Evolution, 12(1):131-151
     public final static NucleotideEvolutionaryModel RN95 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 13);
    */

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Takahata and Kimura (1981) evolutionary model of <code>NucleotideSequence</code> (five substitution types).
     *  <br><br>
     *  Takahata, N. and Kimura, M. (1981) <i>A model of evolutionary base substitution and its application with special reference to rapid change of pseudo-genes</i>, Genetics, 98:641-657
     */
    public final static NucleotideEvolutionaryModel TK81 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 14);

    /**
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the Gojobori, Ishii and Nei (1982) evolutionary model of <code>NucleotideSequence</code> (six substitution types).
     *  <br><br>
     *  Gojobori, T., Ishii, K. and Nei, M. (1982) <i>Estimation of average number of nucleotide substitutions when the rate of substitution varies with nucleotide</i>, Journal of Molecular Evolution, 18:414-423
     */
    public final static NucleotideEvolutionaryModel GIN82 = new NucleotideEvolutionaryModel((byte) 0 , (byte) 15);

    /*
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the log-det distance (Steel, 1994; Lockhart et al., 1994, Lake, 1994) between <code>NucleotideSequence</code>.
     *  <br><br>
     *  Lake, J. A. (1994) <i>Reconstructing evolutionary trees from DNA and protein sequences: paralinear distance</i>, Proceeding of the National Academy of Science USA, 91:1455-1459
     *  <br>
     *  Lockhart, P. J., Steel, M. A., Hendy, M. D. and Penny, D. (1994) <i>Recovering evolutionary trees under a more realistic model of sequence evolution</i>, Molecular Biology and Evolution, 11:605-612
     *  <br>
     *  Steel, M. (1994) <i>Recovering a tree from the leaf colourations it generates under a Markov model</i>, Applied Mathematics Letters, 7(2):19-23
     public final static NucleotideEvolutionaryModel logdet = new NucleotideEvolutionaryModel((byte) 0 , (byte) 19);
     *  The <code>NucleotideEvolutionaryModel</code> corresponding to the evolutionary distance due to deletion and insertion (Tajima and Nei, 1984).
     *  <br><br>
     *  Tajima, F. and Nei, M. (1984) <i>Estimation of evolutionary distance between nucleotide sequences</i>, Molecular Biology and Evolution, 1(3):269-285
     public final static NucleotideEvolutionaryModel indel = new NucleotideEvolutionaryModel((byte) 0 , (byte) 20);
    */


    private NucleotideEvolutionaryModel(byte type , byte name) {
	this.type = type;
	this.name = name;
    }
    
    /**
     *  Tests if two <code>EvolutionaryModel</code> are the identical.
     *  <br>
     *  Be careful that two <code>EvolutionaryModel</code> with same name but not with the same implementations are not identical.
     *  For example:
     *  <br><br>
     *  &nbsp; &nbsp; &nbsp; <code>NucleotideEvolutionaryModel.JC.equals( CodonEvolutionaryModel.JC )</code>
     *  <br><br>
     *  will return <code>false</code>.
     *  @return <code>true</code> if the specified <code>EvolutionaryModel</code> is identical to the tested one; <code>false</code> otherwise
     */
    public boolean equals (NucleotideEvolutionaryModel model) {
	mod = (NucleotideEvolutionaryModel) model;
	return (this.type == mod.type) && (this.name == mod.name);
    }
}
