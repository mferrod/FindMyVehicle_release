package dev.marianof.locatevehicles.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import dev.marianof.locatevehicles.Model.Coordenadas;

public class Response {
    public ArrayList<Coordenadas> parsePosistions(String res) {
        JsonElement head = JsonParser.parseString(res);
        JsonArray elements = head.getAsJsonArray();
        ArrayList<Coordenadas> coordenadas = new ArrayList<>();
        for (JsonElement element: elements)
        {
            JsonObject jsonObject = element.getAsJsonObject();
            Coordenadas cor = new Coordenadas(jsonObject.get("username").getAsString(), jsonObject.get("latitude").getAsDouble(), jsonObject.get("longitude").getAsDouble());
            coordenadas.add(cor);
        }
        return coordenadas;
    }
}
