package definedentity.xenon.render;

import definedentity.xenon.render.lighting.LC;
import definedentity.xenon.render.pipeline.IVertexSource;
import definedentity.xenon.render.pipeline.attribute.AttributeKey;
import definedentity.xenon.render.pipeline.attribute.LightCoordAttribute;
import definedentity.xenon.render.pipeline.attribute.SideAttribute;
import definedentity.xenon.vec.Cuboid6;
import definedentity.xenon.vec.Vertex5;

public class BlockRenderer {

    public static class BlockFace implements IVertexSource {

        public Vertex5[] verts = new Vertex5[] {new Vertex5(), new Vertex5(), new Vertex5(), new Vertex5()};
        public LC[] lightCoords = new LC[] {new LC(), new LC(), new LC(), new LC()};
        public boolean lcComputed = false;
        public int side;

        @Override
        public Vertex5[] getVertices() {
            return verts;
        }

        @Override
        public <T> T getAttribute(AttributeKey<T> attr) {
            return LightCoordAttribute.attributeKey.equals(attr) && lcComputed ? (T) lightCoords : null;
        }

        @Override
        public boolean hasAttribute(AttributeKey<?> attr) {
            return SideAttribute.attributeKey.equals(attr)
                    || LightCoordAttribute.attributeKey.equals(attr) && lcComputed;
        }

        @Override
        public void prepareVertex(CCRenderState ccrs) {
            ccrs.side = side;
        }

        public BlockFace computeLightCoords() {
            if (!lcComputed) {
                for (int i = 0; i < 4; i++) {
                    lightCoords[i].compute(verts[i].vec, side);
                }
                lcComputed = true;
            }
            return this;
        }

        public BlockFace loadCuboidFace(Cuboid6 c, int side) {
            double x1 = c.min.x;
            double x2 = c.max.x;
            double y1 = c.min.y;
            double y2 = c.max.y;
            double z1 = c.min.z;
            double z2 = c.max.z;
            double u1;
            double u2;
            double v1;
            double v2;
            this.side = side;
            lcComputed = false;

            switch (side) {
                case 0:
                    u1 = x1;
                    v1 = z1;
                    u2 = x2;
                    v2 = z2;
                    verts[0].set(x1, y1, z2, u1, v2, 0);
                    verts[1].set(x1, y1, z1, u1, v1, 0);
                    verts[2].set(x2, y1, z1, u2, v1, 0);
                    verts[3].set(x2, y1, z2, u2, v2, 0);
                    break;
                case 1:
                    u1 = x1;
                    v1 = z1;
                    u2 = x2;
                    v2 = z2;
                    verts[0].set(x2, y2, z2, u2, v2, 1);
                    verts[1].set(x2, y2, z1, u2, v1, 1);
                    verts[2].set(x1, y2, z1, u1, v1, 1);
                    verts[3].set(x1, y2, z2, u1, v2, 1);
                    break;
                case 2:
                    u1 = 1 - x1;
                    v1 = 1 - y2;
                    u2 = 1 - x2;
                    v2 = 1 - y1;
                    verts[0].set(x1, y1, z1, u1, v2, 2);
                    verts[1].set(x1, y2, z1, u1, v1, 2);
                    verts[2].set(x2, y2, z1, u2, v1, 2);
                    verts[3].set(x2, y1, z1, u2, v2, 2);
                    break;
                case 3:
                    u1 = x1;
                    v1 = 1 - y2;
                    u2 = x2;
                    v2 = 1 - y1;
                    verts[0].set(x2, y1, z2, u2, v2, 3);
                    verts[1].set(x2, y2, z2, u2, v1, 3);
                    verts[2].set(x1, y2, z2, u1, v1, 3);
                    verts[3].set(x1, y1, z2, u1, v2, 3);
                    break;
                case 4:
                    u1 = z1;
                    v1 = 1 - y2;
                    u2 = z2;
                    v2 = 1 - y1;
                    verts[0].set(x1, y1, z2, u2, v2, 4);
                    verts[1].set(x1, y2, z2, u2, v1, 4);
                    verts[2].set(x1, y2, z1, u1, v1, 4);
                    verts[3].set(x1, y1, z1, u1, v2, 4);
                    break;
                case 5:
                    u1 = 1 - z1;
                    v1 = 1 - y2;
                    u2 = 1 - z2;
                    v2 = 1 - y1;
                    verts[0].set(x2, y1, z1, u1, v2, 5);
                    verts[1].set(x2, y2, z1, u1, v1, 5);
                    verts[2].set(x2, y2, z2, u2, v1, 5);
                    verts[3].set(x2, y1, z2, u2, v2, 5);
            }
            return this;
        }
    }

    public static class FullBlock implements IVertexSource {

        public Vertex5[] verts = CCModel.quadModel(24).generateBlock(0, Cuboid6.full).verts;
        public LC[] lightCoords = new LC[24];

        public FullBlock() {
            for (int i = 0; i < 24; i++) {
                lightCoords[i] = new LC().compute(verts[i].vec, i / 4);
            }
        }

        @Override
        public Vertex5[] getVertices() {
            return verts;
        }

        @Override
        public <T> T getAttribute(AttributeKey<T> attr) {
            return LightCoordAttribute.attributeKey.equals(attr) ? (T) lightCoords : null;
        }

        @Override
        public boolean hasAttribute(AttributeKey<?> attr) {
            return SideAttribute.attributeKey.equals(attr) || LightCoordAttribute.attributeKey.equals(attr);
        }

        @Override
        public void prepareVertex(CCRenderState ccrs) {
            ccrs.side = ccrs.vertexIndex >> 2;
        }
    }

    public static ThreadLocal<FullBlock> fullBlocks = ThreadLocal.withInitial(FullBlock::new);

    public static void renderFullBlock(CCRenderState state, int sideMask) {
        state.setModel(fullBlocks.get());
        renderFaces(state, sideMask);
    }

    /**
     * Renders faces of a block-like model based on a sideMask. Eg for side 2, verts 8-11 will be rendered
     *
     * @param sideMask A mask of faces not to render
     */
    public static void renderFaces(CCRenderState state, int sideMask) {
        if (sideMask == 0x3F) {
            return;
        }
        for (int s = 0; s < 6; s++) {
            if ((sideMask & 1 << s) == 0) {
                state.setVertexRange(s * 4, (s + 1) * 4);
                state.render();
            }
        }
    }

    private static ThreadLocal<BlockFace> blockFaces = ThreadLocal.withInitial(BlockFace::new);

    /**
     * Renders faces of a cuboid with texture coordinates mapped to match a standard minecraft block
     *
     * @param bounds The bounding cuboid to render
     * @param sideMask A mask of faces not to render
     */
    public static void renderCuboid(CCRenderState state, Cuboid6 bounds, int sideMask) {
        if (sideMask == 0x3F) {
            return;
        }
        BlockFace face = blockFaces.get();
        state.setModel(face);
        for (int s = 0; s < 6; s++) {
            if ((sideMask & 1 << s) == 0) {
                face.loadCuboidFace(bounds, s);
                state.render();
            }
        }
    }
}
