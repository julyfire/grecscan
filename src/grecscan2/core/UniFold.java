/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.core;

import wbitoolkit.rna.Fold;

/**
 *
 * @author wb
 */
public class UniFold {
    
    private Fold f;
    
    public static void initFold(int length){
        Fold.loadEnergyParameters();
        Fold.update_fold_params(length);
    }
    
    public UniFold(int n){
        f=new Fold();
        f.initialize_fold(n);
    }
    public float fold(String seq){
        f.reset_fold();
        return f.fold(seq);
    }

}
