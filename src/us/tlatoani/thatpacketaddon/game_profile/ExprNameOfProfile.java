package us.tlatoani.thatpacketaddon.game_profile;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import us.tlatoani.mundocore.property_expression.EvolvingPropertyExpression;

public class ExprNameOfProfile extends EvolvingPropertyExpression<WrappedGameProfile, String> {
    @Override
    public WrappedGameProfile set(WrappedGameProfile gameProfile, String s) {
        WrappedGameProfile result = new WrappedGameProfile(gameProfile.getUUID(), s);
        gameProfile.getProperties().forEach((key, property) -> {
            result.getProperties().put(key, property);
        });
        return result;
    }

    @Override
    public String convert(WrappedGameProfile gameProfile) {
        return gameProfile.getName();
    }
}
