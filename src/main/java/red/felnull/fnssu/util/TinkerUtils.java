package red.felnull.fnssu.util;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class TinkerUtils {
    public static void addTinkerLevel(ItemStack stack, int level) {
        if (stack.getItem() instanceof ToolItem) {
            ToolStack.from(stack).addModifier(TinkerModifiers.creativeUpgrade.get(), level);
        }
    }
}
