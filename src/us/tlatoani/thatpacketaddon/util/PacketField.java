package us.tlatoani.thatpacketaddon.util;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

import java.util.function.Function;

/**
 * Created by Tlatoani on 8/13/16.
 */
public abstract class PacketField<T> {
    public final String name;
    public final Class<T> type;
    public final Class<?> singleType;
    public final boolean isSingle;

    protected PacketField(String name, Class<T> type) {
        this.name = name;
        this.type = type;
        if (type == null ) {
            singleType = null;
            isSingle = true;
        } else if (type.isArray()) {
            singleType = type.getComponentType();
            isSingle = false;
        } else {
            singleType = type;
            isSingle = true;
        }
    }

    public abstract T get(PacketContainer packet, int index);

    public abstract void set(PacketContainer packet, int index, T value);

    public static <T> PacketField<T> simple(String name, Class<T> type, Function<PacketContainer, StructureModifier<T>> function) {
        return new SimplePacketField<>(name, type, function);
    }

    public static <F, T> PacketField<T> converted(String name, Class<T> type, Function<PacketContainer, StructureModifier<F>> function, Function<? super F, ? extends T> get, Function<? super T, ? extends F> set) {
        return new ConvertedPacketField<>(name, type, function, get, set);
    }

    private static class SimplePacketField<T> extends PacketField<T> {
        private final Function<PacketContainer, StructureModifier<T>> function;

        protected SimplePacketField(String name, Class<T> type, Function<PacketContainer, StructureModifier<T>> function) {
            super(name, type);
            this.function = function;
        }

        @Override
        public T get(PacketContainer packet, int index) {
            return function.apply(packet).readSafely(index);
        }

        @Override
        public void set(PacketContainer packet, int index, T value) {
            function.apply(packet).writeSafely(index, value);
        }
    }

    private static class ConvertedPacketField<F, T> extends PacketField<T> {
        private final Function<PacketContainer, StructureModifier<F>> function;
        private final Function<? super F, ? extends T> get;
        private final Function<? super T, ? extends F> set;

        private ConvertedPacketField(String name, Class<T> type, Function<PacketContainer, StructureModifier<F>> function, Function<? super F, ? extends T> get, Function<? super T, ? extends F> set) {
            super(name, type);
            this.function = function;
            this.get = get;
            this.set = set;
        }
        @Override
        public T get(PacketContainer packet, int index) {
            F raw = function.apply(packet).readSafely(index);
            if (raw == null) {
                return null;
            }
            return get.apply(raw);
        }

        @Override
        public void set(PacketContainer packet, int index, T value) {
            F raw = value == null ? null : set.apply(value);
            function.apply(packet).writeSafely(index, raw);
        }
    }
}
