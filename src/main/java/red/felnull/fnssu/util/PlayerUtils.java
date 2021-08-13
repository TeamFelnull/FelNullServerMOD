package red.felnull.fnssu.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.UUID;

public class PlayerUtils {
    private static final UUID TRANP_ID = UUID.fromString("5c751dd1-0882-4f31-ad61-c4ee928c4595");

    public static List<ServerPlayerEntity> getAllPlayer() {
        return ServerUtils.getMinecraftServer().getPlayerList().getPlayers();
    }

    public static void displayAllPlayer(ITextComponent component, boolean statusBar) {
        for (ServerPlayerEntity player : getAllPlayer()) {
            player.displayClientMessage(component, statusBar);
        }
    }

    public static boolean isTranp(PlayerEntity player) {
        return player.getGameProfile().getId().equals(TRANP_ID);
    }

    public static void displayAllPlayerTranpFlg(ITextComponent component, ITextComponent tranpComponent, boolean statusBar) {
        for (ServerPlayerEntity player : getAllPlayer()) {
            if (PlayerUtils.isTranp(player))
                player.displayClientMessage(tranpComponent, statusBar);
            else
                player.displayClientMessage(component, statusBar);
        }
    }
}
