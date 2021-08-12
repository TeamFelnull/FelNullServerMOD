package red.felnull.fnssu;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vexsoftware.votifier.model.Vote;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import red.felnull.fnssu.commands.MusicCommand;
import red.felnull.fnssu.music.MusicManager;
import red.felnull.fnssu.util.ItemUtils;
import red.felnull.fnssu.util.ServerUtils;
import red.felnull.katyouvotifier.event.VotifierEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ServerHandler {
    private static final Map<String, Integer> VOLS = new HashMap<>();
    private static final Random random = new Random();
    private static final Gson GSON = new Gson();
    private static long itemDeleteTime;
    private static boolean itemDeleteWaring;
    private static int lastCuntdown = 10;
    private static Set<ResourceLocation> noDeleteItems = new HashSet<>();

    static {
        noDeleteItems.add(new ResourceLocation("appliedenergistics2", "purified_certus_quartz_crystal"));
        noDeleteItems.add(new ResourceLocation("appliedenergistics2", "purified_nether_quartz_crystal"));
        noDeleteItems.add(new ResourceLocation("appliedenergistics2", "purified_fluix_crystal"));
        noDeleteItems.add(new ResourceLocation("appliedenergistics2", "nether_quartz_seed"));
        noDeleteItems.add(new ResourceLocation("appliedenergistics2", "certus_crystal_seed"));
        noDeleteItems.add(new ResourceLocation("appliedenergistics2", "fluix_crystal_seed"));
    }

    @SubscribeEvent
    public static void onVotifier(VotifierEvent e) {
        if (!VOLS.containsKey(e.getVote().getUsername())) {
            VOLS.put(e.getVote().getUsername(), 1);
        } else {
            VOLS.put(e.getVote().getUsername(), VOLS.get(e.getVote().getUsername()) + 1);
        }

        Vote v = e.getVote();
        boolean trflg = v.getUsername().equals("toranpfan6433");
        Voltifiers sv = Voltifiers.getByServiceName(v.getServiceName());
        IFormattableTextComponent voltsv = new StringTextComponent(sv == Voltifiers.NON ? v.getServiceName() : sv.getName());

        if (sv != Voltifiers.NON)
            voltsv = voltsv.withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, sv.getUrl())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(sv.getServiceName()).withStyle(TextFormatting.BLUE))));

        IFormattableTextComponent component = new StringTextComponent(v.getUsername()).append(" さんが ").append(voltsv).append(" で投票しました！").withStyle(trflg ? TextFormatting.DARK_PURPLE : TextFormatting.YELLOW);

        if (trflg)
            component.append("じゃあ君も投票しようか");

        ServerUtils.getMinecraftServer().getPlayerList().getPlayers().forEach(n -> n.displayClientMessage(component, false));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (!e.player.level.isClientSide) {
            String name = e.player.getGameProfile().getName();
            if (VOLS.containsKey(name)) {
                int cont = VOLS.get(name);
                if (cont <= 0)
                    return;
                int added = 0;
                if (random.nextInt(10) == 0) {
                    added++;
                }
                if (e.player.getGameProfile().getId().equals(UUID.fromString("5c751dd1-0882-4f31-ad61-c4ee928c4595")))
                    e.player.displayClientMessage(new StringTextComponent("投票ありがとナス！").withStyle(TextFormatting.DARK_PURPLE), false);
                else
                    e.player.displayClientMessage(new StringTextComponent("投票ありがとうございます！").withStyle(TextFormatting.GREEN), false);

                e.player.playNotifySound(SoundEvents.ANVIL_PLACE, SoundCategory.PLAYERS, 10, 1);
                for (int i = 0; i < cont + added; i++) {
                    ItemUtils.giveItem(e.player, ItemUtils.getVItem());
                }
                //      TinkerUtils.addTinkerLevel(e.player.getMainHandItem(), cont);
                for (int i = 0; i < 30; i++) {
                    Vector3d vec3d = new Vector3d(((double) random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                    Vector3d pls = new Vector3d(e.player.getX() + ((double) random.nextFloat() - 0.5D), e.player.getY() + ((double) random.nextFloat() - 0.5D), e.player.getZ() + ((double) random.nextFloat() - 0.5D));
                    ((ServerWorld) e.player.level).sendParticles(ParticleTypes.TOTEM_OF_UNDYING, pls.x, pls.y, pls.z, 50, vec3d.x, vec3d.y + 0.05D, vec3d.z, 2.0D);
                }
                VOLS.remove(name);
            }
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent e) {
        if (e.side.isServer() && e.phase == TickEvent.Phase.START) {
            MusicManager.getInstance().tick();

            if (itemDeleteTime == 0)
                itemDeleteTime = System.currentTimeMillis();

            long deleteTime = System.currentTimeMillis() - itemDeleteTime;

            if (!itemDeleteWaring && deleteTime >= 14 * 60 * 1000) {
                itemDeleteWaring = true;
                ServerUtils.getMinecraftServer().getPlayerList().getPlayers().forEach(n -> {
                    if (n.getGameProfile().getId().equals(UUID.fromString("5c751dd1-0882-4f31-ad61-c4ee928c4595")))
                        n.displayClientMessage(new StringTextComponent("１分後にドロップしているアイテムが壊れるわ").withStyle(TextFormatting.DARK_RED), false);
                    else
                        n.displayClientMessage(new StringTextComponent("１分後にドロップしているアイテムが削除されます").withStyle(TextFormatting.DARK_RED), false);
                });
            }
            int currentLastct = (int) (((15f * 60f * 1000f) - (float) deleteTime) / 1000f);
            if (currentLastct <= 10) {
                if (lastCuntdown > currentLastct) {
                    ServerUtils.getMinecraftServer().getPlayerList().getPlayers().forEach(n -> {
                        n.displayClientMessage(new StringTextComponent(String.format("%s秒後にドロップしているアイテムが削除されます", currentLastct)).withStyle(TextFormatting.DARK_RED), false);
                    });
                    lastCuntdown = currentLastct;
                }
            }

            if (deleteTime >= 15 * 60 * 1000) {
                itemDeleteTime = 0;
                itemDeleteWaring = false;
                lastCuntdown = 10;

                int cont = 0;

                for (ServerWorld level : ServerUtils.getMinecraftServer().getAllLevels()) {
                    for (Entity entity : level.getAllEntities()) {
                        if (entity instanceof ItemEntity) {
                            ItemEntity itemEntity = (ItemEntity) entity;
                            ItemStack stack = itemEntity.getItem();
                            if (!noDeleteItems.contains(stack.getItem().getRegistryName())) {
                                entity.hurt(DamageSource.OUT_OF_WORLD, 114514F);
                                cont++;
                            }
                        }
                    }
                }

                int finalCont = cont;
                ServerUtils.getMinecraftServer().getPlayerList().getPlayers().forEach(n -> {
                    n.displayClientMessage(new StringTextComponent(String.format("%s 個のドロップしているアイテムが削除されました", finalCont)).withStyle(TextFormatting.GOLD), false);
                });
            }

        }
    }

    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent e) {

        itemDeleteTime = 0;
        itemDeleteWaring = false;
        lastCuntdown = 10;

        File file = new File("touhyou.json");
        if (file.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new String(Files.readAllBytes(file.toPath())), JsonObject.class);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
                VOLS.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }

        File musicFile = new File("musicurls.json");
        if (musicFile.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new String(Files.readAllBytes(musicFile.toPath())), JsonObject.class);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
                MusicManager.getInstance().getUrlCach().put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }


    @SubscribeEvent
    public static void onServerStop(FMLServerStoppingEvent e) {

        itemDeleteTime = 0;
        itemDeleteWaring = false;
        lastCuntdown = 10;

        MusicManager.getInstance().stopAll();
        {
            File file = new File("touhyou.json");
            JsonObject jo = new JsonObject();
            for (Map.Entry<String, Integer> stringIntegerEntry : VOLS.entrySet()) {
                jo.addProperty(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
            }
            try {
                Files.write(file.toPath(), GSON.toJson(jo).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        {
            File file = new File("musicurls.json");
            JsonObject jo = new JsonObject();
            for (Map.Entry<String, String> stringIntegerEntry : MusicManager.getInstance().getUrlCach().entrySet()) {
                jo.addProperty(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
            }
            try {
                Files.write(file.toPath(), GSON.toJson(jo).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }


        MusicManager.getInstance().getUrlCach().clear();
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent e) {
        MusicCommand.reg(e.getDispatcher());
    }
}
