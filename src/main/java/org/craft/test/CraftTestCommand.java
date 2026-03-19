package org.craft.test;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.rukon0621.database.RkDatabase;
import me.rukon0621.database.handler.DataLoader;
import me.rukon0621.rkutils.bukkit.item.ItemData;
import me.rukon0621.rkutils.bukkit.util.CommandUtil;
import me.rukon0621.rkutils.bukkit.util.Msg;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptors;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemManager;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.item.Item;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import net.momirealms.craftengine.libraries.nbt.Tag;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.trk.craft.api.chat.ChatManager;
import org.trk.craft.api.data.PlayerData;
import org.trk.craft.api.utils.HangulUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CraftTestCommand implements CommandExecutor {

    public CraftTestCommand() {
        CraftTest.getInst().getCommand("crafttest").setExecutor(this);
    }

    private final BossBar bar = BossBar.bossBar(Component.empty(), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return false;

        /*
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(args.length > 0) {
            Msg.send(player, itemStack.getItemMeta().getAsComponentString());
            return true;
        }
        Item<ItemStack> item = BukkitItemManager.instance().wrap(itemStack);
        CompoundTag tag = (CompoundTag) Optional.ofNullable(item.getTag("craftengine:arguments")).orElse(new CompoundTag());
        tag.putInt("a", 30);
        item.setTag(tag, "craftengine:arguments");       // custom_data.owner = "Steve"
        player.getInventory().setItemInMainHand(item.getItem());
         */

        if (args.length == 0) {
            Msg.send(player, "사용법: /crafttest <args>");
            return true;
        }
        else if (args[0].equals("nbt")) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            Msg.send(player, itemStack.getItemMeta().getAsComponentString());
        }
        else if (args[0].equals("nbtset")) {

            //crafttest nbtset <key> <value>

            if (args.length < 3) {
                Msg.send(player, "사용법: /crafttest nbtset <key> <value>");
                return true;
            }
            String key = args[1];
            String value = StringEscapeUtils.unescapeJava(CommandUtil.mergeArgs(args, 2));

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            Item<ItemStack> item = BukkitItemManager.instance().wrap(itemStack);
            CompoundTag tag = (CompoundTag) Optional.ofNullable(item.getTag("craftengine:arguments")).orElse(new CompoundTag());

            if (value.equals("null")) {
                tag.remove(key);
                item.setTag(tag, "craftengine:arguments");
                player.getInventory().setItemInMainHand(item.getItem());
                return true;
            }

            tag.putString(key, value);
            item.setTag(tag, "craftengine:arguments");       // custom_data.owner = "Steve"
            player.getInventory().setItemInMainHand(item.getItem());
        }
        else if(args[0].equals("test")) {
            if (args.length < 2) {
                Msg.send(player, "사용법: /crafttest test <text>");
                return true;
            }
            String text = CommandUtil.mergeArgs(args, 1);
            bar.name(Msg.mm(StringEscapeUtils.unescapeJava(text)));
            player.showBossBar(bar);

            Iterable<? extends BossBar> bossBars = player.activeBossBars();
                bossBars.forEach(bossBar -> {
                    Msg.send(player, "Active BossBar: " + bossBar.name());
                });
        }
        else if(args[0].equals("meta")) {

            ItemData itemData = ItemData.get(player.getInventory().getItemInMainHand());
            Msg.send(player, itemData.getMeta().toString());
            itemData.setName(Msg.mm("<red>Test Name"));
            ItemStack itemStack = itemData.get();
            //player.getInventory().setItemInMainHand(itemStack);
        }
        else if (args[0].equals("한글")) {
            Msg.send(player, String.valueOf(ChatManager.getInst().getTextLength(args[1])));
        }
        else if (args[0].equals("bgtest")) {

            String text = CommandUtil.mergeArgs(args, 1);
            StringBuilder sb = new StringBuilder("<!shadow><font:hud/alert>\u00A0\u0101");
            int length = ChatManager.getInst().getTextLength(text);
            Msg.send(player, "TextLength: " + length);
            sb.append(Msg.getColor(0, length / 255, length % 255));
            sb.append("\u00A1<#000001>\u0102\u0202").append(text).append("\u0103\u00A2");
            Msg.send(player, Msg.mm(sb.toString()));
            player.sendActionBar(Msg.mm(sb.toString()));
        }
        else if (args[0].equals("nickname")) {
            String name = args.length >= 2 ? CommandUtil.mergeArgs(args, 1) : "";
            if (name.isEmpty()) {
                PlayerData.get(player.getUniqueId()).setNickName(null);
                Msg.send(player, "닉네임이 초기화되었습니다.");
            }
            else {
                PlayerData.get(player.getUniqueId()).setNickName(name);
                Msg.send(player, "닉네임이 " + name + "(으)로 설정되었습니다.");
            }
        }
        return true;
    }
}
