package de.technikforlife.firstaid.damagesystem.capability;

import de.technikforlife.firstaid.damagesystem.PlayerDamageModel;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityExtendedHealthSystem {

    @CapabilityInject(PlayerDamageModel.class)
    public static Capability<PlayerDamageModel> CAP_EXTENDED_HEALTH_SYSTEM;

    public static void register() {
        CapabilityManager.INSTANCE.register(PlayerDamageModel.class, new Capability.IStorage<PlayerDamageModel>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<PlayerDamageModel> capability, PlayerDamageModel instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<PlayerDamageModel> capability, PlayerDamageModel instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }
        , () -> new PlayerDamageModel(null));
    }
}