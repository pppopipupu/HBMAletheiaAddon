package com.pppopipupu.aletheia.tileentity;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.hbm.config.VersatileConfig;
import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.recipes.MachineRecipes;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.sound.AudioWrapper;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.IUpgradeInfoProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.BobMathUtil;
import com.hbm.util.i18n.I18nUtil;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;
import com.pppopipupu.aletheia.inventory.ContainerMachineSchrabidiumTransmutator;
import com.pppopipupu.aletheia.inventory.GUIMachineSchrabidiumTransmutator;

import api.hbm.energymk2.IBatteryItem;
import api.hbm.energymk2.IEnergyReceiverMK2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class TileEntityMachineSchrabidiumTransmutator extends TileEntityMachineBase
    implements IEnergyReceiverMK2, IGUIProvider, IUpgradeInfoProvider {

    public long power = 0;
    public int process = 0;
    public static final long maxPower = 5_000_000;
    public static final int baseProgress = 600;
    private static final int baseConsumption = 8_000;

    private AudioWrapper audio;
    private UpgradeManagerNT upgradeManager = new UpgradeManagerNT(this);

    public int progress = baseProgress;
    private int consumption;

    private static final int[] slots_io = new int[] { 0, 1, 2, 3 };

    public TileEntityMachineSchrabidiumTransmutator() {
        super(4);
    }

    @Override
    public String getName() {
        return "container.machine_schrabidium_transmutator";
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        switch (i) {
            case 0:
                if (MachineRecipes.mODE(stack, OreDictManager.U.ingot())) return true;
                break;
            case 3:
                if (stack.getItem() instanceof IBatteryItem) return true;
                break;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        power = nbt.getLong("power");
        process = nbt.getInteger("process");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setLong("power", power);
        nbt.setInteger("process", process);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return slots_io;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack stack, int j) {
        if (i == 2) return false;
        if (i == 1) return true;
        if (i == 3)
            return stack.getItem() instanceof IBatteryItem && ((IBatteryItem) stack.getItem()).getCharge(stack) == 0;
        return false;
    }

    public boolean canProcess() {
        int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
        return power >= consumption && slots[0] != null
            && MachineRecipes.mODE(slots[0], OreDictManager.U.ingot())
            && (slots[1] == null || (slots[1].getItem() == VersatileConfig.getTransmutatorItem()
                && slots[1].stackSize + mult <= slots[1].getMaxStackSize()));
    }

    public boolean isProcessing() {
        return process > 0;
    }

    public void process() {
        process++;
        power -= consumption;
        if (power < 0) power = 0;

        if (process >= progress) {
            process = 0;

            slots[0].stackSize--;
            if (slots[0].stackSize <= 0) {
                slots[0] = null;
            }

            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
            if (slots[1] == null) {
                slots[1] = new ItemStack(VersatileConfig.getTransmutatorItem(), mult);
            } else {
                slots[1].stackSize += mult;
            }

            this.worldObj.playSoundEffect(
                this.xCoord,
                this.yCoord,
                this.zCoord,
                "ambient.weather.thunder",
                10000.0F,
                0.8F + this.worldObj.rand.nextFloat() * 0.2F);
        }
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            this.updateConnections();
            power = Library.chargeTEFromItems(slots, 3, power, maxPower);

            upgradeManager.checkSlots(slots, 2, 2);
            int speedLevel = upgradeManager.getLevel(UpgradeType.SPEED);
            int overLevel = upgradeManager.getLevel(UpgradeType.OVERDRIVE);

            progress = baseProgress * (4 - speedLevel) / 4 / (overLevel * overLevel + 1);
            consumption = baseConsumption * (speedLevel + 1) * (overLevel * overLevel + 1);

            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                int speedFactor = 1 + uCount * 4;
                progress = Math.max(progress / speedFactor, 1);
                consumption = (int) (consumption * Math.pow(0.5D, uCount));
            }

            if (canProcess()) {
                process();
            } else {
                process = 0;
            }

            this.networkPackNT(50);
        } else {
            if (process > 0) {
                if (audio == null) {
                    audio = createAudioLoop();
                    audio.startSound();
                } else if (!audio.isPlaying()) {
                    audio = rebootAudio(audio);
                }
                audio.updateVolume(getVolume(1F));
            } else {
                if (audio != null) {
                    audio.stopSound();
                    audio = null;
                }
            }
        }
    }

    public long getPowerScaled(long i) {
        return (power * i) / maxPower;
    }

    public int getProgressScaled(int i) {
        return (process * i) / progress;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(this.power);
        buf.writeInt(this.process);
        buf.writeInt(this.progress);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.power = buf.readLong();
        this.process = buf.readInt();
        this.progress = buf.readInt();
    }

    @Override
    public AudioWrapper createAudioLoop() {
        return MainRegistry.proxy.getLoopedSound("hbm:weapon.tauChargeLoop", xCoord, yCoord, zCoord, 1.0F, 10F, 1.0F);
    }

    private void updateConnections() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            this.trySubscribe(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir);
        }
    }

    @Override
    public void onChunkUnload() {
        if (audio != null) {
            audio.stopSound();
            audio = null;
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (audio != null) {
            audio.stopSound();
            audio = null;
        }
    }

    @Override
    public void setPower(long i) {
        power = i;
    }

    @Override
    public long getPower() {
        return power;
    }

    @Override
    public long getMaxPower() {
        return maxPower;
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerMachineSchrabidiumTransmutator(player.inventory, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GUIMachineSchrabidiumTransmutator(player.inventory, this);
    }

    @Override
    public boolean canProvideInfo(UpgradeType type, int level, boolean extendedInfo) {
        return type == UpgradeType.SPEED || type == UpgradeType.OVERDRIVE;
    }

    @Override
    public void provideInfo(UpgradeType type, int level, List<String> info, boolean extendedInfo) {
        if (type == UpgradeType.SPEED) {
            info.add(
                EnumChatFormatting.GREEN
                    + I18nUtil.resolveKey(IUpgradeInfoProvider.KEY_DELAY, "-" + (level * 25) + "%"));
            info.add(
                EnumChatFormatting.RED
                    + I18nUtil.resolveKey(IUpgradeInfoProvider.KEY_CONSUMPTION, "+" + (level * 100) + "%"));
        }
        if (type == UpgradeType.OVERDRIVE) {
            info.add((BobMathUtil.getBlink() ? EnumChatFormatting.RED : EnumChatFormatting.DARK_GRAY) + "YES");
        }
    }

    @Override
    public HashMap<UpgradeType, Integer> getValidUpgrades() {
        HashMap<UpgradeType, Integer> upgrades = new HashMap<>();
        upgrades.put(UpgradeType.SPEED, 3);
        upgrades.put(UpgradeType.OVERDRIVE, 3);
        return upgrades;
    }
}
