package red.felnull.fnssu.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ItemUtils {
    public static ItemStack volteItem;

    public static ItemStack getVItem() {
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("appliedenergistics2:singularity")));
        stack.setHoverName(new StringTextComponent("投票玉").withStyle(Style.EMPTY.withItalic(false)));
        Map<Enchantment, Integer> encs = new HashMap<>();
        encs.put(Enchantments.RIPTIDE, 0);
        EnchantmentHelper.setEnchantments(encs, stack);
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean("Unbreakable", true);
        tag.putInt("HideFlags", 5);
        return stack;
    }

    public static ITextComponent getVItemComponent() {
        if (volteItem == null)
            volteItem = getVItem();
        return volteItem.getDisplayName();
    }

    public static void giveItem(PlayerEntity player, ItemStack stack) {
        if (!player.addItem(stack))
            player.drop(stack, false, true);
    }

}
