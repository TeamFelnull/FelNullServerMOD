package dev.felnull.fnsm;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vexsoftware.votifier.model.Vote;
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

public class ServerHandler {
    private static final Gson GSON = new Gson();
    private static final Map<String, Integer> VOTES = new HashMap<>();
    private static final Random random = new Random();
    private static long lastVote;

    @SubscribeEvent
    public static void onVote(VotifierEvent e) {
        Vote v = e.getVote();
        IFormattableTextComponent svm = VoteService.getByServiceName(v.getServiceName()).getComponent(v.getServiceName());
        IFormattableTextComponent msg = new StringTextComponent(v.getUsername() + "さんが").append(svm).append("で投票しました!").withStyle(TextFormatting.YELLOW);
        FNSMUtil.sendMessageAllPlayer(msg);

        if (!VOTES.containsKey(e.getVote().getUsername())) {
            VOTES.put(e.getVote().getUsername(), 1);
        } else {
            VOTES.put(e.getVote().getUsername(), VOTES.get(e.getVote().getUsername()) + 1);
        }

        if (System.currentTimeMillis() - lastVote >= 10000) {
            lastVote = System.currentTimeMillis();
            FNSMUtil.sendMessageAllPlayer(VoteService.getPromotion(null));
        }
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
                if (random.nextInt(30) == 0) {
                    added++;
                }
            }
            if (added >= 1)
                e.player.displayClientMessage(new StringTextComponent("投票ありがとナス！").withStyle(TextFormatting.DARK_PURPLE), false);
            else
                e.player.displayClientMessage(new StringTextComponent("投票ありがとうございます！").withStyle(TextFormatting.GREEN), false);

            // e.player.playNotifySound(SoundEvents.ANVIL_PLACE, SoundCategory.PLAYERS, 10, 1);

            MusicManager.getInstance().start(FelNullServerMOD.hqmCompNBS, e.player::position, () -> e.player.level.dimension().location());

            for (int i = 0; i < cont + added; i++) {
                FNSMUtil.giveItem(e.player, FNSMUtil.createVoteItem());
            }

            for (int i = 0; i < 30; i++) {
                Vector3d vec3d = new Vector3d(((double) random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                Vector3d pls = new Vector3d(e.player.getX() + ((double) random.nextFloat() - 0.5D), e.player.getY() + ((double) random.nextFloat() - 0.5D), e.player.getZ() + ((double) random.nextFloat() - 0.5D));
                ((ServerWorld) e.player.level).sendParticles(ParticleTypes.HAPPY_VILLAGER, pls.x, pls.y, pls.z, 50, vec3d.x, vec3d.y + 0.05D, vec3d.z, 2.0D);
            }
            VOTES.remove(name);
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
    }

    @SubscribeEvent
    public static void onServerStop(FMLServerStoppingEvent e) {
        File vfile = new File(FelNullServerMOD.MODID, "votifier.json");
        vfile.getParentFile().mkdirs();
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

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent e) {
        // MusicCommand.reg(e.getDispatcher());
    }
}
