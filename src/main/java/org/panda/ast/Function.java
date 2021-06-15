package org.panda.ast;
import java.util.*;

public class Function extends Exp{
    public Label lab;
    public List<org.panda.backend.ast.Var> args;
    public Exp body;
    public Function(Label lab, List<org.panda.backend.ast.Var> args, Exp body){
        this.lab = lab;
        this.args = args;
        this.body = body;
    }
    public Label getLabel(){
        return lab;
    }
    public List<org.panda.backend.ast.Var> getArgs(){
        return args;
    }
    public Exp getBody(){
        return body;
    }

    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}