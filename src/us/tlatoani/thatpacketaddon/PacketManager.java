package us.tlatoani.thatpacketaddon;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import us.tlatoani.mundocore.base.Logging;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class PacketManager {

    public static void onPacketEvent(PacketType[] packetTypes, ListenerPriority priority, Consumer<PacketEvent> handler) {
        ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new PacketAdapter(ThatPacketAddon.get(), priority, packetTypes) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handler.accept(event);
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                handler.accept(event);
            }
        }).syncStart();
    }

    public static void sendPacket(PacketContainer packet, Object exceptLoc, Player[] players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            Logging.reportException(exceptLoc, e);
        }
    }
}
