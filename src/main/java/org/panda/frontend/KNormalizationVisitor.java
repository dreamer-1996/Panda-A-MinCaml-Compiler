package org.panda.frontend;

import org.panda.ast.rep_classes.*;
import org.panda.ast.*;
import org.panda.ast.Float;
import org.panda.ast.Label;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * KNormalizationVisitor implements KNormalization as defined in the min-caml article
 */
public class KNormalizationVisitor implements ObjVisitor<Exp> {
    public Exp visit(Unit e) {
        return e;
    }

    public Exp visit(Bool e) {
        return e;
    }

    public Exp visit(Int e) {
        return e;
    }

    public Exp visit(Float e) {
        return e;
    }

    /**
     * We apply the K-Normalization to not(e) using the specification:
     * K(not(e1, . . . , en)) =
     *      let x1 = K(e1) in
     *      . . .
     *      let xn = K(en) in
     *      not(x1, . . . , xn)
     * @param e a Not expression
     * @return Let
     */
    public Exp visit(Not e) throws Exception {
        Id var = Id.gen();
        Not newNot = new Not(new Var(var));
        return new Let(var, Type.gen(), e.e.accept(this), newNot);
    }

    /**
     * We apply the K-Normalization to -(e) using the specification:
     * <pre>
     *     {@code
     *      K(-(e1, . . . , en)) =
     *        let x1 = K(e1) in
     *        . . .
     *        let xn = K(en) in
     *        -(x1, . . . , xn)
     *     }
     * </pre>
     * @param e Not expression
     * @return Let
     */
    public Let visit(Neg e) throws Exception{
        Id var = Id.gen();
        Neg newNeg = new Neg(new Var(var));
        return new Let(var, Type.gen(), e.e.accept(this), newNeg);
    }

    /**
     * Given an operation {@code (1 + 2) } we apply KNormalization as follows:
     *<pre>
     * {@code
     *      K(1 + 2) =
     *          let x1 = K(1) in
     *          let x2 = K(2) in
     *          (x1 + x2)
     *}
     *</pre>
     *
     * @param e {@code Add } expression
     * @return {@code Let } expression
     */
    public Let visit(Add e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        Add add = new Add(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), add);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(Sub e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        Sub sub = new Sub(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), sub);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(FNeg e) throws Exception {
        Id var = Id.gen();
        FNeg newFNeg = new FNeg(new Var(var));
        return new Let(var, Type.gen(), e.e.accept(this), newFNeg);
    }

    public Exp visit(FAdd e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        FAdd fAdd = new FAdd(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), fAdd);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(FSub e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        FSub fSub = new FSub(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), fSub);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(FMul e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        FMul fMul = new FMul(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), fMul);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(FDiv e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        FDiv fDiv = new FDiv(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), fDiv);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(Eq e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        Eq eq = new Eq(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), eq);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(LE e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        LE le = new LE(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), le);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(If e) throws Exception {
        if (e.e1 instanceof Eq) {
            return newLet((Eq) e.e1, e.e2, e.e3);
        }

        if (e.e1 instanceof LE) {
            return newLet((LE) e.e1, e.e2, e.e3);
        }

        if (e.e1 instanceof Not) {
            Not not = (Not) e.e1;
            if (not.e instanceof Eq) {
                return newLet((Eq) not.e, e.e3, e.e2);
            }

            if (not.e instanceof LE) {
                return newLet((LE) not.e, e.e3, e.e2);
            }
        }

        Id id = Id.gen();
        return new Let(id, Type.gen(), e.e1.accept(this), new If(new Var(id), e.e2.accept(this), e.e3.accept(this)));
    }

    private Let newLet(Eq eq, Exp e2, Exp e3) throws Exception {
        Id x = Id.gen();
        Id y = Id.gen();

        Eq newEq = new Eq(new Var(x), new Var(y));

        If fi = new If(newEq, e2.accept(this), e3.accept(this));
        Let l1 = new Let(y, Type.gen(), eq.e2.accept(this),fi);
        return new Let(x, Type.gen(), eq.e1.accept(this), l1);
    }

    private Let newLet(LE le, Exp e2, Exp e3) throws Exception {
        Id x = Id.gen();
        Id y = Id.gen();

        LE newLE = new LE(new Var(x), new Var(y));

        If fi = new If(newLE, e2.accept(this), e3.accept(this));
        Let l1 = new Let(y, Type.gen(), le.e2.accept(this),fi);
        return new Let(x, Type.gen(), le.e1.accept(this), l1);
    }

    public Exp visit(Let e) throws Exception {
        return new Let(e.id, e.t, e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(Var e) {
        return e;
    }

    public Exp visit(LetRec e) throws Exception {
        Exp exp2 = e.fd.e.accept(this);
        Exp exp1 = e.e.accept(this);
        return new LetRec(new FunDef(e.fd.id, e.fd.type, e.fd.args, exp2), exp1);
    }

    /**
     * We apply reduction to function calls using the specifications:
     * K(M N1 . . . Nn) =
     *      let x = K(M) in
     *      let y1 = K(N1) in
     *      . . .
     *      let yn = K(Nn) in
     *      x y1 . . . yn
     * @param e unnormalized App
     * @return KNormalized App
     */
    public Exp visit(App e) throws Exception{
        // Generate a new Id for every expression
        List<Id> newIds = getNewIds(e.es);

        // Transform the original App with new variables
        Exp original;
        if (e.e instanceof Var) {
            original = new App(e.e, getNewVarsFromIds(newIds));
        } else {
            Id id = Id.gen();
            App app = new App(new Var(id), getNewVarsFromIds(newIds));
            original = new Let(id, Type.gen(), e.e.accept(this), app);
        }

        return newNestedLet(e.es, newIds, original);
    }

    public Exp visit(Tuple e) throws Exception {
        // Generate a new Id for every expression
        List<Id> newIds = getNewIds(e.es);

        // Transform the original App with new variables
        Exp original = new Tuple(getNewVarsFromIds(newIds));

        return newNestedLet(e.es, newIds, original);
    }

    private Exp newNestedLet(List<Exp> es, List<Id> newIds, Exp original) throws Exception {
        for (int i = es.size() - 1; i >= 0 ; i--) {
            original = new Let(newIds.get(i), Type.gen(), es.get(i).accept(this), original);
        }

        return original;
    }

    private List<Exp> getNewVarsFromIds(List<Id> newIds) {
        return newIds.stream()
                .map(Var::new)
                .collect(Collectors.toList());
    }

    private List<Id> getNewIds(List<Exp> es) {
        return Stream.generate(Id.gen())
                .limit(es.size())
                .collect(Collectors.toList());
    }

    public Exp visit(LetTuple e) throws Exception {
        return new LetTuple(e.getIds(), e.getTs(), e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(Array e) throws Exception {
        Id size = Id.gen();
        Id initVal = Id.gen();

        Array arr = new Array(new Var(size), new Var(initVal));
        Let let = new Let(initVal, Type.gen(), e.e2.accept(this), arr);
        return new Let(size, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(Get e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();

        Get get = new Get(new Var(var1), new Var(var2));
        Let let = new Let(var2, Type.gen(), e.e2.accept(this), get);
        return new Let(var1, Type.gen(), e.e1.accept(this), let);
    }

    public Exp visit(Put e) throws Exception {
        Id var1 = Id.gen();
        Id var2 = Id.gen();
        Id var3 = Id.gen();

        Put put = new Put(new Var(var1), new Var(var2), new Var(var3));

        Let l1 = new Let(var3, Type.gen(), e.e3.accept(this), put);
        Let l2 = new Let(var2, Type.gen(), e.e2.accept(this), l1);
        return new Let(var1, Type.gen(), e.e1.accept(this), l2);
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
