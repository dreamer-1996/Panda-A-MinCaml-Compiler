package org.panda.ast;

import org.panda.ast.rep_classes.Let;
import org.panda.ast.rep_classes.Var;

public interface ObjVisitor<E> {
    E visit(Unit e);

    E visit(Bool e);

    E visit(Int e);

    E visit(Float e);

    E visit(Not e) throws Exception;

    E visit(Neg e) throws Exception;

    E visit(Add e) throws Exception;

    E visit(Sub e) throws Exception;

    E visit(FNeg e) throws Exception;;

    E visit(FAdd e) throws Exception;;

    E visit(FSub e) throws Exception;;

    E visit(FMul e) throws Exception;;

    E visit(FDiv e) throws Exception;;

    E visit(Eq e) throws Exception;;

    E visit(LE e) throws Exception;;

    E visit(If e) throws Exception;;

    E visit(Let e) throws Exception;;

    E visit(Var e);

    E visit(LetRec e) throws Exception;;

    E visit(App e) throws Exception;;

    E visit(Tuple e) throws Exception;;

    E visit(LetTuple e) throws Exception;;

    E visit(Array e) throws Exception;;

    E visit(Get e) throws Exception;;

    E visit(Put e) throws Exception;;

    E visit(org.panda.backend.ast.Var e);

    E visit(org.panda.backend.ast.Let e);

    E visit(New e);

    E visit(Mem e);

    E visit(Mem_Assign e);

    E visit(Nop e);

    E visit(Function e);

    E visit(Call_clo e);

    E visit(If_eq e);

    E visit(If_le e);

    E visit(If_ge e);

    E visit(If_feq e);

    E visit(If_fle e);

    E visit(Call e);

    E visit(Label e);
}


