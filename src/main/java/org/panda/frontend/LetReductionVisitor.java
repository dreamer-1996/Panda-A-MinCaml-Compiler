package org.panda.frontend;

import org.panda.ast.rep_classes.*;
import org.panda.ast.*;
import org.panda.ast.Float;
import org.panda.ast.Label;

public class LetReductionVisitor implements ObjVisitor<Exp> {
    @Override
    public Exp visit(Unit e) {
        return e;
    }

    @Override
    public Exp visit(Bool e) {
        return e;
    }

    @Override
    public Exp visit(Int e) {
        return e;
    }

    @Override
    public Exp visit(Float e) {
        return e;
    }

    @Override
    public Exp visit(Not e) {
        return e;
    }

    @Override
    public Exp visit(Neg e) {
        return e;
    }

    @Override
    public Exp visit(Add e) {
        return e;
    }

    @Override
    public Exp visit(Sub e) {
        return e;
    }

    @Override
    public Exp visit(FNeg e) {
        return e;
    }

    @Override
    public Exp visit(FAdd e) {
        return e;
    }

    @Override
    public Exp visit(FSub e) {
        return e;
    }

    @Override
    public Exp visit(FMul e) {
        return e;
    }

    @Override
    public Exp visit(FDiv e) {
        return e;
    }

    @Override
    public Exp visit(Eq e) {
        return e;
    }

    @Override
    public Exp visit(LE e) {
        return e;
    }

    @Override
    public Exp visit(If e) throws Exception {
        return new If(e.e1.accept(this), e.e2.accept(this), e.e3.accept(this));
    }

    @Override
    public Exp visit(Let e) throws Exception {
        // check for nested Lets
        if (e.e1 instanceof Let) {
            Let let_e1 = (Let)e.e1;
            Let let1 = new Let(e.id, e.t, let_e1.e2.accept(this), e.e2.accept(this));

            Let let2 = new Let(let_e1.id, let_e1.t, let_e1.e1.accept(this), let1.accept(this));
            return let2.accept(this);
        }
        return new Let(e.id, e.t, e.e1.accept(this), e.e2.accept(this));
    }

    @Override
    public Exp visit(Var e) {
        return e;
    }

    @Override
    public Exp visit(LetRec e) throws Exception {
        FunDef fd = e.fd;
        return new LetRec(new FunDef(fd.id, fd.type, fd.args, fd.e.accept(this)), e.e.accept(this));

    }

    @Override
    public Exp visit(App e) {
        return e;
    }

    @Override
    public Exp visit(Tuple e) {
        return e;
    }

    @Override
    public Exp visit(LetTuple e) throws Exception {
        return new LetTuple(e.getIds(), e.getTs(), e.e1.accept(this), e.e2.accept(this));
    }

    @Override
    public Exp visit(Array e) {
        return e;
    }

    @Override
    public Exp visit(Get e) {
        return e;
    }

    @Override
    public Exp visit(Put e) {
        return e;
    }

    public Exp visit(Call e) {
        return e;
    }

    public Exp visit(Label e) {
        return e;
    }

    public Exp visit(org.panda.backend.ast.Var e) {
        return e;
    }

    public Exp visit(org.panda.backend.ast.Let e) {
        return e;
    }

    public Exp visit(New e) {
        return e;
    }

    public Exp visit(Mem e) {
        return e;
    }

    public Exp visit(Mem_Assign e) {
        return e;
    }

    public Exp visit(Nop e) {
        return e;
    }

    public Exp visit(Function e) {
        return e;
    }

    public Exp visit(Call_clo e) {
        return e;
    }

    public Exp visit(If_eq e) {
        return e;
    }

    public Exp visit(If_le e) {
        return e;
    }

    public Exp visit(If_ge e) {
        return e;
    }

    public Exp visit(If_feq e) {
        return e;
    }

    public Exp visit(If_fle e) {
        return e;
    }
}
