package org.panda.backend.ast;
import org.panda.ast.*;

public class Let extends Exp {
    public final Var id;
    public final Exp e1;
    public final Exp e2;

    public Let(Var id,  Exp e1, Exp e2) {
        this.id = id;
        this.e1 = e1;
        this.e2 = e2;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
