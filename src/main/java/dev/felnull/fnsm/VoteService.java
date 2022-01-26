package dev.felnull.fnsm;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.function.Supplier;

public enum VoteService {
    NONE("不明", "unknown", () -> null, 0),
    TESTER("Votifier Tester", "minestatus.net test vote", () -> "https://minestatus.net/tools/votifier", 0x45526E),
    JMS("JMS", "minecraft.jp", ServerConfig::getJmsUrl, 0x8CF4E2),
    MONOCRAFT("ものくらふと", "monocraft.net", ServerConfig::getMonocraftUrl, 0x4C7B57);
    private final String name;
    private final String serviceName;
    private final Supplier<String> url;
    private final int color;

    private VoteService(String name, String serviceName, Supplier<String> url, int color) {
        this.name = name;
        this.serviceName = serviceName;
        this.url = url;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUrl() {
        return url.get();
    }

    public static VoteService getByServiceName(String name) {
        for (VoteService service : values()) {
            if (service.getServiceName().equals(name)) {
                return service;
            }
        }
        return NONE;
    }

    public IFormattableTextComponent getComponent(String serviceName) {
        if (serviceName == null)
            serviceName = getServiceName();
        IFormattableTextComponent svm = new StringTextComponent(getName()).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(serviceName).withStyle(TextFormatting.BLUE))).withColor(Color.fromRgb(getColor())));
        if (getUrl() != null && !getUrl().isEmpty())
            svm = svm.withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, getUrl())));
        return svm;
    }

    public static IFormattableTextComponent getPromotion(VoteService last) {
        ItemStack stack = FNSMUtil.createVoteItem();
        IFormattableTextComponent vi = stack.getHoverName().copy().setStyle(Style.EMPTY);
        vi = vi.setStyle(stack.getDisplayName().getStyle());

        if (last == null)
            return new StringTextComponent("").append(JMS.getComponent(null)).append("または").append(MONOCRAFT.getComponent(null)).append("で投票して").append(vi).append("を手に入れよう!").withStyle(TextFormatting.DARK_AQUA);

        VoteService nl = last == JMS ? MONOCRAFT : JMS;
        return new StringTextComponent("").append(nl.getComponent(null)).append("でも投票可能です").withStyle(TextFormatting.DARK_AQUA);
    }
}
