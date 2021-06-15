package org.panda.ast;
import java.util.List;

public class Call extends Exp {
    public final Label function;
    public final List<org.panda.backend.ast.Var> args;

    public Call(Label function, List<org.panda.backend.ast.Var> args) {
        this.function = function;
        this.args = args;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}