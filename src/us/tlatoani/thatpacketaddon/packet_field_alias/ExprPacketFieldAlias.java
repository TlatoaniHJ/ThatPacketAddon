package us.tlatoani.thatpacketaddon.packet_field_alias;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.DefaultExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;
import us.tlatoani.mundocore.grouped_list.GroupedList;
import us.tlatoani.mundocore.reflective_registration.ModifiableSyntaxElementInfo;

import java.util.Collection;

/**
 * Created by Tlatoani on 10/7/17.
 */
public class ExprPacketFieldAlias extends SimpleExpression<Object> {
    private static final ModifiableSyntaxElementInfo.Expression<ExprPacketFieldAlias, Object> syntaxElementInfo =
            new ModifiableSyntaxElementInfo.Expression<>(ExprPacketFieldAlias.class, Object.class, ExpressionType.PROPERTY);
    private static final GroupedList<PacketFieldAlias> aliases = new GroupedList<>();
    private static boolean registered = false;

    private PacketFieldAlias alias;
    private Expression<PacketContainer> packetExpression;

    public static void registerSyntaxElementInfo() {
        syntaxElementInfo.register();
    }

    public static GroupedList.Key registerAliases(Collection<PacketFieldAlias> aliases) {
        if (!registered) {
            syntaxElementInfo.register();
        }
        GroupedList.Key key = ExprPacketFieldAlias.aliases.addGroup(aliases);
        setPatterns();
        return key;
    }

    private static void setPatterns() {
        syntaxElementInfo.setPatterns(aliases.stream().map(alias -> alias.alias).toArray(String[]::new));
    }

    public static void unregisterAliases(GroupedList.Key key) {
        aliases.removeGroup(key);
        setPatterns();
    }

    public static void unregisterAllAliases() {
        aliases.clear();
        syntaxElementInfo.setPatterns();
    }

    @Override
    protected Object[] get(Event event) {
        return alias.get(packetExpression.getSingle(event));
    }

    @Override
    public boolean isSingle() {
        return alias.expression.isSingle();
    }

    @Override
    public Class<?> getReturnType() {
        return alias.expression.getReturnType();
    }

    @Override
    public String toString(Event event, boolean b) {
        return alias.toString(packetExpression, event, b);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        for (Expression<?> expression : expressions) {
            if (expression != null) {
                packetExpression = (Expression<PacketContainer>) expression;
                break;
            }
        }
        if (packetExpression == null) {
            DefaultExpression<PacketContainer> defaultExpression = Classes.getExactClassInfo(PacketContainer.class).getDefaultExpression();
            if (defaultExpression.init()) {
                packetExpression = defaultExpression;
            } else {
                return false;
            }
        }
        alias = aliases.get(i);
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        alias.change(packetExpression.getSingle(event), delta, mode);
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return alias.acceptChange(mode);
    }
}
