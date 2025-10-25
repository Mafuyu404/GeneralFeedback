package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.sighs.generalfeedback.Generalfeedback.MODID;

public class Textarea extends AbstractWidget {
    private static final ResourceLocation EDITOR_TEXTURE = new ResourceLocation(MODID, "textures/gui/editor.png");
    private static final int OFFSET_X = 10, OFFSET_Y = 20, OFFSET_W = 20, OFFSET_H = 30;
    private static final int INNER_PADDING = 4;

    private final Font font;
    private final Component title;
    private final int lineHeight, maxWidth;

    private String text = "";
    private int cursorIndex = 0;
    private int scrollOffset = 0;
    private Consumer<String> onChange;

    public Textarea(int x, int y, int width, int height, Component title) {
        super(x + OFFSET_X, y + OFFSET_Y, width - OFFSET_W, height - OFFSET_H, Component.empty());
        this.font = Minecraft.getInstance().font;
        this.lineHeight = font.lineHeight;
        this.maxWidth = this.width - INNER_PADDING * 2;
        this.title = title;
        this.active = true;
    }

    public void onChange(Consumer<String> onChange) {
        this.onChange = onChange;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
        GuiUtils.drawNinePatch(g, EDITOR_TEXTURE, getX() - OFFSET_X, getY() - OFFSET_Y, width + OFFSET_W, height + OFFSET_H, 256, 20);
        g.drawString(font, title, getX(), getY() - OFFSET_Y + 7, 0xFF695B8B, false);

        var layout = new TextLayout(text, font, maxWidth);
        int maxVisible = Math.max(1, (height - INNER_PADDING * 2) / lineHeight);
        int maxStart = Math.max(0, layout.lines.size() - maxVisible);
        scrollOffset = Math.min(scrollOffset, maxStart);

        int drawX = getX() + INNER_PADDING;
        int drawY = getY() + INNER_PADDING;

        for (int i = 0; i < Math.min(maxVisible, layout.lines.size() - scrollOffset); i++) {
            g.drawString(font, layout.lines.get(scrollOffset + i), drawX, drawY + i * lineHeight, 0xFFFFFFFF, true);
        }

        if (isFocused() && (System.currentTimeMillis() / 500L) % 2L == 0L) {
            CursorPos cp = layout.cursorPos(cursorIndex);
            int relLine = cp.line - scrollOffset;
            if (relLine >= 0 && relLine < maxVisible) {
                int cx = drawX + font.width(cp.before);
                int cy = drawY + relLine * lineHeight;
                g.fill(cx, cy, cx + 1, cy + lineHeight, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (!visible) return false;
        if (Character.isISOControl(c) && c != '\n') return false;
        insertChar(c);
        return true;
    }

    private void insertChar(char c) {
        if (text.length() >= 1024) return;
        text = text.substring(0, cursorIndex) + c + text.substring(cursorIndex);
        cursorIndex++;
        ensureCursorVisible();
        notifyChange();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!visible) return false;
        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> deleteChar(-1);
            case GLFW.GLFW_KEY_DELETE -> deleteChar(0);
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> insertChar('\n');
            case GLFW.GLFW_KEY_LEFT -> cursorIndex = Math.max(0, cursorIndex - 1);
            case GLFW.GLFW_KEY_RIGHT -> cursorIndex = Math.min(text.length(), cursorIndex + 1);
            case GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_DOWN -> moveVertical(keyCode == GLFW.GLFW_KEY_UP ? -1 : 1);
            default -> {
                return false;
            }
        }
        ensureCursorVisible();
        return true;
    }

    private void deleteChar(int offset) {
        if (offset == -1 && cursorIndex > 0) {
            text = text.substring(0, cursorIndex - 1) + text.substring(cursorIndex);
            cursorIndex--;
        } else if (offset == 0 && cursorIndex < text.length()) {
            text = text.substring(0, cursorIndex) + text.substring(cursorIndex + 1);
        }
        notifyChange();
    }

    private void moveVertical(int direction) {
        var layout = new TextLayout(text, font, maxWidth);
        CursorPos cp = layout.cursorPos(cursorIndex);
        int target = cp.line + direction;
        if (target < 0 || target >= layout.lines.size()) return;
        int col = cp.before.length();
        cursorIndex = layout.indexAt(target, col);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            setFocused(false);
            return false;
        }
        setFocused(true);
        int localX = (int) (mouseX - getX() - INNER_PADDING);
        int localY = (int) (mouseY - getY() - INNER_PADDING);

        var layout = new TextLayout(text, font, maxWidth);
        int line = scrollOffset + Math.max(0, Math.min(localY / lineHeight, layout.lines.size() - 1));
        int col = layout.charIndexAt(line, localX);
        cursorIndex = layout.indexAt(line, col);
        ensureCursorVisible();
        return true;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (!isMouseOver(mx, my)) return false;
        int step = (int) Math.signum(sy);
        scrollOffset = Math.max(0, scrollOffset - step);
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
    }

    private void ensureCursorVisible() {
        var layout = new TextLayout(text, font, maxWidth);
        int maxVisible = Math.max(1, (height - INNER_PADDING * 2) / lineHeight);
        CursorPos cp = layout.cursorPos(cursorIndex);
        if (cp.line < scrollOffset) scrollOffset = cp.line;
        else if (cp.line >= scrollOffset + maxVisible) scrollOffset = cp.line - maxVisible + 1;
    }

    private void notifyChange() {
        if (onChange != null) onChange.accept(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        this.text = t == null ? "" : t;
        cursorIndex = Math.min(cursorIndex, text.length());
    }

    private record CursorPos(int line, String before) {
    }

    private static class TextLayout {
        final List<String> lines = new ArrayList<>();
        final List<Integer> starts = new ArrayList<>();
        private final String src;

        TextLayout(String src, Font font, int width) {
            this.src = src == null ? "" : src;
            if (this.src.isEmpty()) {
                lines.add("");
                starts.add(0);
                return;
            }
            int idx = 0;
            for (String para : this.src.split("\n", -1)) {
                if (para.isEmpty()) {
                    lines.add("");
                    starts.add(idx++);
                    continue;
                }
                while (!para.isEmpty()) {
                    int cut = findCut(font, para, width);
                    lines.add(para.substring(0, cut));
                    starts.add(idx);
                    idx += cut;
                    para = para.substring(cut);
                }
                if (idx < this.src.length()) idx++; // skip '\n'
            }
        }

        CursorPos cursorPos(int globalIndex) {
            int ci = Math.max(0, Math.min(globalIndex, src.length()));
            for (int i = 0; i < lines.size(); i++) {
                int start = starts.get(i);
                int end = start + lines.get(i).length();
                if (ci >= start && ci <= end) {
                    return new CursorPos(i, lines.get(i).substring(0, ci - start));
                }
            }
            int last = lines.size() - 1;
            return new CursorPos(last, lines.get(last));
        }

        int indexAt(int line, int col) {
            line = Math.max(0, Math.min(line, lines.size() - 1));
            col = Math.min(col, lines.get(line).length());
            return starts.get(line) + col;
        }

        int charIndexAt(int line, int px) {
            line = Math.max(0, Math.min(line, lines.size() - 1));
            String s = lines.get(line);
            if (px <= 0) return 0;
            int len = s.length();
            int full = Minecraft.getInstance().font.width(s);
            if (px >= full) return len;
            for (int i = 1; i <= len; i++) {
                if (Minecraft.getInstance().font.width(s.substring(0, i)) > px) return i - 1;
            }
            return len;
        }

        private static int findCut(Font font, String s, int w) {
            if (font.width(s) <= w) return s.length();
            int i = 0;
            while (i < s.length() && font.width(s.substring(0, i + 1)) <= w) i++;
            return Math.max(i, 1);
        }
    }
}
