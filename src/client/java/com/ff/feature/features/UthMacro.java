package com.ff.feature.features;

import com.ff.feature.Feature;
import com.ff.feature.State;
import com.ff.ipc.IpcManager;
import com.ff.util.InventoryUtil;
import com.ff.util.MovementUtil;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MinecartItem;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

import static com.ff.FluffyFoxClient.MC;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class UthMacro extends Feature {
    private static final Vec3d center = new Vec3d(-215.5, 137.75, -4459.5);
    private static final double squareRadius = 24;

    private static final Vec3d[] altPositions = new Vec3d[] {
        new Vec3d(-216.5, 137.5, -4459.5),
        new Vec3d(-214.5, 137.5, -4459.5),
        new Vec3d(-215.5, 137.5, -4458.5),
        new Vec3d(-215.5, 137.5, -4460.5),
        new Vec3d(-218.5, 137.5, -4459.5),
        new Vec3d(-212.5, 137.5, -4459.5),
        new Vec3d(-219.5, 137.5, -4459.5),
        new Vec3d(-211.5, 137.5, -4459.5),
        new Vec3d(-220.5, 137.5, -4459.5),
        new Vec3d(-210.5, 137.5, -4459.5),
    };

    public static UthMacro INSTANCE = new UthMacro();

    /// 0 = Main, 1+ is an alt
    private static int role = -1;

    private static double spellClickDelayMS = 89;

    public void resetState() {
        INSTANCE.setState(new ClassState());
    }

    /// (MAIN ONLY) Jump off of cliff, place feather, switch to idle state.
    private class StartState extends State {
        private boolean hasEscaped = false;
        private boolean hasBordered = false;
        private double lastActionTime = System.currentTimeMillis();
        private Vec3d borderTarget = null;
        private int actionIndex = 0;

        public StartState() { super(UthMacro.INSTANCE); }

        @Override
        public void onTick() {
            if (role != 0) {
                MC.inGameHud.getChatHud().addMessage(Text.literal(
                    "UthMacro State ticked as StartState, but this instance is running as an alt!"
                ));
                return;
            }

            PlayerEntity player = MC.player;
            World world = MC.world;
            ClientPlayerInteractionManager interactionManager = MC.interactionManager;
            if (player == null || world == null || interactionManager == null) return;

            Vec3d playerPosition = player.getEyePos();
            Vec3d footPos = new Vec3d(playerPosition.x, player.getY() + 0.6, playerPosition.z);

            if (borderTarget == null) {
                Vec3d closestPoint = closestPointOnBorder(playerPosition);
                Vec3d delta = closestPoint.subtract(playerPosition);
                borderTarget = closestPoint.add(delta.normalize().multiply(3.0));
            }

            MC.options.forwardKey.setPressed(false);
            MC.options.rightKey.setPressed(false);
            MC.options.leftKey.setPressed(false);
            if (!hasBordered) {
                double dist = borderTarget.subtract(playerPosition).getHorizontal().length();

                double stopThreshold = 2.0;
                if (dist < stopThreshold) {
                    MC.inGameHud.getChatHud().addMessage(Text.literal("Bordered"));
                    hasBordered = true;
                }

                MovementUtil.lookAtCoordinate(
                        borderTarget,
                        0.2
                );
                double yawOffset = MovementUtil.updateCamera(
                        10.0,
                        42.8, 0.0,
                        10.0 * (1.0 + (1.0 / (-dist - 2))),
                        15.0,
                        5.0,
                        90.0
                );

                if (!hasBordered) {
                    MC.options.forwardKey.setPressed(dist > stopThreshold);

                    Vec3d dir = player.getRotationVector().getHorizontal().normalize();
                    BlockHitResult hit = world.raycast(new RaycastContext(
                            footPos,
                            footPos.add(dir.multiply(2.0)),
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            player
                    ));
                    BlockHitResult hitRight = MC.world.raycast(new RaycastContext(
                            playerPosition.subtract(dir),
                            playerPosition.add(dir.rotateY((float) (Math.PI / 4.0)).multiply(5.0)),
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            MC.player
                    ));
                    BlockHitResult hitLeft = MC.world.raycast(new RaycastContext(
                            playerPosition,
                            playerPosition.add(dir.rotateY((float) (-Math.PI / 4.0)).multiply(5.0)),
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            MC.player
                    ));

                    MC.options.rightKey.setPressed(yawOffset > 0.0 || hitRight.getType() == HitResult.Type.BLOCK);
                    MC.options.leftKey.setPressed(yawOffset < 0.0 || hitLeft.getType() == HitResult.Type.BLOCK);

                    if (MC.player.isOnGround() && (hit.getType() == HitResult.Type.BLOCK || dist > 5.0)) {
                        MC.player.jump();
                    }
                }
            } else if (!hasEscaped) {
                Vec3d dir = playerPosition.subtract(center).normalize();
                double dTheta = Math.acos(
                    player.getRotationVector().dotProduct(dir)
                );

                if (dTheta > Math.PI * 0.125) {
                    MovementUtil.lookAtCoordinate(playerPosition.add(dir.multiply(3.0)), 0.1);
                    MovementUtil.updateCamera(
                        -90.0,
                        90.0, 0.0,
                        0.0,
                        0.0,
                        0.0,
                        120.0
                    );
                } else {
                    if (System.currentTimeMillis() - lastActionTime > spellClickDelayMS) {
                        switch (actionIndex) {
                            case 0, 1, 2 -> player.swingHand(Hand.MAIN_HAND);
                            case 3 -> {
                                hasEscaped = true;
                                actionIndex = 0;
                            }
                        }
                        actionIndex++;
                        lastActionTime = System.currentTimeMillis();
                    }
                }
            } else {
                Vec3d target = center.add(new Vec3d(0.0, 0.0, 0.0));
                Vec3d delta = target.subtract(playerPosition);
                double dist = delta.length();

                MovementUtil.lookAtCoordinate(
                        target,
                        0.1
                );
                MovementUtil.updateCamera(
                        -90.0,
                        90.0, 0.0,
                        0.0,
                        0.0,
                        0.0,
                        90.0
                );

                double stopThreshold = 2.5;
                if (dist < stopThreshold) {
                    Vec3d dir = target.subtract(playerPosition).normalize();
                    double dTheta = Math.acos(
                            player.getRotationVector().dotProduct(dir)
                    );

                    if (dTheta < Math.PI / 8.0 && System.currentTimeMillis() - lastActionTime > spellClickDelayMS) {
                        switch (actionIndex) {
                            case 8 -> InventoryUtil.switchToSlot(InventoryUtil.getSlotOfItemWithString("Avia Feather"));
//                            case 2 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                            case 12 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                            case 14 -> InventoryUtil.switchToSlot(InventoryUtil.getSlotOfItemWithString("Ignis"));
                            case 16 -> feature.setState(new IdleState());
                        }
                        MC.inGameHud.getChatHud().addMessage(Text.literal("tick " + actionIndex));
                        actionIndex++;
                        lastActionTime = System.currentTimeMillis();
                    }
                    return;
                }

                Vec3d dir = player.getRotationVector().getHorizontal().normalize();
                BlockHitResult hit = world.raycast(new RaycastContext(
                        footPos,
                        footPos.add(dir.multiply(2.0)),
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player
                ));

                MC.options.forwardKey.setPressed(dist > stopThreshold);

                if (MC.player.isOnGround() && (hit.getType() == HitResult.Type.BLOCK || dist > 5.0)) {
                    MC.player.jump();
                }
            }
        }

        private Vec3d closestPointOnBorder(Vec3d p) {
            double XM = center.x + squareRadius;
            double Xm = center.x - squareRadius;
            double ZM = center.z + squareRadius;
            double Zm = center.z - squareRadius;

            double dx = Math.min(p.x - Xm, XM - p.x);
            double dz = Math.min(p.z - Zm, ZM - p.z);

            double safeHalfWidth = 5.0;

            if (dx < dz) {
                double xEdge = (p.x - Xm < XM - p.x) ? Xm : XM;

                double lowerSafe = center.z - safeHalfWidth;
                double upperSafe = center.z + safeHalfWidth;

                double z = p.z;

                if (z > lowerSafe && z < upperSafe) {
                    z = (z < center.z) ? lowerSafe : upperSafe;
                }

                return new Vec3d(xEdge, p.y, z);
            } else {
                double zEdge = (p.z - Zm < ZM - p.z) ? Zm : ZM;

                double lowerSafe = center.x - safeHalfWidth;
                double upperSafe = center.x + safeHalfWidth;

                double x = p.x;

                if (x > lowerSafe && x < upperSafe) {
                    x = (x < center.x) ? lowerSafe : upperSafe;
                }

                return new Vec3d(x, p.y, zEdge);
            }
        }
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
            double radius = 50.0;
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
            } else if (role == 0) {
                // do things
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
                if (role == 0) {
                    // main kill logic
                    switch (actionIndex) {
                        // angels (llr)
                        case 0 -> player.swingHand(Hand.MAIN_HAND);
                        case 1 -> player.swingHand(Hand.MAIN_HAND);
                        case 2 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                        // bomb (lrr)
                        case 3 -> player.swingHand(Hand.MAIN_HAND);
                        case 4 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                        case 5 -> interactionManager.interactItem(player, Hand.MAIN_HAND);
                        // next state
                        case 6 -> {
                            feature.setState(new CollectState());
                            MC.inGameHud.getChatHud().addMessage(Text.literal("Switching to collect state"));
                        }
                    }
                } else {
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
                }
            );

            if (!uthRunes.isEmpty()) hasSeenRune = true;

            if (hasSeenRune) {
                Vec3d targetPosition = role > 0 ? altPositions[role - 1] : playerPosition;
                boolean targetIsItem = false;
                double goThreshold = 3.0;

                // Check if collection is complete
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

                MC.options.forwardKey.setPressed(false);
                MC.options.rightKey.setPressed(false);
                MC.options.leftKey.setPressed(false);

                MovementUtil.lookAtCoordinate(targetPosition, Math.min(0.1 * currentDist, 0.2));
                double yawOffset = MovementUtil.updateCamera(
                        -10.0,
                        42.8, 10.0,
                        10.0 * (1.0 + (1.0 / (-currentDist - 2))),
                        15.0,
                        5.0,
                        90.0
                );

                if (!atTarget) {
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
    private class ClassState extends State {
        private final long startTime = System.currentTimeMillis();
        private final double randomDelay = Math.random() * 200.0;
        private boolean hasClassed = false;

        public ClassState() { super(UthMacro.INSTANCE); }

        @Override
        public void onTick() {
            PlayerEntity player = MC.player;
            ClientPlayNetworkHandler networkHandler = MC.getNetworkHandler();
            ClientPlayerInteractionManager interactionManager = MC.interactionManager;
            if (player == null || networkHandler == null || interactionManager == null) return;

            if (!hasClassed) {
                networkHandler.sendChatCommand("class");
                hasClassed = true;
            } else if (System.currentTimeMillis() - startTime > 1600.0 + randomDelay + 150.0 * role) {
                if (MC.crosshairTarget instanceof EntityHitResult hit) {
                    interactionManager.attackEntity(player, hit.getEntity());
                    player.swingHand(Hand.MAIN_HAND);
                }
                feature.setState(role == 0 ? new StartState() : new IdleState());
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

                        InventoryUtil.switchToSlot(InventoryUtil.getSlotOfItemWithString("Avia Feather"));

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
                .then(argument("role", IntegerArgumentType.integer(0))
                    .executes(ctx -> {
                        int roleInput = IntegerArgumentType.getInteger(ctx, "role");

                        role = roleInput;
                        ctx.getSource().sendFeedback(
                            Text.literal(role == 0 ? "Uth role set to MAIN" : "Uth role set to ALT" + roleInput
                        ));
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
                                    "idle", "attack", "collect", "reload", "start"
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
                                case "RELOAD" -> state = new ClassState();
                                case "START" -> {
                                    if (role == 0) {
                                        state = new StartState();
                                    } else {
                                        ctx.getSource().sendError(
                                                Text.literal("State attempted to switch to START, but this instance is an alt!")
                                        );
                                        return 1;
                                    }
                                }
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
