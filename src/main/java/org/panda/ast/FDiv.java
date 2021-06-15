package org.panda.ast;

public class FDiv extends Exp {
    public final Exp e1;
    public final Exp e2;

    public FDiv(Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public <E> E accept(ObjVisitor<E> v) throws Exception {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
