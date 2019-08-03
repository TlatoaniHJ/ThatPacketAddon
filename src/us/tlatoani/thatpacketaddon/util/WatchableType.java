package us.tlatoani.thatpacketaddon.util;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import java.util.Optional;
import java.util.function.Function;

public abstract class WatchableType<T> {
    public final String name;
    public final Class<T> type;
    public final WrappedDataWatcher.Serializer serializer;

    protected WatchableType(String name, Class<T> type, WrappedDataWatcher.Serializer serializer) {
        this.name = name;
        this.type = type;
        this.serializer = serializer;
    }

    public abstract T get(Object object);

    public abstract Object set(T value);

    public static <T> WatchableType<T> simple(String name, Class<T> type, WrappedDataWatcher.Serializer serializer) {
        return new SimpleWatchableType<>(name, type, serializer);
    }

    public static <F, T> WatchableType<T> converted(String name, Class<T> type, Class<F> wrappedType, WrappedDataWatcher.Serializer serializer, Function<? super F, ? extends T> get, Function<? super T, ? extends F> set) {
        return new ConvertedWatchableType<>(name, type, wrappedType, serializer, get, set);
    }

    public static <F, T> WatchableType<T> optional(String name, Class<T> type, Class<F> wrappedType, WrappedDataWatcher.Serializer serializer, Function<? super F, ? extends T> get, Function<? super T, ? extends F> set) {
        return new WatchableOptionalType<>(name, type, wrappedType, serializer, get, set);
    }

    private static class SimpleWatchableType<T> extends WatchableType<T> {

        protected SimpleWatchableType(String name, Class<T> type, WrappedDataWatcher.Serializer serializer) {
            super(name, type, serializer);
        }

        @Override
        public T get(Object object) {
            if (type.isInstance(object)) {
                return (T) object;
            } else {
                return null;
            }
        }

        @Override
        public Object set(T value) {
            return value;
        }
    }

    private static class ConvertedWatchableType<F, T> extends WatchableType<T> {
        private final Class<F> wrappedType;
        private final Function<? super F, ? extends T> get;
        private final Function<? super T, ? extends F> set;

        private ConvertedWatchableType(String name, Class<T> type, Class<F> wrappedType, WrappedDataWatcher.Serializer serializer, Function<? super F, ? extends T> get, Function<? super T, ? extends F> set) {
            super(name, type, serializer);
            this.wrappedType = wrappedType;
            this.get = get;
            this.set = set;
        }

        @Override
        public T get(Object object) {
            if (wrappedType.isInstance(object)) {
                return get.apply((F) object);
            } else {
                return null;
            }
        }

        @Override
        public Object set(T value) {
            if (wrappedType.isInstance(value)) {
                return set.apply(value);
            } else {
                return null;
            }
        }
    }

    private static class WatchableOptionalType<F, T> extends WatchableType<T> {
        private final Class<F> wrappedType;
        private final Function<? super F, ? extends T> get;
        private final Function<? super T, ? extends F> set;

        private WatchableOptionalType(String name, Class<T> type, Class<F> wrappedType, WrappedDataWatcher.Serializer serializer, Function<? super F, ? extends T> get, Function<? super T, ? extends F> set) {
            super(name, type, serializer);
            this.wrappedType = wrappedType;
            this.get = get;
            this.set = set;
        }

        @Override
        public T get(Object object) {
            if (!(object instanceof Optional)) {
                return null;
            }
            Optional optional = (Optional) object;
            if (optional.isPresent() && wrappedType.isInstance(optional.get())) {
                return get.apply((F) optional.get());
            } else {
                return null;
            }
        }

        @Override
        public Object set(T value) {
            if (value == null) {
                return Optional.empty();
            } else if (wrappedType.isInstance(value)) {
                return Optional.ofNullable(set.apply(value));
            } else {
                return null;
            }
        }
    }
}
