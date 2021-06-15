package org.panda.ast;
import java.util.*;

public class Label extends Exp{
    static List<Label> all_lab = new ArrayList<Label>();

    public String id;
    public Label(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return id;
    }

    public static Label get(String name) {
        for (Label lab : all_lab){
            if (lab.id.equals(name)){
                return lab;
            }
        }
        // Label l = new Label(name.substring(1));
        Label l = new Label(name);
        all_lab.add(l);
        return l;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
