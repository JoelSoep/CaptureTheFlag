package me.joelsoep.ctf.discord;

import com.eduardomcb.discord.webhook.WebhookClient;
import com.eduardomcb.discord.webhook.WebhookManager;
import com.eduardomcb.discord.webhook.models.Message;
import me.joelsoep.ctf.file.LocalConfig;

public final class DiscordWebhook {

    private static final WebhookManager manager = new WebhookManager();

    public static void init() {
        manager.setChannelUrl(LocalConfig.DISCORD_WEBHOOK_URL);
    }

    public static WebhookClient.Callback sendMessage(WebhookClient.Callback callback, String content) {
        Message message = new Message();
        message.setUsername("CTF Transactions");
        message.setAvatarUrl(LocalConfig.DISCORD_WEBHOOK_AVATAR_URL);
        message.setContent(content);

        manager.setListener(callback);
        manager.setMessage(message);
        manager.exec();
        return callback;
    }
}
