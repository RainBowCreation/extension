package net.rainbowcreation.spongelobby.portal;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Locale;

public class Portal {
    private static List<Location<World>> portalLocation;


    public static void registerPortal(Location<World> locationTopLeft, Location<World> locationBotRight) {

    }

    public static List<Location<World>> getPortalLocation() {
        return portalLocation;
    }
}
