package definedentity.xenon.render.pipeline.attribute;

import definedentity.xenon.render.CCRenderState;
import definedentity.xenon.render.lighting.LC;
import definedentity.xenon.render.pipeline.VertexAttribute;
import definedentity.xenon.vec.Transformation;
import definedentity.xenon.vec.Vector3;

/** Uses the position of the lightmatrix to compute LC if not provided */
public class LightCoordAttribute extends VertexAttribute<LC[]> {

    public static final AttributeKey<LC[]> attributeKey = AttributeKey.create("light_coord", LC[]::new);

    private final Vector3 vec = new Vector3(); // for computation

    private LC[] lcRef;

    public LightCoordAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        lcRef = ccrs.model.getAttribute(attributeKey);
        if (ccrs.model.hasAttribute(attributeKey)) {
            return lcRef != null;
        }

        ccrs.pipeline.addDependency(ccrs.sideAttrib);
        ccrs.pipeline.addRequirement(Transformation.operationIndex);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        if (lcRef != null) {
            ccrs.lc.set(lcRef[ccrs.vertexIndex]);
        } else {
            ccrs.lc.compute(vec.set(ccrs.vert.vec), ccrs.side);
        }
    }
}
