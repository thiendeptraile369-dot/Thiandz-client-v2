package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/** Máu, giáp hấp thụ và độ đói của bản thân. */
public class HealthModule extends HudModule {
    public HealthModule() { super("health", 120, 146, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        float hp = mc.player.getHealth();
        float max = mc.player.getMaxHealth();
        float abs = mc.player.getAbsorptionAmount();
        String c = hp / max > 0.5f ? "§a" : hp / max > 0.25f ? "§e" : "§c";
        String line = c + Lang.t("hud.health", String.format("%.1f", hp), String.format("%.0f", max));
        if (abs > 0) line += " §6+" + String.format("%.0f", abs);
        List<String> out = new ArrayList<>();
        out.add(line);
        out.add("§6" + Lang.t("hud.hunger", mc.player.getHungerManager().getFoodLevel()));
        return out;
    }
}
