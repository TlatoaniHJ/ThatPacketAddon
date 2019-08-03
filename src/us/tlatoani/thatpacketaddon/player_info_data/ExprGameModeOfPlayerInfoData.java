package us.tlatoani.thatpacketaddon.player_info_data;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.GameMode;
import us.tlatoani.mundocore.property_expression.EvolvingPropertyExpression;

public class ExprGameModeOfPlayerInfoData extends EvolvingPropertyExpression<PlayerInfoData, GameMode> {

    @Override
    public PlayerInfoData set(PlayerInfoData playerInfoData, GameMode gameMode) {
        return new PlayerInfoData(
                playerInfoData.getProfile(),
                playerInfoData.getLatency(),
                toNative(gameMode),
                playerInfoData.getDisplayName()
        );
    }

    @Override
    public GameMode convert(PlayerInfoData playerInfoData) {
        return playerInfoData.getGameMode().toBukkit();
    }

    public static EnumWrappers.NativeGameMode toNative(GameMode gameMode) {
        if (gameMode == null) {
            return EnumWrappers.NativeGameMode.NOT_SET;
        }
        return EnumWrappers.NativeGameMode.fromBukkit(gameMode);
    }
}
