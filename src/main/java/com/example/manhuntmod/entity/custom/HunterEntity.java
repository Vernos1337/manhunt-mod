package com.example.manhuntmod.entity.custom;

import com.example.manhuntmod.command.ManhuntCommand;
import com.example.manhuntmod.entity.ModEntities;
import com.example.manhuntmod.entity.custom.goal.*;
import com.example.manhuntmod.util.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class HunterEntity extends PathfinderMob {

    private ServerPlayer target;
    private final Map<Block, Integer> virtualInventory = new HashMap<>();
    private int planks = 0, cobblestone = 0, ironOre = 0, ironIngot = 0, coal = 0, diamonds = 0;
    private int toolTier = 0, smeltTimer = 0;
    private float hunterHunger = 20.0f;
    private BlockPos bedSpawn = null;
    private boolean craftingTablePlaced = false;
    private boolean furnacePlaced = false;
    private long lastAttackTime = 0;
    private static final int ATTACK_COOLDOWN_TICKS = 160;
    public boolean debugMode = true;

    public HunterEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.ARMOR, 0.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FOLLOW_RANGE, 9999.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SmartChaseGoal(this));
        this.goalSelector.addGoal(1, new SmartCombatGoal(this));
        this.goalSelector.addGoal(2, new SmartFarmManagerGoal(this));
        this.goalSelector.addGoal(3, new MineTreesGoal(this));
        this.goalSelector.addGoal(4, new MineStoneGoal(this));
        this.goalSelector.addGoal(5, new MineOresGoal(this));
        this.goalSelector.addGoal(6, new HuntAnimalsGoal(this));
        this.goalSelector.addGoal(7, new BridgeIfStuckGoal(this));
        this.goalSelector.addGoal(8, new PortalBuilderGoal(this));
        this.goalSelector.addGoal(9, new TrackDimensionGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomStrollGoal(this, 1.0D));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && this.getTarget() instanceof Player player) {
            this.getLookControl().setLookAt(player, 30.0F, 30.0F);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!(target instanceof LivingEntity livingTarget)) return false;
        if (this.level().isClientSide) return false;

        long currentTick = this.level().getGameTime();
        if (currentTick - lastAttackTime < ATTACK_COOLDOWN_TICKS) {
            if (debugMode) System.out.println("[Hunter] Skipping attack: cooldown.");
            return false;
        }

        if (livingTarget.isInvulnerable()) {
            if (debugMode) System.out.println("[Hunter] Target is invulnerable: " + livingTarget.getName().getString());
            return false;
        }

        if (livingTarget instanceof Player player && player.isCreative()) {
            if (debugMode) System.out.println("[Hunter] ERROR: Target is in Creative!");
            player.sendSystemMessage(Component.literal("§c[ERROR] You are in creative or immune."));
            return false;
        }

        this.swing(InteractionHand.MAIN_HAND); // swing animation

        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean success = livingTarget.hurt(this.damageSources().mobAttack(this), damage);

        if (success) {
            lastAttackTime = currentTick;
            if (debugMode)
                System.out.println("[Hunter] Hit " + target.getName().getString() + " for " + damage);
            if (livingTarget.isDeadOrDying()) {
                System.out.println("[Hunter] Target died: " + target.getName().getString());
                if (this.level() instanceof ServerLevel serverLevel) {
                    ManhuntCommand.checkManhuntVictory(serverLevel);
                }
                this.kill();
            }
        }

        return success;
    }

    public void setTargetPlayer(ServerPlayer player) {
        this.target = player;
        this.setTarget(player);
        this.setAggressive(true);
    }

    public ServerPlayer getTargetPlayer() {
        return target;
    }

    public void useHunger(float amount) {
        hunterHunger = Math.max(0f, hunterHunger - amount);
    }

    public void feed(float amount) {
        hunterHunger = Math.min(20f, hunterHunger + amount);
        this.setHealth(Math.min(this.getMaxHealth(), this.getHealth() + 4.0f));
    }

    public float getHunterHunger() {
        return hunterHunger;
    }

    public void setBedSpawn(BlockPos pos) {
        this.bedSpawn = pos;
    }

    public BlockPos getBedSpawn() {
        return bedSpawn;
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (!serverLevel.getGameRules().getBoolean(ModGameRules.MANHUNT_ACTIVE)) return;
            BlockPos spawn = this.bedSpawn != null ? this.bedSpawn : serverLevel.getSharedSpawnPos();
            HunterEntity newHunter = ModEntities.HUNTER.get().create(serverLevel);
            if (newHunter != null && this.getTargetPlayer() != null) {
                newHunter.setTargetPlayer(this.getTargetPlayer());
                newHunter.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(9999.0D);
                newHunter.moveTo(spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);
                serverLevel.addFreshEntity(newHunter);
            }
        }
    }

    // Inventory + crafting logic

    public void addToVirtualInventory(Block block) {
        virtualInventory.merge(block, 1, Integer::sum);
    }

    public int getVirtualInventoryCount(Block block) {
        return virtualInventory.getOrDefault(block, 0);
    }

    public void addWoodLog(int count) { planks += count * 4; }

    public int getPlanks() { return planks; }

    public void usePlanks(int count) { planks = Math.max(0, planks - count); }

    public void tryCrafting() {
        if (!craftingTablePlaced && planks >= 4) {
            level().setBlockAndUpdate(this.blockPosition().below(), Blocks.CRAFTING_TABLE.defaultBlockState());
            usePlanks(4);
            craftingTablePlaced = true;
        }
        if (toolTier < 1 && planks >= 3) {
            usePlanks(3);
            toolTier = 1;
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.5D);
        }
    }

    public int getToolTier() { return toolTier; }

    public int getStoneCount() { return cobblestone; }

    public void addStone(int count) { cobblestone += count; }

    public void tryCraftStoneTools() {
        if (toolTier < 2 && cobblestone >= 8) {
            cobblestone -= 8;
            toolTier = 2;
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0D);
            this.getAttribute(Attributes.ARMOR).setBaseValue(2.0D);
        }
    }

    public void addIronOre(int count) { ironOre += count; }

    public void addCoal(int count) { coal += count; }

    public void trySmeltingIron() {
        if (!furnacePlaced && cobblestone >= 8) {
            level().setBlockAndUpdate(this.blockPosition().below(), Blocks.FURNACE.defaultBlockState());
            furnacePlaced = true;
            cobblestone -= 8;
        }
        if (furnacePlaced && ironOre > 0 && coal > 0) {
            smeltTimer++;
            if (smeltTimer >= 100) {
                ironOre--;
                coal--;
                ironIngot++;
                smeltTimer = 0;
                tryCraftIronGear();
            }
        }
    }

    public void tryCraftIronGear() {
        if (toolTier < 3 && ironIngot >= 3) {
            toolTier = 3;
            ironIngot -= 3;
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(7.0D);
            this.getAttribute(Attributes.ARMOR).setBaseValue(5.0D);
        }
    }

    public void addDiamond(int count) { diamonds += count; }

    public void craftDiamondGear() {
        if (toolTier < 4 && diamonds >= 7) {
            toolTier = 4;
            diamonds -= 7;
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(9.0D);
            this.getAttribute(Attributes.ARMOR).setBaseValue(10.0D);
        }
    }
}
