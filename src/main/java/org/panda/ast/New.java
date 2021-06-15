package org.panda.ast;

public class New extends Exp {
    public Exp e;

    public New(Exp e) {
        this.e = e;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
