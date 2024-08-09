package net.slqmy.firework_wars_plugin.arena.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.bukkit.Color;
import org.bukkit.DyeColor;

import java.lang.reflect.Type;

public class ColorDeserializer implements JsonDeserializer<Color> {
  @Override
  public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return DyeColor.valueOf(json.getAsString().toUpperCase()).getColor();
  }
}
