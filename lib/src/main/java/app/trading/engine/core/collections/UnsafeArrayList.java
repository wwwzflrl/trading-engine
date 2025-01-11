package app.trading.engine.core.collections;

import app.trading.engine.core.api.API;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import static java.lang.System.arraycopy;

/**
 * Implement stack, we need care about pop.
 * So first element in elements will always be null
 */
public class UnsafeArrayList<T> extends AbstractList<T> implements Stack<T> {
    private final IteratorImpl itr = new IteratorImpl();
    private Object[] elements;
    private int tail;
    private int limit;

    public UnsafeArrayList(final int initCapacity) {
        limit = Math.max(initCapacity, 7);
        elements = new Object[limit + 1];
        tail = 0;
    }

    public UnsafeArrayList() { this(15); }

    @API
    public static <T> UnsafeArrayList<T> copyOf(final Collection<T> c) {
        final var list = new UnsafeArrayList<T>(c.size());
        list.addAll(c);
        return list;
    }

    @Override
    public T peek() {
        return (T) elements[tail];
    }

    @Override
    public void push(@NotNull T element) {
        if (tail++ == limit) {
            grow();
        }
        elements[tail] = element;
    }

    private void grow() {
        final Object[] grown = new Object[tail + (tail >> 1)];
        arraycopy(elements, 1, grown, 1, limit);
        limit = grown.length - 1;
        elements = grown;
    }

    @Override
    public T pop() {
        final T obj = peek();
        if (obj == null) return null;
        elements[tail]  =  null;
        tail = tail - 1;
        return obj;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= tail) throw  new IndexOutOfBoundsException();
        return (T) elements[index + 1];
    }

    @Override
    public int size() {
        return tail;
    }

    @Override
    public T remove(final int index) {
        final var removed = get(index);
        removeRange(index, index);
        return removed;
    }

    @Override
    public void removeRange(final int from, int to) {
        to++;
        final var moved = tail - to;
        arraycopy(elements, to + 1, elements, from + 1, moved);
        tail = from + moved;
    }

    @Override
    public void add(int index, final T element) {
        final var moved = tail - index;
        if (tail++ == limit) {
            grow();
        }
        index++;
        arraycopy(elements, index, elements, index + 1, moved);
        elements[index] = element;
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends T> c) {
        int cursor = tail + 1;
        final int added = c.size();
        tail += added;
        if (tail >= limit) {
            grow();
        }
        for (final T element : c) {
            elements[cursor++] = element;
        }
        return true;
    }

    @Override
    public boolean add(final T t) {
        push(t);
        return true;
    }

    @Override
    public void clear() { tail = 0; }

    @Override
    public @NotNull Iterator<T> iterator() { return itr.reset(0); }

    @Override
    public @NotNull ListIterator<T> listIterator(final int index) { return itr.reset(index); }

    class IteratorImpl implements ListIterator<T> {
        private int cursor;

        ListIterator<T> reset(final int index) {
            cursor = index + 1;
            return this;
        }

        @Override
        public boolean hasNext() {
            return cursor <= tail;
        }

        @Override
        public T next() {
            return (T) elements[cursor++];
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 1;
        }

        @Override
        public T previous() {
            return (T) elements[--cursor];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 2;
        }

        @Override
        public void remove() {
            final var removed = cursor - 2;
            removeRange(removed, removed);
            cursor--;
        }

        @Override
        public void set(T t) {
            elements[cursor - 1] = t;
        }

        @Override
        public void add(T t) {
            UnsafeArrayList.this.add(cursor -1, t);
            cursor++;
        }
    }
}
