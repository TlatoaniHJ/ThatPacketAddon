package us.tlatoani.thatpacketaddon.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tlatoani on 4/29/16.
 */
public class BukkitPacketEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public final PacketEvent packetEvent;
    public final ListenerPriority priority;

    public BukkitPacketEvent(PacketEvent event, ListenerPriority priority) {
        this.packetEvent = event;
        this.priority = priority;
    }

    public PacketType getPacketType() {
        return packetEvent.getPacketType();
    }

    public PacketContainer getPacket() {
        return packetEvent.getPacket();
    }

    public Player getPlayer() {
        return packetEvent.getPlayer();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return packetEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean b) {
        packetEvent.setCancelled(b);
    }
}
