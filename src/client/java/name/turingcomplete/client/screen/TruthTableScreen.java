package name.turingcomplete.client.screen;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.truthtable.TruthTableScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOfferList;

@Environment(EnvType.CLIENT)
public class TruthTableScreen extends HandledScreen<TruthTableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(TuringComplete.MOD_ID, "textures/gui/container/truth_table.png");
    private static final Identifier SCROLL_BAR = Identifier.ofVanilla("container/villager/scroller");
    private static final Identifier SCROLL_BAR_DISABLED = Identifier.ofVanilla("container/villager/scroller_disabled");
    int indexStartOffset;
    
    
    public TruthTableScreen(TruthTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 320;
        this.backgroundHeight = 192;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void init(){
        super.init();
        this.titleX = (backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    private void renderScrollbar (DrawContext context, int x, int y, TradeOfferList offerList){
        int i = offerList.size() + 1 - 7;
        if (i > 1){
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int m = Math.min(113, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 113;
            }
            context.drawGuiTexture(SCROLL_BAR,x+106, y+20+m, 0, 6, 27);
        } else {
            context.drawGuiTexture(SCROLL_BAR_DISABLED,x+106, y+7, 0, 6, 27);
        }


    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        
        TradeOfferList offers = this.handler.getRecipes();
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.renderScrollbar(context,x,y,offers);
    }
}
