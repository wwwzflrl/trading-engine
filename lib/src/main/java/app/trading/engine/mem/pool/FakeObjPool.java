package app.trading.engine.mem.pool;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FakeObjPool<T> implements ObjPool<T>{
    private final Supplier<? extends T> factory;
    private final Consumer<? super T> initializer;

    public FakeObjPool(Supplier<? extends T> factory, Consumer<? super T> initializer) {
        this.factory = factory;
        this.initializer = initializer;
    }

    @Override
    public void release(T obj) {

    }

    @Override
    public T get() {
        final T created = factory.get();
        initializer.accept(created);
        return created;
    }
}
