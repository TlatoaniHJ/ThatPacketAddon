package us.tlatoani.thatpacketaddon.server_ping;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.json.simple.JSONObject;
import us.tlatoani.thatpacketaddon.syntaxes.Converters;

public class ExprMOTDOfServerPing extends SimpleExpression<JSONObject> {
    private Expression<WrappedServerPing> serverPingExpression;

    @Override
    protected JSONObject[] get(Event event) {
        WrappedServerPing serverPing = serverPingExpression.getSingle(event);
        if (serverPing == null) {
            return new JSONObject[0];
        }
        return new JSONObject[]{Converters.fromWrappedChatComponent(serverPing.getMotD())};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends JSONObject> getReturnType() {
        return JSONObject.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "motd of ping " + serverPingExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        serverPingExpression = (Expression<WrappedServerPing>) exprs[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        WrappedServerPing serverPing = serverPingExpression.getSingle(event);
        if (serverPing == null || delta[0] == null) {
            return;
        }
        if (delta[0] instanceof JSONObject) {
            WrappedChatComponent value = Converters.toWrappedChatComponent((JSONObject) delta[0]);
            serverPing.setMotD(value);
        } else {
            String value = (String) delta[0];
            serverPing.setMotD(value);
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(JSONObject.class, String.class);
        }
        return null;
    }
}
