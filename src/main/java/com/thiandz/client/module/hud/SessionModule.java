package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;

import java.util.List;

/** Thời gian chơi trong phiên hiện tại (tính từ lúc vào thế giới). */
public class SessionModule extends HudModule {
    private long start = 0;

    public SessionModule() { super("session", 380, 212, true); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null) start = 0;
        else if (start == 0) start = System.currentTimeMillis();
    }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        if (start == 0) return List.of();
        long s = (System.currentTimeMillis() - start) / 1000;
        return List.of(Lang.t("hud.session", String.format("%02d:%02d:%02d", s / 3600, (s / 60) % 60, s % 60)));
    }
}
