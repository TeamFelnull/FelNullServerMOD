package dev.felnull.fnsm;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FNSMUtil {
    public static MinecraftServer getServer() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    public static void executionAllPlayer(Consumer<ServerPlayerEntity> consumer) {
        getServer().getPlayerList().getPlayers().forEach(consumer);
    }

    public static void sendMessageAllPlayer(ITextComponent component) {
        executionAllPlayer(n -> n.displayClientMessage(component, false));
    }

    public static ItemStack createVoteItem() {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("appliedenergistics2:singularity"));
        if (item == null || item == Items.AIR)
            item = Items.DIAMOND;
        ItemStack stack = new ItemStack(item);

        stack.setHoverName(new StringTextComponent("投票玉").withStyle(Style.EMPTY.withItalic(false).withColor(Color.fromRgb(0x32cd32))));
        Map<Enchantment, Integer> encs = new HashMap<>();
        encs.put(Enchantments.RIPTIDE, 0);
        EnchantmentHelper.setEnchantments(encs, stack);
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean("Unbreakable", true);
        tag.putInt("HideFlags", 5);

        CompoundNBT display = tag.getCompound("display");
        ListNBT lore = display.getList("Lore", 8);

        IFormattableTextComponent com = new StringTextComponent("お、先輩こいつ玉とか言いだしましたよ？").withStyle(Style.EMPTY.withColor(Color.fromRgb(0x364364)));
        IFormattableTextComponent com2 = new StringTextComponent("やっぱ好きなんですねぇ").withStyle(Style.EMPTY.withColor(Color.fromRgb(0x364364)));
        lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(com)));
        lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(com2)));
        display.put("Lore", lore);
        tag.put("display", display);
        return stack;
    }

    public static void giveItem(PlayerEntity player, ItemStack stack) {
        if (!player.addItem(stack))
            player.drop(stack, false, true);
    }
}
