package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;

import java.util.List;

/** Thanh hồi chiêu tấn công (độ đầy của đòn đánh). */
public class CooldownModule extends HudModule {
    public CooldownModule() { super("cooldown", 200, 160, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        float p = Math.max(0f, Math.min(1f, mc.player.getAttackCooldownProgress(0f)));
        int filled = Math.round(p * 10);
        String bar = (p >= 1f ? "§a" : "§d") + "|".repeat(filled) + "§8" + "|".repeat(10 - filled);
        return List.of(bar + " §7" + Math.round(p * 100) + "%");
    }
}
