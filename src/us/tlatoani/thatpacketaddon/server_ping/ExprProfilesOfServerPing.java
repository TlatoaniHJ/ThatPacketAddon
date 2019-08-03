package us.tlatoani.thatpacketaddon.server_ping;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.util.coll.iterator.EmptyIterator;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ExprProfilesOfServerPing extends SimpleExpression<WrappedGameProfile> {
    private Expression<WrappedServerPing> serverPingExpression;

    @Override
    protected WrappedGameProfile[] get(Event event) {
        WrappedServerPing serverPing = serverPingExpression.getSingle(event);
        if (serverPing == null) {
            return new WrappedGameProfile[0];
        }
        return serverPing.getPlayers().toArray(new WrappedGameProfile[0]);
    }

    @Override
    public Iterator<WrappedGameProfile> iterator(Event event) {
        WrappedServerPing serverPing = serverPingExpression.getSingle(event);
        if (serverPing == null) {
            return new EmptyIterator<>();
        }
        return serverPing.getPlayers().iterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends WrappedGameProfile> getReturnType() {
        return WrappedGameProfile.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "game profiles of ping " + serverPingExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        serverPingExpression = (Expression<WrappedServerPing>) exprs[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        WrappedServerPing serverPing = serverPingExpression.getSingle(event);
        if (serverPing == null) {
            return;
        }
        if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
            serverPing.setPlayers(ImmutableList.of());
            return;
        }
        List<WrappedGameProfile> profiles = new ArrayList<>(delta.length);
        for (Object value : delta) {
            if (value instanceof WrappedGameProfile) {
                profiles.add((WrappedGameProfile) value);
            } else if (value instanceof Player) {
                profiles.add(WrappedGameProfile.fromPlayer((Player) value));
            } else {
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), (String) value));
            }
        }
        if (mode == Changer.ChangeMode.ADD) {
            profiles.addAll(serverPing.getPlayers());
        }
        serverPing.setPlayers(profiles);
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(WrappedGameProfile[].class, Player[].class, String[].class);
        }
        return null;
    }
}
