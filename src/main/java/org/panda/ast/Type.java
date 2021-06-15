package org.panda.ast;

import java.util.List;

public abstract class Type {
    private static int x = 0;
    public static Type gen() {
        return new TVar("?" + x++);
    }

}

class TBool extends Type { }

class TInt extends Type { }

class TFloat extends Type { }

class TFun extends Type {

}

class TTuple extends Type {
    List<Type> type_in_tuple;
    TTuple(List<Type> type_in_tuple) {
        this.type_in_tuple = type_in_tuple;
    }
}

class TArray extends Type {
    Type type;
    TArray(Type type) {
        this.type = type;
    }
}

class TVar extends Type {
    String v;
    TVar(String v) {
        this.v = v;
    }
    @Override
    public String toString() {
        return v;
    }
}

