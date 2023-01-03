package it.ohalee.minecraftgpt;

public enum Type {

    SINGLE,
    BROADCAST,
    FULL;

    public static Type getType(String type) {
        return switch (type.toLowerCase()) {
            case "single" -> Type.SINGLE;
            case "broadcast" -> Type.BROADCAST;
            case "full" -> Type.FULL;
            default -> null;
        };
    }

}
