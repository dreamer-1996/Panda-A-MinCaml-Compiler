package org.panda.ast;

public class Nop extends Exp {
    public Nop() {}

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}