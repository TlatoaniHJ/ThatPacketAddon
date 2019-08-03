package us.tlatoani.thatpacketaddon.game_profile;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import us.tlatoani.mundocore.property_expression.EvolvingPropertyExpression;
import us.tlatoani.thatpacketaddon.skin.Skin;

public class ExprThatPacketAddonSkinOfProfile extends EvolvingPropertyExpression<WrappedGameProfile, Skin> {
    @Override
    public WrappedGameProfile reset(WrappedGameProfile gameProfile) {
        return new WrappedGameProfile(gameProfile.getUUID(), gameProfile.getName());
    }

    @Override
    public WrappedGameProfile set(WrappedGameProfile gameProfile, Skin skin) {
        if (skin == null) {
            return reset(gameProfile);
        } else {
            return skin.toGameProfile(gameProfile.getUUID(), gameProfile.getName());
        }
    }

    @Override
    public Skin convert(WrappedGameProfile gameProfile) {
        return Skin.fromGameProfile(gameProfile);
    }
}
