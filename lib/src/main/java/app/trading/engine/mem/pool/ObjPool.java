package app.trading.engine.mem.pool;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ObjPool<T> extends Supplier<T> {
    void release(T obj);

    static <T> ObjPool<T> newPool(final Supplier<? extends T> factory, final Consumer<? super T> initializer) {
        return new SimpleObjPool<>(16, factory, initializer);
    }

    static <T> ObjPool<T> newFakePool(final Supplier<? extends T> factory, final Consumer<? super T> initializer) {
        return new FakeObjPool<>(factory, initializer);
    }
}
