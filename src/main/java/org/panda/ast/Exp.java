package org.panda.ast;

import java.util.*;

public abstract class Exp {
    public abstract void accept(Visitor v);

    public abstract <E> E accept(ObjVisitor<E> v) throws Exception;
}

