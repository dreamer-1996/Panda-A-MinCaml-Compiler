package org.panda.ast;

public class Mem extends Exp {
    public Exp e1;
    public Exp e2;

    public Mem(Exp e1, Exp e2) {
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
