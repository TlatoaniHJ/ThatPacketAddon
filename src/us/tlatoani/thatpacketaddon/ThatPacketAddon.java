package us.tlatoani.thatpacketaddon;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Version;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.base.MundoAddon;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.reflective_registration.ModifiableSyntaxElementInfo;
import us.tlatoani.mundocore.registration.Documentation;
import us.tlatoani.mundocore.registration.DocumentationBuilder;
import us.tlatoani.mundocore.registration.EnumClassInfo;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.mundocore.updating.Updating;
import us.tlatoani.thatpacketaddon.data_watcher.DataWatcherMundo;
import us.tlatoani.thatpacketaddon.game_profile.GameProfileMundo;
import us.tlatoani.thatpacketaddon.json.JSONMundo;
import us.tlatoani.thatpacketaddon.minecraft_key.MinecraftKeyMundo;
import us.tlatoani.thatpacketaddon.packet_field_alias.PacketFieldAliasMundo;
import us.tlatoani.thatpacketaddon.player_info_data.PlayerInfoDataMundo;
import us.tlatoani.thatpacketaddon.server_ping.ServerPingMundo;
import us.tlatoani.thatpacketaddon.skin.Skin;
import us.tlatoani.thatpacketaddon.skin.SkinMundo;
import us.tlatoani.thatpacketaddon.syntaxes.*;
import us.tlatoani.thatpacketaddon.syntaxes_legacy.*;
import us.tlatoani.thatpacketaddon.util.BukkitPacketEvent;

import java.util.*;

public class ThatPacketAddon extends MundoAddon {
    private static Map<String, PacketType> packetTypesByName;
    private static List<ExpressionInfo<?, ?>> packetFieldExpressionInfos = new ArrayList<>();

    public final static Version MINIMUM_PROTOCOLLIB_VERSION = new Version(4, 4);

    public ThatPacketAddon() {
        super(
                "packetaddon",
                ChatColor.DARK_PURPLE,
                ChatColor.LIGHT_PURPLE,
                ChatColor.GREEN
        );
    }

    @Override
    public void afterPluginsEnabled() {
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Documentation.load();
        Updating.load();
        Version protocolLibVersion =
                new Version(Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion());
        if (protocolLibVersion.isSmallerThan(MINIMUM_PROTOCOLLIB_VERSION)) {
            Logging.info("Your version of ProtocolLib is " + protocolLibVersion);
            Logging.info("ThatPacketAddon requires that you run at least version 4.4 of ProtocolLib");
        }
        Registration.setRequiredPlugins("ProtocolLib");
        Registration.register("JSON", JSONMundo::load);
        Registration.register("Packet", ThatPacketAddon::loadSyntaxes);
        Registration.register("Legacy", ThatPacketAddon::loadLegacySyntaxes);
        Registration.register("Alias", PacketFieldAliasMundo::load);
        if (!Skin.isTablisknuSkinUsed()) {
            Registration.register("Skin", SkinMundo::load);
        }
        Registration.register("DataWatcher", DataWatcherMundo::load);
        Registration.register("GameProfile", GameProfileMundo::load);
        Registration.register("MinecraftKey", MinecraftKeyMundo::load);
        Registration.register("PlayerInfoData", PlayerInfoDataMundo::load);
        Registration.register("ServerPing", ServerPingMundo::load);
    }

    private static void loadSyntaxes() {
        Converters.registerFields();

        packetTypesByName = createNameToPacketTypeMap();
        EnumClassInfo.registerEnum(PacketType.class, "packettype", packetTypesByName)
                .document("PacketType", "1.0", "A type of a packet. The ones that are available for you depend on your Minecraft version. "
                        + "If you would like to see them, do '/mundosk doc packettype' in your console. "
                        + "Alternatively, use the All Packettypes expression, loop through it, and print them.")
                .example("command /allpackettypes [<string>]:"
                        , "\tpermission: admin"
                        , "\tusage: /allpackettypes [filter]"
                        , "\ttrigger:"
                        , "\t\tif string-arg is set:"
                        , "\t\t\tmessage \"&2Messaging all packettypes that contain &6%string-arg%&2!\""
                        , "\t\telse:"
                        , "\t\t\tmessage \"&2Messaging all packettypes!\""
                        , "\t\tloop all packettypes:"
                        , "\t\t\tif string-arg is set:"
                        , "\t\t\t\tif \"%loop-value%\" contains string-arg:"
                        , "\t\t\t\t\tmessage \"&a%loop-value%\""
                        , "\t\t\telse:"
                        , "\t\t\t\tmessage \"&a%loop-value%\"");
        Registration.registerType(PacketContainer.class, "packet")
                .document("Packet", "1.0", "A packet. Packets are used by the Minecraft client and server to transmit information, "
                        + "and can be intercepted, read, and modified in order to gain information and modify the behavior of your server "
                        + "in certain ways that are not possible through Bukkit.")
                .defaultExpression(new EventValueExpression<>(PacketContainer.class));

        Registration.registerEffect(EffSendPacket.class, "send packet[s] %packets% to %players%", "send %players% packet[s] %packets%")
                .document("Send Packet", "1.0", "Sends the specified packet(s) to the specified player(s).");
        Registration.registerEffect(EffReceivePacket.class, "rec(ei|ie)ve packet[s] %packets% from %players%") //Included incorrect spelling to avoid wasted time
                .document("Receive Packet", "1.0", "Makes the server simulate receiving the specified packet(s) from the specified player(s)");
        Registration.registerEffect(EffPacketInfo.class, "packet info %packet%");

        StringJoiner priorityJoiner = new StringJoiner("|", "(", ")");
        for (int i = 1; i <= ListenerPriority.values().length; i++) {
            priorityJoiner.add(i + '¦' + ListenerPriority.values()[i - 1].name().toLowerCase());
        }
        Registration.registerEvent("Packet Event", EvtPacketEvent.class, BukkitPacketEvent.class, "packet event %packettypes% [with " + priorityJoiner.toString() + " priority]")
                .document("Packet Event", "1.0", "Called when a packet of one of the specified types is being sent or received.")
                .eventValue(PacketContainer.class, "1.0", "The packet being sent or received.")
                .eventValue(PacketType.class, "1.0", "The packettype of the packet being sent or received. Equivalent to 'event packet's packettype'.")
                .eventValue(Player.class, "1.0", "The player sending or receiving the packet.");
        Registration.registerEventValue(BukkitPacketEvent.class, PacketContainer.class, BukkitPacketEvent::getPacket);
        Registration.registerEventValue(BukkitPacketEvent.class, PacketType.class, BukkitPacketEvent::getPacketType);
        Registration.registerEventValue(BukkitPacketEvent.class, Player.class, BukkitPacketEvent::getPlayer);

        MundoPropertyExpression.registerPropertyExpression(ExprTypeOfPacket.class, PacketType.class, "packet", "packettype")
                .document("Type of Packet", "1.0", "An expression for the packettype of the specified packet.");
        registerPacketFieldExpression(ExprNewPacket.class, PacketContainer.class, "new %packettype% packet")
                .document("New Packet", "1.0", "An expression for a new packet of the specified type.");
        registerPacketFieldExpression(ExprFieldOfPacket.class, Object.class,
                Converters.getFieldNames().map(name -> name + " [packet] field [%-number%] [of %packet%]").toArray(String[]::new));
        registerPacketFieldExpression(ExprEntityFieldOfPacket.class, Entity.class,
                "%world% (pentity|entity [packet] field) %number% [of %packet%]",
                "%world% (pentity array|entity array [packet] field) %number% [of %packet%]")
                .document("Entity Field of Packet", "1.0",
                        "The entity in the specified world with id equal to value of "
                        + "the field numbered at the specified index of the specified packet's int fields. "
                        + "The specified world should match with the world of the player sending/receiving the specified packet");
    }

    private static void loadLegacySyntaxes() {
        ExprObjectOfPacket.registerConverters();
        ExprJSONObjectOfPacket.registerConverters();
        registerPacketFieldExpression(ExprJSONObjectOfPacket.class, JSONObject.class,
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(true) + ") pjson %number% [of %packet%]",
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(false) + ") array pjson %number% [of %packet%]")
                .document("Legacy JSON Field of Packet", "1.0", "An expression for certain fields of packets (first see the Packet Info expression for a more general explanation) "
                        + "that don't have equivalent types in Skript, and thus must be represented in the form of a jsonobject. "
                        + "The names of the fields can be written as strings but don't have to be. "
                        + "Current accept JSON infos: 'chatcomponent', 'serverping', 'datawatcher', 'watchablecollection', 'gameprofile', 'nbt', "
                        + "'chatcomponent' array, 'playerinfodata' array.");
        registerPacketFieldExpression(ExprObjectOfPacket.class, Object.class,
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(true) + ") pinfo %number% [of %packet%]",
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(false) + ") array pinfo %number% [of %packet%]")
                .document("Legacy Field of Packet", "1.0", "An expression for the packet field of either the specified type or referred to by the specified string, "
                        + "with the specified index, of the specified packet. For example, 'string' can be used as the specified type to get a string field "
                        + "in the specified packet. 'array' must be included in the syntax when the fields are plural (ex. 'string array'). "
                        + "Use 'object' in order to access all fields of the packet. However, some fields will have objects in raw forms "
                        + "that cannot be easily used in Skript without the use of addons such as skript-mirror. Many of these fields can be converted into Skript "
                        + "types; most of these fields must be referred to by a certain string rather than a type: "
                        + "\"uuid\", \"material\", \"blockdata\", \"collection\" array, \"bytebuffer\" array (The last two are plural and must have array included in the syntax.");
        registerPacketFieldExpression(ExprPrimitiveOfPacket.class, Number.class, "(0¦byte|1¦short|2¦int|3¦long|4¦float|5¦double) pnum %number% [of %packet%]")
                .document("Legacy Number Field of Packet", "1.0", "An expression for different kinds of number fields of packets. "
                        + "First see the Packet Info expression for a more general explanation of packet fields.");
        registerPacketFieldExpression(ExprPrimitiveArrayOfPacket.class, Number.class, "(0¦int|1¦byte) array pnum %number% [of %packet%]")
                .document("Legacy Number Array Field of Packet", "1.0", "An expression for int array and byte array fields of packets. "
                        + "First see the Packet Info expression for a more general explanation of packet fields.");
        registerPacketFieldExpression(ExprEnumOfPacket.class, String.class, "(arbitrary|%-string%) penum %number% [of %packet%]")
                .document("Legacy Enum Field of Packet", "1.0", "An expression for an enum field of a packet "
                        + "(first see the Packet Info o expression for a more general explanation of packet fields). "
                        + "The specified string is the name of the enum you are getting/setting. "
                        + "Using arbitrary gives you access to all enum fields, rather than just one particular type, "
                        + "and allows you to access certain enums that are NMS types rather than ProtocolLib and thus "
                        + "can't be accessed by their name.");
    }

    private static <E extends Expression<T>, T> DocumentationBuilder registerPacketFieldExpression(Class<E> exprClass, Class<T> returnType, String... patterns) {
        packetFieldExpressionInfos.add(ModifiableSyntaxElementInfo.createExpressionInfo(patterns, returnType, exprClass));
        return Registration.registerExpression(exprClass, returnType, ExpressionType.COMBINED, patterns);
    }

    public static Iterator<ExpressionInfo<?, ?>> packetInfoExpressionInfoIterator() {
        Logging.debug(PacketManager.class, "pIEI = " + packetFieldExpressionInfos);
        return packetFieldExpressionInfos.iterator();
    }

    private static Map<String, PacketType> createNameToPacketTypeMap() {
        Map<String, PacketType> packetTypesByName = new HashMap<>();
        addPacketTypes(packetTypesByName, PacketType.Play.Server.getInstance().iterator(), "PLAY", true);
        addPacketTypes(packetTypesByName, PacketType.Play.Client.getInstance().iterator(), "PLAY", false);
        addPacketTypes(packetTypesByName, PacketType.Handshake.Server.getInstance().iterator(), "HANDSHAKE", true);
        addPacketTypes(packetTypesByName, PacketType.Handshake.Client.getInstance().iterator(), "HANDSHAKE", false);
        addPacketTypes(packetTypesByName, PacketType.Login.Server.getInstance().iterator(), "LOGIN", true);
        addPacketTypes(packetTypesByName, PacketType.Login.Client.getInstance().iterator(), "LOGIN", false);
        addPacketTypes(packetTypesByName, PacketType.Status.Server.getInstance().iterator(), "STATUS", true);
        addPacketTypes(packetTypesByName, PacketType.Status.Client.getInstance().iterator(), "STATUS", false);
        return packetTypesByName;
    }

    private static void addPacketTypes(Map<String, PacketType> map, Iterator<PacketType> packetTypeIterator, String prefix, Boolean isServer) {
        while (packetTypeIterator.hasNext()) {
            PacketType current = packetTypeIterator.next();
            String fullname = prefix + "_" + (isServer ? "SERVER" : "CLIENT") + "_" + current.name().toUpperCase();
            map.put(fullname, current);
        }
    }
}
