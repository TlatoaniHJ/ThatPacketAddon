package us.tlatoani.thatpacketaddon.player_info_data;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.json.simple.JSONObject;

import java.util.Optional;

public class ExprNewPlayerInfoData extends SimpleExpression<PlayerInfoData> {
    private Expression<WrappedGameProfile> profileExpression;
    private Optional<Expression<Number>> latencyExpression;
    private Optional<Expression<GameMode>> gameModeExpression;
    private Expression<JSONObject> displayNameExpression;

    @Override
    protected PlayerInfoData[] get(Event event) {
        WrappedGameProfile profile = profileExpression.getSingle(event);
        int latency = latencyExpression
                .map(expr -> expr.getSingle(event))
                .map(Number::intValue)
                .orElse(0);
        EnumWrappers.NativeGameMode gameMode = gameModeExpression
                .map(expr -> expr.getSingle(event))
                .map(EnumWrappers.NativeGameMode::fromBukkit)
                .orElse(EnumWrappers.NativeGameMode.NOT_SET);
        Optional<WrappedChatComponent> displayName = Optional
                .ofNullable(displayNameExpression.getSingle(event))
                .map(JSONObject::toString)
                .map(WrappedChatComponent::fromJson);
        if (profile == null || !displayName.isPresent()) {
            return new PlayerInfoData[0];
        }
        return new PlayerInfoData[]{new PlayerInfoData(
                profile,
                latency,
                gameMode,
                displayName.get()
        )};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends PlayerInfoData> getReturnType() {
        return PlayerInfoData.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "playerinfodata with"
                + " profile " + profileExpression
                + " display name " + displayNameExpression
                + latencyExpression.map(expr -> " latency " + expr).orElse("")
                + gameModeExpression.map(expr -> " gamemode " + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        profileExpression = (Expression<WrappedGameProfile>) exprs[0];
        displayNameExpression = (Expression<JSONObject>) exprs[1];
        latencyExpression = Optional.ofNullable((Expression<Number>) exprs[2]);
        gameModeExpression = Optional.ofNullable((Expression<GameMode>) exprs[3]);
        return true;
    }
}
