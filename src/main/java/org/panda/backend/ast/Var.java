package org.panda.backend.ast;
import org.panda.ast.*;
import java.util.*;

public class Var extends Exp{
    static List<Var> all_var  = new LinkedList<Var>();

    public String id;
    public Var(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return id;
    }

    public static Var get(String name) {
        for (Var var : all_var){
            if (var.id.equals(name) ){
                return var;
            }
        }
        Var v = new Var(name);
        all_var.add(v);
        return v;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
