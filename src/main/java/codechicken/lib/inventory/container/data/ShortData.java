package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class ShortData extends AbstractDataStore<Short> {

    public ShortData() {
        super((short) 0);
    }

    public ShortData(short defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(MCDataOutput buf) {
        buf.writeShort(value);
    }

    @Override
    public void fromBytes(MCDataInput buf) {
        value = buf.readShort();
    }

    @Override
    public Tag toTag() {
        return ShortTag.valueOf(value);
    }

    @Override
    public void fromTag(Tag tag) {
        value = ((NumericTag) tag).getAsShort();
    }
}
