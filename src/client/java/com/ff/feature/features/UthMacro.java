package com.ff.feature.features;

import com.ff.feature.Feature;
import com.ff.feature.State;
import com.ff.ipc.IpcManager;
import com.ff.util.MovementUtil;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ff.FluffyFoxClient.MC;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class UthMacro extends Feature {
    public static UthMacro INSTANCE = new UthMacro();

    private enum Role {
        MAIN,
        ALT1,
        ALT2,
        ALT3,
        ALT4,
        ALT5,
        ALT6,
        ALT7,
        ALT8,
        ALT9,
        ALT10,
    }

    private static Role role = Role.MAIN;
    private static double spellClickDelayMS = 89;

    public void resetState() {
        INSTANCE.setState(new ReloadState());
    }

    /// Do nothing, unless there's an uth mob, in which case switch state to attack,
    private class IdleState extends State {
        public IdleState() { super(UthMacro.INSTANCE); }

        @Override
        public void onTick() {
            PlayerEntity player = MC.player;
            World world = MC.world;
            if (player == null || world == null) return;
            Vec3d center = player.getEyePos();
            double radius = 30.0;
            Box box = new Box(
                center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius
            );

            List<DisplayEntity.TextDisplayEntity> uthMobs = world.getEntitiesByClass(
                DisplayEntity.TextDisplayEntity.class,
                box,
                entity -> getMobName(entity).contains("Uth")
            );
            if (!uthMobs.isEmpty()) {
                MC.inGameHud.getChatHud().addMessage(Text.literal("Uth mob detected, switching to attack state"));
                feature.setState(new AttackState(uthMobs.getFirst()));
            }
        }
    }

    /// Look at uth mob and execute bash (rlr) and then scream (rrl), then switch state to collect.
    private class AttackState extends State {
        private DisplayEntity.TextDisplayEntity uthMob;

        private double lastActionTime = System.currentTimeMillis();
        private int actionIndex = 0;

        public AttackState(DisplayEntity.TextDisplayEntity target) {
            super(UthMacro.INSTANCE);
            uthMob = target;
        }

        @Override
        public void onTick() {
            PlayerEntity player = MC.player;
            World world = MC.world;
            ClientPlayerInteractionManager interactionManager = MC.interactionManager;
            if (player == null || interactionManager == null || world == null) return;

            if (uthMob == null) {
                MC.inGameHud.getChatHud().addMessage(Text.literal("No Uth mob detected while in attack state"));
                Vec3d center = player.getEyePos();
                double radius = 30.0;
                Box box = new Box(
                        center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                        center.getX() + radius, center.getY() + radius, center.getZ() + radius
                );

                List<DisplayEntity.TextDisplayEntity> uthMobs = world.getEntitiesByClass(
                    DisplayEntity.TextDisplayEntity.class,
                    box,
                    entity -> getMobName(entity).contains(" Uth")
                );

                uthMob = uthMobs.getFirst();
            }

            Vec3d targetPosition = uthMob.getEyePos().subtract(0.0, 1.0, 0.0);
            MovementUtil.lookAtCoordinate(targetPosition, 0.15);
            MovementUtil.updateCamera(
                -20.0,
                42.8,
                5.0,
                15.0,
                0.0,
                5.0,
                90.0
            );

            double dt = System.currentTimeMillis() - lastActionTime;
            if (dt > spellClickDelayMS) {
                switch (actionIndex) {
                    // bash (rlr)
                    case 0 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                    case 1 -> player.swingHand(Hand.MAIN_HAND);
                    case 2 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                    // scream (rrl)
                    case 3 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                    case 4 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                    case 5 -> player.swingHand(Hand.MAIN_HAND);
                    // next state
                    case 6 -> {
                        feature.setState(new CollectState());
                        MC.inGameHud.getChatHud().addMessage(Text.literal("Switching to collect state"));
                    }
                }
                lastActionTime = System.currentTimeMillis();
                actionIndex++;
            }
        }
    }

    /**
     * 1. Wait for a rune to exist.
     * 2. Wait 4 seconds
     * 3. If there are no runes present, switch state to reload, otherwise collect them, return to position, and then switch state to reset.
     */
    private class CollectState extends State {
        private boolean hasSeenRune = false;
        private boolean returnToOrigin = false;
        private boolean atTarget = false;
        private Vec3d lastTarget = null;

        public CollectState() { super(UthMacro.INSTANCE); }

        @Override
        public void onTick() {
            PlayerEntity player = MC.player;
            World world = MC.world;
            if (player == null || world == null) return;

            Vec3d playerPosition = new Vec3d(MC.player.getX(), MC.player.getY() + 0.6, MC.player.getZ());
            double radius = 30.0;
            Box box = new Box(
                playerPosition.getX() - radius, playerPosition.getY() - radius, playerPosition.getZ() - radius,
                playerPosition.getX() + radius, playerPosition.getY() + radius, playerPosition.getZ() + radius
            );

            List<ItemEntity> uthRunes = world.getEntitiesByClass(
                ItemEntity.class,
                box,
                entity -> {
                    ItemStack stack = entity.getStack();

                    Text name = stack.get(DataComponentTypes.CUSTOM_NAME);
                    return (name != null && name.getString().contains("Uth Rune"));
//                    return stack.isOf(Items.EMERALD);
                }
            );

            if (!uthRunes.isEmpty()) hasSeenRune = true;

            if (hasSeenRune) {
                Vec3d targetPosition = switch (role) {
                    case Role.ALT1 -> new Vec3d(-216.5, 137.5, -4459.5);
                    case Role.ALT2 -> new Vec3d(-214.5, 137.5, -4459.5);
                    case Role.ALT3 -> new Vec3d(-215.5, 137.5, -4458.5);
                    case Role.ALT4 -> new Vec3d(-215.5, 137.5, -4460.5);
                    case Role.ALT5 -> new Vec3d(-218.5, 137.5, -4459.5);
                    case Role.ALT6 -> new Vec3d(-212.5, 137.5, -4459.5);
                    case Role.ALT7 -> new Vec3d(-219.5, 137.5, -4459.5);
                    case Role.ALT8 -> new Vec3d(-211.5, 137.5, -4459.5);
                    case Role.ALT9 -> new Vec3d(-220.5, 137.5, -4459.5);
                    case Role.ALT10 -> new Vec3d(-210.5, 137.5, -4459.5);
                    case Role.MAIN -> playerPosition;
                };
                boolean targetIsItem = false;
                double goThreshold = 3.0;
                if (returnToOrigin && playerPosition.distanceTo(targetPosition) < goThreshold && atTarget && uthRunes.isEmpty()) {
                    IpcManager.signalCollectComplete();
                }

                // get only the runes that are older than 4 seconds to allow time for vindicator buff to auto-collect
                List<ItemEntity> remainingRunes = uthRunes.stream().filter(entity -> entity.getItemAge() > 80).toList();
                if (!remainingRunes.isEmpty()) {
                    targetIsItem = true;
                    targetPosition = remainingRunes.getFirst().getEyePos();
                    for (int i = 1; i < remainingRunes.size(); i++) {
                        if (remainingRunes.get(i).squaredDistanceTo(playerPosition) < targetPosition.squaredDistanceTo(playerPosition)) {
                            targetPosition = remainingRunes.get(i).getEyePos();
                        }
                    }
                } else { // No runes remaining, return to origin and then switch state
                    returnToOrigin = true;
                }

                double friction = 0.91 * MC.world.getBlockState(MC.player.getBlockPos().down()).getBlock().getSlipperiness();

                Vec3d testVelocity = MC.player.getVelocity().getHorizontal();
                Vec3d coastPosition = playerPosition;
                for (int i = 0; i < 100; i++) {
                    coastPosition = coastPosition.add(testVelocity);
                    testVelocity = testVelocity.multiply(friction);
                }

                Vec3d delta = targetPosition.subtract(playerPosition);
                Vec3d coastDelta = targetPosition.subtract(coastPosition);
                double currentDist = targetIsItem ? delta.length() : delta.getHorizontal().length();
                double coastDist = targetIsItem ? coastDelta.length() : coastDelta.getHorizontal().length();

                if (lastTarget == null || lastTarget.distanceTo(targetPosition) > 0.5 || currentDist > goThreshold) {
                    lastTarget = targetPosition;
                    atTarget = false;
                }
                double stopThreshold = 0.5;
                if (currentDist < stopThreshold) atTarget = true;

                System.out.println("dist: " + currentDist);

                MC.options.forwardKey.setPressed(false);
                MC.options.rightKey.setPressed(false);
                MC.options.leftKey.setPressed(false);
                if (!atTarget) {
                    MovementUtil.lookAtCoordinate(targetPosition, Math.min(0.1 * currentDist, 0.2));
                    double yawOffset = MovementUtil.updateCamera(
                            -10.0,
                            42.8, 10.0,
                            10.0 * (1.0 + (1.0 / (-currentDist - 2))),
                            15.0,
                            5.0,
                            90.0
                    );

                    MC.options.forwardKey.setPressed(coastDist > stopThreshold);
                    MC.options.rightKey.setPressed(yawOffset > 0.0);
                    MC.options.leftKey.setPressed(yawOffset < 0.0);

                    BlockHitResult hit = MC.world.raycast(new RaycastContext(
                            playerPosition,
                            playerPosition.add(MC.player.getRotationVector().getHorizontal().normalize().multiply(2.0)),
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            MC.player
                    ));

                    if (MC.player.isOnGround() && (hit.getType() == HitResult.Type.BLOCK || currentDist > 5.0)) {
                        MC.player.jump();
                    }
                }
            }
        }

        @Override
        public void onExit() {
            MC.options.forwardKey.setPressed(false);
            MC.options.rightKey.setPressed(false);
            MC.options.leftKey.setPressed(false);
        }
    }

    /// Run /class, wait 3 seconds, then left click to re-enter and switch state to idle.
    private class ReloadState extends State {
        private final long startTime = System.currentTimeMillis();
        private boolean hasClassed = false;

        public ReloadState() { super(UthMacro.INSTANCE); }

        @Override
        public void onTick() {
            PlayerEntity player = MC.player;
            ClientPlayNetworkHandler networkHandler = MC.getNetworkHandler();
            ClientPlayerInteractionManager interactionManager = MC.interactionManager;
            if (player == null || networkHandler == null || interactionManager == null) return;

            if (!hasClassed) {
                networkHandler.sendChatCommand("class");
                hasClassed = true;
            } else if (System.currentTimeMillis() - startTime > 8000) {
                if (MC.crosshairTarget instanceof EntityHitResult hit) {
                    interactionManager.attackEntity(player, hit.getEntity());
                    player.swingHand(Hand.MAIN_HAND);
                }
                feature.setState(new IdleState());
            }
        }
    }

    public UthMacro() {
        super("uthmacro", "um");
    }

    @Override
    public void onEnable() { state = new IdleState(); }

    private static String getMobName(DisplayEntity.TextDisplayEntity textDisplay) {
        Text text = textDisplay.getText();

        for (Text sibling : text.getSiblings()) {
            String s = sibling.getString();
            if (s != null && !s.isBlank()) {
                int cutoff = s.length();
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    if (c >= 0xE000 || c == '\n') {
                        cutoff = i;
                        break;
                    }
                }
                String name = s.substring(0, cutoff).trim();
                if (!name.isEmpty()) return name;
            }
        }

        String plain = text.getString();
        StringBuilder name = new StringBuilder();
        for (char c : plain.toCharArray()) {
            if (c >= 0xE000 || c == '\n') break;
            name.append(c);
        }
        return name.toString().trim();
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("test")
                    .executes(ctx -> {
                        Vec3d center = MC.player.getEyePos();

                        IpcManager.signalCollectComplete();

                        double radius = 5.0;
                        Box box = new Box(
                            center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                            center.getX() + radius, center.getY() + radius, center.getZ() + radius
                        );

                        List<Entity> entities = MC.world.getEntitiesByClass(
                            Entity.class,
                            box,
                            entity -> true
//                            entity -> entity instanceof PlayerEntity || entity instanceof ZombieEntity || entity instanceof ArmorStandEntity
                        );

                        if (MC.crosshairTarget instanceof EntityHitResult hit) {
                            MC.interactionManager.attackEntity(MC.player, hit.getEntity());
                            MC.player.swingHand(Hand.MAIN_HAND);
                        }

                        for (Entity entity : entities) {
                            if (entity instanceof DisplayEntity.TextDisplayEntity textDisplay) {
                                String name = getMobName(textDisplay);
                                System.out.println("found mob named: " + name + " --- at: " + entity.getEyePos() + "\n\n");
                                ctx.getSource().sendFeedback(
                                    Text.literal("found: " + name)
                                );
                            }
                        }

                        List<ItemEntity> uthRunes = MC.world.getEntitiesByClass(
                                ItemEntity.class,
                                box,
                                entity -> true
                        );
                        for (ItemEntity itemEntity : uthRunes) {
                            ItemStack stack = itemEntity.getStack();

                            Text name = stack.get(DataComponentTypes.CUSTOM_NAME);
                            ctx.getSource().sendFeedback(Text.literal("ITEM: " + name + ",\n "
                                    + stack.get(DataComponentTypes.LORE) + ", \n"
                                    + stack.get(DataComponentTypes.ITEM_MODEL) + ", \n"
                            ));
                        }

                        return 1;
                    })
            )
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("UthMacro: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            )
            .then(literal("role")
                .then(argument("role", StringArgumentType.word())
                    .suggests((context, builder) ->
                        CommandSource.suggestMatching(
                            new String[]{
                                "main", "alt1", "alt2"
                            },
                            builder
                        )
                    )
                    .executes(ctx -> {
                        String roleInput = StringArgumentType.getString(ctx, "role").toUpperCase();

                        try {
                            role = Role.valueOf(roleInput);
                            ctx.getSource().sendFeedback(
                                Text.literal("Uth role set to: " + roleInput)
                            );
                        } catch (IllegalArgumentException e) {
                            ctx.getSource().sendError(
                                Text.literal("Invalid role.")
                            );
                        }
                        return 1;
                    })
                )
            ).then(literal("clickDelay")
                .then(argument("delay_ms", StringArgumentType.word())
                    .executes(ctx -> {
                        spellClickDelayMS = DoubleArgumentType.getDouble(ctx, "delay_ms");
                        ctx.getSource().sendFeedback(Text.literal("Spell Click Delay set to: " + spellClickDelayMS + "ms"));
                        return 1;
                    })
                )
            )
            .then(literal("override_state")
                .then(argument("state", StringArgumentType.word())
                    .suggests((context, builder) ->
                        CommandSource.suggestMatching(
                            new String[]{
                                    "idle", "attack", "collect", "reload"
                            },
                            builder
                        )
                    )
                    .executes(ctx -> {
                        String stateInput = StringArgumentType.getString(ctx, "state").toUpperCase();

                        try {
                            switch (stateInput) {
                                case "IDLE" -> state = new IdleState();
                                case "ATTACK" -> state = new AttackState(null);
                                case "COLLECT" -> state = new CollectState();
                                case "RELOAD" -> state = new ReloadState();
                            }
                            ctx.getSource().sendFeedback(
                                Text.literal("State set to: " + stateInput)
                            );
                        } catch (IllegalArgumentException e) {
                            ctx.getSource().sendError(
                                Text.literal("Invalid state.")
                            );
                        }
                        return 1;
                    })
                )
            );
    }
}
