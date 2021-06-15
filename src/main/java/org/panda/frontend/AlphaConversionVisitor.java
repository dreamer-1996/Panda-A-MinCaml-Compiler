package org.panda.frontend;

import org.panda.ast.Float;
import org.panda.ast.*;
import org.panda.ast.rep_classes.Let;
import org.panda.ast.rep_classes.Var;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AlphaConversionVisitor implements ObjVisitor<Exp> {
    private static final String ROOT = "root";
    private static int varCounter = 0;
    private static int funCounter = 0;
    public Env currentEnv;
    public Map<String, Env> globalEnv;

    public AlphaConversionVisitor() {
        this.currentEnv = new Env(null, new HashMap<>());
        this.globalEnv = new HashMap<>();
        this.globalEnv.put(ROOT, this.currentEnv);
    }

    private static String getId(String s) {
        if ("f".equals(s)) {
            funCounter++;
            return s + funCounter;
        }
        varCounter++;
        return s + varCounter;
    }

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

    public Exp visit(Not e) throws Exception {
        return new Not(e.e.accept(this));
    }

    public Exp visit(Neg e) throws Exception {
        return new Neg(e.e.accept(this));
    }

    public Exp visit(Add e) throws Exception {
        return new Add(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(Sub e) throws Exception {
        return new Sub(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(FNeg e) throws Exception {
        return new FNeg(e.e.accept(this));
    }

    public Exp visit(FAdd e) throws Exception {
        return new FAdd(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(FSub e) throws Exception {
        return new FSub(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(FMul e) throws Exception {
        return new FMul(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(FDiv e) throws Exception {
        return new FDiv(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(Eq e) throws Exception {
        return new Eq(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(LE e) throws Exception {
        return new LE(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(If e) throws Exception {
        return new If(e.e1.accept(this), e.e2.accept(this), e.e3.accept(this));
    }

    public Exp visit(Let e) throws Exception {
        Id id = new Id(getId("v"));
        this.currentEnv.mapping.put(e.id.toString(), id.toString());
        return new Let(id, e.t, e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(Var e) {
        return new Var(new Id(findMapping(e)));
    }

    private String findMapping(Var v) {
        Env env = this.currentEnv;
        String s = v.id.toString();
        while (env != null) {
            if (env.mapping.containsKey(s)) {
                return env.mapping.get(s);
            }
            env = env.parent;
        }
        return s;
    }

    public Exp visit(LetRec e) throws Exception {
        Id f = new Id(getId("f"));

        // add function's new variable to root env
        this.globalEnv.get(ROOT).mapping.put(e.fd.id.toString(), f.toString());

        // create function's new environment
        Env newEnv = new Env(this.currentEnv, new HashMap<>());

        // set new env parent
        newEnv.parent = this.currentEnv;

        // create new args
        List<Id> newArgs = getNewIds(e.fd.args);

        // add new args to mapping
        addNewIdsToMapping(newArgs, newEnv, e.fd.args);

        // add newEnv to global
        this.globalEnv.put(e.fd.id.toString(), newEnv);

        // switch env so that e.fd.e uses it
        this.currentEnv = newEnv;

        // define new function
        FunDef fd = new FunDef(f, e.fd.type, newArgs, e.fd.e.accept(this));

        // switch back to parent env
        this.currentEnv = this.currentEnv.parent;

        // Execute e.e in currentEnv
        Exp e1 = e.e.accept(this);

        return new LetRec(fd, e1);
    }

    public Exp visit(App e) throws Exception {
        return new App(e.e.accept(this), mapToNewEs(e.es));
    }

    public Exp visit(Tuple e) {
        return new Tuple(mapToNewEs(e.es));
    }

    private List<Exp> mapToNewEs(List<Exp> es) {
        return es.stream()
                .map(exp -> {
                    try {
                        return exp.accept(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    public Exp visit(LetTuple e) throws Exception {
        Exp e1 = e.e1.accept(this);

        List<Id> newIds = getNewIds(e.getIds());

        addNewIdsToMapping(newIds, this.currentEnv, e.getIds());

        return new LetTuple(newIds, e.getTs(), e1, e.e2.accept(this));
    }

    private List<Id> getNewIds(List<Id> ids) {
        return ids.stream()
                .map(id -> new Id(getId("v")))
                .collect(Collectors.toList());
    }

    private void addNewIdsToMapping(List<Id> newIds, Env env, List<Id> ids) {
        IntStream.range(0, newIds.size())
                .forEach(i -> env.mapping.put(ids.get(i).toString(), newIds.get(i).toString()));
    }

    public Exp visit(Array e) throws Exception {
        return new Array(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(Get e) throws Exception {
        return new Get(e.e1.accept(this), e.e2.accept(this));
    }

    public Exp visit(Put e) throws Exception {
        return new Put(e.e1.accept(this), e.e2.accept(this), e.e3.accept(this));
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

    private static class Env {
        Env parent;
        Map<String, String> mapping;

        public Env(Env parent, Map<String, String> mapping) {
            this.parent = parent;
            this.mapping = mapping;
        }

        @Override
        public String toString() {
            return "Env{" +
                    "parent=" + parent +
                    ", mapping=" + mapping +
                    '}';
        }
    }
}
