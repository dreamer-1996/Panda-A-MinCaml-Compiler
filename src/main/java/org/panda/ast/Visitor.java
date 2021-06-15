package org.panda.ast;

import org.panda.ast.rep_classes.Let;
import org.panda.ast.rep_classes.Var;

public interface Visitor {

    void visit(Unit e);

    void visit(Bool e);

    void visit(Int e);

    void visit(Float e);

    void visit(Not e);

    void visit(Neg e);

    void visit(Add e);

    void visit(Sub e);

    void visit(FNeg e);

    void visit(FAdd e);

    void visit(FSub e);

    void visit(FMul e);

    void visit(FDiv e);

    void visit(Eq e);

    void visit(LE e);

    void visit(If e);

    void visit(Let e);

    void visit(Var e);

    void visit(LetRec e);

    void visit(App e);

    void visit(Tuple e);

    void visit(LetTuple e);

    void visit(Array e);

    void visit(Get e);

    void visit(Put e);

    void visit(org.panda.backend.ast.Var e);

    void visit(org.panda.backend.ast.Let e);

    void visit(New e);

    void visit(Mem e);

    void visit(Mem_Assign e);

    void visit(Nop e);

    void visit(Function e);

    void visit(Call_clo e);

    void visit(If_eq e);

    void visit(If_le e);

    void visit(If_ge e);

    void visit(If_feq e);

    void visit(If_fle e);

    void visit(Call e);

    void visit(Label e);
}


