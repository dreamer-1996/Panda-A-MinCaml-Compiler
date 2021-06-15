package org.panda.ast;


import org.panda.ast.rep_classes.Let;
import org.panda.ast.rep_classes.Var;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class TypeCheckVisitor implements ObjVisitor<Type>{

    Map<Id,Type> var_type_map;
    public List<Type> type = new ArrayList<>(); /*For Type TTuple */

    public TypeCheckVisitor() {
        var_type_map = new HashMap<>();
    }

    @Override
    public Type visit(Unit e) {
        return new TUnit();
    }

    @Override
    public Type visit(Bool e) {
        return new TBool();
    }

    @Override
    public Type visit(Int e) {
        return new TInt();
    }

    @Override
    public Type visit(Float e) {
        return new TFloat();
    }

    @Override
    public Type visit(Not e) throws Exception {

        if (e.e.accept(this) instanceof TBool) {
            return new TBool();
        }
        throw new TypeException("Wrong type:expected TBool");
    }

    @Override
    public Type visit(Neg e) throws Exception {

        if (e.e.accept(this) instanceof TInt) {
            return new TInt();
        }
        throw new TypeException("Wrong type:expected TInt");
    }

    @Override
    public Type visit(Add e) throws Exception { /*Both the expressions on the left and right of + should be TInt*/

        if ((e.e1.accept(this) instanceof TInt) && (e.e2.accept(this) instanceof TInt)) {
            return new TInt();
        }
        throw new TypeException("Wrong type:expected TInt");
    }

    @Override
    public Type visit(Sub e) throws Exception {/*Both the expressions on the left and right of + should be TInt*/

        if ((e.e1.accept(this) instanceof TInt) && (e.e2.accept(this) instanceof TInt)) {
            return new TInt();
        }
        throw new TypeException("Wrong type:expected TInt");
    }

    @Override
    public Type visit(FNeg e) throws Exception{

        if (e.e.accept(this) instanceof TFloat) {
            return new TFloat();
        }
        throw new TypeException("Wrong type:expected TFloat");
    }

    @Override
    public Type visit(FAdd e) throws Exception{/*Both the expressions on the left and right should be TFloat*/

        if (e.e1.accept(this) instanceof TFloat && e.e2.accept(this) instanceof TFloat) {
            return new TFloat();
        }
        throw new TypeException("Wrong type:expected TFloat");
    }

    @Override
    public Type visit(FSub e) throws Exception{/*Both the expressions on the left and right should be TFloat*/

        if (e.e1.accept(this) instanceof TFloat && e.e2.accept(this) instanceof TFloat) {
            return new TFloat();
        }
        throw new TypeException("Wrong type:expected TFloat");
    }

    @Override
    public Type visit(FMul e) throws Exception {/*Both the expressions on the left and right should be TFloat*/

        if (e.e1.accept(this) instanceof TFloat && e.e2.accept(this) instanceof TFloat) {
            return new TFloat();
        }
        throw new TypeException("Wrong type:expected TFloat");
    }

    @Override
    public Type visit(FDiv e) throws Exception {/*Both the expressions on the left and right should be TFloat*/

        if (e.e1.accept(this) instanceof TFloat && e.e2.accept(this) instanceof TFloat) {
            return new TFloat();
        }
        throw new TypeException("Wrong type:expected TFloat");
    }

    @Override
    public Type visit(Eq e) throws Exception{/*Both the expressions on the left and right should not be null and must be of same class*/

        if ((e.e1.accept(this) != null && e.e2.accept(this) != null && e.e1.accept(this).getClass().getName().equals(e.e2.accept(this).getClass().getName()))) {
            return new TBool();
        }
        throw new TypeException("Error: Type mismatch of =");
    }

    @Override
    public Type visit(LE e) throws Exception{/*Both the expressions on the left and right should be TFloat or TInt only*/

        if (((e.e1.accept(this) instanceof TInt) && (e.e2.accept(this) instanceof TInt)) || ((e.e1.accept(this) instanceof TFloat) && (e.e2.accept(this) instanceof TFloat))) {
            return new TBool();
        }
        throw new TypeException("Wrong type:Expected TInt or TFloat");
    }

    @Override
    public Type visit(If e) throws Exception{/*The return type must be TBool and the two expressions must be of same class*/


        if (e.e1.accept(this) instanceof TBool && (e.e2.accept(this).getClass().getName().equals(e.e3.accept(this).getClass().getName()))) {
            return e.e2.accept(this);
        }
        else if (!(e.e1.accept(this) instanceof TBool)) {
            throw new TypeException("Wrong type:Expected TBool");
        }
        else {
            return null;
        }

    }


    @Override
    public Type visit(Let e) throws Exception {

        if ( var_type_map.containsKey(e.id) ){ //if id is already assigned to type
            if ( e.e1.accept(this).getClass().getName().equals( e.id.getClass().getName() ) ){
                return e.e2.accept(this);
            }else{
                throw new TypeException("Wrong type: assigning new type to an already exiting value");
            }
        }else{ //else assign it a type
            var_type_map.put(e.id, e.e1.accept(this)); //associates type to variable in environment
            Type res = e.e2.accept(this);
            var_type_map.remove(e.id); //exits environment and deassociates type to var
            return res;
        }

    }

    @Override
    public Type visit(Var e) {
        return var_type_map.get(e.id);
    }

    @Override
    public Type visit(LetRec e) {
       /* //TODO
        var_type_map.put( e.fd.id , e.e.accept(this) );
        return new TFun();*/
        return null;
    }

    @Override
    public Type visit(App e) {
        /*//TODO
        if ( ! ( e.e.accept(this) instanceof TFun ) ){
            throw new TypeException("Error: caller should be a function");
        }

        return null;*/
        return null;
    }

    @Override
    public Type visit(Tuple e) throws Exception {

        for (Exp ex : e.es ){
            type.add(ex.accept(this));
        }
        return new TTuple(type);
    }

    @Override
    public Type visit(LetTuple e) throws Exception {
        //TODO check
        Type t = e.e1.accept(this);
        if (t instanceof TTuple ){
            TTuple tup = (TTuple) t;
            if ( e.ids.size() != tup.type_in_tuple.size() ){
                throw new TypeException("Error: tuples must have same size on both sides");
            }
            for (int i = 0; i < e.ids.size() ; i++){
                if ( ! e.ts.get(i).getClass().getName().equals( tup.type_in_tuple.get(i).getClass().getName() ) ){
                    throw new TypeException("Error: tuple types are different");
                }
            }
        }else{
            throw new TypeException("Error: right expression must be a tuple");
        }

        return e.e2.accept(this);
    }

    @Override
    public Type visit(Array e) throws Exception{
        if (e.e1.accept(this) instanceof TInt) {
            return new TArray(e.e2.accept(this));
        }
        throw new TypeException("Error: size not in TInt");
    }

    @Override
    //get is only used in arrays
    public Type visit(Get e) throws Exception {
        Type t = e.e1.accept(this);
        if (t instanceof TArray){
            return ((TArray) t).type;
        }
        throw new TypeException("Error: get can only be used in arrays");
    }

    @Override
    public Type visit(Put e) throws Exception {
        Type t = e.e1.accept(this);
        String class_name = e.e3.accept(this).getClass().getName();
        if (t instanceof TArray && class_name.equals( ((TArray) t).type.getClass().getName()) ){
            return new TUnit();
        }
        throw new TypeException("Error: putting value of different type in array");
    }

    @Override
    public Type visit(org.panda.backend.ast.Var e) {
        return null;
    }

    @Override
    public Type visit(org.panda.backend.ast.Let e) {
        return null;
    }

    @Override
    public Type visit(New e) {
        return null;
    }

    @Override
    public Type visit(Mem e) {
        return null;
    }

    @Override
    public Type visit(Mem_Assign e) {
        return null;
    }

    @Override
    public Type visit(Nop e) {
        return null;
    }

    @Override
    public Type visit(Function e) {
        return null;
    }

    @Override
    public Type visit(Call_clo e) {
        return null;
    }

    @Override
    public Type visit(If_eq e) {
        return null;
    }

    @Override
    public Type visit(If_le e) {
        return null;
    }

    @Override
    public Type visit(If_ge e) {
        return null;
    }

    @Override
    public Type visit(If_feq e) {
        return null;
    }

    @Override
    public Type visit(If_fle e) {
        return null;
    }

    @Override
    public Type visit(Call e) {
        return null;
    }

    @Override
    public Type visit(Label e) {
        return null;
    }
}
