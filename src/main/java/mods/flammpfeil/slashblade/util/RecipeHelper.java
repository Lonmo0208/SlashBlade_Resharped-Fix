package mods.flammpfeil.slashblade.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RecipeHelper {
    public static final Gson NETWORK_GSON = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization()
            .excludeFieldsWithoutExposeAnnotation().create();
}
