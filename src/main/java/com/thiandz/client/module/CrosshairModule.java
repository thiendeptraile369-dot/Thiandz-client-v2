package com.thiandz.client.module;

import com.google.gson.JsonObject;
import com.thiandz.client.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/** Tâm ngắm tùy chỉnh thay cho tâm vanilla: 4 kiểu, đổi cỡ/khe hở/độ dày/màu/viền. */
public class CrosshairModule extends Module {
    public static final int STYLES = 4; // 0 chữ thập hở, 1 chấm, 2 chữ thập liền, 3 khung vuông

    private int style = 0;
    private int size = 6;
    private int gap = 3;
    private int thickness = 1;
    private boolean useAccent = true;
    private boolean outline = true;

    public CrosshairModule() { super("crosshair", true); }

    public int getStyle() { return style; }
    public void nextStyle() { style = (style + 1) % STYLES; }
    public int getSize() { return size; }
    public void setSize(int v) { size = Math.max(1, Math.min(20, v)); }
    public int getGap() { return gap; }
    public void setGap(int v) { gap = Math.max(0, Math.min(10, v)); }
    public int getThickness() { return thickness; }
    public void setThickness(int v) { thickness = Math.max(1, Math.min(4, v)); }
    public boolean isUseAccent() { return useAccent; }
    public void setUseAccent(boolean b) { useAccent = b; }
    public boolean isOutline() { return outline; }
    public void setOutline(boolean b) { outline = b; }

    public void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || !mc.options.getPerspective().isFirstPerson()) return;

        int cx = mc.getWindow().getScaledWidth() / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;
        int color = useAccent ? (0xFF000000 | Theme.accent) : 0xFFFFFFFF;
        int t = thickness;
        int h0 = -(t / 2); // lệch để tâm nằm giữa
        List<int[]> r = new ArrayList<>();

        switch (style) {
            case 0 -> { // chữ thập có khe hở
                r.add(new int[]{cx - gap - size, cy + h0, cx - gap, cy + h0 + t});
                r.add(new int[]{cx + gap, cy + h0, cx + gap + size, cy + h0 + t});
                r.add(new int[]{cx + h0, cy - gap - size, cx + h0 + t, cy - gap});
                r.add(new int[]{cx + h0, cy + gap, cx + h0 + t, cy + gap + size});
            }
            case 1 -> { // chấm
                int rr = Math.max(1, t);
                r.add(new int[]{cx - rr, cy - rr, cx + rr, cy + rr});
            }
            case 2 -> { // chữ thập liền
                r.add(new int[]{cx - size, cy + h0, cx + size, cy + h0 + t});
                r.add(new int[]{cx + h0, cy - size, cx + h0 + t, cy + size});
            }
            default -> { // khung vuông
                r.add(new int[]{cx - size, cy - size, cx + size, cy - size + t});
                r.add(new int[]{cx - size, cy + size - t, cx + size, cy + size});
                r.add(new int[]{cx - size, cy - size, cx - size + t, cy + size});
                r.add(new int[]{cx + size - t, cy - size, cx + size, cy + size});
            }
        }

        if (outline) {
            for (int[] q : r) ctx.fill(q[0] - 1, q[1] - 1, q[2] + 1, q[3] + 1, 0xFF000000);
        }
        for (int[] q : r) ctx.fill(q[0], q[1], q[2], q[3], color);
    }

    @Override
    public void write(JsonObject o) {
        super.write(o);
        o.addProperty("style", style);
        o.addProperty("size", size);
        o.addProperty("gap", gap);
        o.addProperty("thickness", thickness);
        o.addProperty("useAccent", useAccent);
        o.addProperty("outline", outline);
    }

    @Override
    public void read(JsonObject o) {
        super.read(o);
        if (o.has("style")) style = Math.floorMod(o.get("style").getAsInt(), STYLES);
        if (o.has("size")) setSize(o.get("size").getAsInt());
        if (o.has("gap")) setGap(o.get("gap").getAsInt());
        if (o.has("thickness")) setThickness(o.get("thickness").getAsInt());
        if (o.has("useAccent")) useAccent = o.get("useAccent").getAsBoolean();
        if (o.has("outline")) outline = o.get("outline").getAsBoolean();
    }
}
