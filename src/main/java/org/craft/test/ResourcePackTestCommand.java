package org.craft.test;

import me.rukon0621.rkutils.bukkit.command.AbstractCommand;
import me.rukon0621.rkutils.bukkit.util.CommandUtil;
import me.rukon0621.rkutils.bukkit.util.Msg;
import me.rukon0621.rkutils.bukkit.yml.Configure;
import me.rukon0621.rkutils.util.FileUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcePackTestCommand extends AbstractCommand {
    private final BossBar bar = BossBar.bossBar(Component.empty(), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

    private final Map<String, String> configMap = new HashMap<>();

    public ResourcePackTestCommand() {
        super(CraftTest.getInst(), "repacktest");
        addArgument("액션바", "/리팩테스트 리로드 - Config를 리로드합니다.");
        addArgument("액션바", "/리팩테스트 액션바 <메세지>");
        addArgument("보스바", "/리팩테스트 보스바 <메세지> - 메세지에 X를 넣으면 보스바가 사라집니다.");
        addArgument("메세지", "/리팩테스트 메세지 <메세지>");
        addArgument("타이틀", "/리팩테스트 타이틀 <메인타이틀> [서브타이틀] [FadeIn] [Stay] [FadeOut] - 시간 단위는 tick이며 \"_\"는 띄어쓰기로 대체할 수 있습니다. ");
        reloadConfig();
    }

    public void reloadConfig() {
        Configure config = new Configure(FileUtil.getOuterPluginFolder() + "/resourcePackTest.yml");
        configMap.clear();
        for (String key : config.getConfig().getKeys(false)) {
            configMap.put(key, StringEscapeUtils.unescapeJava(config.getConfig().getString(key)));
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args.length < 2) {
            if(args[0].equals("보스바")) {
                sender.hideBossBar(bar);
                Msg.send(sender, "보스바를 숨겼습니다.");
                return;
            }
            usages(sender);
            return;
        }
        String message = StringEscapeUtils.unescapeJava(CommandUtil.mergeArgs(args, 1));
        if(message.startsWith("config:")) {
            String configKey = message.substring("config:".length()).trim();
            if(configMap.containsKey(configKey)) {
                message = configMap.get(configKey);
            } else {
                Msg.warn(sender, "Config에 해당 키가 존재하지 않습니다.");
                return;
            }
        }

        if (args[0].equals("리로드")) {
            reloadConfig();
            Msg.send(sender, "Config를 리로드했습니다.");
        }
        else if(args[0].equals("액션바")) {
            sender.sendActionBar(Msg.mm(message));
        }
        else if(args[0].equals("보스바")) {
            bar.name(Msg.mm(message));
            sender.showBossBar(bar);
        }
        else if(args[0].equals("메세지")) {
            Msg.send(sender, message);
        }
        else if(args[0].equals("타이틀")) {
            int fadeIn, stay, fadeOut;
            String mainTitle, subTitle;

            if(args[1].startsWith("config:")) {
                String configKey = args[1].substring("config:".length()).trim();
                if(configMap.containsKey(configKey)) {
                    mainTitle = configMap.get(configKey);
                } else {
                    Msg.warn(sender, "Config에 해당 키가 존재하지 않습니다.");
                    return;
                }
            } else {
                mainTitle = StringEscapeUtils.unescapeJava(args[1]).replaceAll("_", " ").trim();
            }

            if(args.length > 2) {
                if(args[2].startsWith("config:")) {
                    String configKey = args[2].substring("config:".length()).trim();
                    if(configMap.containsKey(configKey)) {
                        subTitle = configMap.get(configKey);
                    } else {
                        Msg.warn(sender, "Config에 해당 키가 존재하지 않습니다.");
                        return;
                    }
                } else {
                    subTitle = StringEscapeUtils.unescapeJava(args[2]).replaceAll("_", " ").trim();
                }
            } else {
                subTitle = "";
            }

            try {
                fadeIn = args.length > 3 ? Integer.parseInt(args[3]) : 10;
                stay = args.length > 4 ? Integer.parseInt(args[4]) : 20;
                fadeOut = args.length > 5 ? Integer.parseInt(args[5]) : 10;
            } catch (Exception e) {
                Msg.warn(sender, "FadeIn, Stay, FadeOut은 숫자여야 합니다. 기본값으로 설정됩니다.");
                fadeIn = 10;
                stay = 20;
                fadeOut = 10;
            }

            Title title = Title.title(
                    Msg.mm(mainTitle),
                    Msg.mm(subTitle),
                    Title.Times.times(
                            Duration.ofMillis(fadeIn * 50L),
                            Duration.ofMillis(stay * 50L),
                            Duration.ofMillis(fadeOut * 50L)
                    )
            );
            sender.showTitle(title);
        }
    }
}
