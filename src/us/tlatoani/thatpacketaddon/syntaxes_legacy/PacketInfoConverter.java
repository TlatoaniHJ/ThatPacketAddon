package us.tlatoani.thatpacketaddon.syntaxes_legacy;

import com.comphenix.protocol.events.PacketContainer;

public abstract class PacketInfoConverter<T> {
    public final Class<? extends T> type;

    protected PacketInfoConverter(Class<? extends T> type) {
        this.type = type;
    }

    public abstract T get(PacketContainer packet, int index);

    public abstract void set(PacketContainer packet, int index, T value);
}