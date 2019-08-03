package us.tlatoani.thatpacketaddon.syntaxes;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import org.bukkit.event.Event;
import us.tlatoani.thatpacketaddon.util.BukkitPacketEvent;
import us.tlatoani.thatpacketaddon.util.SkriptPacketEventListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tlatoani on 4/29/16.
 */
public class EvtPacketEvent extends SkriptEvent {
    private List<PacketType> packetTypes;
    private String packetTypesToString;
    private ListenerPriority priority;

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        PacketType[] packetTypeArray = ((Literal<PacketType>) literals[0]).getAll();
        packetTypesToString = literals[0].toString();
        packetTypes = Arrays.asList(packetTypeArray);
        priority = parseResult.mark == 0 ? ListenerPriority.NORMAL : ListenerPriority.values()[parseResult.mark - 1];
        SkriptPacketEventListener.addPacketTypes(packetTypeArray, priority);
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof BukkitPacketEvent) {
            BukkitPacketEvent packetEvent = (BukkitPacketEvent) event;
            return packetEvent.priority == priority && packetTypes.contains(packetEvent.getPacketType());
        }
        return false;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "packet event " + packetTypesToString + " with " + priority + " priority";
    }
}
