package net.mikoto.pixiv.jpbc.mirai.plugin.bot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.ExternalResource;
import net.mikoto.pixiv.api.pojo.PixivData;
import net.mikoto.pixiv.jpbc.mirai.plugin.JpbcConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static net.mikoto.pixiv.jpbc.mirai.plugin.util.HttpUtil.httpGetBytes;

/**
 * @author mikoto
 * @date 2021/12/26 7:16
 */
public class BotManager {
    /**
     * 单例
     */
    private static final BotManager INSTANCE = new BotManager();

    private final Map<Integer, Bot> botMap = new HashMap<>();

    /**
     * 获取当前实例
     *
     * @return 一个BootManager对象
     */
    public static BotManager getInstance() {
        return INSTANCE;
    }

    /**
     * Create bot and login.
     *
     * @param qq       Bot qq id.
     * @param password Bot qq password.
     * @return A bot object.
     */
    public Bot createBot(long qq, String password) {
        Bot bot = BotFactory.INSTANCE.newBot(qq, password, new BotConfiguration() {{
            setProtocol(MiraiProtocol.ANDROID_PHONE);
        }});
        bot.login();
        botMap.put((int) qq, bot);
        return botMap.get((int) qq);
    }

    public Image uploadPixivData(long qq, long groupId, @NotNull PixivData pixivData) {
        ExternalResource externalResource = null;
        try {
            externalResource = ExternalResource.create(httpGetBytes("https://" + JpbcConfig.INSTANCE.getPICTURE_FORWARD_SERVER() + "/getImage?key=" + JpbcConfig.INSTANCE.getKEY() + "&url=" + pixivData.getIllustUrls().get("regular")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bot bot = botMap.get((int) qq);

        if (externalResource == null) {
            return null;
        } else {
            Group group = bot.getGroup(groupId);
            if (group == null) {
                return null;
            } else {
                return group.uploadImage(externalResource);
            }
        }
    }
}
