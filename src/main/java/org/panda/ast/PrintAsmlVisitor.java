package org.panda.ast;
import org.panda.ast.rep_classes.*;

import java.util.*;

public class PrintAsmlVisitor implements Visitor {
    public void visit(Call e) {
        System.out.print("call ");
        e.function.accept(this);
        System.out.print(" ");
        printInfix3(e.args, " ");

    }

    public void visit(Label e) {
        System.out.print("_"+e.id);
    }

    public void visit(org.panda.backend.ast.Var e) {
        System.out.print(e.id);
    }

    public void visit(org.panda.backend.ast.Let e) {
        System.out.print("\n\tlet ");
        System.out.print(e.id);
        System.out.print(" = ");
        e.e1.accept(this);
        System.out.print(" in ");
        e.e2.accept(this);

    }

    public void visit(New e) {
        System.out.print("new ");
        e.e.accept(this);

    }

    public void visit(Mem e) {
        System.out.print("mem(");
        e.e1.accept(this);
        System.out.print(" + ");
        e.e2.accept(this);
        System.out.print(")");
    }


    public void visit(Mem_Assign e) {
        System.out.print("mem(");
        e.e1.accept(this);
        System.out.print(" + ");
        e.e2.accept(this);
        System.out.print(") <- ");
        e.e3.accept(this);
    }

    public void visit(Nop e) {
        System.out.print(" nop ");
    }

    public void visit(Function e) {
        System.out.print("let ");
        e.lab.accept(this);
        System.out.print(" ");
        printInfix3(e.args, " ");
        System.out.print(" = \t");
        e.body.accept(this);
        System.out.print("\n\n");

    }

    public void visit(Call_clo e) {
        System.out.print("call_closure ");
        e.id.accept(this);
        System.out.print(" ");
        printInfix3(e.args, " ");

    }

    public void visit(If_eq e){
        System.out.print("");
    }
    public void visit(If_le e){
        System.out.print("");
    }
    public void visit(If_ge e){
        System.out.print("");
    }
    public void visit(If_feq e){
        System.out.print("");
    }
    public void visit(If_fle e){
        System.out.print("");
    }

    public void visit(Unit e) {
        System.out.print("()");
    }

    public void visit(Bool e) {
        System.out.print(e.b);
    }

    public void visit(Int e) {
        System.out.print(e.i);
    }

    public void visit(Float e) {
        String s = String.format("%.2f", e.f);
        System.out.print(s);
    }

    public void visit(Not e) {
        System.out.print("not ");
        e.e.accept(this);
        System.out.print("");
    }

    public void visit(Neg e) {
        System.out.print("neg ");
        e.e.accept(this);

    }

    public void visit(Add e) {
        System.out.print("add ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(Sub e) {
        System.out.print("sub ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(FNeg e){
        System.out.print("fneg ");
        e.e.accept(this);
        // System.out.print(")");
    }

    public void visit(FAdd e){
        System.out.print("fadd ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(FSub e){
        System.out.print("fsub ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(FMul e) {
        System.out.print("fmul ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(FDiv e){
        System.out.print("fdiv ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(Eq e){
        // System.out.print("(");
        e.e1.accept(this);
        System.out.print(" = ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(LE e){
        // System.out.print("(");
        e.e1.accept(this);
        System.out.print(" <= ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(If e){
        System.out.print("if ");
        e.e1.accept(this);
        System.out.print(" then (\n\t");
        e.e2.accept(this);
        System.out.print("\n\t) else (\t");
        e.e3.accept(this);
        System.out.print(")");
    }

    public void visit(Let e) {
        System.out.print("\n(let ");
        System.out.print(e.id);
        System.out.print(" = ");
        e.e1.accept(this);
        System.out.print(" in\n\t ");
        e.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Var e){
        System.out.print(e.id);
    }

    // print sequence of identifiers
    static <E> void printInfix(List<E> l, String op) {
        if (l.isEmpty()) {
            return;
        }
        Iterator<E> it = l.iterator();
        System.out.print(it.next());
        while (it.hasNext()) {
            System.out.print(op + it.next());
        }
    }

    // print sequence of Exp
    void printInfix2(List<Exp> l, String op) {
        if (l.isEmpty()) {
            return;
        }
        Iterator<Exp> it = l.iterator();
        it.next().accept(this);
        while (it.hasNext()) {
            System.out.print(op);
            it.next().accept(this);
        }
    }

    // print sequence of Var
    void printInfix3(List<org.panda.backend.ast.Var> l, String op) {
        if (l.isEmpty()) {
            return;
        }
        Iterator<org.panda.backend.ast.Var> it = l.iterator();
        it.next().accept(this);
        while (it.hasNext()) {
            System.out.print(op);
            it.next().accept(this);
        }
    }

    public void visit(LetRec e){
        System.out.print("\n(let rec " + e.fd.id + " ");
        printInfix(e.fd.args, " ");
        System.out.print(" = ");
        e.fd.e.accept(this);
        System.out.print(" in ");
        e.e.accept(this);
        System.out.print(")");
    }

    public void visit(App e){
        System.out.print("(");
        e.e.accept(this);
        System.out.print(" ");
        printInfix2(e.es, " ");
        System.out.print(")");
    }

    public void visit(Tuple e){
        System.out.print("(");
        printInfix2(e.es, ", ");
        System.out.print(")");
    }

    public void visit(LetTuple e){
        System.out.print("let (");
        printInfix(e.ids, ", ");
        System.out.print(") = ");
        e.e1.accept(this);
        System.out.print(" in ");
        e.e2.accept(this);
        // System.out.print(")");
    }

    public void visit(Array e){
        System.out.print("(??Array.create?? ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Get e){
        e.e1.accept(this);
        System.out.print(".(");
        e.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Put e){
        System.out.print("(");
        e.e1.accept(this);
        System.out.print(".(");
        e.e2.accept(this);
        System.out.print(") <- ");
        e.e3.accept(this);
        System.out.print(")");
    }
}


