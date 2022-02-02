package dev.felnull.fnsm.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vexsoftware.votifier.model.Vote;
import dev.felnull.fnnbs.NBS;
import dev.felnull.fnsm.FNSMUtil;
import dev.felnull.fnsm.FelNullServerMOD;
import dev.felnull.fnsm.VoteService;
import dev.felnull.fnsm.commands.MusicCommand;
import dev.felnull.fnsm.music.MusicManager;
import dev.felnull.katyouvotifier.event.VotifierEvent;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ServerHandler {
    private static final Gson GSON = new Gson();
    private static final Map<String, Integer> VOTES = new HashMap<>();
    private static final Map<String, Integer> VOTES_COUNT = new HashMap<>();
    private static final Random random = new Random();
    private static long lastVote;
    private static long lastPr;

    @SubscribeEvent
    public static void onVote(VotifierEvent e) {
        Vote v = e.getVote();
        addVoteCount(v.getUsername());
        IFormattableTextComponent svm = VoteService.getByServiceName(v.getServiceName()).getComponent(v.getServiceName());
        IFormattableTextComponent msg = new StringTextComponent(v.getUsername() + "さんが").append(svm).append("で投票しました!").withStyle(TextFormatting.YELLOW);
        FNSMUtil.sendMessageAllPlayer(msg);

        if (!VOTES.containsKey(e.getVote().getUsername())) {
            VOTES.put(e.getVote().getUsername(), 1);
        } else {
            VOTES.put(e.getVote().getUsername(), VOTES.get(e.getVote().getUsername()) + 1);
        }

        /*if (System.currentTimeMillis() - lastVote >= 1000 * 60) {
            lastVote = System.currentTimeMillis();
            IFormattableTextComponent pr = VoteService.getPromotion(null);

            FNSMUtil.executionAllPlayer(n -> {
                if (!n.getGameProfile().getName().equals(v.getUsername()))
                    n.displayClientMessage(pr, false);
            });
        }*/
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getPlayer().level.isClientSide())
            return;
        e.getPlayer().displayClientMessage(VoteService.getPromotion(null), false);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.player.level.isClientSide())
            return;
        String name = e.player.getGameProfile().getName();
        if (VOTES.containsKey(name)) {
            int cont = VOTES.get(name);
            if (cont <= 0)
                return;

            int added = 0;
            for (int i = 0; i < cont; i++) {
                if (random.nextInt(19) == 0) {
                    added++;
                }
            }
            NBS nbs = random.nextInt(6) == 0 ? FelNullServerMOD.godHand : FelNullServerMOD.hqmCompNBS;
            if (added >= 1) {
                e.player.displayClientMessage(new StringTextComponent("投票ありがとナス！").withStyle(TextFormatting.DARK_PURPLE), false);
                nbs = FelNullServerMOD.worldOfTono;
            } else {
                e.player.displayClientMessage(new StringTextComponent("投票ありがとうございます！").withStyle(TextFormatting.GREEN), false);
            }

            MusicManager.getInstance().start(nbs, e.player::position, () -> e.player.level.dimension().location(), UUID.randomUUID(), false, 0);

            for (int i = 0; i < cont + added; i++) {
                FNSMUtil.giveItem(e.player, FNSMUtil.createVoteItem());
            }

            for (int i = 0; i < 30; i++) {
                Vector3d vec3d = new Vector3d(((double) random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                Vector3d pls = new Vector3d(e.player.getX() + ((double) random.nextFloat() - 0.5D), e.player.getY() + ((double) random.nextFloat() - 0.5D), e.player.getZ() + ((double) random.nextFloat() - 0.5D));
                ((ServerWorld) e.player.level).sendParticles(ParticleTypes.LARGE_SMOKE, pls.x, pls.y, pls.z, 50, vec3d.x, vec3d.y + 0.05D, vec3d.z, 2.0D);
            }
            VOTES.remove(name);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent e) {
        if (e.side.isServer() && e.phase == TickEvent.Phase.START) {
            MusicManager.getInstance().tick();
        }
    }

    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent e) {
        File vfile = new File(FelNullServerMOD.MODID, "votifier.json");
        if (vfile.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new BufferedReader(new FileReader(vfile)), JsonObject.class);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
                VOTES.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }

        File vcfile = new File(FelNullServerMOD.MODID, "votifier_count.json");
        if (vcfile.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new BufferedReader(new FileReader(vcfile)), JsonObject.class);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
                VOTES_COUNT.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }

        File mfile = new File(FelNullServerMOD.MODID, "musics.json");
        if (mfile.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new BufferedReader(new FileReader(mfile)), JsonObject.class);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
                MusicManager.getInstance().getMusicURLs().put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }

    private static void addVoteCount(String userName) {
        Integer ct = VOTES_COUNT.get(userName);
        if (ct == null)
            ct = 0;
        ct++;
        VOTES_COUNT.put(userName, ct);
    }

    @SubscribeEvent
    public static void onServerStop(FMLServerStoppingEvent e) {
        File vfile = new File(FelNullServerMOD.MODID, "votifier.json");
        vfile.getParentFile().mkdirs();
        {
            JsonObject jo = new JsonObject();
            for (Map.Entry<String, Integer> stringIntegerEntry : VOTES.entrySet()) {
                jo.addProperty(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
            }
            try {
                Files.write(vfile.toPath(), GSON.toJson(jo).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        File vcfile = new File(FelNullServerMOD.MODID, "votifier_count.json");
        vcfile.getParentFile().mkdirs();
        {
            JsonObject jo = new JsonObject();
            for (Map.Entry<String, Integer> stringIntegerEntry : VOTES_COUNT.entrySet()) {
                jo.addProperty(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
            }
            try {
                Files.write(vcfile.toPath(), GSON.toJson(jo).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        File mfile = new File(FelNullServerMOD.MODID, "musics.json");
        mfile.getParentFile().mkdirs();
        {
            JsonObject jo = new JsonObject();
            for (Map.Entry<String, String> entry : MusicManager.getInstance().getMusicURLs().entrySet()) {
                jo.addProperty(entry.getKey(), entry.getValue());
            }
            try {
                Files.write(mfile.toPath(), GSON.toJson(jo).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent e) {
        MusicCommand.reg(e.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;
        if (System.currentTimeMillis() - lastPr >= 1000 * 60 * 60) {
            lastPr = System.currentTimeMillis();
            IFormattableTextComponent pr = VoteService.getPromotion(null);
            FNSMUtil.sendMessageAllPlayer(pr);
        }
    }
}
