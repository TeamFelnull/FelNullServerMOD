package dev.felnull.fnsm.discord;

import dev.felnull.fnsm.FNSMUtil;
import dev.felnull.fnsm.ServerConfig;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.util.text.*;
import org.jetbrains.annotations.NotNull;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.getChannel().getIdLong() != ServerConfig.getChannelId() || e.getAuthor().isBot() || e.getAuthor().isSystem())
            return;
        String message = e.getMessage().getContentDisplay();
        TextComponent com = (TextComponent) new StringTextComponent("[Discord] ").withStyle(TextFormatting.DARK_AQUA);
        ITextComponent n1com = new StringTextComponent("<").withStyle(TextFormatting.WHITE);
        TextComponent nkcom = new StringTextComponent(e.getAuthor().getName());
        if (e.getMember() != null && e.getMember().getColor() != null)
            nkcom.withStyle(Style.EMPTY.withColor(Color.fromRgb(e.getMember().getColor().getRGB())));
        ITextComponent n2com = new StringTextComponent("> ").withStyle(TextFormatting.WHITE);
        ITextComponent mcom = new StringTextComponent(message).withStyle(TextFormatting.WHITE);
        sendServerMessage(com.append(n1com).append(nkcom).append(n2com).append(mcom));
    }


    private static void sendServerMessage(ITextComponent text) {
        FNSMUtil.getServer().submit(() -> FNSMUtil.sendMessageAllPlayer(text));
    }
}
