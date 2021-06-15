package org.panda.ast;

public class Mem_Assign extends Exp {
    public Exp e1;
    public Exp e2;
    public Exp e3;

    public Mem_Assign(Exp e1, Exp e2, Exp e3) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}