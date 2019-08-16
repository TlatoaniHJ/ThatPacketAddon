package us.tlatoani.thatpacketaddon.packet_field_alias;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.comphenix.protocol.events.PacketContainer;
import us.tlatoani.mundocore.registration.Registration;

public class PacketFieldAliasMundo {

    public static void load() {
        ExprPacketFieldAlias.registerSyntaxElementInfo();
        EventValues.registerEventValue(PacketFieldAlias.ContainerEvent.class, PacketContainer.class, new Getter<PacketContainer, PacketFieldAlias.ContainerEvent>() {
            @Override
            public PacketContainer get(PacketFieldAlias.ContainerEvent containerEvent) {
                return containerEvent.packet;
            }
        }, 0);
        Registration.registerEvent("Packet Field Alias", ScopePacketFieldAliases.class, PacketFieldAlias.ContainerEvent.class,
                "packet (field|info) aliases for %packettype%")
                .document("Packet Field Alias", "1.0",
                        "Not an actual event, but rather a group of packet field aliases "
                        + "to be used with packets of the specified packettype. "
                        + "Packet info aliases are aliases for specific usages of packet info expressions. "
                        + "Under the main scope lines are written in the form "
                        + "'<new syntax> " + ScopePacketFieldAliases.SEPARATOR + " <old syntax>', "
                        + "where the old syntax is how you would normally write an expression for the desired packet info, "
                        + "and the new syntax is how you want to be able to write it. "
                        + "Note that the new syntax is essentially being registered as a Skript syntax, "
                        + "so you can write it with features of Skript syntax like optional parts enclosed in '[]', "
                        + "and multiple usages of '%packet%' are allowed in your syntax, "
                        + "though only one of them should be possible to use at a time since only one packet is used to evaluate the alias "
                        + "Your syntax does not have to require or even allow the usage of '%packet%'. "
                        + "In this case, 'event-packet' will be used as the packet. "
                        + "A small addition to normal Skript syntax is that now, "
                        + "if you would like to have a group of different options for syntax but also have the whole thing be optional, "
                        + "instead of writing '[(a|b|...)]' you can write '[a|b|...]'.")
                .example("packet info aliases for play_server_world_border:"
                        , "\tborder action of %packet% = worldborderaction field of %packet%"
                        , "\tborder portal teleport boundary of %packet% = int field 0 of %packet%"
                        , "\tborder center x[-coord] of %packet% = double field 0 of %packet%"
                        , "\tborder center z[-coord] of %packet% = double field 1 of %packet%"
                        , "\tborder old radius of %packet% = double field 2 of %packet%"
                        , "\tborder radius of %packet% = double field 3 of %packet%"
                        , "\tborder speed of %packet% = long field 0 of %packet%"
                        , "\tborder warning time [span|length] of %packet% = int field 1 of %packet%"
                        , "\tborder warning (distance|blocks) of %packet% = int field 2 of %packet%"
                        , ""
                        , "on packet event play_server_world_border:"
                        , "\tbroadcast \"Border Action: %border action of event-packet%\"");
    }
}
