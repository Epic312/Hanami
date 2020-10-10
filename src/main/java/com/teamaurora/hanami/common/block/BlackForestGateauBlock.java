package com.teamaurora.hanami.common.block;

import com.teamaurora.hanami.core.registry.HanamiEffects;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

// pretty much a vanilla copy paste. don't mess with this unless you know what you're doing. it just works(TM)
public class BlackForestGateauBlock extends Block {
    public static final IntegerProperty BITES = IntegerProperty.create("bites",0,13);
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(2.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(3.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(4.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(5.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(6.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(7.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(8.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(9.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(10.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(11.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(12.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(13.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), Block.makeCuboidShape(14.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D),};

    public BlackForestGateauBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(BITES, Integer.valueOf(0)));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(BITES)];
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            ItemStack itemstack = player.getHeldItem(handIn);
            if (this.func_226911_a_(worldIn, pos, state, player).isSuccessOrConsume()) {
                return ActionResultType.SUCCESS;
            }

            if (itemstack.isEmpty()) {
                return ActionResultType.CONSUME;
            }
        }

        return this.func_226911_a_(worldIn, pos, state, player);
    }

    private ActionResultType func_226911_a_(IWorld world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        if (!playerEntity.canEat(false)) {
            return ActionResultType.PASS;
        } else {
            playerEntity.addStat(Stats.EAT_CAKE_SLICE);

            int amplifierUnsalutary = playerEntity.isPotionActive(HanamiEffects.UNSALUTARY.get()) ? playerEntity.getActivePotionEffect(HanamiEffects.UNSALUTARY.get()).getAmplifier() : -1;
            int amplifierNourishing = playerEntity.isPotionActive(HanamiEffects.NOURISHING.get()) ? playerEntity.getActivePotionEffect(HanamiEffects.NOURISHING.get()).getAmplifier() : -1;

            playerEntity.getFoodStats().addStats(Math.max(6 - 2 * (amplifierUnsalutary + 1) + 2 * (amplifierNourishing + 1), 0), Math.max(0.3F - 0.2F * (amplifierUnsalutary + 1) + 0.2F * (amplifierNourishing + 1), 0));
            playerEntity.removeActivePotionEffect(HanamiEffects.UNSALUTARY.get());
            playerEntity.addPotionEffect(new EffectInstance(HanamiEffects.UNSALUTARY.get(), 1600, amplifierUnsalutary < 9 ? amplifierUnsalutary + 1 : 9, false, false, true));

            int bites = blockState.get(BITES);

            if (bites < 13) {
                world.setBlockState(blockPos, blockState.with(BITES, bites + 1), 3);
            } else {
                world.removeBlock(blockPos, false);
            }
            return ActionResultType.SUCCESS;
        }
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BITES);
    }

    // these comments are gibberish because for some odd reason i was getting errors from them. wtf java
    /**
     * @deprecated call via {@link(World,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
        return (7 - blockState.get(BITES));
    }

    /**
     * @deprecated call via {@link()} whenever possible. Implementing/overriding
     * is fine.
     */
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
        return false;
    }
}