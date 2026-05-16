package com.robertx22.mine_and_slash.database.data.profession;

import com.robertx22.mine_and_slash.database.data.profession.screen.CraftingStationMenu;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProfessionBlock extends BaseEntityBlock implements WorldlyContainerHolder {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public String profession;

    public ProfessionBlock(String profession) {
        super(Properties.of().strength(2.5F).noOcclusion());
        this.profession = profession;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));


    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {

        List<ItemStack> all = new ArrayList<>();

        BlockEntity blockentity = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockentity instanceof ProfessionBlockEntity be) {
            all.add(asItem().getDefaultInstance());

            for (ItemStack s : be.inventory.getAllStacks(ProfessionBlockEntity.INPUTS)) {
                all.add(s);
            }
            for (ItemStack s : be.inventory.getAllStacks(ProfessionBlockEntity.OUTPUTS)) {
                all.add(s);
            }
        }

        return all;
    }


    // we can't use this for dropping the inventory because cancelling blockplacing event then dupes items with this..
    @Override
    public void onRemove(BlockState pState, Level level, BlockPos p, BlockState pNewState, boolean pIsMoving) {

        super.onRemove(pState, level, p, pNewState, pIsMoving);
    }

    // add rotation
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player p, BlockHitResult pHit) {

        if (!pLevel.isClientSide) {
            ProfessionBlockEntity be = (ProfessionBlockEntity) pLevel.getBlockEntity(pPos);
            p.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.empty();
                }

                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                    return new CraftingStationMenu(be.getProfession().GUID(), pContainerId, pPlayerInventory, be);
                }
            });

        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ProfessionBlockEntity(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
        return createTickerHelper(type, SlashBlockEntities.PROFESSION.get(), pLevel.isClientSide ? null : ProfessionBlock::serverTick);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ProfessionBlockEntity be) {
        be.tick(level);
    }

    @Override
    public WorldlyContainer getContainer(BlockState pState, LevelAccessor pLevel, BlockPos pPos) {
        if (pLevel.getBlockEntity(pPos) instanceof ProfessionBlockEntity be) {
            return be.inventory;
        }
        return null;
    }
}
