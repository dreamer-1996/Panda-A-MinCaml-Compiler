package org.panda.ast;

import java.util.function.Supplier;

public class Id implements Supplier<Id> {
    String id;
    public Id(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return id;
    }
    static int x = -1;
    public static Id gen() {
        x++;
        return new Id("?v" + x);
    }

    @Override
    public Id get() {
        return gen();
    }
}
