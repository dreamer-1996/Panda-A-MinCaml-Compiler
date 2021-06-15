package org.panda.ast.rep_classes;
import org.panda.ast.*;
import java.util.*;

public class Var extends Exp {
    public final Id id;    

    public Var(Id id) {
        this.id = id;
    }          

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
