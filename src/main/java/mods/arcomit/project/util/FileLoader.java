package mods.arcomit.project.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import software.bernie.geckolib3.GeckoLib;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class FileLoader {
    public static String getResourceAsString(ResourceLocation location) {
        try (InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(location).getInputStream()) {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(new FileNotFoundException(location.toString()));
        }
    }
}
