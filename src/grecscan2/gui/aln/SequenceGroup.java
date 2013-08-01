/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import grecscan2.core.Sequence;
import java.awt.Color;
import java.util.LinkedList;

/**
 *
 * @author wb
 */
class SequenceGroup {
    
    private String groupName;

    private String description;
    
    private LinkedList sequences=new LinkedList();
    
    int width = -1;

    private DefaultColorScheme cs;

    private int startRes = 0;

    private int endRes = 0;

    private Color outlineColour = Color.black;

    private Color idColour = null;
    
    public SequenceGroup(){
        groupName = "JGroup:" + this.hashCode();
    }
    

    

    public void addOrRemove(Sequence seq) {
        if (sequences.contains(seq)){
            deleteSequence(seq);
        }
        else{
            addSequence(seq);
        }
    }

    public void addSequence(Sequence seq) {
        if (seq != null && !sequences.contains(seq)){
            sequences.add(seq);
        }
    }
    
    public void deleteSequence(Sequence seq){
        sequences.remove(seq);
    }

    public void deleteAll(){
        sequences.clear();
    }
    /**
     * @return the sequences
     */
    public LinkedList getSequences() {
        return sequences;
    }

    /**
     * @param sequences the sequences to set
     */
    public void setSequences(LinkedList sequences) {
        this.sequences = sequences;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the cs
     */
    public DefaultColorScheme getCs() {
        return cs;
    }

    /**
     * @param cs the cs to set
     */
    public void setCs(DefaultColorScheme cs) {
        this.cs = cs;
    }

    /**
     * @return the startRes
     */
    public int getStartRes() {
        return startRes;
    }

    /**
     * @return the endRes
     */
    public int getEndRes() {
        return endRes;
    }

    /**
     * @return the outlineColour
     */
    public Color getOutlineColour() {
        return outlineColour;
    }

    /**
     * @param outlineColour the outlineColour to set
     */
    public void setOutlineColour(Color outlineColour) {
        this.outlineColour = outlineColour;
    }

    /**
     * @return the idColour
     */
    public Color getIdColour() {
        return idColour;
    }

    /**
     * @param idColour the idColour to set
     */
    public void setIdColour(Color idColour) {
        this.idColour = idColour;
    }

    /**
     * @param startRes the startRes to set
     */
    public void setStartRes(int startRes) {
        this.startRes = startRes;
    }

    /**
     * @param endRes the endRes to set
     */
    public void setEndRes(int endRes) {
        this.endRes = endRes;
    }
    
}
