package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayDeque;
import java.util.List;

/** Thống kê đánh: số lần vung / số lần ngắm trúng thực thể và CPS cao nhất. Reset khi rời thế giới. */
public class StatsModule extends HudModule {
    private final ArrayDeque<Long> clicks = new ArrayDeque<>();
    private boolean last = false;
    private int swings, hits, maxCps;

    public StatsModule() { super("stats", 380, 236, true); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null) {
            clicks.clear();
            last = false;
            swings = hits = maxCps = 0;
            return;
        }
        boolean p = mc.options.attackKey.isPressed() && mc.currentScreen == null;
        long now = System.currentTimeMillis();
        if (p && !last) {
            clicks.addLast(now);
            swings++;
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) hits++;
        }
        last = p;
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) clicks.pollFirst();
        maxCps = Math.max(maxCps, clicks.size());
    }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        int acc = swings == 0 ? 0 : Math.round(100f * hits / swings);
        return List.of(
            Lang.t("hud.stats.hits", hits, swings) + " (" + acc + "%)",
            Lang.t("hud.stats.cps", maxCps)
        );
    }
}
