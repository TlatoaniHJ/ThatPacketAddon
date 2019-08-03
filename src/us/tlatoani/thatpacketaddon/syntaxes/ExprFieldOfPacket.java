package us.tlatoani.thatpacketaddon.syntaxes;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import us.tlatoani.thatpacketaddon.util.PacketField;

import java.lang.reflect.Array;

public class ExprFieldOfPacket extends SimpleExpression<Object> {
    private PacketField field;
    private Expression<Number> indexExpression;
    private Expression<PacketContainer> packetExpression;

    @Override
    protected Object[] get(Event event) {
        Number index = indexExpression.getSingle(event);
        PacketContainer packet = packetExpression.getSingle(event);
        if (index == null || packet == null) {
            return new Object[0];
        }
        if (isSingle()) {
            return new Object[]{field.get(packet, index.intValue())};
        } else {
            return (Object[]) field.get(packet, index.intValue());
        }
    }

    @Override
    public boolean isSingle() {
        return field.isSingle;
    }

    @Override
    public Class<?> getReturnType() {
        return field.singleType;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return field.name + " packet field " + indexExpression + " of " + packetExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        field = Converters.getField(matchedPattern);
        indexExpression = (Expression<Number>) expressions[0];
        if (indexExpression == null) {
            indexExpression = new SimpleLiteral<>(0, false);
        }
        packetExpression = (Expression<PacketContainer>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Number index = indexExpression.getSingle(event);
        PacketContainer packet = packetExpression.getSingle(event);
        if (index == null || packet == null) {
            return;
        }
        if (isSingle()) {
            field.set(packet, index.intValue(), delta[0]);
        } else if (getReturnType() == Object.class) {
            field.set(packet, index.intValue(), delta);
        } else {
            Object[] result = (Object[]) Array.newInstance(getReturnType(), delta.length);
            for (int i = 0; i < delta.length; i++) {
                result[i] = delta[i];
            }
            field.set(packet, index.intValue(), result);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(field.type);
        }
        return null;
    }
}
