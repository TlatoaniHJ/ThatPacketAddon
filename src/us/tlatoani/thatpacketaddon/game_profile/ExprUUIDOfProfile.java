package us.tlatoani.thatpacketaddon.game_profile;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import us.tlatoani.mundocore.property_expression.EvolvingPropertyExpression;

import java.util.Optional;
import java.util.UUID;

public class ExprUUIDOfProfile extends EvolvingPropertyExpression<WrappedGameProfile, String> {
    @Override
    public WrappedGameProfile reset(WrappedGameProfile gameProfile) {
        return set(gameProfile, null);
    }

    @Override
    public WrappedGameProfile set(WrappedGameProfile gameProfile, String s) {
        WrappedGameProfile result = new WrappedGameProfile(s == null ? UUID.randomUUID() : UUID.fromString(s), gameProfile.getName());
        gameProfile.getProperties().forEach((key, property) -> {
            result.getProperties().put(key, property);
        });
        return result;
    }

    @Override
    public String convert(WrappedGameProfile gameProfile) {
        return Optional.ofNullable(gameProfile.getUUID()).map(UUID::toString).orElse(null);
    }
}
