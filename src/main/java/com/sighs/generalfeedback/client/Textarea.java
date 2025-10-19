package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.utils.GuiUtils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.sighs.generalfeedback.Generalfeedback.MODID;

public class Textarea extends AbstractWidget {
    private final Component title;
    private String text = "";
    private int cursorIndex = 0; // 在 raw text 中的字符索引
    private final int innerPadding = 4;
    private final int lineHeight;
    private final int maxWidth;
    private final Font font;
    private int scrollOffset = 0;
    private Consumer<String> onChange;
    private static final ResourceLocation EDITOR_TEXTURE = new ResourceLocation(MODID, "textures/gui/editor.png");

    private static final int offsetX = 10;
    private static final int offsetY = 20;
    private static final int offsetW = 20;
    private static final int offsetH = 30;

    public Textarea(int x, int y, int width, int height, Component title) {
        super(x + offsetX, y + offsetY, width - offsetW, height - offsetH, Component.empty());
        this.font = net.minecraft.client.Minecraft.getInstance().font;
        this.lineHeight = font.lineHeight;
        this.maxWidth = width - offsetW - innerPadding * 2;
        this.active = true;
        this.title = title;
    }

    public void onChange(Consumer<String> onChange) {
        this.onChange = onChange;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        GuiUtils.drawNinePatch(guiGraphics, EDITOR_TEXTURE, getX() - offsetX, getY() - offsetY, width + offsetW, height + offsetH, 256, 20);

        guiGraphics.drawString(font, title, getX(), getY() - offsetY + 7, 0xFF695B8B, false);

        List<String> lines = wrappedLines();
        int maxVisibleLines = Math.max(1, (height - innerPadding * 2) / lineHeight);
        int maxStart = Math.max(0, lines.size() - maxVisibleLines);
        int startLine = Math.min(Math.max(0, scrollOffset), maxStart);

        int drawX = this.getX() + innerPadding;
        int drawY = this.getY() + innerPadding;
        for (int i = 0; i < Math.min(maxVisibleLines, lines.size() - startLine); i++) {
            String ln = lines.get(startLine + i);
            guiGraphics.drawString(font, ln, drawX, drawY + i * lineHeight, 0xFFFFFFFF, true);
        }

        CursorPos cp = cursorPositionInRendered(lines);
        int cursorScreenLine = cp.lineIndex - startLine; // 变为屏幕相对行
        if (cursorScreenLine >= 0 && cursorScreenLine < maxVisibleLines) {
            int cx = drawX + font.width(cp.lineTextBeforeCursor);
            int cy = drawY + cursorScreenLine * lineHeight;
            if (isFocused() && ((System.currentTimeMillis() / 500L) % 2L) == 0L) {
                guiGraphics.fill(cx, cy, cx + 1, cy + lineHeight, 0xFFFFFFFF);
            }
        }
    }

    // 计算当前文本按像素宽换行后的行列表
    private List<String> wrappedLines() {
        List<String> out = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            out.add("");
            return out;
        }
        String[] paragraphs = text.split("\n", -1);
        for (String para : paragraphs) {
            // 对每个段落做按像素宽度拆分
            if (para.isEmpty()) {
                out.add("");
                continue;
            }
            String rem = para;
            while (!rem.isEmpty()) {
                int cut = findCutIndexForWidth(rem, maxWidth);
                out.add(rem.substring(0, cut));
                rem = rem.substring(cut);
            }
        }
        return out;
    }

    // 在字符串 s 中找到最多可放下的字符数（不做智能按词边界断行）
    private int findCutIndexForWidth(String s, int pixelWidth) {
        if (font.width(s) <= pixelWidth) return s.length();
        // 逐字累加直到超出
        int i = 0;
        int len = s.length();
        while (i < len) {
            int w = font.width(s.substring(0, i + 1));
            if (w > pixelWidth) break;
            i++;
        }
        if (i == 0) i = 1; // 至少一个字符
        return i;
    }

    // 根据 cursorIndex 计算它在渲染行中的位置
    private CursorPos cursorPositionInRendered(List<String> _unused) {
        // 为了保持与 mouseClicked 中一致的行为，我们在此重新构建渲染行与 raw text 起始索引的映射
        List<String> lines = new ArrayList<>();
        List<Integer> lineStartIndices = new ArrayList<>();

        String src = this.text == null ? "" : this.text;
        String[] paragraphs = src.split("\n", -1);
        int originalIndex = 0;

        for (int p = 0; p < paragraphs.length; p++) {
            String para = paragraphs[p];
            if (para.isEmpty()) {
                // 空段落渲染为空行，记录该行的 raw 起始索引
                lines.add("");
                lineStartIndices.add(originalIndex);
            } else {
                String rem = para;
                while (!rem.isEmpty()) {
                    int cut = findCutIndexForWidth(rem, maxWidth);
                    String piece = rem.substring(0, cut);
                    lines.add(piece);
                    lineStartIndices.add(originalIndex);
                    originalIndex += piece.length();
                    rem = rem.substring(cut);
                }
            }
            // 段落结束后，如果原始文本还有字符，跳过一个换行符（'\n'）
            if (originalIndex < src.length()) {
                originalIndex += 1; // account for '\n'
            }
        }

        // 保底：至少一行
        if (lines.isEmpty()) {
            lines.add("");
            lineStartIndices.add(0);
        }

        // 找到 cursorIndex 属于哪一渲染行（使用映射）
        int ci = Math.max(0, Math.min(cursorIndex, src.length()));
        for (int i = 0; i < lines.size(); i++) {
            int start = lineStartIndices.get(i);
            int len = lines.get(i).length();
            int endExclusive = start + len; // 不包含
            if (ci >= start && ci <= endExclusive) {
                int posInLine = ci - start;
                String before = lines.get(i).substring(0, Math.max(0, Math.min(posInLine, len)));
                return new CursorPos(i, before);
            }
        }

        // 如果 cursorIndex 在所有映射行之后（例如在末尾），放到最后一行末尾
        int lastIdx = lines.size() - 1;
        String last = lines.get(lastIdx);
        return new CursorPos(lastIdx, last);
    }

    private static class CursorPos {
        int lineIndex;
        String lineTextBeforeCursor;
        CursorPos(int lineIndex, String before) {
            this.lineIndex = lineIndex;
            this.lineTextBeforeCursor = before;
        }
    }

    // 插入字符
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // 接收换行（Enter）和普通字符
        if (!this.visible) return false;
        if (Character.isISOControl(codePoint)) {
            // 控制字符通常不在这里处理（Enter 也会由 keyPressed 捕获），但允许 '\n'
            if (codePoint == '\n') {
                insertChar('\n');
                return true;
            }
            return false;
        } else {
            insertChar(codePoint);
            return true;
        }
    }

    private void insertChar(char c) {
        if (text.length() >= 1024) {
            return;
        }
        if (cursorIndex < 0) cursorIndex = 0;
        if (cursorIndex > text.length()) cursorIndex = text.length();
        text = text.substring(0, cursorIndex) + c + text.substring(cursorIndex);
        cursorIndex++;
        ensureCursorVisible(wrappedLines());
        if (onChange != null) onChange.accept(getText());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.visible) return false;
        // Backspace
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (cursorIndex > 0) {
                text = text.substring(0, cursorIndex - 1) + text.substring(cursorIndex);
                cursorIndex--;
            }
            return true;
        }
        // Delete
        if (keyCode == GLFW.GLFW_KEY_DELETE) {
            if (cursorIndex < text.length()) {
                text = text.substring(0, cursorIndex) + text.substring(cursorIndex + 1);
            }
            return true;
        }
        // Enter (回车) - 插入换行
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            insertChar('\n');
            return true;
        }
        // 左右移动
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            if (cursorIndex > 0) cursorIndex--;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            if (cursorIndex < text.length()) cursorIndex++;
            return true;
        }
        // 上下移动（基本实现：按 visual line 移动）
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_DOWN) {
            List<String> lines = wrappedLines();
            CursorPos cp = cursorPositionInRendered(lines);
            int targetLine = cp.lineIndex + (keyCode == GLFW.GLFW_KEY_UP ? -1 : 1);
            if (targetLine < 0) targetLine = 0;
            if (targetLine >= lines.size()) targetLine = lines.size() - 1;
            // 计算当前在行内列数（字符数）
            int col = cp.lineTextBeforeCursor.length();
            // 将光标移动到目标行同列（或行尾）
            // 先计算 cursorIndex 到目标行起始的全局索引
            int idx = 0;
            for (int i = 0; i < targetLine; i++) idx += lines.get(i).length();
            int newPosInLine = Math.min(col, lines.get(targetLine).length());
            cursorIndex = idx + newPosInLine;
            ensureCursorVisible(lines);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            this.setFocused(true);

            // 转换为控件内部局部坐标（包含 innerPadding 偏移）
            int localX = (int) (mouseX - getX() - innerPadding);
            int localY = (int) (mouseY - getY() - innerPadding);

            // 重新构建渲染行与原始 text 起始索引的映射
            // 这样可以正确把渲染行 -> raw text 索引（考虑 '\n' 占位）
            List<String> lines = new ArrayList<>();
            List<Integer> lineStartIndices = new ArrayList<>(); // 每行在 raw text 中的起始索引

            String src = this.text == null ? "" : this.text;
            // 按段落（由 '\n' 分隔）处理，同时维护原始索引
            String[] paragraphs = src.split("\n", -1);
            int originalIndex = 0; // 当前在 raw text 中的读写位置

            for (int p = 0; p < paragraphs.length; p++) {
                String para = paragraphs[p];
                if (para.isEmpty()) {
                    // 段落为空：渲染为一个空行，但原始中仍有一个换行符占位（除非是最后也可能有）
                    lines.add("");
                    lineStartIndices.add(originalIndex);
                    // 这个段落为空，不增加 originalIndex（因为段落长度 0），但之后我们要跳过 '\n'
                    // 增加 originalIndex 用于跳过换行符（除非这是最后一个段落且原文本没有末尾换行——但 split(...,-1) 保留末尾空段）
                    // 无论如何，我们在段落之间都将原始索引加 1 来跳过换行符
                } else {
                    String rem = para;
                    while (!rem.isEmpty()) {
                        int cut = findCutIndexForWidth(rem, maxWidth);
                        String piece = rem.substring(0, cut);
                        lines.add(piece);
                        lineStartIndices.add(originalIndex);
                        // advance originalIndex by piece length in raw text
                        originalIndex += piece.length();
                        rem = rem.substring(cut);
                    }
                }
                // 在段落结束后，原始文本如果还有换行（即不是 text 末尾），要跳过那个 '\n'，使下个段落的起始索引正确
                // split(..., -1) 会在末尾保留空段，所以这里统一在每个段落后判断是否应跳过换行符：
                // 如果 originalIndex < src.length()，说明接下来确实有一个 '\n'（除非末尾空段）
                if (originalIndex < src.length()) {
                    // 跳过单个换行符（在原始 text 中）
                    // 注意：如果 paragraph 是最后一个段落且原始 text 没有末尾换行，这里不会执行，因为 originalIndex == src.length()
                    originalIndex += 1; // account for '\n'
                }
            }

            // 保底：至少有一行
            if (lines.isEmpty()) {
                lines.add("");
                lineStartIndices.add(0);
            }

            // 计算可见行数（与 render 一致）
            int maxVisibleLines = Math.max(1, (height - innerPadding * 2) / lineHeight);

            // 如果控件高度非常小或点击到了可视范围之外，仍然用最后一行
            int maxStart = Math.max(0, lines.size() - maxVisibleLines);
            int startLine = Math.min(Math.max(0, scrollOffset), maxStart);

            int clickedLineLocal = localY / lineHeight;
            int clickedLine = startLine + clickedLineLocal; // startLine 与 render 中保持一致
            if (clickedLine < 0) clickedLine = 0;
            if (clickedLine >= lines.size()) clickedLine = lines.size() - 1;

            // 用 clickedLine 而不是 clickedLineLocal 去拿 targetLine、lineStartIndices
            String targetLine = lines.get(clickedLine);
            int lineStartInRaw = lineStartIndices.get(clickedLine);

            // 计算点击在目标行中的字符位置（逐字符测量宽度）
            int charIndexInLine = charIndexFromLocalX(targetLine, localX);

            // 将行内位置转为全局 text 索引（使用映射得到的 start）
            int globalIndex = lineStartInRaw + charIndexInLine;

            // 边界保护
            if (globalIndex < 0) globalIndex = 0;
            if (globalIndex > text.length()) globalIndex = text.length();

            this.cursorIndex = lineStartInRaw + charIndexInLine;
            ensureCursorVisible(lines);
            return true;
        } else {
            this.setFocused(false);
        }
        return false;
    }

    // 计算目标行的字符索引
    private int charIndexFromLocalX(String line, int localX) {
        if (localX <= 0) return 0;
        if (line.isEmpty()) return 0;
        int len = line.length();
        // 如果点击在整行文本像素宽度之外，返回行尾
        int fullWidth = font.width(line);
        if (localX >= fullWidth) return len;

        // 逐字符累加测量（因为宽度基于实际字体）
        for (int i = 1; i <= len; i++) {
            int w = font.width(line.substring(0, i));
            if (w > localX) {
                // 点击位置落在第 i 个字符的像素范围内 -> 放在前一位置 i-1 或者更自然地放 i-1 / i 之间。
                // 我们选择放在 i-1（即光标在该字符之前），但如果点击位置靠近字符右半部可以改成 i。
                return Math.max(0, i - 1);
            }
        }
        return len;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        // delta > 0 表示向上滚（通常希望往上滚动内容 -> scrollOffset 减小）
        int step = (int)Math.round(delta); // 一般 ±1，稳妥处理
        scrollOffset -= step;
        // clamp
        List<String> lines = wrappedLines();
        int maxVisibleLines = Math.max(1, (height - innerPadding * 2) / lineHeight);
        int maxStart = Math.max(0, lines.size() - maxVisibleLines);
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxStart) scrollOffset = maxStart;
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    private void ensureCursorVisible(List<String> lines) {
        int maxVisibleLines = Math.max(1, (height - innerPadding * 2) / lineHeight);
        int maxStart = Math.max(0, lines.size() - maxVisibleLines);
        CursorPos cp = cursorPositionInRendered(lines); // 返回全局渲染行索引
        int cursorLine = cp.lineIndex;
        if (cursorLine < scrollOffset) {
            scrollOffset = cursorLine;
        } else if (cursorLine >= scrollOffset + maxVisibleLines) {
            scrollOffset = cursorLine - maxVisibleLines + 1;
        }
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxStart) scrollOffset = maxStart;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String t) {
        this.text = t == null ? "" : t;
        this.cursorIndex = Math.min(this.cursorIndex, this.text.length());
    }
}
