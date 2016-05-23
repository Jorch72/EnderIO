package crazypants.enderio.conduit.facade;

import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.paint.PainterUtil2;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemConduitFacade extends ItemBlock implements IAdvancedTooltipProvider, IResourceTooltipProvider {

  public ItemConduitFacade(Block block, String name) {
    super(block);
    setMaxStackSize(64);
    setHasSubtypes(true);
    setRegistryName(name);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return EnumFacadeType.getTypeFromMeta(stack.getMetadata()).getUnlocName(this);
  }

  @Override
  public String getUnlocalizedName() {
    return "item.enderio." + ModObject.itemConduitFacade.name();
  }
  
  @Override 
 public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

    if(world.isRemote) {
      return EnumActionResult.SUCCESS;
    }

    BlockPos placeAt = pos.offset(side);

    if (player.canPlayerEdit(placeAt, side, itemStack) && PainterUtil2.getSourceBlock(itemStack) != null) {
      if (world.isAirBlock(placeAt)) {
        world.setBlockState(placeAt, EnderIO.blockConduitBundle.getDefaultState());
        IConduitBundle bundle = (IConduitBundle) world.getTileEntity(placeAt);
        IBlockState bs = PainterUtil2.getSourceBlock(itemStack);
        bundle.setPaintSource(bs);
        bundle.setFacadeType(EnumFacadeType.values()[itemStack.getItemDamage()]);
        ConduitUtil.playPlaceSound(bs.getBlock().getSoundType(), world, pos.getX(), pos.getY(), pos.getZ());
        if (!player.capabilities.isCreativeMode) {
          itemStack.stackSize--;
        }
        return EnumActionResult.SUCCESS;
      } else {
        Block blockAt = world.getBlockState(placeAt).getBlock();
        if (blockAt == EnderIO.blockConduitBundle) {
          if(((BlockConduitBundle) blockAt)
              .handleFacadeClick(world, placeAt, player, side.getOpposite(),
              (IConduitBundle) world.getTileEntity(placeAt), itemStack, hand)) {
            return EnumActionResult.SUCCESS;
          }
        }
      }
    }

    return EnumActionResult.PASS;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    super.addInformation(item, par2EntityPlayer, list, par4);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    if (PainterUtil2.getSourceBlock(itemstack) == null) {
      list.add(EnderIO.lang.localize("item.itemConduitFacade.tooltip.notpainted"));
    } else {
      list.add(PainterUtil2.getTooltTipText(itemstack));
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    if (EnumFacadeType.getTypeFromMeta(itemstack.getMetadata()) == EnumFacadeType.HARDENED) {
      list.add("");
      list.add(EnderIO.lang.localizeExact(getUnlocalizedName(itemstack) + ".tooltip"));
    }
  }
 
}
