package us.tlatoani.thatpacketaddon.game_profile;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.event.Event;
import us.tlatoani.thatpacketaddon.skin.Skin;

import java.util.Optional;
import java.util.UUID;

public class ExprNewGameProfile extends SimpleExpression<WrappedGameProfile> {
    private Expression<String> nameExpression;
    private Optional<Expression<String>> uuidExpression;
    private Optional<Expression<?>> skinExpression;

    @Override
    protected WrappedGameProfile[] get(Event event) {
        String name = nameExpression.getSingle(event);
        if (name == null) {
            return new WrappedGameProfile[0];
        }
        UUID uuid = uuidExpression
                .map(expr -> expr.getSingle(event))
                .map(UUID::fromString)
                .orElseGet(UUID::randomUUID);
        Optional<?> skin = skinExpression.map(expr -> expr.getSingle(event));
        return skin.map(s -> new WrappedGameProfile[]{
                Skin.fromSkriptSkin(s).toGameProfile(uuid, name)
        }).orElseGet(() -> new WrappedGameProfile[]{
                new WrappedGameProfile(uuid, name)
        });
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WrappedGameProfile> getReturnType() {
        return WrappedGameProfile.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "game profile with"
                + " name " + nameExpression
                + uuidExpression.map(expr -> " uuid " + expr).orElse("")
                + skinExpression.map(expr -> " skin " + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        nameExpression = (Expression<String>) exprs[0];
        uuidExpression = Optional.ofNullable((Expression<String>) exprs[1]);
        skinExpression = Optional.ofNullable(exprs[2]);
        return true;
    }
}
