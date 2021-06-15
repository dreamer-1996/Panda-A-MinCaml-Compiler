package org.panda.ast;

public class Float extends Exp {
    float f;

    public float getF() {
        return f;
    }

    public Float(float f) {
        this.f = f;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
