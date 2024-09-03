package definedentity.xenon.render.pipeline.attribute;

import definedentity.xenon.colour.ColourRGBA;
import definedentity.xenon.render.CCRenderState;
import definedentity.xenon.render.pipeline.VertexAttribute;

/** Created by covers1624 on 10/10/2016. */
public class LightingAttribute extends VertexAttribute<int[]> {

    public static final AttributeKey<int[]> attributeKey = AttributeKey.create("lighting", int[]::new);

    private int[] colourRef;

    public LightingAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        if (!ccrs.computeLighting || !ccrs.cFmt.hasColor || !ccrs.model.hasAttribute(attributeKey)) {
            return false;
        }

        colourRef = ccrs.model.getAttribute(attributeKey);
        if (colourRef != null) {
            ccrs.pipeline.addDependency(ccrs.colourAttrib);
            return true;
        }
        return false;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        ccrs.colour = ColourRGBA.multiply(ccrs.colour, colourRef[ccrs.vertexIndex]);
    }
}
