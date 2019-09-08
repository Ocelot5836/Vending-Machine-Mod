package com.ocelot.blocks;

import java.util.ArrayList;
import java.util.List;

import com.ocelot.VendingMachineMod;
import com.ocelot.init.ModItems;
import com.ocelot.tileentity.TileEntityVendingMachine;
import com.ocelot.vending.VendingMachine;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockVendingMachine extends ModBlock
{
	public static final BooleanProperty TOP = BooleanProperty.create("top");

	public static final VoxelShape[] TOP_SHAPE = generateShape(true);
	public static final VoxelShape[] BOTTOM_SHAPE = generateShape(false);

	public BlockVendingMachine(String name, EnumDyeColor color)
	{
		super(color.getName() + "_" + name, Block.Properties.from(Blocks.TERRACOTTA));
		this.setDefaultState(this.stateContainer.getBaseState().with(TOP, false).with(FACING, EnumFacing.NORTH).with(WATERLOGGED, false));
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		BlockPos vendingPos = state.get(TOP) ? pos.down() : pos;
		if (world.getTileEntity(vendingPos) instanceof TileEntityVendingMachine)
		{
			TileEntityVendingMachine te = (TileEntityVendingMachine) world.getTileEntity(vendingPos);
			VendingMachine vendingMachine = te.getVendingMachine();
			if (!vendingMachine.isEditing())
			{
				ItemStack heldItem = player.getHeldItem(hand);
				if (heldItem.getItem() == ModItems.KEY)
				{
					if (vendingMachine.getOwner() == null)
					{
						vendingMachine.setOwner(player);
						player.sendStatusMessage(new TextComponentTranslation(ModItems.KEY.getTranslationKey() + ".bind", I18n.format(this.getTranslationKey()), vendingPos.getX(), vendingPos.getY(), vendingPos.getZ(), player.getDisplayName().getFormattedText()), true);
					}
					else if (vendingMachine.getOwner().equals(player.getUniqueID()))
					{
						if (!world.getBlockState(pos).get(TOP) && side == world.getBlockState(pos).get(FACING).rotateYCCW() && hitY < 0.75)
						{
							VendingMachineMod.getGuiOpener().openGui(VendingMachineMod.GUI_VENDING_MACHINE_INTERNAL_ID, player, world, vendingPos);
						}
						else
						{
							vendingMachine.setEditing(true);
							VendingMachineMod.getGuiOpener().openGui(VendingMachineMod.GUI_VENDING_MACHINE_EDIT_ID, player, world, vendingPos);
						}
					}
					else
					{
						player.sendStatusMessage(new TextComponentTranslation(ModItems.KEY.getTranslationKey() + ".invalid", I18n.format(this.getTranslationKey()), vendingMachine.getOwnerName()), true);
					}
					return true;
				}
				if (vendingMachine.getOwner() == null)
				{
					player.sendStatusMessage(new TextComponentTranslation("block." + VendingMachineMod.MOD_ID + ".vending_machine.use"), true);
					return true;
				}
				VendingMachineMod.getGuiOpener().openGui(VendingMachineMod.GUI_VENDING_MACHINE_ID, player, world, vendingPos);
			}
			else
			{
				player.sendStatusMessage(new TextComponentTranslation("block." + VendingMachineMod.MOD_ID + ".vending_machine.edit", I18n.format(this.getTranslationKey()), vendingPos.getX(), vendingPos.getY(), vendingPos.getZ(), vendingMachine.getOwnerName()), true);
			}
			return true;
		}
		return false;
	}

	@Override
	@Deprecated
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, IBlockReader world, BlockPos pos)
	{
		BlockPos vendingPos = state.get(TOP) ? pos.down() : pos;
		if (world.getTileEntity(vendingPos) instanceof TileEntityVendingMachine)
		{
			VendingMachine vendingMachine = ((TileEntityVendingMachine) world.getTileEntity(vendingPos)).getVendingMachine();
			if (vendingMachine.getOwner() != null && !vendingMachine.getOwner().equals(player.getUniqueID()))
			{
				return 0.0f;
			}
		}
		return super.getPlayerRelativeBlockHardness(state, player, world, pos);
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader reader, BlockPos pos)
	{
		EnumFacing facing = state.get(FACING);
		return state.get(TOP) ? TOP_SHAPE[facing.getHorizontalIndex()] : BOTTOM_SHAPE[facing.getHorizontalIndex()];
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		boolean waterlogged = false;
		IFluidState fluidState = world.getFluidState(pos.up());
		if (fluidState.isTagged(FluidTags.WATER) && fluidState.getLevel() == 8)
		{
			waterlogged = true;
		}
		world.setBlockState(pos.up(), this.getDefaultState().with(TOP, true).with(FACING, state.get(FACING)).with(WATERLOGGED, waterlogged), 3);
	}

	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, IBlockState state)
	{
		BlockPos otherPos = pos.offset(state.get(TOP) ? EnumFacing.DOWN : EnumFacing.UP);
		world.setBlockState(otherPos, world.getFluidState(otherPos).getBlockState(), 3);
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context)
	{
		BlockPos pos = context.getPos();
		return pos.getY() < context.getWorld().getHeight() - 1 && context.getWorld().getBlockState(pos.up()).isReplaceable(context) ? super.getStateForPlacement(context).with(FACING, context.getPlacementHorizontalFacing().getOpposite()) : null;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return !state.get(TOP);
	}

	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader world)
	{
		return new TileEntityVendingMachine();
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(TOP, FACING, WATERLOGGED);
	}

	private static VoxelShape[] generateShape(boolean top)
	{
		List<List<VoxelShape>> shapeList = new ArrayList<List<VoxelShape>>();
		VoxelShape[] result = new VoxelShape[4];
		float offset = top ? -1 : 0;

		for (int i = 0; i < result.length; i++)
		{
			List<VoxelShape> shapes = new ArrayList<VoxelShape>();

			switch (i)
			{
			case 0:
			{
				// BOTTOM
				shapes.add(VoxelShapes.create(0, 0.062 + offset, 0, 1, 0.562 + offset, 0.875)); // BASE
				shapes.add(VoxelShapes.create(0, offset, 0, 0.062, 0.062 + offset, 0.062)); // LEG_4
				shapes.add(VoxelShapes.create(0.938, offset, 0, 1, 0.062 + offset, 0.062)); // LEG_3
				shapes.add(VoxelShapes.create(0, offset, 0.812, 0.062, 0.062 + offset, 0.875)); // LEG_1
				shapes.add(VoxelShapes.create(0.938, offset, 0.812, 1, 0.062 + offset, 0.875)); // LEG_2
				shapes.add(VoxelShapes.create(0.75, 0.562 + offset, 0, 1, 1 + offset, 0.875)); // WALL_RIGHT
				shapes.add(VoxelShapes.create(0, 0.562 + offset, 0, 0.062, 1 + offset, 0.875)); // WALL_LEFT
				shapes.add(VoxelShapes.create(0.062, 0.562 + offset, 0, 0.75, 1 + offset, 0.062)); // WALL_BACK
				shapes.add(VoxelShapes.create(0.062, 0.562 + offset, 0.781, 0.75, 1 + offset, 0.844)); // GLASS
				// shapes.add(VoxelShapes.create(0.062, 0.25 + offset, 0.875, 0.812, 0.438 + offset, 0.887)); // COLLECTION
				shapes.add(VoxelShapes.create(0.062, 0.688 + offset, 0.062, 0.75, 0.75 + offset, 0.75)); // CUBE

				// TOP
				shapes.add(VoxelShapes.create(0.75, 1 + offset, 0, 1, 1.938 + offset, 0.875)); // CUBE
				shapes.add(VoxelShapes.create(0, 1 + offset, 0, 0.062, 1.938 + offset, 0.875)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1 + offset, 0, 0.75, 1.938 + offset, 0.062)); // CUBE
				shapes.add(VoxelShapes.create(0, 1.938 + offset, 0, 1, 2 + offset, 0.875)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1 + offset, 0.062, 0.75, 1.062 + offset, 0.75)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1.312 + offset, 0.062, 0.75, 1.375 + offset, 0.75)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1.625 + offset, 0.062, 0.75, 1.688 + offset, 0.75)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1 + offset, 0.781, 0.75, 1.438 + offset, 0.844)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1.438 + offset, 0.781, 0.75, 1.938 + offset, 0.844)); // CUBE
				// shapes.add(VoxelShapes.create(0.781, 1.062 + offset, 0.875, 0.969, 1.625 + offset, 0.881)); // CUBE
				// shapes.add(VoxelShapes.create(0.781, 1.094 + offset, 0.881, 0.969, 1.594 + offset, 0.887)); // CUBE
				break;
			}
			case 1:
			{
				// BOTTOM
				shapes.add(VoxelShapes.create(0.125, 0.062 + offset, 0, 1, 0.562 + offset, 1)); // BASE
				shapes.add(VoxelShapes.create(0.938, offset, 0, 1, 0.062 + offset, 0.062)); // LEG_4
				shapes.add(VoxelShapes.create(0.938, offset, 0.938, 1, 0.062 + offset, 1)); // LEG_3
				shapes.add(VoxelShapes.create(0.125, offset, 0, 0.188, 0.062 + offset, 0.062)); // LEG_1
				shapes.add(VoxelShapes.create(0.125, offset, 0.938, 0.188, 0.062 + offset, 1)); // LEG_2
				shapes.add(VoxelShapes.create(0.125, 0.562 + offset, 0.75, 1, 1 + offset, 1)); // WALL_RIGHT
				shapes.add(VoxelShapes.create(0.125, 0.562 + offset, 0, 1, 1 + offset, 0.062)); // WALL_LEFT
				shapes.add(VoxelShapes.create(0.938, 0.562 + offset, 0.062, 1, 1 + offset, 0.75)); // WALL_BACK
				shapes.add(VoxelShapes.create(0.156, 0.562 + offset, 0.062, 0.219, 1 + offset, 0.75)); // GLASS
				// shapes.add(VoxelShapes.create(0.113, 0.25 + offset, 0.062, 0.125, 0.438 + offset, 0.812)); // COLLECTION
				shapes.add(VoxelShapes.create(0.25, 0.688 + offset, 0.062, 0.938, 0.75 + offset, 0.75)); // CUBE

				// TOP
				shapes.add(VoxelShapes.create(0.125, 1 + offset, 0.75, 1, 1.938 + offset, 1)); // CUBE
				shapes.add(VoxelShapes.create(0.125, 1 + offset, 0, 1, 1.938 + offset, 0.062)); // CUBE
				shapes.add(VoxelShapes.create(0.938, 1 + offset, 0.062, 1, 1.938 + offset, 0.75)); // CUBE
				shapes.add(VoxelShapes.create(0.125, 1.938 + offset, 0, 1, 2 + offset, 1)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1 + offset, 0.062, 0.938, 1.062 + offset, 0.75)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1.312 + offset, 0.062, 0.938, 1.375 + offset, 0.75)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1.625 + offset, 0.062, 0.938, 1.688 + offset, 0.75)); // CUBE
				shapes.add(VoxelShapes.create(0.156, 1 + offset, 0.062, 0.219, 1.438 + offset, 0.75)); // CUBE
				shapes.add(VoxelShapes.create(0.156, 1.438 + offset, 0.062, 0.219, 1.938 + offset, 0.75)); // CUBE
				// shapes.add(VoxelShapes.create(0.119, 1.062 + offset, 0.781, 0.125, 1.625 + offset, 0.969)); // CUBE
				// shapes.add(VoxelShapes.create(0.113, 1.094 + offset, 0.781, 0.119, 1.594 + offset, 0.969)); // CUBE
				break;
			}
			case 2:
			{
				// BOTTOM
				shapes.add(VoxelShapes.create(0, 0.062 + offset, 0.125, 1, 0.562 + offset, 1)); // BASE
				shapes.add(VoxelShapes.create(0.938, offset, 0.938, 1, 0.062 + offset, 1)); // LEG_4
				shapes.add(VoxelShapes.create(0, offset, 0.938, 0.062, 0.062 + offset, 1)); // LEG_3
				shapes.add(VoxelShapes.create(0.938, offset, 0.125, 1, 0.062 + offset, 0.188)); // LEG_1
				shapes.add(VoxelShapes.create(0, offset, 0.125, 0.062, 0.062 + offset, 0.188)); // LEG_2
				shapes.add(VoxelShapes.create(0, 0.562 + offset, 0.125, 0.25, 1 + offset, 1)); // WALL_RIGHT
				shapes.add(VoxelShapes.create(0.938, 0.562 + offset, 0.125, 1, 1 + offset, 1)); // WALL_LEFT
				shapes.add(VoxelShapes.create(0.25, 0.562 + offset, 0.938, 0.938, 1 + offset, 1)); // WALL_BACK
				shapes.add(VoxelShapes.create(0.25, 0.562 + offset, 0.156, 0.938, 1 + offset, 0.219)); // GLASS
				// shapes.add(VoxelShapes.create(0.188, 0.25 + offset, 0.113, 0.938, 0.438 + offset, 0.125)); // COLLECTION
				shapes.add(VoxelShapes.create(0.25, 0.688 + offset, 0.25, 0.938, 0.75 + offset, 0.938)); // CUBE

				// TOP
				shapes.add(VoxelShapes.create(0, 1 + offset, 0.125, 0.25, 1.938 + offset, 1)); // CUBE
				shapes.add(VoxelShapes.create(0.938, 1 + offset, 0.125, 1, 1.938 + offset, 1)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1 + offset, 0.938, 0.938, 1.938 + offset, 1)); // CUBE
				shapes.add(VoxelShapes.create(0, 1.938 + offset, 0.125, 1, 2 + offset, 1)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1 + offset, 0.25, 0.938, 1.062 + offset, 0.938)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1.312 + offset, 0.25, 0.938, 1.375 + offset, 0.938)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1.625 + offset, 0.25, 0.938, 1.688 + offset, 0.938)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1 + offset, 0.156, 0.938, 1.438 + offset, 0.219)); // CUBE
				shapes.add(VoxelShapes.create(0.25, 1.438 + offset, 0.156, 0.938, 1.938 + offset, 0.219)); // CUBE
				// shapes.add(VoxelShapes.create(0.031, 1.062 + offset, 0.119, 0.219, 1.625 + offset, 0.125)); // CUBE
				// shapes.add(VoxelShapes.create(0.031, 1.094 + offset, 0.113, 0.219, 1.594 + offset, 0.119)); // CUBE
				break;
			}
			case 3:
			{
				// BOTTOM
				shapes.add(VoxelShapes.create(0, 0.062 + offset, 0, 0.875, 0.562 + offset, 1)); // BASE
				shapes.add(VoxelShapes.create(0, offset, 0.938, 0.062, 0.062 + offset, 1)); // LEG_4
				shapes.add(VoxelShapes.create(0, offset, 0, 0.062, 0.062 + offset, 0.062)); // LEG_3
				shapes.add(VoxelShapes.create(0.812, offset, 0.938, 0.875, 0.062 + offset, 1)); // LEG_1
				shapes.add(VoxelShapes.create(0.812, offset, 0, 0.875, 0.062 + offset, 0.062)); // LEG_2
				shapes.add(VoxelShapes.create(0, 0.562 + offset, 0, 0.875, 1 + offset, 0.25)); // WALL_RIGHT
				shapes.add(VoxelShapes.create(0, 0.562 + offset, 0.938, 0.875, 1 + offset, 1)); // WALL_LEFT
				shapes.add(VoxelShapes.create(0, 0.562 + offset, 0.25, 0.062, 1 + offset, 0.938)); // WALL_BACK
				shapes.add(VoxelShapes.create(0.781, 0.562 + offset, 0.25, 0.844, 1 + offset, 0.938)); // GLASS
				// shapes.add(VoxelShapes.create(0.875, 0.25 + offset, 0.188, 0.887, 0.438 + offset, 0.938)); // COLLECTION
				shapes.add(VoxelShapes.create(0.062, 0.688 + offset, 0.25, 0.75, 0.75 + offset, 0.938)); // CUBE

				// TOP
				shapes.add(VoxelShapes.create(0, 1 + offset, 0, 0.875, 1.938 + offset, 0.25)); // CUBE
				shapes.add(VoxelShapes.create(0, 1 + offset, 0.938, 0.875, 1.938 + offset, 1)); // CUBE
				shapes.add(VoxelShapes.create(0, 1 + offset, 0.25, 0.062, 1.938 + offset, 0.938)); // CUBE
				shapes.add(VoxelShapes.create(0, 1.938 + offset, 0, 0.875, 2 + offset, 1)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1 + offset, 0.25, 0.75, 1.062 + offset, 0.938)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1.312 + offset, 0.25, 0.75, 1.375 + offset, 0.938)); // CUBE
				shapes.add(VoxelShapes.create(0.062, 1.625 + offset, 0.25, 0.75, 1.688 + offset, 0.938)); // CUBE
				shapes.add(VoxelShapes.create(0.781, 1 + offset, 0.25, 0.844, 1.438 + offset, 0.938)); // CUBE
				shapes.add(VoxelShapes.create(0.781, 1.438 + offset, 0.25, 0.844, 1.938 + offset, 0.938)); // CUBE
				// shapes.add(VoxelShapes.create(0.875, 1.062 + offset, 0.031, 0.881, 1.625 + offset, 0.219)); // CUBE
				// shapes.add(VoxelShapes.create(0.881, 1.094 + offset, 0.031, 0.887, 1.594 + offset, 0.219)); // CUBE
				break;
			}
			}

			shapeList.add(shapes);
		}

		for (int i = 0; i < result.length; i++)
		{
			VoxelShape axis = VoxelShapes.empty();
			for (VoxelShape shape : shapeList.get(i))
			{
				axis = VoxelShapes.combine(axis, shape, IBooleanFunction.OR);
			}
			result[i] = axis.simplify();
		}

		return result;
	}
}