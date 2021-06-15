package org.panda.ast;

import java.util.List;

public class LetTuple extends Exp {
    final List<Id> ids;
    final List<Type> ts;
    public final Exp e1;
    public final Exp e2;

    public List<Id> getIds() {
        return ids;
    }

    public List<Type> getTs() {
        return ts;
    }

    public LetTuple(List<Id> ids, List<Type> ts, Exp e1, Exp e2) {
        this.ids = ids;
        this.ts = ts;
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
