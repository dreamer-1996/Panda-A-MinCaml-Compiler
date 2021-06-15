package org.panda.backend;
import org.panda.backend.ast.*;

import java.lang.Float;
import java.util.*;

import org.panda.ast.*;

import java.lang.*;

//store(i,e): store the result of possible operations of [e] in register r[i]

public class SpillAllocation {

    String output_txt;

    Map<Var,Integer> stored_var;
    int stack_top; //relative to fp
    int nb_local_var;
    boolean call_in_fun;

    public SpillAllocation() {
        this.reset_stored_var();
    }

    void reset_stored_var() {
        this.stored_var = new HashMap<Var,Integer>();
        this.stack_top = -4;
        this.nb_local_var = 0;
        this.call_in_fun = false;
    }


    // returns the location of the variable in the stack (relative to fp)
    // Ex: Var(x) is at [fp,#-8] so location(Var(x)) = -8
    int location(Var id) {
        if (! stored_var.containsKey(id)){
            stored_var.put(id,stack_top);
            stack_top -= 4;
            nb_local_var += 1;
        }
        return stored_var.get(id);
    }


    // generate unique labels (used mainly for if then else)
    static int gen_labels = -1;

    String gen_label(String name) {
        SpillAllocation.gen_labels += 1;
        return name + SpillAllocation.gen_labels;
    }

    // ==================================================================

    public String genARM(Program p) {
        this.output_txt = "\t .text";
        this.output_txt += "\n"+ ("\t .global _start");

        for (Mincaml_Float f : p.floats) {
            this.genARM(f);
        }
        for (Function f : p.fcts) {
            this.genARM(f);
            //this.output_txt += "\n"+ ("Stored_Vars: " + stored_var);
            this.reset_stored_var();
        }

        this.gen_Main(p.main);

        return this.output_txt;

    }

    public void genARM(Mincaml_Float mincaml_f) {
        this.output_txt += "\n"+ (String.format("%s_val:", mincaml_f.lab));

        //float in binary written to dec
        long value;
        if (mincaml_f.f > 0){
            value = java.lang.Float.floatToIntBits(mincaml_f.f);
        }else{
            //this is needed to prevent negative results
            value = Float.floatToIntBits(-mincaml_f.f);
            value += Math.pow(2,31);
        }

        this.output_txt += "\n"+ (String.format("\t.word \t %d", value));

        this.output_txt += "\n"+ (String.format("%s:", mincaml_f.lab));
        this.output_txt += "\n"+ (String.format("\t.word \t %s_val", mincaml_f.lab));

    }

    public void genARM(Function f) {
        /*fp points to the start of the environment of functions:
            - local variables are stored in [fp, #-4,-8...]
            - extra arguments (5th to n-th) are stored in [fp, #4,#8...]
            (usually the return of the function is stored in [fp, #4], but here
             we use r0 for the return value so we gain space in the stack)
        */

        //first we tell the function where are the arguments allocated

        //if %self argument exists ,it is allocated before every argument
        stored_var.put(Var.get("%self"),(f.args.size()-4)*4+4);

        int i = f.args.size()-1;
        int j = (f.args.size()-4)*4; //first stacked var (last arg)
        for (Var v : f.args) {
            if (i <= 3){
                //Gives local allocation of r0 to r3 in the stack
                this.location(v);
            }else{
                //extra arguments are pushed on the stack (before fp)
                stored_var.put(v,j);
            }
            i--;
            j -= 4;
        }


        String pre_output = this.output_txt;
        this.output_txt = "";

        //To know how many space for local variables we need, we first execute the
        //body of the function then use that number for entering and exiting (sp +/-)

        //return of function is stored in r0
        this.store(0, f.body);

        String mid_output = this.output_txt;
        this.output_txt = pre_output;


        this.output_txt += "\n"+ (String.format("%s:\n@args: %s", f.lab,f.args));

        //we store fp in the top of the stack
        if (this.call_in_fun){
            this.output_txt += "\n"+ ("\tpush \t {fp, lr}");
        }else{
            this.output_txt += "\n"+ ("\tpush \t {fp}");
        }
        this.output_txt += "\n"+ ("\tmov \t fp, sp");
        //we give space in the stack for local var
        if (this.nb_local_var > 0){
            this.output_txt += "\n"+ (String.format("\tsub \t sp, #%d",this.nb_local_var*4));
        }

        //NO CALLEE SAVE REGISTER NEEDED
        //If yes, we would need to increase the stack_top by the number
        //of pushed registers

        this.output_txt += "\n"+ ("\t@^^ start of function ^^");


        i = f.args.size()-1;
        for (Var v : f.args) {
            if (i <= 3){
                //The spilling method stores variables immediatelly
                this.output_txt += "\n"+ (String.format("\tstr \t r%d, [fp, #%d]", i,this.location(v) ));
            }
            i--;
        }
        //we put the body code in place
        this.output_txt += mid_output;

        this.output_txt += "\n"+ ("\t@vv end of function vv");

        //NO CALLEE SAVE REGISTER NEEDED

        //we return to previous environment
        this.output_txt += "\n"+ ("\tmov \t sp, fp");
        if (this.call_in_fun){
            this.output_txt += "\n"+ ("\tpop \t {fp, lr}");
        }else{
            this.output_txt += "\n"+ ("\tpop \t {fp}");
        }
        this.output_txt += "\n"+ ("\tbx \t lr");
    }

    public void gen_Main(Function f) {
        this.output_txt += "\n"+ ("\n_start:");
        this.output_txt += "\n"+ ("\tbl \t _mincaml_init");


        String pre_output = this.output_txt;
        this.output_txt = "";
        //To know how many space for local variables we need, we first execute the
        //body of the function then use that number for entering and exiting (sp +/-)

        // return value stored on r0
        this.store(0, f.body);

        String mid_output = this.output_txt;
        this.output_txt = pre_output;

        //we give space in the stack for local var
        if (this.nb_local_var > 0){
            this.output_txt += "\n"+ (String.format("\tsub \t sp, #%d",this.nb_local_var*4));
        }

        this.output_txt += mid_output;


        this.output_txt += "\n"+ ("\tbl \t _min_caml_exit");
    }

    // ==================================================================

    public void store(int r, Exp e) {
        if (e instanceof Int) {
            this.store(r, (Int) e);
        } else if (e instanceof Var) {
            this.store(r, (Var) e);
        } else if (e instanceof Label) {
            this.store(r, (Label) e);
        } else if (e instanceof Nop) {
            this.store(r, (Nop) e);
        } else if (e instanceof Neg) {
            this.store(r, (Neg) e);
        } else if (e instanceof Add) {
            this.store(r, (Add) e);
        } else if (e instanceof Sub) {
            this.store(r, (Sub) e);
        } else if (e instanceof FNeg) {
            this.store(r, (FNeg) e);
        } else if (e instanceof FAdd) {
            this.store(r, (FAdd) e);
        } else if (e instanceof FSub) {
            this.store(r, (FSub) e);
        } else if (e instanceof FMul) {
            this.store(r, (FMul) e);
        } else if (e instanceof FDiv) {
            this.store(r, (FDiv) e);
        } else if (e instanceof New) {
            this.store(r, (New) e);
        } else if (e instanceof Mem) {
            this.store(r, (Mem) e);
        } else if (e instanceof Mem_Assign) {
            this.store(r, (Mem_Assign) e);
        } else if (e instanceof If_eq) {
            this.store(r, (If_eq) e);
        } else if (e instanceof If_le) {
            this.store(r, (If_le) e);
        } else if (e instanceof If_ge) {
            this.store(r, (If_ge) e);
        } else if (e instanceof If_feq) {
            this.store(r, (If_feq) e);
        } else if (e instanceof If_fle) {
            this.store(r, (If_fle) e);
        } else if (e instanceof Call) {
            this.store(r, (Call) e);
        } else if (e instanceof Call_clo) {
            this.store(r, (Call_clo) e);
        } else if (e instanceof Let) {
            this.store(r, (Let) e);
        }

    }

    public void store(int r, Int e) {
        if ( -256 > e.i|| e.i > 256){
            //TODO handle these cases if important
            System.err.println("ERROR: Numbers too big for mincaml");
        }

        if (e.i >= 0){
            this.output_txt += "\n"+ (String.format("\tmov \t r%d, #%d", r, e.i));
        }else{
            this.output_txt += "\n"+ (String.format("\tmvn \t r%d, #%d", r, -(e.i+1) ));
        }
    }

    public void store(int r, Var e) {
        int pos = this.location(e);
        this.output_txt += "\n"+ (String.format("\tldr \t r%d, [fp, #%d]", r, pos));
    }

    public void store(int r, Label e) {
        this.output_txt += "\n"+ (String.format("\tldr \t r%d, %s", r, e.id));
    }

    public void store(int r, Nop e) {
        this.output_txt += "\n"+ ("\tnop");
    }

    public void store(int r, Neg e) {
        this.store(r, e.e);
        this.output_txt += "\n"+ (String.format("\trsb \t r%d, r%d, #0", r, r));
    }

    public void store(int r,Add e) {
        this.store(1,e.e1);
        if (e.e2 instanceof Int){
            this.output_txt += "\n"+ (String.format("\tadd \t r%d, r1, #%d",r,((Int)e.e2).i));
        }else{
            this.store(2,e.e2);
            this.output_txt += "\n"+ (String.format("\tadd \t r%d, r1, r2",r));
        }
    }

    public void store(int r,Sub e) {
        this.store(1,e.e1);
        if (e.e2 instanceof Int){
            this.output_txt += "\n"+ (String.format("\tsub \t r%d, r1, #%d",r,((Int)e.e2).i));
        }else{
            this.store(2,e.e2);
            this.output_txt += "\n"+ (String.format("\tsub \t r%d, r1, r2",r));
        }
    }

    public void store(int r, FNeg e) {
        this.store(r,e.e);
        this.output_txt += "\n"+ (String.format("\teor \t r%d, r%d, #-2147483648",r,r));
    }

    public void store(int r, FAdd e) {
        this.store(1,e.e1);
        this.store(0,e.e2);
        this.output_txt += "\n"+ ("\tbl \t __aeabi_fadd   @add floats");
    }

    public void store(int r, FSub e) {
        this.store(0,e.e1);
        this.store(1,e.e2);
        this.output_txt += "\n"+ ("\tbl \t __aeabi_fsub   @sub floats");
    }

    public void store(int r, FMul e) {
        this.store(0,e.e1);
        this.store(1,e.e2);
        this.output_txt += "\n"+ ("\tbl \t __aeabi_fmul   @mul floats");
    }

    public void store(int r, FDiv e) {
        this.store(0,e.e1);
        this.store(1,e.e2);
        this.output_txt += "\n"+ ("\tbl \t __aeabi_fdiv   @div floats");
    }


    public void store(int r, New e) {
        this.store(0, e.e);

        this.output_txt += "\n"+ ("\tbl \t _mincaml_alloc");
        //return adress in r0
        //this.output_txt += "\n"+ (String.format("\tmov r%d, r0", r));
    }

    public void store(int r, Mem e) {
        this.store(1, e.e1);
        if (e.e2 instanceof Int) {
            this.output_txt += "\n"+ (String.format("\tldr \t r%d, [r1,#%d]", r, ((Int) e.e2).i));
        } else {
            this.store(2, e.e2);
            this.output_txt += "\n"+ (String.format("\tldr \t r%d, [r1,r2]", r));
        }
    }

    public void store(int r, Mem_Assign e) {
        this.store(1, e.e1);
        this.store(0, e.e3);
        if (e.e2 instanceof Int) {
            this.output_txt += "\n"+ (String.format("\tstr \t r0, [r1,#%d]",((Int) e.e2).i));
        } else {
            this.store(2, e.e2);
            this.output_txt += "\n"+ (String.format("\tstr \t r0, [r1,r2]"));
        }
        // The return of this function doesn t matter
    }

    public void store(int r, If_eq e) {
        String else_lab = this.gen_label("else");
        String end_lab = this.gen_label("fi");

        this.store(r + 1, e.e1);
        this.store(r + 2, e.e2);
        this.output_txt += "\n"+ (String.format("\tcmp \t r%d, r%d", r + 1, r + 2));
        this.output_txt += "\n"+ (String.format("\tbne \t %s", else_lab));

        this.store(r, e.e3);

        this.output_txt += "\n"+ (String.format("\tb \t %s", end_lab));
        this.output_txt += "\n"+ (String.format("%s:", else_lab));

        this.store(r, e.e4);

        this.output_txt += "\n"+ (String.format("%s:", end_lab));
    }

    public void store(int r, If_le e) {
        String else_lab = this.gen_label("else");
        String end_lab = this.gen_label("fi");

        this.store(r + 1, e.e1);
        this.store(r + 2, e.e2);
        this.output_txt += "\n"+ (String.format("\tcmp \t r%d, r%d", r + 1, r + 2));
        this.output_txt += "\n"+ (String.format("\tbgt \t %s", else_lab));

        this.store(r, e.e3);

        this.output_txt += "\n"+ (String.format("\tb \t %s", end_lab));
        this.output_txt += "\n"+ (String.format("%s:", else_lab));

        this.store(r, e.e4);

        this.output_txt += "\n"+ (String.format("%s:", end_lab));
    }

    public void store(int r, If_ge e) {
        String else_lab = this.gen_label("else");
        String end_lab = this.gen_label("fi");

        this.store(r + 1, e.e1);
        this.store(r + 2, e.e2);
        this.output_txt += "\n"+ (String.format("\tcmp \t r%d, r%d", r + 1, r + 2));
        this.output_txt += "\n"+ (String.format("\tblt \t %s", else_lab));

        this.store(r, e.e3);

        this.output_txt += "\n"+ (String.format("\tb \t %s", end_lab));
        this.output_txt += "\n"+ (String.format("%s:", else_lab));

        this.store(r, e.e4);

        this.output_txt += "\n"+ (String.format("%s:", end_lab));
    }

    public void store(int r, If_feq e) {
        String else_lab = this.gen_label("else");
        String end_lab = this.gen_label("fi");

        this.store(0, e.e1);
        this.store(1, e.e2);
        this.output_txt += "\n"+ ("\tbl \t __aeabi_fcmpeq");
        this.output_txt += "\n"+ ("\tcmp \t r0, #0");
        this.output_txt += "\n"+ (String.format("\tbeq \t %s", else_lab));

        this.store(r, e.e3);

        this.output_txt += "\n"+ (String.format("\tb \t %s", end_lab));
        this.output_txt += "\n"+ (String.format("%s:", else_lab));

        this.store(r, e.e4);

        this.output_txt += "\n"+ (String.format("%s:", end_lab));
    }

    public void store(int r, If_fle e) {
        String else_lab = this.gen_label("else");
        String end_lab = this.gen_label("fi");

        this.store(0, e.e1);
        this.store(1, e.e2);
        this.output_txt += "\n"+ ("\tbl \t __aeabi_fcmple");
        this.output_txt += "\n"+ ("\tcmp \t r0, #0");
        this.output_txt += "\n"+ (String.format("\tbeq \t %s", else_lab));

        this.store(r, e.e3);

        this.output_txt += "\n"+ (String.format("\tb \t %s", end_lab));
        this.output_txt += "\n"+ (String.format("%s:", else_lab));

        this.store(r, e.e4);

        this.output_txt += "\n"+ (String.format("%s:", end_lab));
    }

    public void store(int r, Call e) {
        this.call_in_fun = true;

        int loc;
        int nb_pushed_args = 0;
        int i = e.args.size()-1;
        for (Var v : e.args) {
            loc = this.location(v);
            if (i >= 4) {
                //Extra arguments are stored in stack
                this.output_txt += "\n"+ (String.format("\tldr \t r0, [fp, #%d]", loc));
                this.output_txt += "\n"+ (String.format("\tpush \t {r0} @push %dth param",i));
                nb_pushed_args += 1;
            } else {
                //First 4 arguments are stored in registers r0 to r3
                this.output_txt += "\n"+ (String.format("\tldr \t r%d, [fp, #%d]", i, loc));
            }
            i--;
        }

        this.output_txt += "\n"+ (String.format("\tbl \t %s", e.function));

        if (nb_pushed_args > 0){
            this.output_txt += "\n"+ (String.format("\tadd \t sp, #%d", nb_pushed_args*4));
        }
    }

    public void store(int r, Call_clo e) {
        this.call_in_fun = true;

        //TODO check if works

        /*
        apply_closure X a1 a2...an
        mem(X + 0) contains F, the function passed
        mem(X + 1...m) contains the free variables of F
        a1 a2...a3 are the arguments of F
        */


        //we store %self before the arguments
        this.store(4,e.id);
        this.output_txt += "\n"+ ("\tpush \t {r4} @push %self parameter");

        int loc;
        int nb_pushed_args = 0;
        int i = e.args.size()-1;
        for (Var v : e.args) {
            loc = this.location(v);
            if (i >= 4) {
                //Extra arguments are stored in stack
                this.output_txt += "\n"+ (String.format("\tldr \t r0, [fp, #%d]", loc));
                this.output_txt += "\n"+ (String.format("\tpush \t {r0} @push %dth param",i));
                nb_pushed_args += 1;
            } else {
                //First 4 arguments are stored in registers r0 to r3
                this.output_txt += "\n"+ (String.format("\tldr \t r%d, [fp, #%d]", i, loc));
            }
            i--;
        }

        //exec function to closure
        this.output_txt += "\n"+ ("\tldr \t r4, [r4, #0]");
        //(
        //this.output_txt += "\n"+ ("\tmov \t lr, pc");
        //this.output_txt += "\n"+ ("\tbx \t r4");
        //)
        this.output_txt += "\n"+ ("\tbl \t r4");

        if (nb_pushed_args > 0){
            this.output_txt += "\n"+ (String.format("\tadd \t sp, #%d", nb_pushed_args*4));
        }
    }

    public void store(int r, Let e) {

        this.store(r, e.e1);

        int pos = this.location(e.id);
        this.output_txt += "\n"+ (String.format("\tstr \t r%d, [fp, #%d]", r, pos));

        this.store(r, e.e2);
    }

}
