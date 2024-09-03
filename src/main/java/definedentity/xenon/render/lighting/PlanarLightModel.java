package definedentity.xenon.render.lighting;

import definedentity.xenon.colour.ColourRGBA;
import definedentity.xenon.render.CCRenderState;
import definedentity.xenon.render.pipeline.IVertexOperation;

/** Faster precomputed version of LightModel that only works for axis planar sides */
public class PlanarLightModel implements IVertexOperation {

    public static PlanarLightModel standardLightModel = LightModel.standardLightModel.reducePlanar();

    public int[] colours;

    public PlanarLightModel(int[] colours) {
        this.colours = colours;
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        if (!ccrs.computeLighting) {
            return false;
        }

        ccrs.pipeline.addDependency(ccrs.sideAttrib);
        ccrs.pipeline.addDependency(ccrs.colourAttrib);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        ccrs.colour = ColourRGBA.multiply(ccrs.colour, colours[ccrs.side]);
    }

    @Override
    public int operationID() {
        return LightModel.operationIndex;
    }
}
