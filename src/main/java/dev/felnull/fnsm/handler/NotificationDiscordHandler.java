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
import java.text.SimpleDateFormat;

public class NotificationDiscordHandler {
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.0");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日hh時mm分");
    private static long lastUpdate;
    private static long startTime;

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent e) {
        if (System.currentTimeMillis() - lastUpdate >= 1000 * 60 * 3) {
            lastUpdate = System.currentTimeMillis();
            FNSMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing(createActivityMessage(FNSMUtil.getServer())));
            //  FNSMDiscord.setChannelMessage(String.format("現在の状態: %s/%s人のオンラインプレイヤー | %sからオンライン", server.getPlayerCount(), server.getMaxPlayers(), timeFormat.format(new Date(startTime))));
        }
    }

    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent e) {
        FNSMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing("2022春季サーバー"));
        FNSMDiscord.sendMessage(":fish: サーバーが開きました！");
        lastUpdate = System.currentTimeMillis() - (1000 * 60 * 2);
        startTime = System.currentTimeMillis();
        FNSMDiscord.setStatus(OnlineStatus.ONLINE, Activity.playing(createActivityMessage(e.getServer())));
        //FNSMDiscord.setChannelMessage(String.format("現在の状態: %s/%s人のオンラインプレイヤー | %sからオンライン", server.getPlayerCount(), server.getMaxPlayers(), timeFormat.format(new Date(startTime))));
    }

    private static String createActivityMessage(MinecraftServer server) {
        ServerWorld ovlv = null;
        for (ServerWorld lv : server.getAllLevels()) {
            if (DimensionType.OVERWORLD_LOCATION.location().equals(lv.dimension().location())) {
                ovlv = lv;
                break;
            }
        }
        if (ovlv != null) {
            double tpsc = FNSMUtil.getTPS(ovlv);
            if (tpsc >= 0)
                return String.format("2022春季サーバー(%sTPS %s/%s人)", TIME_FORMATTER.format(tpsc), server.getPlayerCount(), server.getMaxPlayers());
        }
        return String.format("2022春季サーバー(%s/%s人)", server.getPlayerCount(), server.getMaxPlayers());
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
        VoteService vs = VoteService.getByServiceName(v.getServiceName());
        if (player != null) {
            FNSMDiscord.sendMessageByPlayerAsync(player, String.format("**%s** で投票しました", vs.getName()));
        } else {
            FNSMDiscord.sendMessage(String.format("%sさんが **%s** で投票しました", v.getUsername(), vs.getName()));
        }
    }

    @SubscribeEvent
    public static void onServerStop(FMLServerStoppingEvent e) {
        //    FNSMDiscord.setChannelMessage("起動していません");
        FNSMDiscord.sendMessage(":fishing_pole_and_fish: サーバーが停止しました！");
        FNSMDiscord.setStatus(OnlineStatus.IDLE, Activity.watching("サーバー停止"));
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent e) {
        FNSMDiscord.shutdown();
    }
}
