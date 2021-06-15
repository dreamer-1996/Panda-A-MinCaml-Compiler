package org.panda.frontend;

import org.panda.ast.Float;
import org.panda.ast.*;
import org.panda.ast.rep_classes.Let;
import org.panda.ast.rep_classes.Var;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.*;
import java.util.*;

public class ClosuresVisitor implements ObjVisitor<Exp> {
    static Program program;
    List<Label> known;
    static Map<String, String> closureMapping;
    static int tupleCount;
    List<String> currLabs;

    public ClosuresVisitor() {
        // known : list of functions with no free variables
        known = new ArrayList<>();
        closureMapping = new HashMap<String, String>();
        tupleCount = -1;
        // current labels in the prog
        currLabs = new ArrayList<>();

        List<String> predFuns = Arrays.asList("min_caml_print_int", "min_caml_create_array", "min_caml_create_float_array",
                "min_caml_print_newline", "min_caml_sin", "min_caml_cos", "min_caml_sqrt",
                "min_caml_abs_float", "min_caml_int_of_float", "min_caml_float_of_int",
                "min_caml_truncate");

        for (String s : predFuns) {
            known.add(Label.get(s));
        }

        program = new Program();
    }

    public Program getProgram(Exp exp) {
        program.setMain(Label.get(""), exp);
        return program;
    }

    // List<Id> -> List<org.panda.backend.ast.Var>
    public <T> List<org.panda.backend.ast.Var> getVars(List<T> list) {
        List<org.panda.backend.ast.Var> args = new ArrayList<>();
        for (T item : list) {
            if (item instanceof Id || item instanceof String) {
                org.panda.backend.ast.Var arg = org.panda.backend.ast.Var.get(item.toString());
                args.add(arg);
            } else if (item instanceof Var) {
                Var v = (Var) item;
                org.panda.backend.ast.Var arg = org.panda.backend.ast.Var.get(v.id.toString());
                args.add(arg);
            }
        }
        return args;
    }

    public void printProgram() {
        PrintAsmlVisitor pav = new PrintAsmlVisitor();
        for (Function f : program.fcts) {
            f.accept(pav);
        }
        program.main.accept(pav);
    }

    public void generateAsmlFile(String s) {
        PrintAsmlVisitor pav = new PrintAsmlVisitor();
        try {
            final File file = new File(s);
            final PrintStream fileStream = new PrintStream(file);
            final PrintStream console;

            // Store current System.out
            console = System.out;

            // Assign file to output stream
            System.setOut(fileStream);

            for (Function f : program.fcts) {
                f.accept(pav);
            }
            program.main.accept(pav);

            System.setOut(console);
            fileStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // public void addFunProg(Function f){

    // }

    @Override
    public Exp visit(Unit e) {
        return new Nop();
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
        org.panda.backend.ast.Var newVar = org.panda.backend.ast.Var.get(e.id.toString());
        org.panda.backend.ast.Let newLet =
                new org.panda.backend.ast.Let(newVar, e.e1.accept(this), e.e2.accept(this));
        return newLet.accept(this);
        // return new Let(e.id, e.t, e.e1.accept(this), e.e2.accept(this));
    }

    @Override
    public Exp visit(Var e) {
        String id = getClosMapping(e);
        org.panda.backend.ast.Var nVar = org.panda.backend.ast.Var.get(id);

        if (id.equals("print_int")) {
            Label lab = Label.get("min_caml_print_int");
            currLabs.add(id);
            return lab;
        } else if (id.equals("Array.create")) {
            Label lab = Label.get("min_caml_create_array");
            currLabs.add(id);
            return lab;
        } else if (id.equals("create_float_array")) {
            Label lab = Label.get("min_caml_create_float_array");
            currLabs.add(id);
            return lab;
        } else if (id.equals("print_newline")) {
            Label lab = Label.get("min_caml_print_newline");
            currLabs.add(id);
            return lab;
        } else if (id.equals("sin")) {
            Label lab = Label.get("min_caml_sin");
            currLabs.add(id);
            return lab;
        } else if (id.equals("cos")) {
            Label lab = Label.get("min_caml_cos");
            currLabs.add(id);
            return lab;
        } else if (id.equals("sqrt")) {
            Label lab = Label.get("min_caml_sqrt");
            currLabs.add(id);
            return lab;
        } else if (id.equals("abs_float")) {
            Label lab = Label.get("min_caml_abs_float");
            currLabs.add(id);
            return lab;
        } else if (id.equals("int_of_float")) {
            Label lab = Label.get("min_caml_int_of_float");
            currLabs.add(id);
            return lab;
        } else if (id.equals("float_of_int")) {
            Label lab = Label.get("min_caml_float_of_int");
            currLabs.add(id);
            return lab;
        } else if (id.equals("truncate")) {
            Label lab = Label.get("min_caml_truncate");
            currLabs.add(id);
            return lab;
        } else {
            Label lab = Label.get(id);
            for (Function f : program.fcts){
                if (lab.equals(f.getLabel()))
                    return lab;
            }
            return nVar;
        }
    }

    public List<String> getFreeVars(FunDef fd) throws Exception {
        VariableVisitor vv = new VariableVisitor();
        // localEnv : all vars in body
        Set<String> localEnv = fd.e.accept(vv);

        // List<String> currLabs = new ArrayList<>();
        for (Function f : program.fcts) {
            currLabs.add(f.getLabel().toString());
        }
        // remove labels from free vars

        localEnv.removeIf(currLabs::contains);

        // availableVars : avail vars in body + args of fd
        List<String> availableVars = vv.availVars;
        for (Id arg : fd.args) {
            availableVars.add(arg.toString());
        }

        List<String> freeVars = new ArrayList<>(localEnv);
        freeVars.removeAll(availableVars);

        return freeVars;
    }

    public String getClosMapping(Var v) {
        String s = v.id.toString();
        if (closureMapping.containsKey(s)) {
            return closureMapping.get(s);
        }
        return s;
    }

    // constructs a closure
    public Exp makeClosure(LetRec lr, List<org.panda.backend.ast.Var> freeV) throws Exception {

        String id_name = lr.fd.id.toString();
        org.panda.backend.ast.Var var0 = org.panda.backend.ast.Var.get(id_name + "_clo");
        org.panda.backend.ast.Var var1 = org.panda.backend.ast.Var.get("addr_" + id_name);
        org.panda.backend.ast.Var var2 = org.panda.backend.ast.Var.get("tu0");
        // bytesToAlloc : (label + freeVars) * 4 bytes
        Int bytesToAlloc = new Int((freeV.size() + 1) * 4);

        int i = freeV.size();
        ListIterator<org.panda.backend.ast.Var> iter = freeV.listIterator(i - 1);
        // get last free var from freeV list
        org.panda.backend.ast.Var lastFreeVar = freeV.get(i - 1);

        // set last 'type unit' var
        org.panda.backend.ast.Var tuLast = org.panda.backend.ast.Var.get("tu" + i);
        Mem_Assign memAss = new Mem_Assign(var0, new Int(i * 4), lastFreeVar);

        // Add closure to closureMapping
        closureMapping.put(id_name, var0.id);

        // set last mem assign statement
        org.panda.backend.ast.Let let3 = new org.panda.backend.ast.Let(tuLast, memAss, lr.e.accept(this));

        // create new mem_assign for each free var
        while (iter.hasPrevious()) {
            i--;
            org.panda.backend.ast.Var fv = iter.previous();
            org.panda.backend.ast.Var tu = org.panda.backend.ast.Var.get("tu" + i);
            let3 = new org.panda.backend.ast.Let(tu, new Mem_Assign(var0, new Int(i * 4), fv), let3);
        }

        org.panda.backend.ast.Let let2 = new org.panda.backend.ast.Let(var2, new Mem_Assign(var0, new Int(0), var1), let3);
        org.panda.backend.ast.Let let1 = new org.panda.backend.ast.Let(var1, Label.get(id_name), let2);
        org.panda.backend.ast.Let let0 = new org.panda.backend.ast.Let(var0, new New(bytesToAlloc), let1);

        return let0;
    }

    @Override
    public Exp visit(LetRec e) throws Exception {
        FunDef fdef = e.fd;

        //initially we assume func has not free vars, so add it to "known",
        //  convert its body and register it in "fcts" list
        Label lab = Label.get(fdef.id.toString());
        known.add(lab);

        List<org.panda.backend.ast.Var> args = getVars(fdef.args);
        Exp body = fdef.e.accept(this);
        Function func = new Function(lab, args, body);

        // if (!program.fcts.contains(func))
        program.fcts.add(func);

        // check for free vars
        List<org.panda.backend.ast.Var> freeVars = getVars(getFreeVars(fdef));

        // case: no free vars in func body
        if (freeVars.size() == 0) {
            Exp rest = e.e.accept(this);
            // if (rest.equals(obj)) -------------------
            return rest;
        }
        // case: free vars in func
        else {

            // remove func from "known" and from "fncs"
            known.remove(lab);
            program.fcts.remove(func);

            Exp exp = makeClosure(e, freeVars);

            // redo conversion of body
            int i = freeVars.size();
            ListIterator<org.panda.backend.ast.Var> iter = freeVars.listIterator(i - 1);
            org.panda.backend.ast.Var lastFreeVar = freeVars.get(i - 1);

            org.panda.backend.ast.Var self = org.panda.backend.ast.Var.get("%self");
            Mem mem = new Mem(self, new Int(i * 4));
            org.panda.backend.ast.Let let0 = new org.panda.backend.ast.Let(lastFreeVar, mem, fdef.e.accept(this));

            while (iter.hasPrevious()) {
                i--;
                org.panda.backend.ast.Var fVar = iter.previous();
                let0 = new org.panda.backend.ast.Let(fVar, new Mem(self, new Int(i * 4)), let0);
            }

            body = let0;
            func = new Function(lab, args, body);
            program.fcts.add(func);

            return exp;
        }
    }

    @Override
    public Exp visit(App e) throws Exception {
        List<org.panda.backend.ast.Var> args;
        org.panda.backend.ast.Var nVar;
        Label lab ;
        args = getVars(e.es);
        if (e.e.accept(this) instanceof org.panda.backend.ast.Var){
            nVar = (org.panda.backend.ast.Var)(e.e.accept(this));
            lab = Label.get(nVar.id);
            // check if var name belongs to "known"
            if (known.contains(lab)){
                return new Call(lab, args);
            } else {
                return new Call_clo(nVar, args);
            }

        } else {//if (e.e.accept(this) instanceof Label)
            lab = (Label)(e.e.accept(this));
            return new Call(lab, args);
        }
    }

    @Override
    public Exp visit(Tuple e) {
        int i = e.es.size();
        tupleCount++;

        org.panda.backend.ast.Var var0 = org.panda.backend.ast.Var.get("v" + i);
        org.panda.backend.ast.Var tuple0 = org.panda.backend.ast.Var.get("tuple" + tupleCount);

        // Add closure to closureMapping
        // closureMapping.put(id_name, var0.id);

        // bytesToAlloc : nbExp * 4 bytes
        Int bytesToAlloc = new Int(e.es.size() * 4);

        ListIterator<Exp> iter = e.es.listIterator(i - 1);

        // get last exp from tuple
        // Exp lastExp = e.es.get(i - 1);

        // set last 'type unit' var
        org.panda.backend.ast.Var tuLast = org.panda.backend.ast.Var.get("tu" + i);

        // set last mem assign statement
        org.panda.backend.ast.Let let2 = new org.panda.backend.ast.Let(tuLast, new Mem_Assign(tuple0, new Int(i * 4), var0), tuple0);

        // create new mem_assign for each exp in tuple
        while (iter.hasPrevious()) {
            i--;
            Exp it = iter.previous();
            org.panda.backend.ast.Var var = org.panda.backend.ast.Var.get("v" + i);
            org.panda.backend.ast.Var tu = org.panda.backend.ast.Var.get("tu" + i);
            let2 = new org.panda.backend.ast.Let(tu, new Mem_Assign(tuple0, new Int(i * 4), var), let2);
        }

        org.panda.backend.ast.Let let1 = new org.panda.backend.ast.Let(tuple0, new New(bytesToAlloc), let2);

        return let1;
    }

    @Override
    public Exp visit(LetTuple e) throws Exception {
        return new LetTuple(e.getIds(), e.getTs(), e.e1.accept(this), e.e2.accept(this));
    }

    @Override
    public Exp visit(Array e) throws Exception {
        Label lab = Label.get("min_caml_create_array");
        List<Exp> expList = new ArrayList<Exp>();
        expList.add(e.e1);
        expList.add(e.e2);
        App app = new App(lab, expList);
        return app.accept(this);
    }

    @Override
    public Exp visit(Get e) throws Exception {
        return new Mem(e.e1.accept(this), e.e2.accept(this));
    }

    @Override
    public Exp visit(Put e) throws Exception {
        return new Mem_Assign(e.e1.accept(this), e.e2.accept(this), e.e3.accept(this));
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
