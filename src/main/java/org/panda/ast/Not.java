package org.panda.ast;

public class Not extends Exp {
    public final Exp e;

    public Not(Exp e) {
        this.e = e;
    }

    public <E> E accept(ObjVisitor<E> v) throws Exception {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
