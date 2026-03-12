package org.craft.test;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftTest extends JavaPlugin {

    @Getter
    private static CraftTest inst;

    @Override
    public void onLoad() {
        inst = this;
    }

    @Override
    public void onEnable() {
        new CraftTestCommand();
        new ResourcePackTestCommand();
        //new SpeedCalculator();
    }
}
