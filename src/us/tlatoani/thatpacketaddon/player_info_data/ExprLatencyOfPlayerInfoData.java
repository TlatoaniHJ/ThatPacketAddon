package us.tlatoani.thatpacketaddon.player_info_data;

import com.comphenix.protocol.wrappers.PlayerInfoData;
import us.tlatoani.mundocore.property_expression.EvolvingPropertyExpression;

public class ExprLatencyOfPlayerInfoData extends EvolvingPropertyExpression<PlayerInfoData, Number> {
    @Override
    public PlayerInfoData set(PlayerInfoData playerInfoData, Number number) {
        return new PlayerInfoData(
                playerInfoData.getProfile(),
                number.intValue(),
                playerInfoData.getGameMode(),
                playerInfoData.getDisplayName()
        );
    }

    @Override
    public Number convert(PlayerInfoData playerInfoData) {
        return playerInfoData.getLatency();
    }
}
