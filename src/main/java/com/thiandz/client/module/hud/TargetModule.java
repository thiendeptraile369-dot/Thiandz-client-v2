package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.List;

/** Tên block / thực thể đang nhìn vào, kèm máu nếu là sinh vật hoặc người chơi. */
public class TargetModule extends HudModule {
    public TargetModule() { super("target", 120, 128, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        HitResult hit = mc.crosshairTarget;
        if (hit == null) return List.of();
        if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult b) {
            String name = mc.world.getBlockState(b.getBlockPos()).getBlock().getName().getString();
            return List.of(Lang.t("hud.target", name));
        }
        if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult e) {
            var ent = e.getEntity();
            String name = ent.getName().getString();
            if (ent instanceof LivingEntity le) {
                float hp = le.getHealth(), max = le.getMaxHealth();
                String c = hp / max > 0.5f ? "§a" : hp / max > 0.25f ? "§e" : "§c";
                return List.of(Lang.t("hud.target", name),
                    c + Lang.t("hud.health", String.format("%.1f", hp), String.format("%.0f", max)),
                    "§7" + Lang.t("hud.target_armor", le.getArmor()));
            }
            return List.of(Lang.t("hud.target", name));
        }
        return List.of();
    }
}
