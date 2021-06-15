package org.panda.ast;

public class Mincaml_Float{
    public float f;
    public Label lab;
    public Mincaml_Float(float f, Label lab){
        this.f = f;
        this.lab = lab;
    }
    public float getFloat(){
        return f;
    }
    public Label getLabel(){
        return lab;
    }
}