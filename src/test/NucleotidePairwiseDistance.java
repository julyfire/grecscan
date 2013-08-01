/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author wb
 */
public class NucleotidePairwiseDistance {
    public enum Model{P,JC69,F81,TN82_EI,K80,F84,HKY85,K81_3ST,TN93,TN84,T92,TK81,GIN82};
    private final static double PRECISION = 0.000001;    // to detect convergence when computing the unbiased distances

    private static double dist, b1, b2;                  // distance
    private static int _k, _n;
    private static double _f, _g, _h, _a, _b, _c;
    private static double _a1, _a2, _a3, _a4, _a5, _b1, _b2, _b3, _e1, _e2, _e3;
    private static double _v1, _v2, _c12, _c13, _c23, _piR, _piY, _gam, _fRY, _fAG, _fCT;

    private static double avg, var;                      // for bootstrap variance
    private static int r, cpt;
    private static double[] boot;

    private static double p, q, _p, _q, _p1, _p2;        // ts and tv proportions

    private static double a;                             // shape parameter for gamma distances

    private static double f, g, h, norm;                 // for unbiased distance computation
    private static int k, n, s, v, w, i, y, j, min, max;

    private static double[][] mJ, mC;                    // for LogDet
    private static double det;
    private static int modif, ii, jj;



    private NucleotidePairwiseDistance() {
    }

    /**
     *  Returns the <i>p</i>-distance, which is merely the proportion (<i>p</i>) of nucleotide sites at which the two sequences compared are different. 
     *  This is obtained by dividing the number of nucleotide differences by the total number of positions compared.
     *  @param npm a <code>NucleotideFrequency</code>
     *  @return the <i>p</i>-distance
     */
    public static double getPdistance(NucleotideFrequency npm) {
	return P(npm);
    }
    
    /**
     *  Returns the TN84 distance (Tajima and Nei, 1984).
     *  This estimator is not exactly based on an explicit <code>NucleotideEvolutionaryModel</code> but is an adaptation of the two distances based on the Equal-Input model (<a href="#F81"><code>NucleotideEvolutionaryModel.F81</code></a> and <a href="#TN82_EI"><code>NucleotideEvolutionaryModel.TN82_EI</code></a>).
     *  For this reason, it is not available from the <code>getDistance</code> method.
     *  This distance is given by 
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - <i>b</i> log<sub>e</sub> ( 1 - <i>p</i> / <i>b</i> ),
     *  </ul> 
     *  <br>
     *  with 
     *  <br><br>
     *  <ul>
     *  <i>b</i> = 0.5 <font size="+1">[</font> 1 
     *  - &Sigma;<sub><code>I</code>=<code>A</code>,<code>C</code>,<code>G</code>,<code>T</code> </sub><i>&pi;</i><sub><code>I</code></sub><sup>2</sup> 
     *  + <i>p</i><sup>2</sup> / ( &Sigma;<sub><code>I</code><code>J</code>;<code>I</code>,<code>J</code>=<code>A</code>,<code>C</code>,<code>G</code>,<code>T</code> </sub><i>&pi;</i><sub><code>I</code><code>J</code></sub><sup>2</sup> / (2<i>&pi;</i><sub><code>I</code></sub><i>&pi;</i><sub><code>J</code></sub>) ) <font size="+1">]</font>,
     *  </ul>
     *  <br>
     *  where <i>p</i> represents the <i>p</i>-distance, <i>&pi;</i><sub><code>I</code></sub> the frequency of nucleotide <code>I</code>, and <i>&pi;</i><sub><code>I</code><code>J</code></sub> the frequency of nucleotide pair <code>I</code> and <code>J</code> when the two sequences are compared.
     *  <br>
     *  <b>Be careful</b> that this method could return <code>NaN</code> or <code>Infinity</code>.
     *  <br><br>
     *  Tajima, F. and Nei, M. (1984) <i>Estimation of evolutionary distance between nucleotide sequences</i>, Molecular Biology and Evolution, 1(3):269-285
     *  @param npm a <code>NucleotideFrequency</code>
     *  @return the TN84 distance estimation if it is possible; <code>NaN</code> otherwise
     */
    public static double getTN84distance(NucleotideFrequency npm) {
	if ( P(npm) == 0 ) 
	    return 0;
	return TN84(npm);
    }
    
    /**
     *  Returns the unbiased TN84 distance (Tajima and Nei, 1984).
     *  This estimator is not exactly based on an explicit <code>NucleotideEvolutionaryModel</code> but is an adaptation of the two distances based on the Equal-Input model (<a href="#F81"><code>NucleotideEvolutionaryModel.F81</code></a> and <a href="#TN82_EI"><code>NucleotideEvolutionaryModel.TN82_EI</code></a>).
     *  For this reason, it is not available from the <code>getDistance</code> method.
     *  This distance is given by 
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - <i>b</i> log<sub>e</sub> ( 1 - <i>p</i> / <i>b</i> ),
     *  </ul> 
     *  <br>
     *  where the logarithmic term is expanded into Taylor's series (see <code><a href="#getUnbiasedDistance(jnt.NucleotideEvolutionaryModel, jnt.NucleotideFrequency, boolean)">getUnbiasedDistance</a></code>), and with
     *  <br><br>
     *  <ul>
     *  <i>b</i> = 0.5 <font size="+1">[</font> 1 
     *  - &Sigma;<sub><code>I</code>=<code>A</code>,<code>C</code>,<code>G</code>,<code>T</code> </sub><i>&pi;</i><sub><code>I</code></sub><sup>2</sup> 
     *  + <i>p</i><sup>2</sup> / ( &Sigma;<sub><code>I</code><code>J</code>;<code>I</code>,<code>J</code>=<code>A</code>,<code>C</code>,<code>G</code>,<code>T</code> </sub><i>&pi;</i><sub><code>I</code><code>J</code></sub><sup>2</sup> / (2<i>&pi;</i><sub><code>I</code></sub><i>&pi;</i><sub><code>J</code></sub>) ) <font size="+1">]</font>,
     *  </ul>
     *  <br>
     *  where <i>p</i> represents the <i>p</i>-distance, <i>&pi;</i><sub><code>I</code></sub> the frequency of nucleotide <code>I</code>, and <i>&pi;</i><sub><code>I</code><code>J</code></sub> the frequency of nucleotide pair <code>I</code> and <code>J</code> when the two sequences are compared.
     *  <br><br>
     *  Tajima, F. and Nei, M. (1984) <i>Estimation of evolutionary distance between nucleotide sequences</i>, Molecular Biology and Evolution, 1(3):269-285
     *  <br>
     *  Tajima, F. (1993) <i>Unbiased estimation of evolutionary distance between nucleotide sequences</i>, Molecular Biology and Evolution, 10(3):677-688
     *  @param npm a <code>NucleotideFrequency</code>
     *  @return the TN84 distance unbiased estimation
     */
    public static double getTN84unbiasedDistance(NucleotideFrequency npm) {
	if ( P(npm) == 0 ) 
	    return 0;
	return UTN84(npm);
    }

    /**
     *  Returns the LogDet distance (Lake, 1994; Lockhart et al., 1994; Steel, 1994).
     *  <br><br>
     *  Lake, J. A. (1994) <i>Reconstructing evolutionary trees from DNA and protein sequences: paralinear distances</i>, Proceedings of the National Academy of Science USA, 91:1455-1459
     *  <br>
     *  Lockhart, P. J., Steel, M. A., Hendy, M. D. and Penny, D. (1994) <i>Recovering evolutionary trees under a more realistic model of sequence evolution</i>, Molecular Biology and Evolution, 11:605-612
     *  <br>
     *  Steel, M. A. (1994) <i>Recovering a tree from the leaf colourations it generates under a Markov model</i>, Applied Mathematics Letters, 7:19-24
     *  @param npm a <code>NucleotideFrequency</code>
     *  @return the LogDet distance unbiased estimation
     */
    public static double getLOGDETdistance(NucleotideFrequency npm) {
	if ( P(npm) == 0 ) 
	    return 0;
	return LD(npm , false);
    }

    /**
     * Returns the unbiased LogDet distance (Lake, 1994; Lockhart et al., 1994; Steel, 1994; Gu and Li, 1996).
     *  <br><br>
     *  Gu, X. and Li, W.-H. (1996) <i>Bias-corrected paralinear and LogDet distances and tests of molecular clocks and phylogenies under nonstationary nucleotide frequencies</i>, Molecular Biology and Evolution, 13(10):1375-1383
     *  <br>
     *  Lake, J. A. (1994) <i>Reconstructing evolutionary trees from DNA and protein sequences: paralinear distances</i>, Proceedings of the National Academy of Science USA, 91:1455-1459
     *  <br>
     *  Lockhart, P. J., Steel, M. A., Hendy, M. D. and Penny, D. (1994) <i>Recovering evolutionary trees under a more realistic model of sequence evolution</i>, Molecular Biology and Evolution, 11:605-612
     *  <br>
     *  Steel, M. A. (1994) <i>Recovering a tree from the leaf colourations it generates under a Markov model</i>, Applied Mathematics Letters, 7:19-24
     *  @param npm a <code>NucleotideFrequency</code>
     *  @return the LogDet distance unbiased estimation
     */
    public static double getLOGDETunbiasedDistance(NucleotideFrequency npm) {
	if ( P(npm) == 0 ) 
	    return 0;
	return LD(npm , true);
    }

    
    /**
     *  Returns the transition/transvertion ratio estimated from a specified <code>NucleotideFrequency</code>.
     *  This value is given by
     *  <br><br>
     *  <ul>
     *  &kappa; = 2 log<sub>e</sub> ( 1 - 2<i>p</i> - <i>q</i> ) / log<sub>e</sub> ( 1 - 2<i>q</i> ) - 1,
     *  </ul>
     *  <br>
     *  where <i>p</i> and <i>q</i> are the proportion of sites with transitional and transversional nucleotide differences, respectively.
     *  If the <code>unbiased</code> option is set to true, the logarithmic terms are replaced by their unbiased expression (see <code><a href="#getUnbiasedDistance(jnt.NucleotideEvolutionaryModel, jnt.NucleotideFrequency, boolean)">getUnbiasedDistance</a></code>).
     *  @param npm a <code>NucleotideFrequency</code>
     *  @param unbiased a <code>boolean</code> option
     *  @return the transition/transversion ratio
     */
    public static double getTransitionTransversionRatio(NucleotideFrequency npm , boolean unbiased) {
	if ( P(npm) == 0 ) 
	    return 0;
	if ( unbiased )
	    return UTSTV(npm);
	return TSTV(npm);
    }


    /**
     *  Returns an evolutionary distance estimated from a specified <code>NucleotideFrequency</code> under a specified <code>NucleotideEvolutionaryModel</code>.
     *  If the specified <code>NucleotideEvolutionaryModel</code> takes into account the base frequency bias, setting the <code>homogeneity</code> option to <code>true</code> allows assuming the homogeneity of substitution pattern between the two sequences; otherwise setting the <code>homogeneity</code> option to <code>false</code> allows assuming an heterogeneous substitution pattern.
     *  If the specified <code>NucleotideEvolutionaryModel</code> does not use the base frequencies, the <code>homogeneity</code> option does not change the returned value.
     *  <br>
     *  <b>Be careful</b> that this method could return <code>NaN</code> or <code>Infinity</code>.
     *  <br><br>
     *  <b><code><a name="JC">NucleotideEvolutionaryModel.JC69</a></code></b>
     *  <ul>
     *  This distance is given by 
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - <i>b</i><sub>1</sub> log<sub>e</sub> ( 1 - <i>p</i> / <i>b</i><sub>2</sub>  ),
     *  </ul> 
     *  <br>
     *  where <i>p</i> represents the <i>p</i>-distance and 
     *  <br><br>
     *  <ul>
     *  <i>b</i><sub>1</sub> = <i>b</i><sub>2</sub> = 3/4.
     *  </ul>
     *  <br>
     *  This value is always the same, whether the <code>homogeneity</code> assumption is assumed or not.
     *  <br><br>
     *  Jukes, T.H. and Cantor C.R. (1969) <i>Evolution of Protein Molecules</i>, <i>in</i> Mammalian Protein Metabolism, vol. III, ed. H. N. Munro, Academic Press, 21-132.
     *  </ul>
     *  <br>
     *  <b><code><a name="F81">NucleotideEvolutionaryModel.F81</a></code></b>
     *  <ul>
     *  This distance is given by the same formula as the <code><a href="#JC">JC69</a></code> distance but with 
     *  <br><br>
     *  <ul>
     *  <i>b</i><sub>1</sub> = 1 - &Sigma;<sub><code>I</code>=<code>A</code>,<code>C</code>,<code>G</code>,<code>T</code></sub> <i>&pi;</i><sub><code>I</code></sub><sup>2</sup>,
     *  </ul> 
     *  <br>
     *  where <i>&pi;</i><sub><code>I</code></sub> is the frequency of nucleotide <code>I</code> in the two sequences.
     *  When the <code>homogeneity</code> option is set to <code>true</code>, then
     *  <br><br>
     *  <ul>
     *  <i>b</i><sub>2</sub> = <i>b</i><sub>1</sub>,
     *  </ul>
     *  <br>
     *  else the formula is computed with
     *  <br><br>
     *  <ul>
     *  <i>b</i><sub>2</sub> = 1 - &Sigma;<sub><code>I</code>=<code>A</code>,<code>C</code>,<code>G</code>,<code>T</code></sub> <i>&pi;</i><sub>1<code>I</code></sub><i>&pi;</i><sub>2<code>I</code></sub>,
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><i>i</i><code>I</code></sub> is the frequency of nucleotide <code>I</code> in the sequence <i>i</i>.
     *  <br><br>
     *  McGuire, G., Prentice, M. J. and Wright, F. (1999) <i>Improved error bounds for genetic distances from DNA sequences</i>, Biometrics, 55:1064-1070
     *  <br>
     *  Tamura, K. and Kumar, S. (2002) <i>Evolutionary distance estimation under heterogeneous substitution pattern among lineage</i>, Molecular Biology and Evolution, 19(10):1727-1736
     *  </ul>
     *  <br>
     *  <b><code><a name="TN82_EI">NucleotideEvolutionaryModel.TN82_EI</a></code></b>
     *  <ul>
     *  When the <code>homogeneity</code> option is set to <code>true</code>, this distance is given by the same formula as the <code><a href="#JC">JC69</a></code> distance but with different <i>b</i><sub>1</sub> and <i>b</i><sub>2</sub> values:
     *  <br><br>
     *  <ul>
     *  <i>b</i><sub>1</sub> = <i>b</i><sub>2</sub> = 
     *  <i>p</i><sup>2</sup> / [ &Sigma;<sub><code>I</code><code>J</code>;<code>I</code>,<code>J</code>=<code>A</code>,<code>C</code>,<code>G</code>,<code>T</code></sub> &psi;<sub><code>I</code><code>J</code></sub><sup>2</sup> / (2<i>&pi;</i><sub><code>I</code></sub><i>&pi;</i><sub><code>J</code></sub>) ],
     *  </ul>
     *  <br>
     *  where <i>p</i> represents the <i>p</i>-distance, <i>&pi;</i><sub><code>I</code></sub> the frequency of nucleotide <code>I</code>, and &psi;<sub><code>I</code><code>J</code></sub> the frequency of nucleotide pair <code>I</code> and <code>J</code> when the two sequences are compared.
     *  When the <code>homogeneity</code> option is set to <code>false</code>, this distance is given by
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - 2 &Sigma;<sub><code>I</code><code>J</code>;<code>I</code>,<code>J</code>=<code>A</code>,<code>C</code>,<code>G</code>,<code>T</code></sub> <i>&pi;</i><sub><code>I</code></sub><i>&pi;</i><sub><code>J</code></sub> log<sub>e</sub> [ 1 - &psi;<sub><code>I</code><code>J</code></sub> / ( <i>&pi;</i><sub>1<code>I</code></sub><i>&pi;</i><sub>2<code>J</code></sub> + <i>&pi;</i><sub>1<code>J</code></sub><i>&pi;</i><sub>2<code>I</code></sub> ) ],
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><i>i</i><code>I</code></sub> is the frequency of nucleotide <code>I</code> in the sequence <i>i</i>.
     *  <br><br>
     *  Tajima, F. and Nei, M. (1984) <i>Estimation of evolutionary distance between nucleotide sequences</i>, Molecular Biology and Evolution, 1(3):269-285
     *  <br>
     *  </ul>
     *  <br>
     *  <b><code><a name="K2P">NucleotideEvolutionaryModel.K80</a></code></b>
     *  <ul>
     *  The <code>homogeneity</code> option does not modify the returned value.
     *  This distance is given by
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - 0.5 log<sub>e</sub> ( 1 - 2 <i>p</i> - <i>q</i> ) - 0.25 log<sub>e</sub> ( 1 - 2 <i>q</i> )
     *  </ul>
     *  <br>
     *  where <i>p</i> and <i>q</i> are the proportion of sites with transitional and transversional nucleotide differences, respectively.
     *  <br><br>
     *  Kimura, M. (1980) <i>A simple method for estimating evolutionary rates of base substitutions through comparative studies of nucleotide sequences</i>, Journal of Molecular Evolution, 16:111-120
     *  </ul>
     *  <br>
     *  <b><code><a name="F84">NucleotideEvolutionaryModel.F84</a></code></b>
     *  <ul>
     *  The <code>homogeneity</code> option does not modify the returned value, since no formula exists for the heterogeneity assumption of the substitution pattern between sequences.
     *  This distance is given by
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - 2 <i>a</i> log<sub>e</sub> [ 1 - <i>p</i>/(2<i>a</i>) - (<i>a</i>-<i>b</i>)<i>q</i>/(2<i>a</i><i>c</i>) ] 
     *  + 2 (<i>a</i> - <i>b</i> - <i>c</i>) log<sub>e</sub> [ 1 - <i>q</i>/(2<i>c</i>) ],
     *  </ul>
     *  <br>
     *  where <i>p</i> and <i>q</i> are the proportion of sites with transitional and transversional nucleotide differences, respectively, and with
     *  <br><br>
     *  <ul>
     *  <i>a</i> = <i>&pi;</i><sub><code>A</code></sub><i>&pi;</i><sub><code>G</code></sub>/(<i>&pi;</i><sub><code>A</code></sub>+<i>&pi;</i><sub><code>G</code></sub>) + <i>&pi;</i><sub><code>C</code></sub><i>&pi;</i><sub><code>T</code></sub>/(<i>&pi;</i><sub><code>C</code></sub>+<i>&pi;</i><sub><code>T</code></sub>),
     *  <br>
     *  <i>b</i> = <i>&pi;</i><sub><code>A</code></sub><i>&pi;</i><sub><code>G</code></sub> + <i>&pi;</i><sub><code>C</code></sub><i>&pi;</i><sub><code>T</code></sub>, and
     *  <br>
     *  <i>c</i> = (<i>&pi;</i><sub><code>A</code></sub>+<i>&pi;</i><sub><code>G</code></sub>)(<i>&pi;</i><sub><code>C</code></sub>+<i>&pi;</i><sub><code>T</code></sub>),
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><code>I</code></sub> is the frequency of nucleotide <code>I</code> in the two sequences.
     *  <br><br>
     *  Felsenstein, J. and Churchill, G. A. (1996) <i>A hidden Markov model approach to variation among sites in rate of evolution</i>, Molecular Biology and Evolution, 13:93-104
     *  <br>
     *  McGuire, G., Prentice, M. J. and Wright, F. (1999) <i>Improved error bounds for genetic distances from DNA sequences</i>, Biometrics, 55:1064-1070
     *  </ul>
     *  <br>
     *  <b><code><a name="HKY85">NucleotideEvolutionaryModel.HKY85</a></code></b>
     *  <ul>
     *  This distance is given by
     *  <br><br>
     *  <ul>
     *  <i>d</i> = 2 (<i>&pi;</i><sub><code>A</code></sub><i>&pi;</i><sub><code>G</code></sub>+<i>&pi;</i><sub><code>C</code></sub><i>&pi;</i><sub><code>T</code></sub>) [ &gamma;B<sub>1</sub> + (1-&gamma;)B<sub>2</sub> ] + 2<i>&pi;</i><sub><code>R</code></sub><i>&pi;</i><sub><code>Y</code></sub>B<sub>3</sub>,
     *  </ul>
     *  <br>
     *  with
     *  <br><br>
     *  <ul>
     *  B<sub>1</sub> = (<i>&pi;</i><sub><code>Y</code></sub>/<i>&pi;</i><sub><code>R</code></sub>) log<sub>e</sub> E<sub>1</sub> - (1/<i>&pi;</i><sub><code>R</code></sub>) log<sub>e</sub> E<sub>2</sub>,
     *  <br>
     *  B<sub>2</sub> = (<i>&pi;</i><sub><code>R</code></sub>/<i>&pi;</i><sub><code>Y</code></sub>) log<sub>e</sub> E<sub>1</sub> - (1/<i>&pi;</i><sub><code>Y</code></sub>) log<sub>e</sub> E<sub>3</sub>,
     *  <br>
     *  B<sub>3</sub> = - log<sub>e</sub> E<sub>1</sub>,
     *  <br>
     *  E<sub>1</sub> = 1 - <i>q</i>/<i>f</i><sub><code>R</code><code>Y</code></sub>,
     *  <br>
     *  E<sub>2</sub> = 1 - <i>q</i>/(2<i>&pi;</i><sub><code>R</code></sub>) - <i>&pi;</i><sub><code>R</code></sub><i>p</i><sub>1</sub>/<i>f</i><sub><code>A</code><code>G</code></sub>,
     *  <br>
     *  E<sub>3</sub> = 1 - <i>q</i>/(2<i>&pi;</i><sub><code>Y</code></sub>) - <i>&pi;</i><sub><code>Y</code></sub><i>p</i><sub>2</sub>/<i>f</i><sub><code>C</code><code>T</code></sub>,
     *  <br>
     *  &gamma; = [Var(B<sub>2</sub>)-Cov(B<sub>1</sub>,B<sub>2</sub>)] / [Var(B<sub>1</sub>)+Var(B<sub>2</sub>)-2Cov(B<sub>1</sub>,B<sub>2</sub>)]
     *  <br>
     *  &nbsp; &nbsp; &nbsp; + <i>&pi;</i><sub><code>R</code></sub><i>&pi;</i><sub><code>Y</code></sub>[Cov(B<sub>1</sub>,B<sub>3</sub>)-Cov(B<sub>2</sub>,B<sub>3</sub>)]/[(<i>&pi;</i><sub><code>A</code></sub><i>&pi;</i><sub><code>G</code></sub>+<i>&pi;</i><sub><code>C</code></sub><i>&pi;</i><sub><code>T</code></sub>)(Var(B<sub>1</sub>)+Var(B<sub>2</sub>)-2Cov(B<sub>1</sub>,B<sub>2</sub>))]
     *  <br>
     *  Var(B<sub>1</sub>) = <i>n</i><sup>-1</sup> [ <i>a</i><sub>1</sub><sup>2</sup><i>q</i> + <i>a</i><sub>2</sub><sup>2</sup><i>p</i><sub>1</sub> - (<i>a</i><sub>1</sub><i>q</i> + <i>a</i><sub>2</sub><i>p</i><sub>1</sub>)<sup>2</sup> ],
     *  <br>
     *  Var(B<sub>2</sub>) = <i>n</i><sup>-1</sup> [ <i>a</i><sub>3</sub><sup>2</sup><i>q</i> + <i>a</i><sub>4</sub><sup>2</sup><i>p</i><sub>2</sub> - (<i>a</i><sub>3</sub><i>q</i> + <i>a</i><sub>4</sub><i>p</i><sub>2</sub>)<sup>2</sup> ],
     *  <br>
     *  Cov(B<sub>1</sub>,B<sub>2</sub>) = <i>n</i><sup>-1</sup> [ <i>a</i><sub>1</sub><i>a</i><sub>3</sub><i>q</i>(1-<i>q</i>) - <i>a</i><sub>1</sub><i>a</i><sub>4</sub><i>q</i><i>p</i><sub>2</sub> - <i>a</i><sub>2</sub><i>a</i><sub>4</sub><i>p</i><sub>1</sub><i>p</i><sub>2</sub> ],
     *  <br>
     *  Cov(B<sub>1</sub>,B<sub>3</sub>) = <i>a</i><sub>5</sub><i>q</i><i>n</i><sup>-1</sup> [ <i>a</i><sub>1</sub>(1-<i>q</i>) - <i>a</i><sub>2</sub><i>p</i><sub>1</sub> ],
     *  <br>
     *  Cov(B<sub>2</sub>,B<sub>3</sub>) = <i>a</i><sub>5</sub><i>q</i><i>n</i><sup>-1</sup> [ <i>a</i><sub>3</sub>(1-<i>q</i>) - <i>a</i><sub>4</sub><i>p</i><sub>2</sub> ],
     *  <br>
     *  <i>a</i><sub>1</sub> = (E<sub>2</sub><sup>-1</sup> - E<sub>1</sub><sup>-1</sup>) / (2 <i>&pi;</i><sub><code>R</code></sub><sup>2</sup>),
     *  <br>
     *  <i>a</i><sub>2</sub> = (<i>f</i><sub><code>A</code><code>G</code></sub> E<sub>2</sub>)<sup>-1</sup>,
     *  <br>
     *  <i>a</i><sub>3</sub> = (E<sub>3</sub><sup>-1</sup> - E<sub>1</sub><sup>-1</sup>) / (2 <i>&pi;</i><sub><code>Y</code></sub><sup>2</sup>),
     *  <br>
     *  <i>a</i><sub>4</sub> = (<i>f</i><sub><code>C</code><code>T</code></sub> E<sub>3</sub>)<sup>-1</sup>,
     *  <br>
     *  <i>a</i><sub>5</sub> = (<i>f</i><sub><code>R</code><code>Y</code></sub> E<sub>1</sub>)<sup>-1</sup>,
     *  <br>
     *  <i>&pi;</i><sub><code>R</code></sub> = <i>&pi;</i><sub><code>A</code></sub> + <i>&pi;</i><sub><code>G</code></sub>,
     *  <br>
     *  <i>&pi;</i><sub><code>Y</code></sub> = <i>&pi;</i><sub><code>C</code></sub> + <i>&pi;</i><sub><code>T</code></sub>,
     *  </ul>
     *  <br>
     *  where <i>p</i><sub>1</sub> and <i>p</i><sub>2</sub> are the proportion of sites with <code>A</code><code>G</code> and <code>C</code><code>T</code> differences, respectively, <i>q</i> the proportion of sites with transversional nucleotide differences, <i>n</i> the length of the two sequences, and <i>&pi;</i><sub><code>I</code></sub> the frequency of nucleotide <code>I</code> in the two sequences.
     *  When the <code>homogeneity</code> option is set to <code>true</code>, then
     *  <br><br>
     *  <ul>
     *  <i>f</i><sub><code>I</code><code>J</code></sub> = 2 <i>&pi;</i><sub><code>I</code></sub> <i>&pi;</i><sub><code>J</code></sub>,
     *  </ul>
     *  <br>
     *  else
     *  <br><br>
     *  <ul>
     *  <i>f</i><sub><code>I</code><code>J</code></sub> = <i>&pi;</i><sub>1<code>I</code></sub> <i>&pi;</i><sub>2<code>J</code></sub> + <i>&pi;</i><sub>1<code>J</code></sub> <i>&pi;</i><sub>2<code>I</code></sub>,
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><i>i</i><code>I</code></sub> is the frequency of nucleotide <code>I</code> in the sequence <i>i</i>.
     *  <br><br>
     *  Rzhetsky, A. and Nei, M. (1995) <i>Tests of applicability of several substitution models for DNA sequence data</i>, Molecular Biology and Evolution, 12(1):131-151
     *  <br>
     *  Tamura, K. and Kumar, S. (2002) <i>Evolutionary distance estimation under heterogeneous substitution pattern among lineage</i>, Molecular Biology and Evolution, 19(10):1727-1736
     *  </ul>
     *  <br>
     *  <b><code><a name="TN93">NucleotideEvolutionaryModel.TN93</a></code></b>
     *  <ul>
     *  This distance is given by the same formula as the <code><a href="#HKY85">HKY85</a></code> distance but with
     *  <br><br>
     *  <ul>
     *  &gamma; = <i>&pi;</i><sub><code>A</code></sub><i>&pi;</i><sub><code>G</code></sub> / (<i>&pi;</i><sub><code>A</code></sub><i>&pi;</i><sub><code>G</code></sub> + <i>&pi;</i><sub><code>C</code></sub><i>&pi;</i><sub><code>T</code></sub>),
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><code>I</code></sub> the frequency of nucleotide <code>I</code> in the two sequences.
     *  <br><br>
     *  Rzhetsky, A. and Nei, M. (1995) <i>Tests of applicability of several substitution models for DNA sequence data</i>, Molecular Biology and Evolution, 12(1):131-151
     *  <br>
     *  Tamura, K. and Kumar, S. (2002) <i>Evolutionary distance estimation under heterogeneous substitution pattern among lineage</i>, Molecular Biology and Evolution, 19(10):1727-1736
     *  <br>
     *  Tamura, K. and Nei, M. (1993) <i>Estimation of the number of nucleotide substitutions in the control region of mitochondrial DNA in human and chimpanzees</i>, Molecular Biology and Evolution, 10:512-526
     *  </ul>
     *  <br>
     *  <b><code><a name="T92">NucleotideEvolutionaryModel.T92</a></code></b>
     *  <ul>
     *  This distance is given by
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - <i>b</i><sub>1</sub> log<sub>e</sub> ( 1 - <i>p</i>/<i>b</i><sub>2</sub> - <i>q</i> )
     *  - 0.5 ( 1 - <i>b</i><sub>1</sub> ) log<sub>e</sub> ( 1 - 2<i>q</i> ),
     *  </ul>
     *  <br>
     *  where <i>p</i> and <i>q</i> are the proportion of sites with transitional and transversional nucleotide differences, respectively, and
     *  <br><br>
     *  <ul>
     *  <i>b</i><sub>1</sub> = 2 (<i>&pi;</i><sub><code>C</code></sub> + <i>&pi;</i><sub><code>G</code></sub>) (1 - <i>&pi;</i><sub><code>C</code></sub> - <i>&pi;</i><sub><code>G</code></sub>),
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><code>I</code></sub> the frequency of nucleotide <code>I</code> in the two sequences.
     *  When the <code>homogeneity</code> option is set to <code>true</code>, then 
     *  <br><br>
     *  <ul>
     *  <i>b</i><sub>2</sub> = <i>b</i><sub>1</sub>,
     *  </ul>
     *  <br>
     *  else
     *  <br><br>
     *  <ul>
     *  <i>b</i><sub>2</sub> = (<i>&pi;</i><sub>1<code>C</code></sub> + <i>&pi;</i><sub>1<code>G</code></sub>) (1 - <i>&pi;</i><sub>2<code>C</code></sub> - <i>&pi;</i><sub>2<code>G</code></sub>) + (<i>&pi;</i><sub>2<code>C</code></sub> + <i>&pi;</i><sub>2<code>G</code></sub>) (1 - <i>&pi;</i><sub>1<code>C</code></sub> - <i>&pi;</i><sub>1<code>G</code></sub>),
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><i>i</i><code>I</code></sub> is the frequency of nucleotide <code>I</code> in the sequence <i>i</i>.
     *  <br><br>
     *  Tamura, K. (1992) <i>Estimation of the number of nucleotide substitutions when there are strong transition-transversion and G+C-content biases</i>, Molecular Biology and Evolution, 9(4):678-687
     *  <br>
     *  Tamura, K. and Kumar, S. (2002) <i>Evolutionary distance estimation under heterogeneous substitution pattern among lineage</i>, Molecular Biology and Evolution, 19(10):1727-1736
     *  </ul>
     *  <br>
     *  <b><code><a name="K81_3ST">NucleotideEvolutionaryModel.K81_3ST</a></code></b>
     *  <ul>
     *  The <code>homogeneity</code> option does not modify the returned value.
     *  This distance is given by
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - 0.25 [ log<sub>e</sub> ( 1 - 2 <i>p</i> - 2 <i>q</i> ) + log<sub>e</sub> ( 1 - 2 <i>p</i> - 2 <i>r</i> ) + log<sub>e</sub> ( 1 - 2 <i>q</i> - 2 <i>r</i> ) ],
     *  </ul>
     *  <br>
     *  where <i>p</i> is the proportion of sites with transitional nucleotide differences (i.e. <code>A</code><code>G</code> and <code>C</code><code>T</code> differences), <i>q</i> the proportion of sites with <code>A</code><code>T</code> and <code>C</code><code>G</code> differences, and <i>r</i> the proportion of sites with <code>A</code><code>C</code> and <code>G</code><code>T</code> differences.
     *  <br><br>
     *  Kimura, M. (1981) <i>Estimation of evolutionary distances between homologous nucleotide sequences</i>, Proceedings of the National Academy of Science USA, 78(1):454-458.
     *  </ul>
     *  <br>
     *  <b><code><a name="GIN82">NucleotideEvolutionaryModel.GIN82</a></code></b>
     *  <ul>
     *  The <code>homogeneity</code> option does not modify the returned value, since no formula exists for the heterogeneity assumption of the substitution pattern between sequences.
     *  This distance is given by 
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - &pi; &pi;' log<sub>e</sub> [ B<sub>1</sub> / ( &pi; &pi;' ) ]
     *  <br>
     *  &nbsp; &nbsp; &nbsp; - ( 2 <i>&pi;</i><sub><code>A</code></sub> <i>&pi;</i><sub><code>T</code></sub> / &pi; ) log<sub>e</sub> [ &pi; ( B<sub>2</sub> - B<sub>1</sub> + 3B<sub>4</sub>/B<sub>1</sub> ) / ( 3 <i>&pi;</i><sub><code>A</code></sub> <i>&pi;</i><sub><code>T</code></sub> ) ]
     *  <br>
     *  &nbsp; &nbsp; &nbsp; - ( 2 <i>&pi;</i><sub><code>C</code></sub> <i>&pi;</i><sub><code>G</code></sub> / &pi;' ) log<sub>e</sub> [ &pi;' ( B<sub>3</sub> - B<sub>1</sub> + 3B<sub>5</sub>/B<sub>1</sub> ) / ( 3 <i>&pi;</i><sub><code>C</code></sub> <i>&pi;</i><sub><code>G</code></sub> ) ],
     *  </ul>
     *  <br>
     *  where
     *  <br><br>
     *  <ul>
     *  &pi; = <i>&pi;</i><sub><code>A</code></sub> + <i>&pi;</i><sub><code>T</code></sub>,
     *  <br>
     *  &pi;' = <i>&pi;</i><sub><code>C</code></sub> + <i>&pi;</i><sub><code>G</code></sub>,
     *  <br>
     *  B<sub>1</sub> = &pi; &pi;' - &psi;<sub><code>A</code></sub><sub><code>C</code></sub> - &psi;<sub><code>A</code></sub><sub><code>G</code></sub> - &psi;<sub><code>C</code></sub><sub><code>T</code></sub> - &psi;<sub><code>G</code></sub><sub><code>T</code></sub>,
     *  <br>
     *  B<sub>2</sub> = &psi;<sub><code>A</code></sub><sub><code>A</code></sub> + &psi;<sub><code>T</code></sub><sub><code>T</code></sub> - &psi;<sub><code>A</code></sub><sub><code>T</code></sub> - &pi;<sup>2</sup> + 3 <i>&pi;</i><sub><code>A</code></sub> <i>&pi;</i><sub><code>T</code></sub>,
     *  <br>
     *  B<sub>3</sub> = &psi;<sub><code>C</code></sub><sub><code>C</code></sub> + &psi;<sub><code>G</code></sub><sub><code>G</code></sub> - &psi;<sub><code>C</code></sub><sub><code>G</code></sub> - &pi;'<sup>2</sup> + 3 <i>&pi;</i><sub><code>C</code></sub> <i>&pi;</i><sub><code>G</code></sub>,
     *  <br>
     *  B<sub>4</sub> = ( <i>&pi;</i><sub><code>A</code></sub> &pi;' - &psi;<sub><code>A</code></sub><sub><code>C</code></sub> - &psi;<sub><code>A</code></sub><sub><code>G</code></sub> ) ( <i>&pi;</i><sub><code>T</code></sub> &pi;' - &psi;<sub><code>C</code></sub><sub><code>T</code></sub> - &psi;<sub><code>G</code></sub><sub><code>T</code></sub> )
     *  <br>
     *  B<sub>5</sub> = ( <i>&pi;</i><sub><code>C</code></sub> &pi; - &psi;<sub><code>A</code></sub><sub><code>C</code></sub> - &psi;<sub><code>C</code></sub><sub><code>T</code></sub> ) ( <i>&pi;</i><sub><code>G</code></sub> &pi; - &psi;<sub><code>A</code></sub><sub><code>G</code></sub> - &psi;<sub><code>G</code></sub><sub><code>T</code></sub> ),
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><code>I</code></sub> the frequency of nucleotide <code>I</code>, and &psi;<sub><code>I</code><code>J</code></sub> the frequency of nucleotide pair <code>I</code> and <code>J</code> when the two sequences are compared.
     *  <br><br>
     *  Gojobori, T., Ishii, K. and Nei, M. (1982) <i>Estimation of average number of nucleotide substitutions when the rate of substitution varies with nucleotide</i>, Journal of Molecular Evolution, 18:414-423
     *  </ul>
     *  <br>
     *  <b><code><a name="TK81">NucleotideEvolutionaryModel.TK81</a></code></b>
     *  <ul>
     *  The <code>homogeneity</code> option does not modify the returned value.
     *  This distance is given by 
     *  <br><br>
     *  <ul>
     *  <i>d</i> = - 0.25 log<sub>e</sub> [ &Omega;<sup>-1</sup> ( (&psi;<sub><code>A</code><code>A</code></sub> + &psi;<sub><code>T</code><code>T</code></sub> + &psi;<sub><code>A</code><code>T</code></sub>) (&psi;<sub><code>C</code><code>C</code></sub> + &psi;<sub><code>G</code><code>G</code></sub> - &psi;<sub><code>C</code><code>G</code></sub>) - (<i>p</i> - <i>q</i>)<sup>2</sup>/4 ) ]
     *  <br>
     *  &nbsp; &nbsp; &nbsp; - ( 2 &Omega; - 1/4 ) log<sub>e</sub> [ 1 - &Omega;<sup>-1</sup>(<i>p</i> + <i>q</i>)/2 ],
     *  </ul>
     *  <br>
     *  with
     *  <br><br>
     *  <ul>
     *  &Omega; = (<i>&pi;</i><sub><code>A</code></sub> + <i>&pi;</i><sub><code>T</code></sub>)(1 - <i>&pi;</i><sub><code>A</code></sub> - <i>&pi;</i><sub><code>T</code></sub>),
     *  <br>
     *  <i>p</i> = &psi;<sub><code>A</code><code>G</code></sub> + &psi;<sub><code>C</code><code>T</code></sub>, and
     *  <br>
     *  <i>q</i> = &psi;<sub><code>A</code><code>C</code></sub> + &psi;<sub><code>G</code><code>T</code></sub>,
     *  </ul>
     *  <br>
     *  where <i>&pi;</i><sub><code>I</code></sub> the frequency of nucleotide <code>I</code>, and &psi;<sub><code>I</code><code>J</code></sub> the frequency of nucleotide pair <code>I</code> and <code>J</code> when the two sequences are compared.
     *  <br><br>
     *  Takahata, N. and Kimura, M. (1981) <i>A model of evolutionary base substitutions and its application with special reference to rapid change of pseudogenes</i>, Genetics, 98:641-657
     *  </ul>
     *  @param model a <code>NucleotideEvolutionaryModel</code>
     *  @param npm a <code>NucleotideFrequency</code>
     *  @param homogeneity a <code>boolean</code> to indicate whether the homogeneity of substitution pattern is assumed or not
     *  @return an evolutionary distance estimation if it is possible; <code>NaN</code> otherwise
     */
    public static double getDistance(Model model , NucleotideFrequency npm , boolean homogeneity) {
	if ( P(npm) == 0 ) 
	    return 0;
        switch(model){
            case F81: return F81( npm , homogeneity );
            case JC69: return JC(npm);
            case TN82_EI: return TN82_EI( npm , homogeneity );
            case K80: return K2P(npm);
            case F84: return F84(npm);
            case HKY85: return HKY85( npm , homogeneity );
            case TN93: return TN93(npm, homogeneity);
            case T92: return T92( npm , homogeneity );
            case K81_3ST: return K81_3ST(npm);
            case GIN82: return F84(npm);
            case TK81: return TK81(npm);
            case P: return P(npm);
            case TN84: return UTN84(npm);
            default: return Double.MAX_EXPONENT;
        }
    }

    /**
     *  Returns an evolutionary distance estimated from a specified <code>NucleotideFrequency</code> under a specified <code>NucleotideEvolutionaryModel</code> with among-site rate heterogeneity assumed to follow a gamma distribution with specified shape parameter.
     *  <br><br>
     *  <b><code><a name="JC_G">NucleotideEvolutionaryModel.JC69</a></code></b>
     *  <ul>
     *  This distance is given by the same formula as the <code><a href="#JC">JC69</a></code> distance but with replacing the function log<sub>e</sub>(<i>x</i>) with 
     *  <br><br>
     *  <ul>
     *  <i>&alpha;</i>(1 - <i>x</i><sup>-1/<i>&alpha;</i></sup>), 
     *  </ul>
     *  <br>
     *  where <i>&alpha;</i> is the specified shape parameter of the gamma distribution (<code>alpha</code>).
     *  <br><br>
     *  Golding, G. B. (1983) <i>Estimates of DNA and protein sequence divergence: an examination of some assumptions</i>, Molecular Biology and Evolution, 1:125-142
     *  <br>
     *  Jin, L. and Nei, M. (1990) <i>Limitations of the evolutionary parsimony method of phylogenetic analysis</i>, Molecular Biology and Evolution, 7:82-102
     *  <br>
     *  Nei, M. and Gojobori, T. (1986) <i>Simple methods for estimating the number of synonymous and nonsynonymous nucleotide substitutions</i>, Molecular Biology and Evolution, 3, 418-426
     *  </ul>
     *  <br>
     *  <b><code><a name="K2P_G">NucleotideEvolutionaryModel.K80</a></code></b>
     *  <ul>
     *  This distance is given by the same formula as the <code><a href="#K2P">K80</a></code> distance but with replacing the function log<sub>e</sub>(<i>x</i>) with 
     *  <br><br>
     *  <ul>
     *  <i>&alpha;</i>(1 - <i>x</i><sup>-1/<i>&alpha;</i></sup>), 
     *  </ul>
     *  <br>
     *  where <i>&alpha;</i> is the specified shape parameter of the gamma distribution (<code>alpha</code>).
     *  <br><br>
     *  Jin, L. and Nei, M. (1990) <i>Limitations of the evolutionary parsimony method of phylogenetic analysis</i>, Molecular Biology and Evolution, 7:82-102
     *  </ul>
     *  <br>
     *  <b><code><a name="TN93_G">NucleotideEvolutionaryModel.TN93</a></code></b>
     *  <ul>
     *  This distance is given by the same formula as the <code><a href="#TN93">TN93</a></code> distance but with replacing the function log<sub>e</sub>(<i>x</i>) with 
     *  <br><br>
     *  <ul>
     *  <i>&alpha;</i>(1 - <i>x</i><sup>-1/<i>&alpha;</i></sup>), 
     *  </ul>
     *  <br>
     *  where <i>&alpha;</i> is the specified shape parameter of the gamma distribution (<code>alpha</code>).
     *  <br><br>
     *  Tamura, K. and Kumar, S. (2002) <i>Evolutionary distance estimation under heterogeneous substitution pattern among lineage</i>, Molecular Biology and Evolution, 19(10):1727-1736
     *  <br>
     *  Tamura, K. and Nei, M. (1993) <i>Estimation of the number of nucleotide substitutions in the control region of mitochondrial DNA in human and chimpanzees</i>, Molecular Biology and Evolution, 10:512-526
     *  </ul>
     *  @param model a <code>NucleotideEvolutionaryModel</code>
     *  @param npm a <code>NucleotideFrequency</code>
     *  @param alpha the shape parameter of the gamma distribution
     *  @param homogeneity a <code>boolean</code> to indicate whether the homogeneity of substitution pattern is assumed or not
     *  @return an gamma-corrected evolutionary distance estimation if it is possible; <code>NaN</code> otherwise
     */
    public static double getDistance(NucleotideEvolutionaryModel model , NucleotideFrequency npm , double alpha , boolean homogeneity) {
	if ( P(npm) == 0 ) 
	    return 0;
	if ( model.equals(NucleotideEvolutionaryModel.JC69) )
	    return JC( npm , alpha );
	if ( model.equals(NucleotideEvolutionaryModel.K80) )
	    return K2P( npm , alpha );
	if ( model.equals(NucleotideEvolutionaryModel.F81) )
	    return F81( npm , alpha , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.K81_3ST) )
	    return K81_3ST( npm , alpha );
	if ( model.equals(NucleotideEvolutionaryModel.F84) )
	    return F84( npm , alpha );
	if ( model.equals(NucleotideEvolutionaryModel.T92) )
	    return T92( npm , alpha , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.TN93) )
	    return TN93( npm , alpha , homogeneity );
	return Double.NaN;
    }

    /**
     *  Returns an unbiased evolutionary distance estimated from a specified <code>NucleotideFrequency</code> under a specified <code>NucleotideEvolutionaryModel</code>.
     *  The unbiased distances are obtained by expanding the logarithmic terms into Taylor's series.
     *  If <i>p</i>=<i>k</i>/<i>n</i> is a proportion of nucleotide differences (for example the <i>p</i>-distance), then one have
     *  <br><br>
     *  <ul>
     *  - <i>b</i> log<sub>e</sub> ( 1 - <i>p</i>/<i>b</i> ) = &Sigma;<sub><i>i</i>=1...<i>k</i></sub> &nbsp; <i>f</i><sub><i>k</i>,<i>n</i>,<i>b</i></sub>(<i>i</i>)
     *  </ul>
     *  <br>
     *  where
     *  <br><br>
     *  <ul>
     *  <i>f</i><sub><i>k</i>,<i>n</i>,<i>b</i></sub>(<i>i</i>) = <i>k</i><sup>(<i>i</i>)</sup> / ( <i>i</i> <i>b</i><sup><i>i</i>-1</sup> <i>n</i><sup>(<i>i</i>)</sup> ), &nbsp; with, by definition: &nbsp;
     *  <i>x</i><sup>(<i>i</i>)</sup> = <i>x</i><code>!</code> / (<i>x</i>-<i>i</i>)<code>!</code>.
     *  </ul>
     *  <br>
     *  If <i>p</i>=<i>s</i>/<i>n</i> and <i>q</i>=<i>v</i>/<i>n</i> are two distinct proportion of nucleotide differences (for example the proportion of transition and transversion nucleotide differences), then one have
     *  <br><br>
     *  <ul>
     *  - log<sub>e</sub> ( 1 - <i>a</i><i>p</i> - <i>b</i><i>q</i> ) = &Sigma;<sub><i>i</i>=1...<i>s</i>+<i>v</i></sub> &nbsp; <i>f</i><sub><i>s</i>,<i>v</i>,<i>n</i>,<i>a</i>,<i>b</i></sub>(<i>i</i>) / ( <i>i</i> <i>n</i><sup>(<i>i</i>)</sup> ),
     *  </ul>
     *  <br>
     *  where
     *  <br><br>
     *  <ul>
     *  <i>f</i><sub><i>s</i>,<i>v</i>,<i>n</i>,<i>a</i>,<i>b</i></sub>(<i>i</i>) = &Sigma;<sub><i>j</i>=<code>MIN</code>...<code>MAX</code></sub> &nbsp; <i>g</i><sub><i>s</i>,<i>v</i>,<i>a</i>,<i>b</i>,<i>i</i></sub>(<i>j</i>),
     *  <br>
     *  <code>MIN</code> = max( 0 ; <i>i</i> - <i>v</i> ),
     *  <br>
     *  <code>MAX</code> = min( <i>i</i> ; <i>s</i> ),
     *  <br>
     *  <i>g</i><sub><i>s</i>,<i>v</i>,<i>a</i>,<i>b</i>,<i>i</i></sub>(<i>j</i>) = <i>i</i><code>!</code> <i>a</i><sup><i>j</i></sup> <i>b</i><sup><i>i-j</i></sup> <i>s</i><sup>(<i>j</i>)</sup> <i>v</i><sup>(<i>i</i>-<i>j</i>)</sup> / ( <i>j</i><code>!</code> (<i>i</i>-<i>j</i>)<code>!</code> ), &nbsp; with, by definition: &nbsp;
     *  <i>x</i><sup>(<i>i</i>)</sup> = <i>x</i><code>!</code> / (<i>x</i>-<i>i</i>)<code>!</code>.
     *  </ul>
     *  <br>
     *  Using these mathematical modifications, all distances are available (<code><a href="#JC">JC69</a></code>, <code><a href="#F81">F81</a></code>, <code><a href="#TN82_EI">TN82_EI</a></code>, <code><a href="#K2P">K80</a></code>, <code><a href="#F84">F84</a></code>, <code><a href="#HKY85">HKY85</a></code>, <code><a href="#TN93">TN93</a></code>, <code><a href="#T92">T92</a></code> and <code><a href="#K81_3ST">K81_3ST</a></code>), except <code><a href="#GIN82">GIN82</a></code> and <code><a href="#TK81">TK81</a></code>, whose arithmetic formulations do not allow using the previous equations.
     *  <br><br>
     *  Rzhetsky, A. and Nei, M. (1994) <i>Unbiased estimates of the number of nucleotide substitutions when substitution rate varies among different sites</i>, Journal of Molecular Evolution, 38:295-299
     *  <br>
     *  Tajima, F. (1993) <i>Unbiased estimation of evolutionary distance between nucleotide sequences</i>, Molecular Biology and Evolution, 10(3):677-688
     *  @param model a <code>NucleotideEvolutionaryModel</code>
     *  @param npm a <code>NucleotideFrequency</code>
     *  @param homogeneity a <code>boolean</code> to indicate whether the homogeneity of substitution pattern is assumed or not
     *  @return an unbiased evolutionary distance estimation
     */
    public static double getUnbiasedDistance(NucleotideEvolutionaryModel model , NucleotideFrequency npm , boolean homogeneity) {
	if ( P(npm) == 0 ) 
	    return 0;
	if ( model.equals(NucleotideEvolutionaryModel.F81) )
	    return UF81( npm , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.JC69) )
	    return UJC( npm );
	if ( model.equals(NucleotideEvolutionaryModel.TN82_EI) )
	    return UTN82_EI( npm , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.K80) )
	    return UK2P( npm );
	if ( model.equals(NucleotideEvolutionaryModel.F84) )
	    return UF84( npm );
	if ( model.equals(NucleotideEvolutionaryModel.HKY85) )
	    return UHKY85( npm , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.TN93) )
	    return UTN93( npm , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.T92) )
	    return UT92( npm , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.K81_3ST) )
	    return UK81_3ST( npm );
	return Double.NaN;
    }

    /**
     *  Returns an unbiased evolutionary distance estimated from a specified <code>NucleotideFrequency</code> under a specified <code>NucleotideEvolutionaryModel</code> with among-site rate heterogeneity assumed to follow a gamma distribution with specified shape parameter.
     *  The unbiased distances are obtained by expanding the function (1-<i>x</i>)<sup>-<i>m</i></sup> into Taylor's series.
     *  If <i>p</i>=<i>k</i>/<i>n</i> is a proportion of nucleotide differences (for example the <i>p</i>-distance), then one have
     *  <br><br>
     *  <ul>
     *  <i>b</i> &alpha; [ ( 1 - <i>p</i>/<i>b</i> )<sup>-1/&alpha;</sup> - 1 ] = &Sigma;<sub><i>i</i>=1...<i>k</i></sub> &nbsp; <i>f</i><sub><i>k</i>,<i>n</i>,<i>b</i>,&alpha;</sub>(<i>i</i>)
     *  </ul>
     *  <br>
     *  where
     *  <br><br>
     *  <ul>
     *  <i>f</i><sub><i>k</i>,<i>n</i>,<i>b</i>,&alpha;</sub>(<i>i</i>) = [ <i>k</i><sup>(<i>i</i>)</sup> / ( <i>i</i><code>!</code> &alpha;<sup><i>i</i>-1</sup> <i>b</i><sup><i>i</i>-1</sup> <i>n</i><sup>(<i>i</i>)</sup> ) ] &Pi;<sub><i>j</i>=1...<i>i</i></sub>  [ 1 + &alpha;(<i>j</i> - 1) ], &nbsp; with, by definition: &nbsp;
     *  <i>x</i><sup>(<i>i</i>)</sup> = <i>x</i><code>!</code> / (<i>x</i>-<i>i</i>)<code>!</code>.
     *  </ul>
     *  <br>
     *  If <i>p</i>=<i>s</i>/<i>n</i> and <i>q</i>=<i>v</i>/<i>n</i> are two distinct proportion of nucleotide differences (for example the proportion of transition and transversion nucleotide differences), then one have
     *  <br><br>
     *  <ul>
     *  &alpha; [ ( 1 - <i>a</i><i>p</i> - <i>b</i><i>q</i> )<sup>-1/&alpha;</sup> - 1 ] = &alpha; &Sigma;<sub><i>i</i>=1...<i>s</i>+<i>v</i></sub> &nbsp; <i>h</i><sub><i>n</i>,&alpha;</sub>(<i>i</i>) <i>f</i><sub><i>s</i>,<i>v</i>,<i>n</i>,<i>a</i>,<i>b</i></sub>(<i>i</i>)
     *  </ul>
     *  <br>
     *  where
     *  <br><br>
     *  <ul>
     *  <i>f</i><sub><i>s</i>,<i>v</i>,<i>n</i>,<i>a</i>,<i>b</i></sub>(<i>i</i>) = &Sigma;<sub><i>j</i>=<code>MIN</code>...<code>MAX</code></sub> &nbsp; <i>g</i><sub><i>s</i>,<i>v</i>,<i>a</i>,<i>b</i>,<i>i</i></sub>(<i>j</i>),
     *  <br>
     *  <code>MIN</code> = max( 0 ; <i>i</i> - <i>v</i> ),
     *  <br>
     *  <code>MAX</code> = min( <i>i</i> ; <i>s</i> ),
     *  <br>
     *  <i>g</i><sub><i>s</i>,<i>v</i>,<i>a</i>,<i>b</i>,<i>i</i></sub>(<i>j</i>) = <i>i</i><code>!</code> <i>a</i><sup><i>j</i></sup> <i>b</i><sup><i>i-j</i></sup> <i>s</i><sup>(<i>j</i>)</sup> <i>v</i><sup>(<i>i</i>-<i>j</i>)</sup> / ( <i>j</i><code>!</code> (<i>i</i>-<i>j</i>)<code>!</code> ), &nbsp; with, by definition: &nbsp;
     *  <i>x</i><sup>(<i>i</i>)</sup> = <i>x</i><code>!</code> / (<i>x</i>-<i>i</i>)<code>!</code>, and
     *  <br>
     *  <i>h</i><sub><i>n</i>,&alpha;</sub>(<i>i</i>) = [ &Pi;<sub><i>j</i>=1...<i>i</i></sub> ( 1 + &alpha;(<i>j</i> - 1) ) ] / ( <i>i</i><code>!</code> &alpha;<sup><i>i</i></sup> <i>n</i><sup>(<i>i</i>)</sup> ).
     *  </ul>
     *  <br>
     *  Using these mathematical modifications, the three gamma distances are available (<code><a href="#JC_G">JC69+&Gamma;</a></code>, <code><a href="#K2P_G">K80+&Gamma;</a></code> and <code><a href="#TN93_G">TN93+&Gamma;</a></code>).
     *  <br><br>
     *  Rzhetsky, A. and Nei, M. (1994) <i>Unbiased estimates of the number of nucleotide substitutions when substitution rate varies among different sites</i>, Journal of Molecular Evolution, 38:295-299
     *  @param model a <code>NucleotideEvolutionaryModel</code>
     *  @param npm a <code>NucleotideFrequency</code>
     *  @param alpha the shape parameter of the gamma distribution
     *  @param homogeneity a <code>boolean</code> to indicate whether the homogeneity of substitution pattern is assumed or not
     *  @return an unbiased gamma-corrected evolutionary distance estimation
     */
    public static double getUnbiasedDistance(NucleotideEvolutionaryModel model , NucleotideFrequency npm , double alpha , boolean homogeneity) {
	if ( P(npm) == 0 ) 
	    return 0;
	if ( model.equals(NucleotideEvolutionaryModel.JC69) )
	    return UJC( npm , alpha );
	if ( model.equals(NucleotideEvolutionaryModel.K80) )
	    return UK2P( npm , alpha );
	if ( model.equals(NucleotideEvolutionaryModel.F81) )
	    return UF81( npm , alpha , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.K81_3ST) )
	    return UK81_3ST( npm , alpha );
	if ( model.equals(NucleotideEvolutionaryModel.F84) )
	    return UF84( npm , alpha );
	if ( model.equals(NucleotideEvolutionaryModel.T92) )
	    return UT92( npm , alpha , homogeneity );
	if ( model.equals(NucleotideEvolutionaryModel.TN93) )
	    return UTN93( npm , alpha , homogeneity );
	return Double.NaN;
    }

    



    
    //##################################
    //##################################
    //##### evolutionary distances #####
    //##################################
    //##################################

    // generic method to get value of -b.log(1-p/b)
    private static double log1p( double pp , double bb ) {
	if ( pp == 0 )
	    return 0;
	return - bb * Math.log( 1 - pp / bb );
    }
    
    // generic method to get value of -log(1 - a.p - b.q)
    private static double log2p( double pp , double qq , double aa , double bb ) {
	if ( ((aa == 0) || (pp == 0))
	     && ((bb == 0) || (qq == 0)) )
	    return 0;
	return - Math.log( 1 - aa * pp - bb * qq );
    }
    
    private static double P( NucleotideFrequency npm ) {
	//_f = 1 - ((double) (npm.getAA() + npm.getCC() + npm.getGG() + npm.getTT())) / ((double) npm.size());
	//if ( Double.isNaN(_f) || Double.isInfinite(_f) )
	//System.out.println(npm.getAA() + " " + npm.getCC() + " " + npm.getGG() + " " + npm.getTT() + " " + npm.size());
	return 1 - ((double) (npm.getAA() + npm.getCC() + npm.getGG() + npm.getTT())) / ((double) npm.size());
    }

    private static double TS( NucleotideFrequency npm ) {
	return ((double) (npm.getAG() + npm.getGA() + npm.getCT() + npm.getTC())) / ((double) npm.size());
    }

    private static double TV( NucleotideFrequency npm ) {
	return ((double) (npm.getAC() + npm.getCA() 
			  + npm.getAT() + npm.getTA() 
			  + npm.getCG() + npm.getGC() 
			  + npm.getGT() + npm.getTG())) / ((double) npm.size());
    }

    private static double TSTV( NucleotideFrequency npm ) {
	return log2p( TS(npm) , TV(npm) , 2 , 1 ) / log1p( TV(npm) , 0.5 ) - 1;
    }

    private static double JC( NucleotideFrequency npm ) {
	return log1p( P(npm) , 0.75 );
    }
    
    private static double F81( NucleotideFrequency npm , boolean homogeneity ) {
	_f = npm.total();
	b1 = 1 - square(npm.getA()/_f) - square(npm.getC()/_f) - square(npm.getG()/_f) - square(npm.getT()/_f);
	if ( homogeneity )
	    return log1p( P(npm) , b1 );
	_g = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	b2 = 1 - (((double) npm.getAfirst()) * ((double) npm.getAsecond()))/_g 
	    - (((double) npm.getCfirst()) * ((double) npm.getCsecond()))/_g
	    - (((double) npm.getGfirst()) * ((double) npm.getGsecond()))/_g 
	    - (((double) npm.getTfirst()) * ((double) npm.getTsecond()))/_g;
	return b1 * log1p( P(npm) , b2 ) / b2;
    }
    
    private static double TN82_EI( NucleotideFrequency npm , boolean homogeneity ) {
	_f = square(npm.total());
	_g = npm.size();
	if ( homogeneity ) {
	    b1 =  square((npm.getAC()+npm.getCA())/_g) / (2*(((double) npm.getA())*((double) npm.getC()))/_f)
		+ square((npm.getAG()+npm.getGA())/_g) / (2*(((double) npm.getA())*((double) npm.getG()))/_f)
		+ square((npm.getAT()+npm.getTA())/_g) / (2*(((double) npm.getA())*((double) npm.getT()))/_f)
		+ square((npm.getCG()+npm.getGC())/_g) / (2*(((double) npm.getC())*((double) npm.getG()))/_f)
		+ square((npm.getCT()+npm.getTC())/_g) / (2*(((double) npm.getC())*((double) npm.getT()))/_f)
		+ square((npm.getGT()+npm.getTG())/_g) / (2*(((double) npm.getG())*((double) npm.getT()))/_f);
	    return log1p( P(npm) , square(P(npm))/b1 );
	}
	_h = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	dist = 0;
	b1 = (((double) npm.getA()) * ((double) npm.getC())) / _f;
	b2 = (((double) npm.getAfirst()) * ((double) npm.getCsecond()) 
	      + ((double) npm.getAsecond()) * ((double) npm.getCfirst())) / _h;
	dist += b1 * log1p( (npm.getAC() + npm.getCA())/_g , b2 ) / b2;
	b1 = (((double) npm.getA()) * ((double) npm.getG())) / _f;
	b2 = (((double) npm.getAfirst()) * ((double) npm.getGsecond()) 
	      + ((double) npm.getAsecond()) * ((double) npm.getGfirst())) / _h;
	dist += b1 * log1p( (npm.getAG() + npm.getGA())/_g , b2 ) / b2;
	b1 = (((double) npm.getA()) * ((double) npm.getT())) / _f;
	b2 = (((double) npm.getAfirst()) * ((double) npm.getTsecond()) 
	      + ((double) npm.getAsecond()) * ((double) npm.getTfirst())) / _h;
	dist += b1 * log1p( (npm.getAT() + npm.getTA())/_g , b2 ) / b2;
	b1 = (((double) npm.getC()) * ((double) npm.getG())) / _f;
	b2 = (((double) npm.getCfirst()) * ((double) npm.getGsecond()) 
	      + ((double) npm.getCsecond()) * ((double) npm.getGfirst())) / _h;
	dist += b1 * log1p( (npm.getCG() + npm.getGC())/_g , b2 ) / b2;
	b1 = (((double) npm.getC()) * ((double) npm.getT())) / _f;
	b2 = (((double) npm.getCfirst()) * ((double) npm.getTsecond()) 
	      + ((double) npm.getCsecond()) * ((double) npm.getTfirst())) / _h;
	dist += b1 * log1p( (npm.getCT() + npm.getTC())/_g , b2 ) / b2;
	b1 = (((double) npm.getG()) * ((double) npm.getT())) / _f;
	b2 = (((double) npm.getGfirst()) * ((double) npm.getTsecond()) 
	      + ((double) npm.getGsecond()) * ((double) npm.getTfirst())) / _h;
	dist += b1 * log1p( (npm.getGT() + npm.getTG())/_g , b2 ) / b2;
	return 2 * dist;
    }

    private static double TN84( NucleotideFrequency npm ) {
	_f = npm.total();
	b1 = 1 - square(npm.getA()/_f) - square(npm.getC()/_f) - square(npm.getG()/_f) - square(npm.getT()/_f);
	_f = square(npm.total());
	_g = npm.size();
	b1 +=  square(P(npm)) 
	    / ( square((npm.getAC()+npm.getCA())/_g) / (2*(((double) npm.getA())*((double) npm.getC()))/_f)
		+ square((npm.getAG()+npm.getGA())/_g) / (2*(((double) npm.getA())*((double) npm.getG()))/_f)
		+ square((npm.getAT()+npm.getTA())/_g) / (2*(((double) npm.getA())*((double) npm.getT()))/_f)
		+ square((npm.getCG()+npm.getGC())/_g) / (2*(((double) npm.getC())*((double) npm.getG()))/_f)
		+ square((npm.getCT()+npm.getTC())/_g) / (2*(((double) npm.getC())*((double) npm.getT()))/_f)
		+ square((npm.getGT()+npm.getTG())/_g) / (2*(((double) npm.getG())*((double) npm.getT()))/_f) );
	return log1p( P(npm) , b1 );
    }

    private static double K2P( NucleotideFrequency npm ) {
	return 0.5 * ( log2p( TS(npm) , TV(npm) , 2 , 1 ) + log1p( TV(npm) , 0.5 ) );
    }
    
    private static double F84( NucleotideFrequency npm ) {
	_f = npm.total();
	_a = ((double) npm.getA()) * ((double) npm.getG()) / ( _f * (npm.getA() + npm.getG()) )
	    + ((double) npm.getC()) * ((double) npm.getT()) / ( _f * (npm.getC() + npm.getT()) );
	_f = square( npm.total() );
	_b = ( ((double) npm.getA()) * ((double) npm.getG()) + ((double) npm.getC()) * ((double) npm.getT()) ) / _f;
	_c = ((double) (npm.getA() + npm.getG())) * ((double) (npm.getC() + npm.getT())) / _f;
	return 2 * _a * log2p( TS(npm) , TV(npm) , 1 / (2 * _a) , (_a - _b)/(2 * _a * _c) )
	    - (_a - _b - _c) * log1p( TV(npm) , 2 * _c ) / _c;
    }

    private static double HKY85( NucleotideFrequency npm , boolean homogeneity ) {
	_f = npm.size();
	_p1 = (npm.getAG() + npm.getGA()) / _f;
	_p2 = (npm.getCT() + npm.getTC()) / _f;
	_q = TV(npm);
	_g = npm.total();
	_piR = ( npm.getA() + npm.getG() ) / _g;
	_piY = ( npm.getC() + npm.getT() ) / _g;
	if ( homogeneity ) {
	    _h = square(_g);
	    _fRY = 2 * _piR * _piY;
	    _fAG = 2 * ((double) npm.getA()) * ((double) npm.getG()) / _h;
	    _fCT = 2 * ((double) npm.getC()) * ((double) npm.getT()) / _h;
	}
	else {
	    _h = npm.totalFirst() * npm.totalSecond();
	    _fRY = (((double) (npm.getAfirst() + npm.getGfirst()))*((double) (npm.getCsecond() + npm.getTsecond())) 
		    + ((double) (npm.getAsecond() + npm.getGsecond()))*((double) (npm.getCfirst() + npm.getTfirst()))) / _h;
	    _fAG = (((double) npm.getAfirst()) * ((double) npm.getGsecond()) 
		    + ((double) npm.getAsecond()) * ((double) npm.getGfirst())) / _h;
	    _fCT = (((double) npm.getCfirst()) * ((double) npm.getTsecond()) 
		    + ((double) npm.getCsecond()) * ((double) npm.getTfirst())) / _h;
	}
	_e1 = 1 - _q / _fRY;
	_e2 = 1 - _q / (2 * _piR) - _piR * _p1 / _fAG;
	_e3 = 1 - _q / (2 * _piY) - _piY * _p2 / _fCT;
	_a1 = (1/_e2 - 1/_e1) / (2 * square(_piR));
	_a2 = 1 / (_fAG * _e2);
	_a3 = (1/_e3 - 1/_e1) / (2 * square(_piY));
	_a4 = 1 / (_fCT * _e3);
	_a5 = 1 / (_fRY * _e1);
	_v1 = square(_a1) * _q + square(_a2) * _p1 - square(_a1 * TV(npm) + _a2 * _p1);
	_v1 = square(_a3) * _q + square(_a4) * _p2 - square(_a3 * TV(npm) + _a4 * _p2);
	_c12 = _a1 * _a3 * _q * (1 - _q) - _a1 * _a4 * _q * _p2 - _a2 * _a4 * _p1 * _p2;
	_c13 = _a5 * _q * ( _a1 * (1 - _q) - _a2 * _p1 );
	_c23 = _a5 * _q * ( _a3 * (1 - _q) - _a4 * _p2 );
	_h = square(_g);
	_gam = (_v2 - _c12) / (_v1 + _v2 - 2*_c12)
	    + _piR * _piY * (_c13 - _c23) * _h / ((((double) npm.getA())*((double) npm.getG())+((double) npm.getC())*((double) npm.getT())) * (_v1 + _v2 - 2*_c12));
	_b3 = log1p( _q , _fRY ) / _fRY;
	_b1 = -(_piY/_piR)*_b3  +  log2p( _q , _p1 , 1/(2*_piR) , _piR/_fAG ) / _piR;
	_b2 = -(_piR/_piY)*_b3  +  log2p( _q , _p2 , 1/(2*_piY) , _piY/_fCT ) / _piY;
	return 2 * (npm.getA()*npm.getG()+npm.getC()*npm.getT()) * (_gam*_b1 + (1-_gam)*_b2) / _h
	     + 2 * _piR * _piY * _b3;
    }

    private static double TN93( NucleotideFrequency npm , boolean homogeneity ) {
	_f = npm.size();
	_p1 = (npm.getAG() + npm.getGA()) / _f;
	_p2 = (npm.getCT() + npm.getTC()) / _f;
	_q = TV(npm);
	_g = npm.total();
	_piR = ( npm.getA() + npm.getG() ) / _g;
	_piY = ( npm.getC() + npm.getT() ) / _g;
	if ( homogeneity ) {
	    _h = square(_g);
	    _fRY = 2 * _piR * _piY;
	    _fAG = 2 * ((double) npm.getA()) * ((double) npm.getG()) / _h;
	    _fCT = 2 * ((double) npm.getC()) * ((double) npm.getT()) / _h;
	}
	else {
	    _h = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	    _fRY = (((double) (npm.getAfirst() + npm.getGfirst()))*((double) (npm.getCsecond() + npm.getTsecond())) 
		    + ((double) (npm.getAsecond() + npm.getGsecond()))*((double) (npm.getCfirst() + npm.getTfirst()))) / _h;
	    _fAG = (((double) npm.getAfirst()) * ((double) npm.getGsecond()) 
		    + ((double) npm.getAsecond()) * ((double) npm.getGfirst())) / _h;
	    _fCT = (((double) npm.getCfirst()) * ((double) npm.getTsecond()) 
		    + ((double) npm.getCsecond()) * ((double) npm.getTfirst())) / _h;
	    _h = square(_g);
	}
	return 2 * ( ((double) npm.getA()) * ((double) npm.getG()) * log2p(_q , _p1 , 1/(2*_piR) , _piR/_fAG) / (_piR * _h)
		     + ((double) npm.getC()) * ((double) npm.getT()) * log2p(_q , _p2 , 1/(2*_piY) , _piY/_fCT) / (_piY * _h)
		     + (_piR * _piY 
			- ((double) npm.getA()) * ((double) npm.getG()) * _piY / (_piR * _h) 
			- ((double) npm.getC()) * ((double) npm.getT()) * _piY / (_piR * _h)) * log1p(_q , _fRY) / _fRY );
    }

    private static double T92( NucleotideFrequency npm , boolean homogeneity ) {
	_f = square(npm.total());
	_b1 = 2 * ((double) (npm.getC() + npm.getG())) * ((double) (npm.total() - npm.getC() - npm.getG())) / _f;
	if ( homogeneity )
	    _b2 = _b1;
	else 
	    _b2 = ( ((double) (npm.getCfirst() + npm.getGfirst())) * ((double) (npm.totalSecond() - npm.getCsecond() - npm.getGsecond()))
		    + ((double) (npm.getCsecond() + npm.getGsecond())) * ((double) (npm.totalFirst() - npm.getCfirst() - npm.getGfirst())) )
		/ ((double) (npm.totalFirst() * npm.totalSecond()));
	return _b1 * log2p(TS(npm) , TV(npm) , 1/_b2 , 1) + (1 - _b1) * log1p(TV(npm) , 0.5);
    }

    private static double K81_3ST( NucleotideFrequency npm ) {
	_f = npm.size();
	_k = npm.getAT() + npm.getTA() + npm.getCG() + npm.getGC();
	_n = npm.getAC() + npm.getCA() + npm.getGT() + npm.getTG();
	return 0.25 * ( log2p(TS(npm) , _k/_f , 2 , 2) + log2p(TS(npm) , _n/_f , 2 , 2) + log2p(_k/_f , _n/_f , 2 , 2) );
    }

    private static double GIN82( NucleotideFrequency npm ) {
	_f = npm.total();
	_piR = (npm.getA() + npm.getT()) / _f;
	_piY = (npm.getC() + npm.getG()) / _f;
	_g = npm.size();
	_h = square(_f);
	_a1 = _piR * _piY - ( npm.getAC() + npm.getCA() + npm.getAG() + npm.getGA() 
			      + npm.getCT() + npm.getTC() + npm.getGT() + npm.getTG() ) / _g;
	_a2 = ( npm.getAA() + npm.getTT() - npm.getAT() - npm.getTA() ) / _g
	    - square(_piR) + 3 * ((double) npm.getA()) * ((double) npm.getT()) / _h;
	_a3 = ( npm.getCC() + npm.getGG() - npm.getCG() - npm.getGC() ) / _g
	    - square(_piY) + 3 * ((double) npm.getC()) * ((double) npm.getG()) / _h;
	_a4 = (npm.getA() * _piY / _f - (npm.getAC() + npm.getCA() + npm.getAG() + npm.getGA()) / _g)
	    * (npm.getT() * _piY / _f - (npm.getCT() + npm.getTC() + npm.getGT() + npm.getTG()) / _g);
	_a5 = (npm.getC() * _piR / _f - (npm.getAC() + npm.getCA() + npm.getCT() + npm.getTC()) / _g)
	    * (npm.getG() * _piR / _f - (npm.getAG() + npm.getGA() + npm.getGT() + npm.getTG()) / _g);
	return - _piR * _piY * Math.log( _a1 / (_piR * _piY) )
	    - 2*((double) npm.getA())*((double) npm.getT())
	    * Math.log( _piR*(_a2-_a1+3*_a4/_a1)*_h/(3*((double) npm.getA())*((double) npm.getT())) )/(_piR*_h)
	    - 2*((double) npm.getC())*((double) npm.getG())
	    * Math.log( _piY*(_a3-_a1+3*_a5/_a1)*_h/(3*((double) npm.getC())*((double) npm.getG())) )/(_piY*_h);
    }

    private static double TK81( NucleotideFrequency npm ) {
	_f = npm.total();
	_g = npm.size();
	_h = square(_g);
	_gam = (npm.getA() + npm.getT()) / _f;
	_gam *= 1 - _gam;
	_p = (npm.getAG() + npm.getGA() + npm.getCT() + npm.getTC()) / _g;
	_q = (npm.getAC() + npm.getCA() + npm.getGT() + npm.getTG()) / _g;
	return - 0.25 * Math.log( (((double) (npm.getAA() + npm.getTT() + npm.getAT() + npm.getTA()))
				   * ((double) (npm.getCC() + npm.getGG() + npm.getCG() + npm.getGC())) / _h
				   - 0.25 * square(_p - _q)) / _gam )
	     - (2 * _gam - 0.25) * Math.log( 1 - (_p + _q) / (2 * _gam) );
    }

    //##################################################
    //##################################################
    //##### gamma-corrected evolutionary distances #####
    //##################################################
    //##################################################

    // generic method to get the value of b.alpha.[(1-p/b)^(-1/alpha) - 1]
    private static double gamma1p( double pp , double bb , double aalpha ) { 
	if ( Double.isInfinite( aalpha ) )
	    return log1p(pp , bb);
	if ( pp == 0 )
	    return 0;
	return bb * aalpha * ( Math.pow( 1 - pp / bb , -1 / aalpha ) - 1 );
    }
    
    // generic method to get the value of alpha.[(1-a.p-b.q)^(-1/alpha) - 1]
    private static double gamma2p( double pp , double qq , double aa , double bb , double aalpha ) { 
	if ( Double.isInfinite( aalpha ) )
	    return log2p(pp , qq , aa , bb);
	if ( ((aa == 0) || (pp == 0))
	     && ((bb == 0) || (qq == 0)) )
	    return 0;
	return aalpha * ( Math.pow( 1 - aa * pp - bb * qq , -1 / aalpha ) - 1 );
    }

    private static double JC( NucleotideFrequency npm , double alpha ) {
	return gamma1p( P(npm) , 0.75 , alpha );
    }
    
    private static double K2P( NucleotideFrequency npm , double alpha ) {
	return 0.5 * ( gamma2p( TS(npm) , TV(npm) , 2 , 1 , alpha ) 
		       + gamma1p( TV(npm) , 0.5 , alpha ) );
    }
    
    private static double F81( NucleotideFrequency npm , double alpha , boolean homogeneity ) {
	_f = npm.total();
	b1 = 1 - square(npm.getA()/_f) - square(npm.getC()/_f) - square(npm.getG()/_f) - square(npm.getT()/_f);
	if ( homogeneity )
	    return gamma1p( P(npm) , b1 , alpha );
	_g = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	b2 = 1 - (((double) npm.getAfirst()) * ((double) npm.getAsecond()))/_g 
	    - (((double) npm.getCfirst()) * ((double) npm.getCsecond()))/_g
	    - (((double) npm.getGfirst()) * ((double) npm.getGsecond()))/_g 
	    - (((double) npm.getTfirst()) * ((double) npm.getTsecond()))/_g;
	return b1 * gamma1p( P(npm) , b2 , alpha ) / b2;
    }
    
    private static double K81_3ST( NucleotideFrequency npm , double alpha ) {
	_f = npm.size();
	_k = npm.getAT() + npm.getTA() + npm.getCG() + npm.getGC();
	_n = npm.getAC() + npm.getCA() + npm.getGT() + npm.getTG();
	return 0.25 * ( gamma2p(TS(npm) , _k/_f , 2 , 2 , alpha) + gamma2p(TS(npm) , _n/_f , 2 , 2 , alpha) + gamma2p(_k/_f , _n/_f , 2 , 2 , alpha) );
    }

    private static double F84( NucleotideFrequency npm , double alpha ) {
	_f = npm.total();
	_a = ((double) npm.getA()) * ((double) npm.getG()) / ( _f * (npm.getA() + npm.getG()) )
	    + ((double) npm.getC()) * ((double) npm.getT()) / ( _f * (npm.getC() + npm.getT()) );
	_f = square( npm.total() );
	_b = ( ((double) npm.getA()) * ((double) npm.getG()) + ((double) npm.getC()) * ((double) npm.getT()) ) / _f;
	_c = ((double) (npm.getA() + npm.getG())) * ((double) (npm.getC() + npm.getT())) / _f;
	return 2 * _a * gamma2p( TS(npm) , TV(npm) , 1 / (2 * _a) , (_a - _b)/(2 * _a * _c) , alpha )
	    - (_a - _b - _c) * gamma1p( TV(npm) , 2 * _c , alpha ) / _c;
    }

    private static double T92( NucleotideFrequency npm , double alpha , boolean homogeneity ) {
	_f = square(npm.total());
	_b1 = 2 * ((double) (npm.getC() + npm.getG())) * ((double) (npm.total() - npm.getC() - npm.getG())) / _f;
	if ( homogeneity )
	    _b2 = _b1;
	else 
	    _b2 = ( ((double) (npm.getCfirst() + npm.getGfirst())) * ((double) (npm.totalSecond() - npm.getCsecond() - npm.getGsecond()))
		    + ((double) (npm.getCsecond() + npm.getGsecond())) * ((double) (npm.totalFirst() - npm.getCfirst() - npm.getGfirst())) )
		/ ((double) (npm.totalFirst() * npm.totalSecond()));
	return _b1 * gamma2p(TS(npm) , TV(npm) , 1/_b2 , 1 , alpha) 
	    + (1 - _b1) * gamma1p(TV(npm) , 0.5 , alpha);
    }

    private static double TN93( NucleotideFrequency npm , double alpha , boolean homogeneity ) {
	_f = npm.size();
	_p1 = (npm.getAG() + npm.getGA()) / _f;
	_p2 = (npm.getCT() + npm.getTC()) / _f;
	_q = TV(npm);
	_g = npm.total();
	_piR = ( npm.getA() + npm.getG() ) / _g;
	_piY = ( npm.getC() + npm.getT() ) / _g;
	if ( homogeneity ) {
	    _h = square(_g);
	    _fRY = 2 * _piR * _piY;
	    _fAG = 2 * ((double) npm.getA()) * ((double) npm.getG()) / _h;
	    _fCT = 2 * ((double) npm.getC()) * ((double) npm.getT()) / _h;
	}
	else {
	    _h = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	    _fRY = (((double) (npm.getAfirst() + npm.getGfirst()))*((double) (npm.getCsecond() + npm.getTsecond())) 
		    + ((double) (npm.getAsecond() + npm.getGsecond()))*((double) (npm.getCfirst() + npm.getTfirst()))) / _h;
	    _fAG = (((double) npm.getAfirst()) * ((double) npm.getGsecond()) 
		    + ((double) npm.getAsecond()) * ((double) npm.getGfirst())) / _h;
	    _fCT = (((double) npm.getCfirst()) * ((double) npm.getTsecond()) 
		    + ((double) npm.getCsecond()) * ((double) npm.getTfirst())) / _h;
	    _h = square(_g);
	}
	return 2 * ( ((double) npm.getA()) * ((double) npm.getG()) * gamma2p(_q , _p1 , 1/(2*_piR) , _piR/_fAG , alpha) / (_piR * _h)
		     + ((double) npm.getC()) * ((double) npm.getT()) * gamma2p(_q , _p2 , 1/(2*_piY) , _piY/_fCT , alpha) / (_piY * _h)
		     + (_piR * _piY 
			- ((double) npm.getA()) * ((double) npm.getG()) * _piY / (_piR * _h) 
			- ((double) npm.getC()) * ((double) npm.getT()) * _piY / (_piR * _h)) * gamma1p(_q , _fRY , alpha) / _fRY );
    }
    

    //###########################################
    //###########################################
    //##### unbiased evolutionary distances #####
    //###########################################
    //###########################################

    // generic method to get the unbiased value of -b.log[1 - k/(n.b)]
    private static double Ulog1p(int kk , int nn , double bb) {
	k = kk;
	n = nn;
	f = ((double) k) / ((double) n);
	if ( f == 0 )
	    return 0;
	dist = f;
	//i = 0;
	//while ( ++i < kk ) {
	//    f *= i * (--k) / ( bb * (i+1) * (--n) );
	i = 1;
	while ( i < kk ) {
	    f *= i * (--k) / ( bb * (++i) * (--n) );
	    if ( Double.isInfinite(f)
	    	 || Double.isNaN(f) ) 
	    	return dist;
	    dist += f;
	    if ( f < PRECISION )       // quick convergence !
	    	return dist;
	}
	return dist;
    }

    // generic method to get the unbiased value of -log(1 - a.s/n - b.v/n)
    private static double Ulog2p(int ss , int vv , int nn , double aa , double bb) {
	//System.out.println(ss + " " + vv + " " + nn + " " + aa + " " + bb);
	if ( (ss == 0)
	     && (vv == 0) )
	    return 0;
	if ( ss == 0 )
	    return bb * Ulog1p( vv , nn , 1 / bb );
	if ( vv == 0 )
	    return aa * Ulog1p( ss , nn , 1 / aa );
	n = nn;
	k = ss + vv;
	dist = 0;
	norm = 1;
	i = 0;
	while ( ++i <= k ) {
	    norm *= n--;
	    min = (0 > i - vv) ? 0 : i - vv; 
	    //min = Math.max(0 , i - vv);
	    g = 1;
	    if ( min == 0 ) {
		v = vv;               // computing g(0) 
		j = i;
		while ( --j >= 0 )
		    g *= bb * (v--);
	    }
	    else {
		y = i;                // computing g(i-vv)
		j = vv;
		while ( --j >= 0 )
		    g *= bb * (y--);
		s = ss;
		j = i - vv;
		while ( --j >= 0 )
		    g *= aa * (s--);
	    }
	    f = g;                    // computing f(i)
	    s = ss - min;
	    v = vv - i + min + 1;
	    y = i - min;
	    max = (i < ss) ? i : ss; 
	    //max = Math.min(i , ss);
	    j = min;
	    while ( j < max ) {
		g *= aa * (y--) * (s--) / ( bb * (++j) * (v++) );
		f += g;
	    }
	    p = f / (i * norm);
	    if ( Double.isNaN(p)
		 || Double.isInfinite(p) )
		return dist;
	    dist += p;
	    //System.out.println(dist);
	    if ( p < PRECISION )      // quick convergence !
		return dist;
	}
	return dist;
    }

    private static int Pint( NucleotideFrequency npm ) {
	return npm.size() - (npm.getAA() + npm.getCC() + npm.getGG() + npm.getTT());
    }

    private static int TSint( NucleotideFrequency npm ) {
	return npm.getAG() + npm.getGA() + npm.getCT() + npm.getTC();
    }

    private static int TVint( NucleotideFrequency npm ) {
	return npm.getAC() + npm.getCA() + npm.getAT() + npm.getTA() 
	    + npm.getCG() + npm.getGC() + npm.getGT() + npm.getTG();
    }

    private static double UTSTV( NucleotideFrequency npm ) {
	return Ulog2p( TSint(npm) , TVint(npm) , npm.size() , 2 , 1 ) / Ulog1p( TVint(npm) , npm.size() , 0.5 ) - 1;
    }

    private static double UJC( NucleotideFrequency npm ) {
	return Ulog1p( Pint(npm) , npm.size() , 0.75 );
    }
    
    private static double UF81( NucleotideFrequency npm , boolean homogeneity ) {
	_f = npm.total();
	b1 = 1 - square(npm.getA()/_f) - square(npm.getC()/_f) - square(npm.getG()/_f) - square(npm.getT()/_f);
	if ( homogeneity )
	    return Ulog1p( Pint(npm) , npm.size() , b1 );
	_g = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	b2 = 1 - (((double) npm.getAfirst()) * ((double) npm.getAsecond()))/_g 
	    - (((double) npm.getCfirst()) * ((double) npm.getCsecond()))/_g
	    - (((double) npm.getGfirst()) * ((double) npm.getGsecond()))/_g 
	    - (((double) npm.getTfirst()) * ((double) npm.getTsecond()))/_g;
	return b1 * Ulog1p( Pint(npm) , npm.size() , b2 ) / b2;
    }
    
    private static double UTN82_EI( NucleotideFrequency npm , boolean homogeneity ) {
	//_k = Pint(npm);
	_f = square(npm.total());
	_g = npm.size();
	if ( homogeneity ) {
	    b1 =  square((npm.getAC()+npm.getCA())/_g) / (2*(((double) npm.getA())*((double) npm.getC()))/_f)
		+ square((npm.getAG()+npm.getGA())/_g) / (2*(((double) npm.getA())*((double) npm.getG()))/_f)
		+ square((npm.getAT()+npm.getTA())/_g) / (2*(((double) npm.getA())*((double) npm.getT()))/_f)
		+ square((npm.getCG()+npm.getGC())/_g) / (2*(((double) npm.getC())*((double) npm.getG()))/_f)
		+ square((npm.getCT()+npm.getTC())/_g) / (2*(((double) npm.getC())*((double) npm.getT()))/_f)
		+ square((npm.getGT()+npm.getTG())/_g) / (2*(((double) npm.getG())*((double) npm.getT()))/_f);
	    return Ulog1p( Pint(npm)/*_k*/ , npm.size() , square(P(npm))/b1 );
	}
	_h = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	dist = 0;
	b1 = (((double) npm.getA()) * ((double) npm.getC())) / _f;
	b2 = (((double) npm.getAfirst()) * ((double) npm.getCsecond()) 
	      + ((double) npm.getAsecond()) * ((double) npm.getCfirst())) / _h;
	dist += b1 * Ulog1p( npm.getAC() + npm.getCA() , npm.size() , b2 ) / b2;
	b1 = (((double) npm.getA()) * ((double) npm.getG())) / _f;
	b2 = (((double) npm.getAfirst()) * ((double) npm.getGsecond()) 
	      + ((double) npm.getAsecond()) * ((double) npm.getGfirst())) / _h;
	dist += b1 * Ulog1p( npm.getAG() + npm.getGA() , npm.size() , b2 ) / b2;
	b1 = (((double) npm.getA()) * ((double) npm.getT())) / _f;
	b2 = (((double) npm.getAfirst()) * ((double) npm.getTsecond()) 
	      + ((double) npm.getAsecond()) * ((double) npm.getTfirst())) / _h;
	dist += b1 * Ulog1p( npm.getAT() + npm.getTA() , npm.size() , b2 ) / b2;
	b1 = (((double) npm.getC()) * ((double) npm.getG())) / _f;
	b2 = (((double) npm.getCfirst()) * ((double) npm.getGsecond()) 
	      + ((double) npm.getCsecond()) * ((double) npm.getGfirst())) / _h;
	dist += b1 * Ulog1p( npm.getCG() + npm.getGC() , npm.size() , b2 ) / b2;
	b1 = (((double) npm.getC()) * ((double) npm.getT())) / _f;
	b2 = (((double) npm.getCfirst()) * ((double) npm.getTsecond()) 
	      + ((double) npm.getCsecond()) * ((double) npm.getTfirst())) / _h;
	dist += b1 * Ulog1p( npm.getCT() + npm.getTC() , npm.size() , b2 ) / b2;
	b1 = (((double) npm.getG()) * ((double) npm.getT())) / _f;
	b2 = (((double) npm.getGfirst()) * ((double) npm.getTsecond())
	      + ((double) npm.getGsecond()) * ((double) npm.getTfirst())) / _h;
	dist += b1 * Ulog1p( npm.getGT() + npm.getTG() , npm.size() , b2 ) / b2;
	return 2 * dist;
    }

    private static double UTN84( NucleotideFrequency npm ) {
	_k = Pint(npm);
	if ( _k == 0 )
	    return 0;
	_f = npm.total();
	b1 = 1 - square(npm.getA()/_f) - square(npm.getC()/_f) - square(npm.getG()/_f) - square(npm.getT()/_f);
	_f = square(npm.total());
	_g = npm.size();
	b1 +=  square(P(npm)) 
	    / ( square((npm.getAC()+npm.getCA())/_g) / (2*(((double) npm.getA())*((double) npm.getC()))/_f)
		+ square((npm.getAG()+npm.getGA())/_g) / (2*(((double) npm.getA())*((double) npm.getG()))/_f)
		+ square((npm.getAT()+npm.getTA())/_g) / (2*(((double) npm.getA())*((double) npm.getT()))/_f)
		+ square((npm.getCG()+npm.getGC())/_g) / (2*(((double) npm.getC())*((double) npm.getG()))/_f)
		+ square((npm.getCT()+npm.getTC())/_g) / (2*(((double) npm.getC())*((double) npm.getT()))/_f)
		+ square((npm.getGT()+npm.getTG())/_g) / (2*(((double) npm.getG())*((double) npm.getT()))/_f) );
	return Ulog1p( _k , npm.size() , b1 );
    }

    private static double UK2P( NucleotideFrequency npm ) {
	return 0.5 * ( Ulog2p( TSint(npm) , TVint(npm) , npm.size() , 2 , 1)
		       + Ulog1p( TVint(npm) , npm.size() , 0.5) );
    }
    
    private static double UF84( NucleotideFrequency npm ) {
	_f = npm.total();
	_a = ((double) npm.getA()) * ((double) npm.getG()) / ( _f * (npm.getA() + npm.getG()) )
	    + ((double) npm.getC()) * ((double) npm.getT()) / ( _f * (npm.getC() + npm.getT()) );
	_f = square( npm.total() );
	_b = ( ((double) npm.getA()) * ((double) npm.getG()) + ((double) npm.getC()) * ((double) npm.getT()) ) / _f;
	_c = ((double) (npm.getA() + npm.getG())) * ((double) (npm.getC() + npm.getT())) / _f;
	return 2 * _a * Ulog2p( TSint(npm) , TVint(npm) , npm.size() , 1 / (2 * _a) , (_a - _b)/(2 * _a * _c) )
	    - (_a - _b - _c) * Ulog1p( TVint(npm) , npm.size() , 2 * _c ) / _c;
    }

    private static double UHKY85( NucleotideFrequency npm , boolean homogeneity ) {
	_f = npm.size();
	_p1 = (npm.getAG() + npm.getGA()) / _f;
	_p2 = (npm.getCT() + npm.getTC()) / _f;
	_q = TV(npm);
	_g = npm.total();
	_piR = ( npm.getA() + npm.getG() ) / _g;
	_piY = ( npm.getC() + npm.getT() ) / _g;
	if ( homogeneity ) {
	    _h = square(_g);
	    _fRY = 2 * _piR * _piY;
	    _fAG = 2 * ((double) npm.getA()) * ((double) npm.getG()) / _h;
	    _fCT = 2 * ((double) npm.getC()) * ((double) npm.getT()) / _h;
	}
	else {
	    _h = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	    _fRY = (((double) (npm.getAfirst() + npm.getGfirst()))*((double) (npm.getCsecond() + npm.getTsecond())) 
		    + ((double) (npm.getAsecond() + npm.getGsecond()))*((double) (npm.getCfirst() + npm.getTfirst()))) / _h;
	    _fAG = (((double) npm.getAfirst()) * ((double) npm.getGsecond()) 
		    + ((double) npm.getAsecond()) * ((double) npm.getGfirst())) / _h;
	    _fCT = (((double) npm.getCfirst()) * ((double) npm.getTsecond()) 
		    + ((double) npm.getCsecond()) * ((double) npm.getTfirst())) / _h;
	}
	_e1 = 1 - _q / _fRY;
	_e2 = 1 - _q / (2 * _piR) - _piR * _p1 / _fAG;
	_e3 = 1 - _q / (2 * _piY) - _piY * _p2 / _fCT;
	_a1 = (1/_e2 - 1/_e1) / (2 * square(_piR));
	_a2 = 1 / (_fAG * _e2);
	_a3 = (1/_e3 - 1/_e1) / (2 * square(_piY));
	_a4 = 1 / (_fCT * _e3);
	_a5 = 1 / (_fRY * _e1);
	_v1 = square(_a1) * _q + square(_a2) * _p1 - square(_a1 * TV(npm) + _a2 * _p1);
	_v1 = square(_a3) * _q + square(_a4) * _p2 - square(_a3 * TV(npm) + _a4 * _p2);
	_c12 = _a1 * _a3 * _q * (1 - _q) - _a1 * _a4 * _q * _p2 - _a2 * _a4 * _p1 * _p2;
	_c13 = _a5 * _q * ( _a1 * (1 - _q) - _a2 * _p1 );
	_c23 = _a5 * _q * ( _a3 * (1 - _q) - _a4 * _p2 );
	_h = square(_g);
	_gam = (_v2 - _c12) / (_v1 + _v2 - 2*_c12)
	    + _piR * _piY * (_c13 - _c23) * _h / ((((double) npm.getA())*((double) npm.getG())+((double) npm.getC())*((double) npm.getT())) * (_v1 + _v2 - 2*_c12));
	_b3 = Ulog1p( TVint(npm) , npm.size() , _fRY ) / _fRY;
	_b1 = -(_piY/_piR)*_b3 + Ulog2p(TVint(npm) , npm.getAG()+npm.getGA() , npm.size() , 1/(2*_piR) , _piR/_fAG)/_piR;
	_b2 = -(_piR/_piY)*_b3 + Ulog2p(TVint(npm) , npm.getCT()+npm.getTC() , npm.size() , 1/(2*_piY) , _piY/_fCT)/_piY;
	return 2 * ((((double) npm.getA())*((double) npm.getG())+((double) npm.getC())*((double) npm.getT()))*(_gam*_b1 + (1-_gam)*_b2)/_h + _piR*_piY*_b3);
     }

    private static double UTN93( NucleotideFrequency npm , boolean homogeneity ) {
	_f = npm.size();
	_p1 = (npm.getAG() + npm.getGA()) / _f;
	_p2 = (npm.getCT() + npm.getTC()) / _f;
	_q = TV(npm);
	_g = npm.total();
	_piR = ( npm.getA() + npm.getG() ) / _g;
	_piY = ( npm.getC() + npm.getT() ) / _g;
	if ( homogeneity ) {
	    _h = square(_g);
	    _fRY = 2 * _piR * _piY;
	    _fAG = 2 * ((double) npm.getA()) * ((double) npm.getG()) / _h;
	    _fCT = 2 * ((double) npm.getC()) * ((double) npm.getT()) / _h;
	}
	else {
	    _h = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	    _fRY = (((double) (npm.getAfirst() + npm.getGfirst()))*((double) (npm.getCsecond() + npm.getTsecond()))
		    + ((double) (npm.getAsecond() + npm.getGsecond()))*((double) (npm.getCfirst() + npm.getTfirst()))) / _h;
	    _fAG = (((double) npm.getAfirst()) * ((double) npm.getGsecond())
		    + ((double) npm.getAsecond()) * ((double) npm.getGfirst())) / _h;
	    _fCT = (((double) npm.getCfirst()) * ((double) npm.getTsecond())
		    + ((double) npm.getCsecond()) * ((double) npm.getTfirst())) / _h;
	    _h = square(_g);
	}
	return 2 * ( ((double) npm.getA()) * ((double) npm.getG()) * Ulog2p(TVint(npm) , npm.getAG()+npm.getGA() , npm.size() , 1/(2*_piR) , _piR/_fAG) / (_piR * _h)
		     + ((double) npm.getC()) * ((double) npm.getT()) * Ulog2p(TVint(npm) , npm.getCT()+npm.getTC() , npm.size(), 1/(2*_piY) , _piY/_fCT) / (_piY * _h)
		     + (_piR * _piY 
			- ((double) npm.getA()) * ((double) npm.getG()) * _piY / (_piR * _h) 
			- ((double) npm.getC()) * ((double) npm.getT()) * _piY / (_piR * _h)) * Ulog1p(TVint(npm) , npm.size() , _fRY) / _fRY );
    }

    private static double UT92( NucleotideFrequency npm , boolean homogeneity ) {
	_f = square(npm.total());
	_b1 = 2 * ((double) (npm.getC() + npm.getG())) * ((double) (npm.total() - npm.getC() - npm.getG())) / _f;
	if ( homogeneity )
	    _b2 = _b1;
	else 
	    _b2 = ( ((double) (npm.getCfirst() + npm.getGfirst())) * ((double) (npm.totalSecond() - npm.getCsecond() - npm.getGsecond()))
		    + ((double) (npm.getCsecond() + npm.getGsecond())) * ((double) (npm.totalFirst() - npm.getCfirst() - npm.getGfirst())) )
		/ ((double) (npm.totalFirst() * npm.totalSecond()));
	return _b1*Ulog2p(TSint(npm) , TVint(npm) , npm.size() , 1/_b2 , 1) + (1-_b1)*Ulog1p(TVint(npm) , npm.size() , 0.5);
    }

    private static double UK81_3ST( NucleotideFrequency npm ) {
	_f = npm.size();
	_k = npm.getAT() + npm.getTA() + npm.getCG() + npm.getGC();
	_n = npm.getAC() + npm.getCA() + npm.getGT() + npm.getTG();
	return 0.25 * ( Ulog2p(TSint(npm) , _k , npm.size() , 2 , 2) 
			+ Ulog2p(TSint(npm) , _n , npm.size() , 2 , 2) 
			+ Ulog2p(_k , _n , npm.size() , 2 , 2) );
    }

    private static double UGIN82( NucleotideFrequency npm ) {
	return Double.NaN;
    }

    private static double UTK81( NucleotideFrequency npm ) {
	return Double.NaN;
    }


    //###########################################################
    //###########################################################
    //##### unbiased gamma-corrected evolutionary distances #####
    //###########################################################
    //###########################################################

    // generic method to get the unbiased value of -b.alpha.[(1-k/(n.b))^(-1/alpha) - 1]
    private static double Ugamma1p(int kk , int nn , double bb , double aalpha) {
	if ( Double.isInfinite( aalpha ) )
	    return Ulog1p(kk , nn , bb);
 	k = kk;
	n = nn;
	f = ((double) k) / ((double) n);
	if ( f == 0 )
	    return 0;
	dist = f;
	//i = 0;
	//while ( ++i < kk ) {
	//    f *= (1 + aa * i) * (--k) / ( aa * bb * (i+1) * (--n) );
	i = 1;
	while ( i < kk ) {
	    f *= (1 + aalpha * i) * (--k) / ( aalpha * bb * (++i) * (--n) );
	    if ( Double.isInfinite(f)
		 || Double.isNaN(f) ) 
		return dist;
	    dist += f;
	    if ( f < PRECISION )       // quick convergence !
	    	return dist;
	}
	return dist;
    }

    // generic method to get the unbiased value of alpha.[(1-(a.s+b.v)/n)^(-1/alpha) - 1]
    private static double Ugamma2p(int ss , int vv , int nn , double aa , double bb , double aalpha) {
	if ( Double.isInfinite( aalpha ) )
	    return Ulog2p(ss , vv , nn , aa , bb);
	if ( (ss == 0)
	     && (vv == 0) )
	    return 0;
	if ( ss == 0 )
	    return bb * Ugamma1p( vv , nn , 1 / bb , aalpha );
	if ( vv == 0 )
	    return aa * Ugamma1p( ss , nn , 1 / aa , aalpha );
	n = nn;
	k = ss + vv;
	dist = 0;
	h = 1;
	i = 0;
	while ( ++i <= k ) {
	    h *= ( (--i) * aalpha + 1 ) / ( (n--) * (++i) * aalpha );
	    min = (0 > i - vv) ? 0 : i - vv; 
	    //min = Math.max(0 , i - vv);
	    g = 1;
	    if ( min == 0 ) {
		v = vv;               // computing g(0) 
		j = i;
		while ( --j >= 0 )
		    g *= bb * (v--);
	    }
	    else {
		y = i;                // computing g(i-vv)
		j = vv;
		while ( --j >= 0 )
		    g *= bb * (y--);
		s = ss;
		j = i - vv;
		while ( --j >= 0 )
		    g *= aa * (s--);
	    }
	    f = g;                    // computing f(i)
	    s = ss - min;
	    v = vv - i + min + 1;
	    y = i - min;
	    max = (i < ss) ? i : ss; 
	    //max = Math.min(i , ss);
	    j = min;
	    while ( j < max ) {
		g *= aa * (y--) * (s--) / ( bb * (++j) * (v++) );
		f += g;
	    }
	    p = h * f;
	    if ( Double.isInfinite(p)
		 || Double.isNaN(p) ) 
		return aalpha * dist;
	    dist += p;
	    if ( p < PRECISION )      // quick convergence !
		return aalpha * dist;
	}
	return aalpha * dist;
    }

    private static double UJC( NucleotideFrequency npm , double alpha ) {
	return Ugamma1p( npm.size() - npm.getAA() - npm.getCC() - npm.getGG() - npm.getTT() ,
			npm.size() ,
			0.75 ,
			alpha );
    }
    
    private static double UK2P( NucleotideFrequency npm , double alpha ) {
	return 0.5 * ( Ugamma2p( TSint(npm) , TVint(npm) , npm.size() , 2 , 1 , alpha) 
		       + Ugamma1p( TVint(npm) , npm.size() , 0.5 , alpha ) );
    }
    
    private static double UF81( NucleotideFrequency npm , double alpha , boolean homogeneity ) {
	_f = npm.total();
	b1 = 1 - square(npm.getA()/_f) - square(npm.getC()/_f) - square(npm.getG()/_f) - square(npm.getT()/_f);
	if ( homogeneity )
	    return Ugamma1p( npm.size() - npm.getAA() - npm.getCC() - npm.getGG() - npm.getTT() ,
			     npm.size() ,
			     b1 , 
			     alpha );
	_g = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	b2 = 1 - (((double) npm.getAfirst()) * ((double) npm.getAsecond()))/_g 
	    - (((double) npm.getCfirst()) * ((double) npm.getCsecond()))/_g
	    - (((double) npm.getGfirst()) * ((double) npm.getGsecond()))/_g 
	    - (((double) npm.getTfirst()) * ((double) npm.getTsecond()))/_g;
	return b1 * Ugamma1p( npm.size() - npm.getAA() - npm.getCC() - npm.getGG() - npm.getTT() ,
			      npm.size() 
			      , b2 
			      , alpha ) / b2;
    }
    
    private static double UK81_3ST( NucleotideFrequency npm , double alpha ) {
	_f = npm.size();
	_k = npm.getAT() + npm.getTA() + npm.getCG() + npm.getGC();
	_n = npm.getAC() + npm.getCA() + npm.getGT() + npm.getTG();
	return 0.25 * ( Ugamma2p(TSint(npm) , _k , npm.size() , 2 , 2 , alpha) 
			+ Ugamma2p(TSint(npm) , _n , npm.size() , 2 , 2 , alpha) 
			+ Ugamma2p(_k , _n , npm.size() , 2 , 2 , alpha) );
    }

    private static double UF84( NucleotideFrequency npm , double alpha ) {
	_f = npm.total();
	_a = ((double) npm.getA()) * ((double) npm.getG()) / ( _f * (npm.getA() + npm.getG()) )
	    + ((double) npm.getC()) * ((double) npm.getT()) / ( _f * (npm.getC() + npm.getT()) );
	_f = square( npm.total() );
	_b = ( ((double) npm.getA()) * ((double) npm.getG()) + ((double) npm.getC()) * ((double) npm.getT()) ) / _f;
	_c = ((double) (npm.getA() + npm.getG())) * ((double) (npm.getC() + npm.getT())) / _f;
	return 2 * _a * Ugamma2p( TSint(npm) , TVint(npm) , npm.size() , 1 / (2 * _a) , (_a - _b)/(2 * _a * _c) , alpha )
	    - (_a - _b - _c) * Ugamma1p( TVint(npm) , npm.size() , 2 * _c , alpha ) / _c;
    }

    private static double UT92( NucleotideFrequency npm , double alpha , boolean homogeneity ) {
	_f = square(npm.total());
	_b1 = 2 * ((double) (npm.getC() + npm.getG())) * ((double) (npm.total() - npm.getC() - npm.getG())) / _f;
	if ( homogeneity )
	    _b2 = _b1;
	else 
	    _b2 = ( ((double) (npm.getCfirst() + npm.getGfirst())) * ((double) (npm.totalSecond() - npm.getCsecond() - npm.getGsecond()))
		    + ((double) (npm.getCsecond() + npm.getGsecond())) * ((double) (npm.totalFirst() - npm.getCfirst() - npm.getGfirst())) )
		/ ((double) (npm.totalFirst() * npm.totalSecond()));
	return _b1*Ugamma2p(TSint(npm) , TVint(npm) , npm.size() , 1/_b2 , 1 , alpha) 
	    + (1-_b1)*Ugamma1p(TVint(npm) , npm.size() , 0.5 , alpha);
    }

    private static double UTN93( NucleotideFrequency npm , double alpha , boolean homogeneity ) {
	_f = npm.size();
	_p1 = (npm.getAG() + npm.getGA()) / _f;
	_p2 = (npm.getCT() + npm.getTC()) / _f;
	_q = TV(npm);
	_g = npm.total();
	_piR = ( npm.getA() + npm.getG() ) / _g;
	_piY = ( npm.getC() + npm.getT() ) / _g;
	if ( homogeneity ) {
	    _h = square(_g);
	    _fRY = 2 * _piR * _piY;
	    _fAG = 2 * ((double) npm.getA()) * ((double) npm.getG()) / _h;
	    _fCT = 2 * ((double) npm.getC()) * ((double) npm.getT()) / _h;
	}
	else {
	    _h = ((double) npm.totalFirst()) * ((double) npm.totalSecond());
	    _fRY = (((double) (npm.getAfirst() + npm.getGfirst()))*((double) (npm.getCsecond() + npm.getTsecond()))
		    + ((double) (npm.getAsecond() + npm.getGsecond()))*((double) (npm.getCfirst() + npm.getTfirst()))) / _h;
	    _fAG = (((double) npm.getAfirst()) * ((double) npm.getGsecond())
		    + ((double) npm.getAsecond()) * ((double) npm.getGfirst())) / _h;
	    _fCT = (((double) npm.getCfirst()) * ((double) npm.getTsecond())
		    + ((double) npm.getCsecond()) * ((double) npm.getTfirst())) / _h;
	    _h = square(_g);
	}
	return 2 * ( ((double) npm.getA()) * ((double) npm.getG()) * Ugamma2p(TVint(npm) , npm.getAG()+npm.getGA() , npm.size() , 1/(2*_piR) , _piR/_fAG , alpha) / (_piR * _h)
		     + ((double) npm.getC()) * ((double) npm.getT()) * Ugamma2p(TVint(npm) , npm.getCT()+npm.getTC() , npm.size(), 1/(2*_piY) , _piY/_fCT , alpha) / (_piY * _h)
		     + (_piR * _piY 
			- ((double) npm.getA()) * ((double) npm.getG()) * _piY / (_piR * _h) 
			- ((double) npm.getC()) * ((double) npm.getT()) * _piY / (_piR * _h)) * Ugamma1p(TVint(npm) , npm.size() , _fRY , alpha) / _fRY );
    }


    //###########################################################
    //###########################################################
    //##### LOGDET distances                                #####
    //###########################################################
    //###########################################################

    private static double LD( NucleotideFrequency npm , boolean unbiased ) {
	_f = npm.size();
	_g = npm.total();
	_p = npm.totalFirst();
	_q = npm.totalSecond();
	mJ = new double[4][4];  // A G T C
	mJ[0][0] = npm.getAA()/_f; mJ[0][1] = npm.getGA()/_f; mJ[0][2] = npm.getTA()/_f; mJ[0][3] = npm.getCA()/_f; 
	mJ[1][0] = npm.getAG()/_f; mJ[1][1] = npm.getGG()/_f; mJ[1][2] = npm.getTG()/_f; mJ[1][3] = npm.getCG()/_f; 
	mJ[2][0] = npm.getAT()/_f; mJ[2][1] = npm.getGT()/_f; mJ[2][2] = npm.getTT()/_f; mJ[2][3] = npm.getCT()/_f; 
	mJ[3][0] = npm.getAC()/_f; mJ[3][1] = npm.getGC()/_f; mJ[3][2] = npm.getTC()/_f; mJ[3][3] = npm.getCC()/_f; 
	modif = 0; // to avoid null determinant
        ii = -1;
	while ( ++ii < 4 ) {
	    if ( mJ[ii][0] + mJ[ii][1] + mJ[ii][2] + mJ[ii][3] == 0 ) {
		mJ[ii][ii] = 1 / _f;
		modif++;
	    }
	    if ( mJ[0][ii] + mJ[1][ii] + mJ[2][ii] + mJ[3][ii] == 0 ) {
		mJ[ii][ii] = 1 / _f;
		modif++;
	    }
	    /*if ( ii == 0 ) System.out.println("");
	      jj = -1;
	      while ( ++jj < 4 )
	      System.out.print(" " + mJ[ii][jj]);
	      System.out.println("");*/
	}
	if ( modif > 0 ) {
	    ii = -1;
	    while ( ++ii < 4 ) {
		jj = -1;
		while ( ++jj < 4 ) 
		    mJ[ii][jj] *= _f/(_f+modif);
	    }
	    _f += modif;
	    _g += 2*modif;
	    _p += modif;
	    _q += modif;
	}

	mC = new double[4][4];  // comatrix of mJ
	mC[0][0] = mJ[1][1]*(mJ[2][2]*mJ[3][3]-mJ[3][2]*mJ[2][3]) 
	    - mJ[1][2]*(mJ[2][1]*mJ[3][3]-mJ[3][1]*mJ[2][3]) + mJ[1][3]*(mJ[2][1]*mJ[3][2]-mJ[3][1]*mJ[2][2]);
	mC[0][1] = -mJ[1][0]*(mJ[2][2]*mJ[3][3]-mJ[3][2]*mJ[2][3]) 
	    + mJ[1][2]*(mJ[2][0]*mJ[3][3]-mJ[3][0]*mJ[2][3]) - mJ[1][3]*(mJ[2][0]*mJ[3][2]-mJ[3][0]*mJ[2][2]);
	mC[0][2] = mJ[1][0]*(mJ[2][1]*mJ[3][3]-mJ[3][1]*mJ[2][3]) 
	    - mJ[1][1]*(mJ[2][0]*mJ[3][3]-mJ[3][0]*mJ[2][3]) + mJ[1][3]*(mJ[2][0]*mJ[3][1]-mJ[3][0]*mJ[2][1]);
	mC[0][3] = -mJ[1][0]*(mJ[2][1]*mJ[3][2]-mJ[3][1]*mJ[2][2]) 
	    + mJ[1][1]*(mJ[2][0]*mJ[3][2]-mJ[3][0]*mJ[2][2]) - mJ[1][2]*(mJ[2][0]*mJ[3][1]-mJ[3][0]*mJ[2][1]);
	mC[1][0] = -mJ[0][1]*(mJ[2][2]*mJ[3][3]-mJ[3][2]*mJ[2][3]) 
	    + mJ[0][2]*(mJ[2][1]*mJ[3][3]-mJ[3][1]*mJ[2][3]) - mJ[0][3]*(mJ[2][1]*mJ[3][2]-mJ[3][1]*mJ[2][2]);
	mC[1][1] = mJ[0][0]*(mJ[2][2]*mJ[3][3]-mJ[3][2]*mJ[2][3]) 
	    - mJ[0][2]*(mJ[2][0]*mJ[3][3]-mJ[3][0]*mJ[2][3]) + mJ[0][3]*(mJ[2][0]*mJ[3][2]-mJ[3][0]*mJ[2][2]);
	mC[1][2] = -mJ[0][0]*(mJ[2][1]*mJ[3][3]-mJ[3][1]*mJ[2][3]) 
	    + mJ[0][1]*(mJ[2][0]*mJ[3][3]-mJ[3][0]*mJ[2][3]) - mJ[0][3]*(mJ[2][0]*mJ[3][1]-mJ[3][0]*mJ[2][1]);
	mC[1][3] = mJ[0][0]*(mJ[2][1]*mJ[3][2]-mJ[3][1]*mJ[2][2]) 
	    - mJ[0][1]*(mJ[2][0]*mJ[3][2]-mJ[3][0]*mJ[2][2]) + mJ[0][2]*(mJ[2][0]*mJ[3][1]-mJ[3][0]*mJ[2][1]);
	mC[2][0] = mJ[0][1]*(mJ[1][2]*mJ[3][3]-mJ[3][2]*mJ[1][3]) 
	    - mJ[0][2]*(mJ[1][1]*mJ[3][3]-mJ[3][1]*mJ[1][3]) + mJ[0][3]*(mJ[1][1]*mJ[3][2]-mJ[3][1]*mJ[1][2]);
	mC[2][1] = -mJ[0][0]*(mJ[1][2]*mJ[3][3]-mJ[3][2]*mJ[1][3]) 
	    + mJ[0][2]*(mJ[1][0]*mJ[3][3]-mJ[3][0]*mJ[1][3]) - mJ[0][3]*(mJ[1][0]*mJ[3][2]-mJ[3][0]*mJ[1][2]);
	mC[2][2] = mJ[0][0]*(mJ[1][1]*mJ[3][3]-mJ[3][1]*mJ[1][3]) 
	    - mJ[0][1]*(mJ[1][0]*mJ[3][3]-mJ[3][0]*mJ[1][3]) + mJ[0][3]*(mJ[1][0]*mJ[3][1]-mJ[3][0]*mJ[1][1]);
	mC[2][3] = -mJ[0][0]*(mJ[1][1]*mJ[3][2]-mJ[3][1]*mJ[1][2]) 
	    + mJ[0][1]*(mJ[1][0]*mJ[3][2]-mJ[3][0]*mJ[1][2]) - mJ[0][2]*(mJ[1][0]*mJ[3][1]-mJ[3][0]*mJ[1][1]);
	mC[3][0] = -mJ[0][1]*(mJ[1][2]*mJ[2][3]-mJ[2][2]*mJ[1][3]) 
	    + mJ[0][2]*(mJ[1][1]*mJ[2][3]-mJ[2][1]*mJ[1][3]) - mJ[0][3]*(mJ[1][1]*mJ[2][2]-mJ[2][1]*mJ[1][2]);
	mC[3][1] = mJ[0][0]*(mJ[1][2]*mJ[2][3]-mJ[2][2]*mJ[1][3]) 
	    - mJ[0][2]*(mJ[1][0]*mJ[2][3]-mJ[2][0]*mJ[1][3]) + mJ[0][3]*(mJ[1][0]*mJ[2][2]-mJ[2][0]*mJ[1][2]);
	mC[3][2] = -mJ[0][0]*(mJ[1][1]*mJ[2][3]-mJ[2][1]*mJ[1][3]) 
	    + mJ[0][1]*(mJ[1][0]*mJ[2][3]-mJ[2][0]*mJ[1][3]) - mJ[0][3]*(mJ[1][0]*mJ[2][1]-mJ[2][0]*mJ[1][1]);
	mC[3][3] = mJ[0][0]*(mJ[1][1]*mJ[2][2]-mJ[2][1]*mJ[1][2]) 
	    - mJ[0][1]*(mJ[1][0]*mJ[2][2]-mJ[2][0]*mJ[1][2]) + mJ[0][2]*(mJ[1][0]*mJ[2][1]-mJ[2][0]*mJ[1][1]);
	det = 0; // determinant of mJ
	//System.out.println("");
	ii = -1;
	while ( ++ii < 4 ) {
	    jj = -1;
	    while ( ++jj < 4 ) {
		det += mJ[ii][jj]*mC[ii][jj] + mJ[jj][ii]*mC[jj][ii];
		//System.out.print(" " + mC[ii][jj]);
	    }
	    //System.out.println("");
	}
	//System.out.println("");
	det /= 8;
	//System.out.println("\n" + det + "\n");
	dist = -(1 - square(Math.max(1,npm.getA())/_g) - square(Math.max(1,npm.getG())/_g) - square(Math.max(1,npm.getT())/_g) - square(Math.max(1,npm.getC())/_g)) * ( Math.log( det ) - 0.5*Math.log( (Math.max(1,npm.getAfirst())/_p)*(Math.max(1,npm.getGfirst())/_p)*(Math.max(1,npm.getTfirst())/_p)*(Math.max(1,npm.getCfirst())/_p)*(Math.max(1,npm.getAsecond())/_q)*(Math.max(1,npm.getGsecond())/_q)*(Math.max(1,npm.getTsecond())/_q)*(Math.max(1,npm.getCsecond())/_q) ) ) / 3;
	if ( ! unbiased ) 
	    return dist;
	var = 0;
	ii = -1;
	while ( ++ii < 4 ) {
	    _p1 = 1 / _p; _p2 = 1 / _q;
	    switch ( ii ) {
	    case 0: _p1 = Math.max(_p1 , npm.getAfirst()/_p); _p2 = Math.max(_p2 , npm.getAsecond()/_q); break;
	    case 1: _p1 = Math.max(_p1 , npm.getGfirst()/_p); _p2 = Math.max(_p2 , npm.getGsecond()/_q); break;
	    case 2: _p1 = Math.max(_p1 , npm.getTfirst()/_p); _p2 = Math.max(_p2 , npm.getTsecond()/_q); break;
	    case 3: _p1 = Math.max(_p1 , npm.getCfirst()/_p); _p2 = Math.max(_p2 , npm.getCsecond()/_q); break;
	    }
	    jj = -1;
	    while ( ++jj < 4 ) 
		var += square(mC[ii][jj]/det)*mJ[ii][jj];
	    var -= 1/Math.sqrt(_p1*_p2);
	    
	}
	var *= square(1 - square(Math.max(1,npm.getA())/_g) - square(Math.max(1,npm.getG())/_g) - square(Math.max(1,npm.getT())/_g) - square(Math.max(1,npm.getC())/_g)) / (9 * _f);
	//System.out.println(dist + " " + var );
	return dist - 2 * Math.max(0 , var);
    }



    


    // to get the square value of the input double
    private static double square( double dist ) {
	return dist * dist;
    }
    
}
