package com.teamaurora.hanami.common.block;

import com.teamabnormals.abnormals_core.common.blocks.wood.AbnormalsLeavesBlock;
import com.teamaurora.hanami.client.particle.HanamiParticles;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class SakuraLeavesBlock extends AbnormalsLeavesBlock {

    public SakuraLeavesBlock(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState blockState, World world, BlockPos pos, Random rand) {
        super.animateTick(blockState, world, pos, rand);
        int particleChance = 60;

        if (world.isRaining() || world.isThundering()) particleChance -= 15;
        if (world.getCurrentMoonPhaseFactor() == 1.0) particleChance -= 30;

        if (rand.nextInt(particleChance) == 0) {
            BlockPos blockpos = pos.down();

            if (world.isAirBlock(blockpos)) {
                double x = (float) pos.getX() + rand.nextFloat();
                double y = (float) pos.getY() - 0.05D;
                double z = (float) pos.getZ() + rand.nextFloat();
                double wind = 0;

                if (world.isRaining() || world.isThundering()) wind += 0.05F;
                if (world.getCurrentMoonPhaseFactor() == 1.0) wind += 0.1F;

                world.addParticle(HanamiParticles.BLOSSOM.get(), x, y, z, wind, 0.0F, 0.0F);
            }
        }
    }
}
