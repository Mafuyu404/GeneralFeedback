package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.event.SubmitEvent;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import com.sighs.generalfeedback.utils.FeedbackUtils;
import com.sighs.generalfeedback.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.common.NeoForge;

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
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return;
        }
        FeedbackUtils.post(event.getEntry(), event.getForm());
    }

    private void showSubmitConfirmation() {
        Minecraft minecraft = Minecraft.getInstance();
        ConfirmScreen confirmScreen = new ConfirmScreen(
                confirmed -> {
                    ClientHooks.popGuiLayer(minecraft);
                    if (confirmed) {
                        sendForm();
                    }
                },
                Component.translatable("gui.generalfeedback.confirm_submit.title"),
                Component.translatable("gui.generalfeedback.confirm_submit.message"),
                Component.translatable("gui.generalfeedback.confirm_submit.confirm"),
                Component.translatable("gui.generalfeedback.confirm_submit.cancel")
        ) {
            @Override
            protected void addButtons(int y) {
                addExitButton(new ActionButton(
                        width / 2 - 155, y, 150, 20,
                        yesButton,
                        button -> callback.accept(true)
                ));
                addExitButton(new ActionButton(
                        width / 2 + 5, y, 150, 20,
                        noButton,
                        button -> callback.accept(false)
                ));
            }
        };
        ClientHooks.pushGuiLayer(minecraft, confirmScreen);
        confirmScreen.setDelay(10);
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

        feedbackTextarea = new Textarea(x, y, width, 120, Component.translatable(entry.title));
        if (entry.placeholder != null) {
            feedbackTextarea.setPlaceholder(Component.translatable(entry.placeholder));
        }
        String preset = FeedbackUtils.cache.getOrDefault(entry.id, "");
        feedbackTextarea.setText(preset);
        feedbackTextarea.onChange(text -> FeedbackUtils.cache.put(entry.id, Component.translatable(text).getString()));
        addRenderableWidget(feedbackTextarea);

        y += 120 + margin;

        storedY = y;

        initMarkValue();
        for (int i = 0; i < 5; i++) {
            int _x = x + 95 + i * 28;
            int mark = 5 - i;
            int markIndex = mark - 1;
            BooleanButton booleanButton = new BooleanButton(
                    _x, y + 4, 26, 16,
                    Component.translatable(mark + Component.translatable("gui.generalfeedback.star").getString()),
                    button -> {
                        BooleanButton b = (BooleanButton) button;
                        if (!b.value && markValue.contains(true)) {
                            initMarkValue();
                            markButtonList.forEach(_b -> _b.value = false);
                        }
                        b.value = !b.value;
                        markValue.set(markIndex, b.value);
                    }
            );
            markButtonList.add(booleanButton);
            addRenderableWidget(booleanButton);
        }

        y += 24 + margin;

        contactTextarea = new Textarea(x, y, width, 58, Component.translatable("gui.generalfeedback.contact"));
        addRenderableWidget(contactTextarea);

        y += 58 + margin;

        int actionButtonWidth = 70;
        int actionButtonGap = 10;
        int actionButtonsX = (this.width - actionButtonWidth * 2 - actionButtonGap) / 2;
        Component submitText = Component.translatable("gui.generalfeedback.submit");
        submitButton = new ActionButton(
                actionButtonsX, y, actionButtonWidth, 20,
                submitText,
                button -> showSubmitConfirmation()
        );
        addRenderableWidget(submitButton);
        addRenderableWidget(new ActionButton(
                actionButtonsX + actionButtonWidth + actionButtonGap, y, actionButtonWidth, 20,
                Component.translatable("gui.generalfeedback.cancel"),
                button -> onClose()
        ));

        setFocused(feedbackTextarea);
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
