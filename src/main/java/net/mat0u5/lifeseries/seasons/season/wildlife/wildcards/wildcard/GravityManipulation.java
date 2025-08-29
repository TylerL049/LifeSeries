package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GravityManipulation extends Wildcard {

    private static final int TICKS_PER_SECOND = 20;
    private static final int SESSION_TICKS = 2 * 60 * 60 * TICKS_PER_SECOND;
    private static final int LEVITATION_START_TICKS = SESSION_TICKS - (20 * 60 * TICKS_PER_SECOND);
    private static final int MAX_JUMP_LEVEL = 30;
    private static final int MAX_LEVITATION_LEVEL = 10;

    private int tickCounter = 0;

    @Override
    public Wildcards getType() {
        return Wildcards.GRAVITY_MANIPULATION;
    }

    @Override
    public void tick() {
        tickCounter++;

        double jumpProgress = Math.min(1.0, (double) tickCounter / LEVITATION_START_TICKS);
        int jumpLevel = 1 + (int) Math.floor(jumpProgress * (MAX_JUMP_LEVEL - 1));

        double levitationProgress = Math.max(0, (tickCounter - LEVITATION_START_TICKS) / (double)(SESSION_TICKS - LEVITATION_START_TICKS));
        int levitationLevel = (int) Math.floor(levitationProgress * MAX_LEVITATION_LEVEL);

        for (ServerPlayerEntity player : PlayerUtils.getAllFunctioningPlayers()) {
            if (player.isSpectator()) continue;

            player.getBukkitEntity().addPotionEffect(
                    new PotionEffect(PotionEffectType.JUMP, 40, jumpLevel - 1, true, false, true)
            );

            if (tickCounter >= LEVITATION_START_TICKS) {
                player.getBukkitEntity().addPotionEffect(
                        new PotionEffect(PotionEffectType.LEVITATION, 40, levitationLevel, true, false, true)
                );
            }
        }
    }

    @Override
    public void stop() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            player.getBukkitEntity().removePotionEffect(PotionEffectType.JUMP);
            player.getBukkitEntity().removePotionEffect(PotionEffectType.LEVITATION);
        }
        tickCounter = 0;
    }
}