package net.bdew.wurm.rmitool;

import com.wurmonline.server.webinterface.WebInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4) err("Usage: rmitool.jar <addr> <port> <pass> <command> [args...]");
        String addr = args[0];
        int port = safeInt(args[1]);
        String pass = args[2];
        String cmd = args[3];
        WebInterface iface = null;
        try {
            iface = setupConnection(addr, port);
            switch (cmd) {
                case "shutdown":
                    if (args.length != 7) err("Usage: shutdown <name> <seconds> <reason>");
                    String name = args[4];
                    int seconds = safeInt(args[5]);
                    String reason = args[6];
                    doShutdown(iface, pass, name, seconds, reason);
                    break;
                case "broadcast":
                    if (args.length != 5) err("Usage: broadcast <message>");
                    String message = args[4];
                    doBroadcast(iface, pass, message);
                    break;
                default:
                    err("Unknown command: " + cmd);
            }
        } catch (RemoteException | NotBoundException e) {
            err("Connection error: " + e.toString());
        }

        System.err.println("Command sent!");
    }

    // ==== COMMAND HANDLERS ====

    public static void doShutdown(WebInterface iface, String pass, String name, int seconds, String reason) throws RemoteException {
        iface.startShutdown(pass, name, seconds, reason);
    }

    public static void doBroadcast(WebInterface iface, String pass, String message) throws RemoteException {
        iface.broadcastMessage(pass, message);
    }

    // ==== MISC ====

    public static int safeInt(String s) {
        try {
            return Integer.parseInt(s, 10);
        } catch (NumberFormatException e) {
            err("Parse error: '" + s + "' is not a valid integer");
            return 0; //unreachable
        }
    }

    public static void err(String s) {
        System.err.println("Error: " + s);
        System.exit(1);
    }

    public static WebInterface setupConnection(String host, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (WebInterface) registry.lookup("wuinterface");
    }
}