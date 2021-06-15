package org.panda.ast;

public class If_ge extends Exp {
    public final Exp e1;
    public final Exp e2;
    public final Exp e3;
    public final Exp e4;

    public If_ge(Exp e1, Exp e2, Exp e3, Exp e4) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.e4 = e4;
    }
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}