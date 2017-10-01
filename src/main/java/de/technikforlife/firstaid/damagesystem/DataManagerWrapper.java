package de.technikforlife.firstaid.damagesystem;

import de.technikforlife.firstaid.FirstAid;
import de.technikforlife.firstaid.damagesystem.capability.PlayerDataManager;
import de.technikforlife.firstaid.network.MessageApplyAbsorption;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
 * This is a hack to intervene all calls to absorption. It's not optimal but it's the best I could come up with without a coremod
 * + this should be compatible with other mods which do so as I respect the parent in any other case.
 */
public class DataManagerWrapper extends EntityDataManager {
    private final EntityPlayer player;
    private final EntityDataManager parent;

    public DataManagerWrapper(EntityPlayer player, EntityDataManager parent) {
        super(player);
        this.player = player;
        this.parent = parent;
    }

    @Override
    @Nonnull
    public <T> T get(@Nonnull DataParameter<T> key) {
        if (key == EntityPlayer.ABSORPTION)
            //noinspection unchecked
            parent.set(key, (T) PlayerDataManager.getDamageModel(player).getAbsorption());
        return parent.get(key);
    }

    @Override
    public <T> void set(@Nonnull DataParameter<T> key, @Nonnull T value) {
        if (key == EntityPlayer.ABSORPTION) {
            float floatValue = (Float) value;
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if (playerMP.connection != null) //also fired when connecting, ignore(otherwise the net handler would crash)
                FirstAid.NETWORKING.sendTo(new MessageApplyAbsorption(floatValue), playerMP);
            PlayerDataManager.getDamageModel(player).setAbsorption(floatValue);
        }
        parent.set(key, value);
    }
    //WRAPPER BELOW


    @Override
    public <T> void register(DataParameter<T> key, @Nonnull T value) {
        parent.register(key, value);
    }

    @Override
    public <T> void setDirty(@Nonnull DataParameter<T> key) {
        parent.setDirty(key);
    }

    @Override
    public boolean isDirty() {
        return parent.isDirty();
    }

    @Nullable
    @Override
    public List<DataEntry<?>> getDirty() {
        return parent.getDirty();
    }

    @Override
    public void writeEntries(PacketBuffer buf) throws IOException {
        parent.writeEntries(buf);
    }

    @Nullable
    @Override
    public List<DataEntry<?>> getAll() {
        return parent.getAll();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setEntryValues(List<DataEntry<?>> entriesIn) {
        parent.setEntryValues(entriesIn);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public <T> void setEntryValue(DataEntry<T> target, DataEntry<?> source) {
        parent.setEntryValue(target, source);
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public void setClean() {
        parent.setClean();
    }

    @Override
    @Nonnull
    public <T> DataEntry<T> getEntry(DataParameter<T> key) {
        return parent.getEntry(key);
    }
}
