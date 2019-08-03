package us.tlatoani.thatpacketaddon.packet_field_alias;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.reflection.Reflection;
import us.tlatoani.mundocore.base_event.BaseEvent;
import us.tlatoani.thatpacketaddon.ThatPacketAddon;

import java.util.Iterator;
import java.util.Optional;

/**
 * Created by Tlatoani on 10/15/17.
 */
public class PacketFieldAlias {
    public final PacketType packetType;
    public final String alias;
    public final String original;
    public final Expression<?> expression;

    public static final Reflection.MethodInvoker SKRIPT_PARSER_PARSE = Reflection.getMethod(SkriptParser.class, "parse", Iterator.class);

    public static class ContainerEvent extends BaseEvent {
        public final PacketContainer packet;

        public ContainerEvent(PacketContainer packet) {
            this.packet = packet;
        }
    }

    private PacketFieldAlias(PacketType packetType, String alias, String original, Expression<?> expression) {
        this.packetType = packetType;
        this.alias = alias;
        this.expression = expression;
        this.original = original;
    }

    public static Optional<PacketFieldAlias> create(PacketType packetType, String syntax, String original) {
        String resultSyntax = original.replace("%packet%", "event-packet"); //note that
        String currentEventName = ScriptLoader.getCurrentEventName();
        Class<? extends Event>[] currentEvents = ScriptLoader.getCurrentEvents();
        ScriptLoader.setCurrentEvent("ExprPacketInfoAliasPacketEvent", ContainerEvent.class);
        Logging.debug(PacketFieldAlias.class, "packetType = " + packetType + ", syntax = " + syntax + ", original = " + original + ", resultSyntax = " + resultSyntax);
        //Expression<?> expression = SkriptParser.parseStatic(resultSyntax, PacketManager.packetInfoExpressionInfoIterator(), "'" + original + "' is not a valid packet info expression");
        Expression<?> expression = parsePacketInfoExpression(resultSyntax, "'" + original + "' is not a valid packet info expression");
        Logging.debug(PacketFieldAlias.class, "expr = " + expression);
        ScriptLoader.setCurrentEvent(currentEventName, currentEvents);
        if (expression == null) {
            return Optional.empty();
        }
        return Optional.of(new PacketFieldAlias(packetType, syntax, original, expression));
    }

    //Method adapted from the parseStatic() method of SkriptParser in Skript
    private static Expression<?> parsePacketInfoExpression(String syntax, String defaultError) {
        final ParseLogHandler log = SkriptLogger.startParseLogHandler();
        final Expression<?> result;
        try {
            SkriptParser parser = new SkriptParser(syntax, SkriptParser.ALL_FLAGS);
            result = (Expression<?>) SKRIPT_PARSER_PARSE.invoke(parser, ThatPacketAddon.packetInfoExpressionInfoIterator());
            if (result != null) {
                log.printLog();
                return result;
            }
            log.printError(defaultError);
            return null;
        } catch (Exception e) {
            Logging.reportException(PacketFieldAlias.class, e);
        } finally {
            log.stop();
        }
        return null;
    }

    public String toString(Expression<PacketContainer> packetExpression, Event event, boolean b) {
        return alias.replace("%packet%", packetExpression.toString(event, b));
    }

    public Object[] get(PacketContainer packet) {
        if (packet.getType() == packetType) {
            return expression.getArray(new ContainerEvent(packet));
        } else {
            return new Object[0];
        }
    }

    public void change(PacketContainer packet, Object[] delta, Changer.ChangeMode mode) {
        if (packet.getType() == packetType) {
            Event event = new ContainerEvent(packet);
            expression.change(event, delta, mode);
        }
    }

    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return expression.acceptChange(mode);
    }
}
