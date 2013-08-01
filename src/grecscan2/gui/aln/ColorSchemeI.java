/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import grecscan2.core.Sequence;
import java.awt.Color;

/**
 *
 * @author wb
 */
public interface ColorSchemeI {
    
    public Color colorOf(char c);
    
    public Color colorOf(String s);
    
    /**
     * color of char c at offset of seq
     * @param c
     * @param seq
     * @param pos
     * @return 
     */
    public Color colorOf(char c, Sequence seq, int offset);
    
    public Color colorOf(Sequence seq);
    
}
