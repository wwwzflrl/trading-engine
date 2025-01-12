package app.trading.engine.mem.pool;

import app.trading.engine.core.collections.Stack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleObjPool<T> implements ObjPool<T> {
    final Stack<T> pool;
    private final Supplier<? extends T> factory;
    private final Consumer<? super T> initializer;

    SimpleObjPool(final int initCapacity, final Supplier<? extends T> factory, final Consumer<? super T> initializer) {
        pool = Stack.newStack(initCapacity);
        this.factory = factory;
        this.initializer = initializer;
    }

    @Override
    public T get() {
        final var obj = pool.pop();
        if (obj == null) {
            final T created = factory.get();
            initializer.accept(created);
            return created;
        }
        initializer.accept(obj);
        return obj;
    }

    @Override
    public void release(T obj) {
        pool.push(obj);
    }
}
