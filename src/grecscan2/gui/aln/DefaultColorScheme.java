/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import grecscan2.core.Sequence;
import java.awt.Color;
import java.util.HashMap;

/**
 *
 * @author wb
 */
public class DefaultColorScheme implements ColorSchemeI{
    private HashMap<String,Color> colorMap=new HashMap<String,Color>();
    
    
    public DefaultColorScheme(){
        addSpecies("A", new Color(229,51,25));
        addSpecies("a", new Color(229,51,25));
        addSpecies("T", new Color(25,204,25));
        addSpecies("t", new Color(25,204,25));
        addSpecies("G", new Color(229,153,76));
        addSpecies("g", new Color(229,153,76));
        addSpecies("C", new Color(25,127,229));
        addSpecies("c", new Color(25,127,229));
        addSpecies("-", Color.white);
        addSpecies(" ", Color.white);
        addSpecies("~", Color.white);
        addSpecies(".", Color.white);
    }
    
    
    public void addSpecies(char label, Color color){
        addSpecies(label+"", color);
    }
    
    public void addSpecies(String label, Color color){
        colorMap.put(label, color);
    }
    
    @Override
    public Color colorOf(String label){
        if(colorMap.containsKey(label)){
            return colorMap.get(label);
        }
        return Color.gray;
    }
    
    @Override
    public Color colorOf(char label){
        return colorOf(label+"");
    }

    @Override
    public Color colorOf(char c, Sequence seq, int offset) {
        return colorOf(c);
    }

    /**
     * @return the colorMap
     */
    public HashMap<String,Color> getColorMap() {
        return colorMap;
    }

    /**
     * @param colorMap the colorMap to set
     */
    public void setColorMap(HashMap<String,Color> colorMap) {
        this.colorMap = colorMap;
    }

    @Override
    public Color colorOf(Sequence seq) {
        return Color.white;
    }

}
