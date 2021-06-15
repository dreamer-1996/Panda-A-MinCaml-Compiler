package org.panda.ast;
import java.util.List;

public class Call_clo extends Exp {
    public final org.panda.backend.ast.Var id;
    public final List<org.panda.backend.ast.Var> args;

    public Call_clo(org.panda.backend.ast.Var id, List<org.panda.backend.ast.Var> args) {
        this.id = id;
        this.args = args;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}