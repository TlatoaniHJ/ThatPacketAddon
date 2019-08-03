package us.tlatoani.thatpacketaddon.player_info_data;

import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.property_expression.EvolvingPropertyExpression;

public class ExprDisplayNameOfPlayerInfoData extends EvolvingPropertyExpression<PlayerInfoData, JSONObject> {
    @Override
    public PlayerInfoData set(PlayerInfoData playerInfoData, JSONObject jsonObject) {
        return new PlayerInfoData(
                playerInfoData.getProfile(),
                playerInfoData.getLatency(),
                playerInfoData.getGameMode(),
                WrappedChatComponent.fromJson(jsonObject.toJSONString())
        );
    }

    @Override
    public JSONObject convert(PlayerInfoData playerInfoData) {
        try {
            if (playerInfoData.getDisplayName() == null) {
                return null;
            }
            return (JSONObject) new JSONParser().parse(playerInfoData.getDisplayName().getJson());
        } catch (ParseException e) {
            Logging.debug(this, e);
            return null;
        }
    }
}
