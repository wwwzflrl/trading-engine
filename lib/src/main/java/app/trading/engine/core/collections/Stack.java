package app.trading.engine.core.collections;

import org.jetbrains.annotations.NotNull;

public interface Stack<T> {

    T peek();

    void push(@NotNull T element);

    T pop();

    static <T> Stack<T> newStack(final int initCapacity) { return new UnsafeArrayList<>(initCapacity); }
}
