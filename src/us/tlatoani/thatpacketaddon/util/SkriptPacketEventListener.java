package us.tlatoani.thatpacketaddon.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import org.bukkit.Bukkit;
import us.tlatoani.thatpacketaddon.PacketManager;

import java.util.*;

public class SkriptPacketEventListener {
    private final ListenerPriority priority;
    private Set<PacketType> packetTypesListenedFor = new HashSet<PacketType>();

    private static final Map<ListenerPriority, SkriptPacketEventListener> listeners = new HashMap<>();

    public static void addPacketTypes(PacketType[] packetTypes, ListenerPriority priority) {
        SkriptPacketEventListener listener = listeners.computeIfAbsent(priority, SkriptPacketEventListener::new);
        listener.addPacketTypes(packetTypes);
    }

    private SkriptPacketEventListener(ListenerPriority priority) {
        this.priority = priority;
    }

    private void addPacketTypes(PacketType[] packetTypes) {
        List<PacketType> packetTypesToStartListeningFor = new ArrayList<PacketType>();
        for (int i = 0; i < packetTypes.length; i++) {
            if (!packetTypesListenedFor.contains(packetTypes[i])) {
                packetTypesListenedFor.add(packetTypes[i]);
                packetTypesToStartListeningFor.add(packetTypes[i]);
            }
        }
        if (!packetTypesToStartListeningFor.isEmpty()) {
            PacketManager.onPacketEvent(packetTypesToStartListeningFor.toArray(new PacketType[0]), priority, packetEvent -> {
                BukkitPacketEvent event = new BukkitPacketEvent(packetEvent, priority);
                Bukkit.getServer().getPluginManager().callEvent(event);
            });
        }
    }
}
