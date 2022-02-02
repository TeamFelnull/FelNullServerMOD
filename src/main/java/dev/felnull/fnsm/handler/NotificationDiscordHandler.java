package dev.felnull.fnsm.handler;

import com.vexsoftware.votifier.model.Vote;
import dev.felnull.fnsm.FNSMUtil;
import dev.felnull.fnsm.VoteService;
import dev.felnull.fnsm.discord.FNSMDiscord;
import dev.felnull.katyouvotifier.event.VotifierEvent;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.text.DecimalFormat;

public class NotificationDiscordHandler {
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.0");
    private static long lastUpdate;

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent e) {
        if (System.currentTimeMillis() - lastUpdate >= 1000 * 60*3) {
            lastUpdate = System.currentTimeMillis();
            MinecraftServer server = FNSMUtil.getServer();
            for (ServerWorld lv : server.getAllLevels()) {
                if (DimensionType.OVERWORLD_LOCATION.location().equals(lv.dimension().location())) {
                    double tps = FNSMUtil.getTPS(lv);
                    if (tps >= 0)
                        FNSMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing(String.format("2022春季サーバー(%sTPS)", TIME_FORMATTER.format(FNSMUtil.getTPS(lv)))));
                    else
                        FNSMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing("2022春季サーバー"));
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent e) {
        FNSMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing("2022春季サーバー"));
        FNSMDiscord.sendMessage(":fish: サーバーが開きました！");
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent e) {
        FNSMDiscord.sendMessageByPlayerAsync(e.getPlayer(), e.getMessage());
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (e.getEntityLiving().level.isClientSide()) return;
        if (e.getEntityLiving() instanceof PlayerEntity) {
            ITextComponent text = e.getSource().getLocalizedDeathMessage(e.getEntityLiving());
            FNSMDiscord.sendMessageByPlayerAsync((PlayerEntity) e.getEntityLiving(), text.getString());
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntityLiving().level.isClientSide()) return;
        FNSMDiscord.sendMessageByPlayerAsync(e.getPlayer(), "サーバーに参加しました");
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntityLiving().level.isClientSide()) return;
        FNSMDiscord.sendMessageByPlayerAsync(e.getPlayer(), "サーバーから退出しました");
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent e) {
        if (e.getPlayer().level.isClientSide()) return;
        if (e.getAdvancement().getDisplay() != null && e.getAdvancement().getDisplay().shouldAnnounceChat()) {
            FNSMDiscord.sendMessageByPlayerAsync(e.getPlayer(), String.format("**%s** を達成した", e.getAdvancement().getDisplay().getTitle().getString()));
        }
    }

    @SubscribeEvent
    public static void onVote(VotifierEvent e) {
        Vote v = e.getVote();
        ServerPlayerEntity player = FNSMUtil.getServer().getPlayerList().getPlayerByName(v.getUsername());
        if (player != null) {
            VoteService vs = VoteService.getByServiceName(v.getServiceName());
            FNSMDiscord.sendMessageByPlayerAsync(player, String.format("**%s** で投票しました", vs.getName()));
        }
    }

    @SubscribeEvent
    public static void onServerStop(FMLServerStoppingEvent e) {
        FNSMDiscord.sendMessage(":fishing_pole_and_fish: サーバーが停止しました！");
        FNSMDiscord.setStatus(OnlineStatus.IDLE, Activity.watching("サーバー停止"));
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent e) {
        FNSMDiscord.shutdown();
    }
}
