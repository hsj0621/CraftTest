package org.craft.test;

import me.rukon0621.rkutils.bukkit.command.AbstractCommand;
import me.rukon0621.rkutils.bukkit.util.CommandUtil;
import me.rukon0621.rkutils.bukkit.util.Msg;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.List;

public class ResourcePackTestCommand extends AbstractCommand {
    private final BossBar bar = BossBar.bossBar(Component.empty(), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

    public ResourcePackTestCommand() {
        super(CraftTest.getInst(), "repacktest");
        addArgument("액션바", "/리팩테스트 액션바 <메세지>");
        addArgument("보스바", "/리팩테스트 보스바 <메세지> - 메세지에 X를 넣으면 보스바가 사라집니다.");
        addArgument("메세지", "/리팩테스트 메세지 <메세지>");
        addArgument("타이틀", "/리팩테스트 타이틀 <메인타이틀> [서브타이틀] [FadeIn] [Stay] [FadeOut] - 시간 단위는 tick이며 \"_\"는 띄어쓰기로 대체할 수 있습니다. ");
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

        if(args[0].equals("액션바")) {
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

            mainTitle = StringEscapeUtils.unescapeJava(args[1]).replaceAll("_", " ").trim();
            subTitle = args.length > 2 ? StringEscapeUtils.unescapeJava(args[2]).replaceAll("_", " ").trim() : "";
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
