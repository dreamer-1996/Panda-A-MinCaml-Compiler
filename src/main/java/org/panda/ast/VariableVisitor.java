package org.panda.ast;

import org.panda.ast.rep_classes.*;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class VariableVisitor implements ObjVisitor<Set<String>>{
    // vars : all vars in current env
    Set<String> vars;
    // availIds : available vars in current env
    public List<String> availVars;

    public VariableVisitor() {
        this.vars = new HashSet<String>();
        this.availVars = new ArrayList<String>();
    }

    public Set<String> visit(Unit e) {
        return this.vars;
    }

    public Set<String> visit(Bool e) {
        return this.vars;
    }

    public Set<String> visit(Int e) {
        return this.vars;
    }

    public Set<String> visit(Float e) {
        return this.vars;
    }

    public Set<String> visit(Not e) throws Exception {
        e.e.accept(this);
        return this.vars;
    }

    public Set<String> visit(Neg e) throws Exception {
        e.e.accept(this);
        return this.vars;
    }

    public Set<String> visit(Add e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(Sub e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(FNeg e) throws Exception {
        e.e.accept(this);
        return this.vars;
    }

    public Set<String> visit(FAdd e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(FSub e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(FMul e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(FDiv e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(Eq e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(LE e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(If e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        e.e3.accept(this);
        return this.vars;
    }

    public Set<String> visit(Let e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        availVars.add(e.id.toString());
        return this.vars;
    }

    public Set<String> visit(Var e) {
        this.vars.add(e.id.toString());
        return this.vars;
    }

    public Set<String> visit(LetRec e) throws Exception {
        e.e.accept(this);
        e.fd.e.accept(this);
        for (Id arg : e.fd.args){
            availVars.add(arg.toString());
        }
        return this.vars;
    }

    public Set<String> visit(App e) throws Exception {
        e.e.accept(this);
        for (Exp exp : e.es) {
            exp.accept(this);
        }
        return this.vars;
    }

    public Set<String> visit(Tuple e) throws Exception {
        for (Exp exp : e.es) {
            exp.accept(this);
        }
        return this.vars;
    }

    public Set<String> visit(LetTuple e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(Array e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(Get e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        return this.vars;
    }

    public Set<String> visit(Put e) throws Exception {
        e.e1.accept(this);
        e.e2.accept(this);
        e.e3.accept(this);
        return this.vars;
    }

    public Set<String> visit(Call e) {
        return this.vars;
    }

    public Set<String> visit(Label e) {
        return this.vars;
    }

    public Set<String> visit(org.panda.backend.ast.Var e) {
        return this.vars;
    }

    public Set<String> visit(org.panda.backend.ast.Let e) {
        return this.vars;
    }

    public Set<String> visit(New e) {
        return this.vars;
    }

    public Set<String> visit(Mem e) {
        return this.vars;
    }

    public Set<String> visit(Mem_Assign e) {
        return this.vars;
    }

    public Set<String> visit(Nop e) {
        return this.vars;
    }

    public Set<String> visit(Function e) {
        return this.vars;
    }

    public Set<String> visit(Call_clo e) {
        return this.vars;
    }

    public Set<String> visit(If_eq e) {
        return this.vars;
    }

    public Set<String> visit(If_le e) {
        return this.vars;
    }

    public Set<String> visit(If_ge e) {
        return this.vars;
    }

    public Set<String> visit(If_feq e) {
        return this.vars;
    }

    public Set<String> visit(If_fle e) {
        return this.vars;
    }
}
