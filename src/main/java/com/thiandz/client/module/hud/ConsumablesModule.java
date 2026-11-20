package com.thiandz.client.module.hud;

import com.thiandz.client.Lang;
import com.thiandz.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

/** Số totem, ngọc ender và mũi tên còn trong túi đồ. */
public class ConsumablesModule extends HudModule {
    public ConsumablesModule() { super("counters", 120, 180, true); }

    private static int count(PlayerInventory inv, Item item) {
        int n = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (s.isOf(item)) n += s.getCount();
        }
        return n;
    }

    @Override
    protected List<String> getLines(MinecraftClient mc) {
        PlayerInventory inv = mc.player.getInventory();
        int totem = count(inv, Items.TOTEM_OF_UNDYING);
        int pearl = count(inv, Items.ENDER_PEARL);
        int arrow = count(inv, Items.ARROW);
        return List.of(
            (totem == 0 ? "§c" : "§e") + Lang.t("hud.counter.totem", totem),
            "§b" + Lang.t("hud.counter.pearl", pearl),
            "§7" + Lang.t("hud.counter.arrow", arrow)
        );
    }
}
