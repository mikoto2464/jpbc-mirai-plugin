package net.mikoto.pixiv.jpbc.mirai.plugin.event;

import com.alibaba.fastjson.JSON;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import net.mikoto.pixiv.api.pojo.PixivData;
import net.mikoto.pixiv.jpbc.mirai.plugin.JpbcConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static java.net.URLEncoder.encode;
import static net.mikoto.pixiv.jpbc.mirai.plugin.util.HttpUtil.httpGet;
import static net.mikoto.pixiv.jpbc.mirai.plugin.util.HttpUtil.httpGetBytes;

/**
 * @author mikoto
 * @date 2022/1/1 14:50
 */
public class PixivCommandEvent {
    private static final PixivCommandEvent INSTANCE = new PixivCommandEvent();

    private PixivCommandEvent() {
    }

    public static PixivCommandEvent getInstance() {
        return INSTANCE;
    }

    private void entry(@NotNull GroupMessageEvent event) {
        String[] rawMessage = event.getMessage().toString().split(" ");

        if (rawMessage[0].contains("/pixiv")) {
            MessageChainBuilder messages = new MessageChainBuilder()
                    .append(new PlainText("====Mikoto-Pixiv====\n"));

            if (rawMessage.length >= 2) {
                if ("random".equals(rawMessage[1])) {
                    PixivData pixivData = new PixivData();
                    try {
                        pixivData.loadJson(JSON.parseObject(httpGet("http://" + JpbcConfig.INSTANCE.getDISPLAYER_URL() + "/getArtworkByTag?tag=;")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    getImage(event, messages, pixivData);
                    event.getSubject().sendMessage(messages.build());
                } else if ("id".equals(rawMessage[1])) {
                    PixivData pixivData = new PixivData();
                    try {
                        pixivData.loadJson(JSON.parseObject(httpGet("http://" + JpbcConfig.INSTANCE.getDISPLAYER_URL() + "/getArtworkById?artworkId=" + rawMessage[2])));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    getImage(event, messages, pixivData);
                    System.out.println(messages.build().contentToString());
                    event.getSubject().sendMessage(messages.build());
                } else if ("tag".equals(rawMessage[1])) {
                    PixivData pixivData = new PixivData();
                    try {
                        pixivData.loadJson(JSON.parseObject(httpGet("http://" + JpbcConfig.INSTANCE.getDISPLAYER_URL() + "/getArtworkByTag?tag=" + encode(rawMessage[2], "UTF-8"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    getImage(event, messages, pixivData);
                    event.getSubject().sendMessage(messages.build());
                } else {
                    messages
                            .append(new PlainText("?????????????????? ???????????????\n"))
                            .append(new PlainText("??????????????????: /pixiv <arg1> <arg2> ...\n"))
                            .append(new PlainText("?????????api:\n"))
                            .append(new PlainText("/pixiv random | ??????????????????\n"))
                            .append(new PlainText("/pixiv id <??????ID> | ??????ID??????????????????\n"))
                            .append(new PlainText("/pixiv tag <??????> | ??????????????????????????????\n"));
                    event.getSubject().sendMessage(messages.build());
                }
            } else {
                messages
                        .append(new PlainText("?????????????????? ???????????????\n"))
                        .append(new PlainText("??????????????????: /pixiv <arg1> <arg2> ...\n"))
                        .append(new PlainText("?????????api:\n"))
                        .append(new PlainText("/pixiv random | ??????????????????\n"))
                        .append(new PlainText("/pixiv id <??????ID> | ??????ID??????????????????\n"))
                        .append(new PlainText("/pixiv tag <??????> | ??????????????????????????????\n"));
                event.getSubject().sendMessage(messages.build());
            }
        }
    }

    private void getImage(@NotNull GroupMessageEvent event, MessageChainBuilder messages, @NotNull PixivData pixivData) {
        ExternalResource externalResource = null;
        try {
            externalResource = ExternalResource.create(httpGetBytes("https://" + JpbcConfig.INSTANCE.getPICTURE_FORWARD_SERVER() + "/getImage?key=" + JpbcConfig.INSTANCE.getKEY() + "&url=" + pixivData.getIllustUrls().get("regular")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (externalResource != null) {
            Image image = event.getSubject().uploadImage(externalResource);
            String grade = "?????????";
            if (pixivData.getGrading() == 1) {
                grade = "R-18";
            } else if (pixivData.getGrading() == 2) {
                grade = "R-18G";
            }
            messages
                    .append(new At(event.getSender().getId()))
                    .append(new PlainText("???????????????????????????:\n"))
                    .append(new PlainText("????????????: " + pixivData.getArtworkTitle() + "\n"))
                    .append(new PlainText("??????ID: " + pixivData.getArtworkId() + "\n"))
                    .append(new PlainText("?????????: " + pixivData.getAuthorName() + "\n"))
                    .append(new PlainText("??????ID: " + pixivData.getAuthorId() + "\n"))
                    .append(new PlainText("?????????: " + pixivData.getLikeCount() + "\n"))
                    .append(new PlainText("?????????: " + pixivData.getBookmarkCount() + "\n"))
                    .append(new PlainText("????????????: " + grade + "\n"))
                    .append(image);
        } else {
            messages
                    .append(new PlainText("??????????????????\n"));
        }
    }

    public Listener<GroupMessageEvent> registerEvent() {
        return GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::entry);
    }
}
