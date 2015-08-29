package org.heat.world.roleplay;

public final class WorldActorLooks {
    private WorldActorLooks() {}

    public static int[] toIndexedColors(int[] colors) {
        // << index :: size(8), color :: size(24) >>
        int[] res = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            res[i] = (((i + 1) & 0xFF) << 24) | (colors[i] & 0xFFFFFF);
        }
        return res;
    }

    public static WorldActorLook fromTiphon(String s) {
        final int numberBase;

        if (s.charAt(0) == '[') {
            int endHeader = s.indexOf(']');
            String header = s.substring(1, endHeader);
            String[] args = header.split(",");

            numberBase = args.length > 1
                    ? parseTiphonNumberBase(args[1])
                    : 10;

            s = s.substring(endHeader + 1);
        } else {
            numberBase = 10;
        }

        if (s.charAt(0) != '{' || s.charAt(s.length() - 1) != '}') {
            throw new IllegalArgumentException("malformated tiphon look");
        }
        s = s.substring(1, s.length() - 1);

        String[] body = s.split("|");

        final short bones;
        final short[] skins;
        final int[] colors;
        final short scaleX, scaleY;

        // bones
        bones = Short.parseShort(body[0], numberBase);

        // skins
        if (body.length > 1 && !body[1].isEmpty()) {
            String[] args = body[1].split(",");
            skins = new short[args.length];
            for (int i = 0; i < args.length; i++) {
                skins[i] = Short.parseShort(args[i], numberBase);
            }
        } else {
            skins = new short[0];
        }

        // colors
        if (body.length > 2 && !body[2].isEmpty()) {
            String[] colorsArgs = body[2].split(",");
            colors = new int[colorsArgs.length];
            for (String colorArgs : colorsArgs) {
                String[] args = colorArgs.split("=");

                final int index = Integer.parseInt(args[0], numberBase);
                final int value;
                if (args[1].charAt(0) == '#') {
                    value = Integer.parseInt(args[1].substring(1), 16);
                } else {
                    value = Integer.parseInt(args[1], numberBase);
                }

                colors[index] = value;
            }
        } else {
            colors = new int[0];
        }

        // scales
        if (body.length > 3 && !body[3].isEmpty()) {
            String[] args = body[3].split(",");
            if (args.length == 1) {
                scaleX = scaleY = Short.parseShort(args[0], numberBase);
            } else if (args.length == 2) {
                scaleX = Short.parseShort(args[0], numberBase);
                scaleY = Short.parseShort(args[1], numberBase);
            } else {
                throw new IllegalArgumentException("malformated tiphon look : got " + args.length + " scales");
            }
        } else {
            scaleX = scaleY = 1;
        }

        // subentities
        // TODO(world/roleplay): parse tiphon subentities

        return new WorldActorLook(
                bones,
                skins,
                new short[] {scaleX, scaleY},
                WorldActorLooks.toIndexedColors(colors)
        );
    }

    private static int parseTiphonNumberBase(String s) {
        switch (s) {
            case "A": return 10;
            case "G": return 16;
            case "Z": return 36;
            default: throw new IllegalArgumentException();
        }
    }
}
