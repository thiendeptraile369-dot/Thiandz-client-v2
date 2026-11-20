package com.thiandz.client.module.hud;

import com.thiandz.client.Theme;
import com.thiandz.client.module.HudModule;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.List;

/** Minimap địa hình nhìn từ trên xuống (hướng Bắc ở trên). Không hiển thị người chơi/sinh vật khác. */
public class MinimapModule extends HudModule {
    private static final int CELLS = 48; // vùng 48x48 block quanh người chơi
    private static final int CELL = 2;   // mỗi block = 2 pixel
    private final int[] colors = new int[CELLS * CELLS];
    private int tickCount = 0;
    private boolean ready = false;

    public MinimapModule() { super("minimap", 380, 110, true); }

    @Override
    protected List<String> getLines(MinecraftClient mc) { return List.of(); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!isEnabled() || mc.player == null || mc.world == null) { ready = false; return; }
        if (tickCount++ % 8 == 0) rebuild(mc);
    }

    private void rebuild(MinecraftClient mc) {
        var world = mc.world;
        int px = mc.player.getBlockX(), pz = mc.player.getBlockZ(), py = mc.player.getBlockY();
        boolean ceiling = world.getRegistryKey().getValue().getPath().equals("the_nether");
        int minY = world.getBottomY();
        int[] h = new int[CELLS * CELLS];
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int dz = 0; dz < CELLS; dz++) {
            for (int dx = 0; dx < CELLS; dx++) {
                int i = dz * CELLS + dx;
                int wx = px - CELLS / 2 + dx, wz = pz - CELLS / 2 + dz;
                h[i] = py;
                if (!world.getChunkManager().isChunkLoaded(wx >> 4, wz >> 4)) { colors[i] = -1; continue; }

                int y;
                if (ceiling) { // Nether: quét xuống từ vị trí người chơi để khỏi thấy trần đá
                    y = py + 1;
                    while (y > minY) {
                        pos.set(wx, y, wz);
                        if (!world.getBlockState(pos).isAir()) break;
                        y--;
                    }
                } else {
                    y = world.getTopY(Heightmap.Type.WORLD_SURFACE, wx, wz) - 1;
                }
                pos.set(wx, y, wz);
                BlockState st = world.getBlockState(pos);
                int c = st.getMapColor(world, pos).color;
                if (c == 0) c = 0x202020;
                colors[i] = c;
                h[i] = y;
            }
        }

        // Đổ bóng nhẹ theo độ cao so với block phía Bắc
        for (int dz = 1; dz < CELLS; dz++) {
            for (int dx = 0; dx < CELLS; dx++) {
                int i = dz * CELLS + dx;
                if (colors[i] < 0) continue;
                int delta = h[i] - h[i - CELLS];
                float f = delta > 0 ? 1.15f : delta < 0 ? 0.8f : 1.0f;
                int c = colors[i];
                int r = Math.min(255, (int) (((c >> 16) & 0xFF) * f));
                int g = Math.min(255, (int) (((c >> 8) & 0xFF) * f));
                int b = Math.min(255, (int) ((c & 0xFF) * f));
                colors[i] = (r << 16) | (g << 8) | b;
            }
        }
        ready = true;
    }

    @Override
    public void render(DrawContext ctx, RenderTickCounter tick) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (!isEnabled() || !ready || mc.player == null || mc.options.hudHidden) return;

            int alpha = Math.max(40, (int) (opacity * 255));
            int size = CELLS * CELL;
            var m = ctx.getMatrices();
            m.pushMatrix();
            try {
                m.translate((float) x, (float) y);
                m.scale(scale, scale);

                ctx.fill(-2, -2, size + 2, size + 2, 0xFF000000 | Theme.accent);
                ctx.fill(-1, -1, size + 1, size + 1, 0xFF000000);

                for (int dz = 0; dz < CELLS; dz++) {
                    int dx = 0;
                    while (dx < CELLS) {
                        int c = colors[dz * CELLS + dx];
                        int j = dx + 1;
                        while (j < CELLS && colors[dz * CELLS + j] == c) j++;
                        if (c != -1) {
                            ctx.fill(dx * CELL, dz * CELL, j * CELL, (dz + 1) * CELL,
                                (alpha << 24) | (c & 0xFFFFFF));
                        }
                        dx = j;
                    }
                }

                // Người chơi (chấm giữa) + chấm đỏ chỉ hướng nhìn
                int c = size / 2;
                ctx.fill(c - 2, c - 2, c + 2, c + 2, 0xFFFFFFFF);
                ctx.fill(c - 1, c - 1, c + 1, c + 1, 0xFF000000 | Theme.accent);
                double rad = Math.toRadians(mc.player.getYaw());
                int fx = c + (int) Math.round(-Math.sin(rad) * 7);
                int fz = c + (int) Math.round(Math.cos(rad) * 7);
                ctx.fill(fx - 1, fz - 1, fx + 1, fz + 1, 0xFFFF5555);

                ctx.drawText(mc.textRenderer, "N", size / 2 - 3, 2, 0xFFFFFFFF, true);
            } finally {
                m.popMatrix();
            }
        } catch (Throwable t) {
            com.thiandz.client.ThiandzClient.LOGGER.error("Loi minimap", t);
        }
    }
}
