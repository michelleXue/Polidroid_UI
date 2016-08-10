/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelanalysis;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Xue
 */
public class Label {
    
    private String name;
    private int frequency;
    
    
    Label( String s ) {
        this.name = s;
        this.frequency = 1;
    }
    
    void addFrequency () {
        this.frequency = this.frequency +1;
    }
    
    void setName (String labelName) {
        this.name = labelName;
    }
    
    String getName () {
        return this.name;
    }
    
    int getFrequency () {
        return this.frequency;
    }
}
