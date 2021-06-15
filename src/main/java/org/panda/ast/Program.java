package org.panda.ast;

import java.io.*;
import java.util.*;

public class Program {
    public List<Function> fcts = new LinkedList<Function>();
    public List<Mincaml_Float> floats = new LinkedList<Mincaml_Float>();
    public Function main;

    public void add_float( Label lab, float f){
        floats.add(new Mincaml_Float(f,lab));
    }
    public void add_fun( Label lab, List<org.panda.backend.ast.Var> args, Exp body){
        fcts.add(new Function(lab,args,body));
    }
    public void setMain(Label lab, Exp body){
        this.main = new Function(lab,new LinkedList<org.panda.backend.ast.Var>(),body);
    }

}
