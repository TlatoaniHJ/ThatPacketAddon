package us.tlatoani.thatpacketaddon.player_info_data;

import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import us.tlatoani.mundocore.property_expression.EvolvingPropertyExpression;

public class ExprProfileOfPlayerInfoData extends EvolvingPropertyExpression<PlayerInfoData, WrappedGameProfile> {

    @Override
    public PlayerInfoData set(PlayerInfoData playerInfoData, WrappedGameProfile gameProfile) {
        return new PlayerInfoData(
                gameProfile,
                playerInfoData.getLatency(),
                playerInfoData.getGameMode(),
                playerInfoData.getDisplayName()
        );
    }

    @Override
    public WrappedGameProfile convert(PlayerInfoData playerInfoData) {
        return playerInfoData.getProfile();
    }
}
