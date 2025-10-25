package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.event.SubmitEvent;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import com.sighs.generalfeedback.utils.FeedbackUtils;
import com.sighs.generalfeedback.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.sighs.generalfeedback.Generalfeedback.MODID;

public class FeedbackScreen extends Screen {
    private final Entry entry;

    private List<Boolean> markValue;

    private List<Boolean> initMarkValue() {
        markValue = new ArrayList<>();
        for (int i = 0; i < 5; i++) markValue.add(false);
        return markValue;
    }

    public FeedbackScreen(Entry entry) {
        super(Component.translatable("gui.generalfeedback.feedback"));
        this.entry = entry;
    }

    public void sendForm() {
        Minecraft.getInstance().setScreen(null);
        SubmitEvent event = new SubmitEvent(entry, getForm());
        SubmitEvent.EVENT.invoker().onSubmit(event);
        if (event.isCanceled()) return;
        FeedbackUtils.post(event.getEntry(), event.getForm());
    }

    public Form getForm() {
        Form form = new Form();
        form.feedback = feedbackTextarea.getText();
        form.contact = contactTextarea.getText();
        form.mark = markValue.indexOf(true) + 1;
        return form;
    }

    private Textarea feedbackTextarea;
    private Textarea contactTextarea;
    private final List<BooleanButton> markButtonList = new ArrayList<>();
    private ActionButton submitButton;

    private int storedY;

    @Override
    protected void init() {
        super.init();

        int margin = 2;
        int width = 240;
        int x = (this.width - width) / 2;
        int y = 10;

        feedbackTextarea = new Textarea(x, y, width, 120, Component.literal(Component.translatable("gui.generalfeedback.feedback").getString() + ": " + Component.translatable(entry.title).getString()));
        String preset = FeedbackUtils.cache.getOrDefault(entry.id, Component.translatable(entry.placeholder).getString());
        feedbackTextarea.setText(preset);
        feedbackTextarea.onChange(text -> FeedbackUtils.cache.put(entry.id, Component.translatable(text).getString()));
        addRenderableWidget(feedbackTextarea);

        y += 120 + margin;

        storedY = y;

        initMarkValue();
        for (int i = 0; i < 5; i++) {
            BooleanButton booleanButton = getButton(x, i, y);
            markButtonList.add(booleanButton);
            addRenderableWidget(booleanButton);
        }

        y += 24 + margin;

        contactTextarea = new Textarea(x, y, width, 58, Component.translatable("gui.generalfeedback.contact"));
        addRenderableWidget(contactTextarea);

        y += 58 + margin;

        Component submitText = Component.translatable("gui.generalfeedback.submit");
        submitButton = new ActionButton(
                (this.width - 70) / 2, y, 70, 20,
                submitText,
                button -> {
                    sendForm();
                }
        );
        addRenderableWidget(submitButton);

        setFocused(feedbackTextarea);
    }

    private @NotNull BooleanButton getButton(int x, int i, int y) {
        int _x = x + 95 + i * 28;
        return new BooleanButton(
                _x, y + 4, 26, 16,
                Component.translatable((i + 1) + Component.translatable("gui.generalfeedback.star").getString()),
                button -> {
                    BooleanButton b = (BooleanButton) button;
                    if (!b.value && markValue.contains(true)) {
                        initMarkValue();
                        markButtonList.forEach(_b -> _b.value = false);
                    }
                    b.value = !b.value;
                    markValue.set(i, b.value);
                }
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        int width = 240;
        int x = (this.width - width) / 2;
        GuiUtils.drawNinePatch(guiGraphics, ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/container.png"), x, storedY, width, 24, 256, 20);
        guiGraphics.drawString(font, Component.translatable("gui.generalfeedback.mark"), x + 10, storedY + 7, 0xFF695B8B, false);

        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
