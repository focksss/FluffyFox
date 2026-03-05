package com.ff.ipc;

import com.ff.feature.features.UthMacro;
import net.minecraft.resource.featuretoggle.FeatureManager;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ff.feature.Manager.FEATURES;


//TODO
// * Action queue
// * Auto de-register instances that close
// * For non-reset actions, allow instantly unless another action is actively in queue
// * Host should send occasional lifetime pings to registered instances to check for life.
// * State enum, host checks for status of each instance, sequentially calls the next to switch.

public class IpcManager {

    private static final int PORT = 47329;

    private static boolean isCoordinator = false;
    private static int myId = -1;

    private static final AtomicInteger idCounter = new AtomicInteger(0);

    private static final Map<Integer, PrintWriter> clients = new ConcurrentHashMap<>();
    private static final Set<Integer> collectCompleted = ConcurrentHashMap.newKeySet();

    private static PrintWriter myConnectionOut;

    public static void init() {
        if (!startServer()) {
            registerWithCoordinator();
        }
    }

    private static boolean startServer() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            isCoordinator = true;

            System.out.println("Server started");

            new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = server.accept();
                        handleClient(socket);
                    } catch (IOException ignored) {}
                }
            }).start();

            myId = idCounter.getAndIncrement();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private static void handleClient(Socket socket) {
        new Thread(() -> {
            try (
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true)
            ) {

                String msg = in.readLine();
                if (!"REGISTER".equals(msg)) return;

                int assignedId = idCounter.getAndIncrement();
                clients.put(assignedId, out);

                out.println("ID:" + assignedId);

                while ((msg = in.readLine()) != null) {
                    if (msg.equals("COLLECT_DONE")) {
                        handleCollectDone(assignedId);
                    }
                }

            } catch (IOException ignored) {}
        }).start();
    }

    private static void handleCollectDone(int id) {
        collectCompleted.add(id);

        if (collectCompleted.size() == clients.size() + 1) { // +1 to include coordinator
            broadcastReset();
            collectCompleted.clear();
        }
        System.out.println("Collects complete: " + collectCompleted.size() + " / " + clients.size() + 1);
    }

    private static void broadcastReset() {
        for (PrintWriter out : clients.values()) {
            if (out != null) {
                out.println("RESET");
            }
        }

        onResetSignal();
    }


    private static void registerWithCoordinator() {
        try {
            Socket socket = new Socket("127.0.0.1", PORT);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            out.println("REGISTER");

            String response = in.readLine();
            if (response != null && response.startsWith("ID:")) {
                myId = Integer.parseInt(response.substring(3));
            }

            System.out.println("Registered as: " + myId);

            myConnectionOut = out;

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        if (msg.equals("RESET")) {
                            onResetSignal();
                        }
                    }
                } catch (IOException ignored) {}
            }).start();

        } catch (IOException e) {
            System.out.println("IPC: Failed to register.");
        }
    }

    public static void signalCollectComplete() {
        if (isCoordinator) {
            handleCollectDone(myId);
        } else {
            if (myConnectionOut != null) {
                myConnectionOut.println("COLLECT_DONE");
            }
        }
    }

    private static void onResetSignal() {
        System.out.println("RESET STATE START");

        UthMacro.INSTANCE.resetState();
    }

    public static int getMyId() {
        return myId;
    }
}