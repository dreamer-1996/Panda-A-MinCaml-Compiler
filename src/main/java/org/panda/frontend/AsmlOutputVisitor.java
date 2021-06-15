package org.panda.frontend;

import org.panda.ast.rep_classes.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import org.panda.ast.*;
import org.panda.ast.Float;
import org.panda.ast.Label;

import java.util.*;

public class AsmlOutputVisitor implements Visitor {
    public static PrintStream file;
    public static PrintStream console;
    public static File f;
    public static File nf;
    public static CopyFiles cp;

    public AsmlOutputVisitor() throws FileNotFoundException, Exception {
        // create asml (text) file
        f = new File("output.asml");
        file = new PrintStream(f);
        nf = new File("new_file.asml");

        // Copy-content Object
        cp = new CopyFiles();

        // Store current System.out
        console = System.out;

        // Assign file to output stream
        System.setOut(file);

        System.out.print("let _ =\n\t");
    }

    public void resetStdOut() {
        System.setOut(console);
    }

    public void visit(Unit e) {
        System.out.print("nil");
    }

    public void visit(Bool e) {
        System.out.print(e.b);
    }

    public void visit(Int e) {
        System.out.print(e.i);
    }

    public void visit(Float e) {
        String s = String.format("%.1f", e.getF());
        System.out.print(s);
    }

    public void visit(Not e) {
        System.out.print("not ");
        e.e.accept(this);
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
    }

    public void visit(Sub e) {
        System.out.print("sub ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
    }

    public void visit(FNeg e) {
        System.out.print("fneg ");
        e.e.accept(this);
    }

    public void visit(FAdd e) {
        System.out.print("fadd ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
    }

    public void visit(FSub e) {
        System.out.print("fsub ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
    }

    public void visit(FMul e) {
        System.out.print("fmul ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
    }

    public void visit(FDiv e) {
        System.out.print("fdiv ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
    }

    public void visit(Eq e) {
        e.e1.accept(this);
        System.out.print(" = ");
        e.e2.accept(this);
    }

    public void visit(LE e) {
        e.e1.accept(this);
        System.out.print(" <= ");
        e.e2.accept(this);
    }

    public void visit(If e) {
        System.out.print("if ");
        e.e1.accept(this);
        System.out.print(" then (\n\t");
        e.e2.accept(this);
        System.out.print("\n) else (\n\t");
        e.e3.accept(this);
        System.out.print("\n)");
    }

    public void visit(Let e) {
        System.out.print("let ");
        System.out.print(e.id);
        System.out.print(" = ");
        e.e1.accept(this);
        System.out.print(" in\n\t");
        e.e2.accept(this);
    }

    public void visit(Var e) {
         String id = e.id.toString();
        if (id.equals("print_int")) {
            id = "min_caml_print_int";
        } else if (id.equals("create_array")) {
            id = "min_caml_create_array";
        } else if (id.equals("create_float_array")) {
            id = "min_caml_create_float_array";
        } else if (id.equals("print_newline")) {
            id = "min_caml_print_newline";
        } else if (id.equals("sin")) {
            id = "min_caml_sin";
        } else if (id.equals("cos")) {
            id = "min_caml_cos";
        } else if (id.equals("sqrt")) {
            id = "min_caml_sqrt";
        } else if (id.equals("abs_float")) {
            id = "min_caml_abs_float";
        } else if (id.equals("int_of_float")) {
            id = "min_caml_int_of_float";
        } else if (id.equals("float_of_int")) {
            id = "min_caml_float_of_int";
        } else if (id.equals("truncate")) {
            id = "min_caml_truncate";
        }

        System.out.print(id);
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

    public void visit(LetRec e) {

        try {
            // copy contents of old (current) file
            FileReader fr = new FileReader(f);

            PrintStream new_file = new PrintStream(nf);

            // Assign new_file to output stream
            System.setOut(new_file);

            // write function def in head of new file
            System.out.print("let _" + e.fd.id + " ");
            printInfix(e.fd.args, " ");
            System.out.print(" = \n\t");
            e.fd.e.accept(this);
            System.out.print("\n\n");

            int i;
            // append old code in new_file
            while ((i = fr.read()) != -1) {
                System.out.print((char) i);
            }

            // copy content from new_file to old file
            cp.copyContent(nf, f);
            e.e.accept(this);
            cp.copyContent(nf, f);

            fr.close();
            new_file.close();
            System.setOut(console);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        System.setOut(file);
    }

    public void visit(App e) {
        System.out.print("call _");
        e.e.accept(this);
        System.out.print(" ");
        printInfix2(e.es, " ");
    }

    public void visit(Tuple e) {
        System.out.print("(");
        printInfix2(e.es, ", ");
        System.out.print(")");
    }

    public void visit(LetTuple e) {
        System.out.print("(let (");
        printInfix(e.getIds(), ", ");
        System.out.print(") = ");
        e.e1.accept(this);
        System.out.print(" in ");
        e.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Array e) {
        System.out.print("(Array.create ");
        e.e1.accept(this);
        System.out.print(" ");
        e.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Get e) {
        e.e1.accept(this);
        System.out.print(".(");
        e.e2.accept(this);
        System.out.print(")");
    }

    public void visit(Put e) {
        System.out.print("(");
        e.e1.accept(this);
        System.out.print(".(");
        e.e2.accept(this);
        System.out.print(") <- ");
        e.e3.accept(this);
        System.out.print(")");
    }

    public void visit(Call e) {
            System.out.print("TO IMPLEMENT");
        }

    public void visit(Label e) {
        System.out.print("TO IMPLEMENT");
    }

    public void visit(org.panda.backend.ast.Var e) {
        System.out.print(e.id);
    }

    public void visit(org.panda.backend.ast.Let e) {
        System.out.print("TO IMPLEMENT");
    }

    public void visit(New e) {
        System.out.print("TO IMPLEMENT");
    }

    public void visit(Mem e) {
        System.out.print("TO IMPLEMENT");
    }

    public void visit(Mem_Assign e) {
        System.out.print("TO IMPLEMENT");
    }

    public void visit(Nop e) {
        System.out.print("TO IMPLEMENT");
    }

    public void visit(Function e) {
        System.out.print("TO IMPLEMENT");
    }

    public void visit(Call_clo e) {
        System.out.print("TO IMPLEMENT");
    }

    public void visit(If_eq e) {
        System.out.print("TO IMPLEMENT");;
    }

    public void visit(If_le e) {
        System.out.print("TO IMPLEMENT");;
    }

    public void visit(If_ge e) {
        System.out.print("TO IMPLEMENT");;
    }

    public void visit(If_feq e) {
        System.out.print("TO IMPLEMENT");;
    }

    public void visit(If_fle e) {
        System.out.print("TO IMPLEMENT");;
    }
}
