package us.tlatoani.thatpacketaddon.game_profile;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.entity.Player;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;

public class ExprProfileOfPlayer extends MundoPropertyExpression<Player, WrappedGameProfile> {
    @Override
    public WrappedGameProfile convert(Player player) {
        return WrappedGameProfile.fromPlayer(player);
    }
}
