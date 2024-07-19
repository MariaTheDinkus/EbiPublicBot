package to.tinypota.ebipublicbot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import to.tinypota.ebipublicbot.Main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    public static <T> T convertJsonStringToObject(String jsonString, Class<T> valueType) throws JsonProcessingException {
        jsonString = jsonString.replace("`", ""); // Remove the ` character

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(jsonString, valueType);
    }

    public static boolean isValidJsonURL(String urlString) {
        try {
            URL url = new URL(urlString);
            return urlString.startsWith("http") && urlString.endsWith(".json");
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static <T> T loadFromJson(Class<T> valueType, String jsonName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = getJsonFile(jsonName);
        return mapper.readValue(jsonFile, valueType);
    }

    public static <T> void saveToJson(T object, String jsonName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = getJsonFile(jsonName);
        mapper.writeValue(jsonFile, object);
    }

    // A method for removing a json object and moving it to the dataold folder
    public static <T> void removeJson(String jsonName) throws IOException {
        File jsonFile = getJsonFile(jsonName);
        File dataOldFolder = new File(getProgramPath(), "dataold");
        if (!dataOldFolder.exists()) {
            dataOldFolder.mkdir();
        }
        File jsonFileOld = new File(dataOldFolder, jsonName + ".json");
        jsonFile.renameTo(jsonFileOld);
    }

    public static <T> ArrayList<T> loadAllFromJson(Class<T> valueType) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<T> objects = new ArrayList<>();
        List<File> files = null;
        try {
            files = getJsonFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(files);
        for (File file : files) {
            T object = null;
            try {
                object = mapper.readValue(file, valueType);
            } catch (IOException e) {
                System.out.println("Not an instance of the class: " + valueType.getName() + " for file: " + file.getName());
            }
            if (object != null)
                objects.add(object);
        }
        return objects;
    }

    public static List<File> getJsonFiles() throws IOException {
        String programPath = getProgramPath();
        File dataFolder = new File(programPath, "data");
        List<File> jsonFiles = new ArrayList<>();

        System.out.println(dataFolder);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        } else {
            Files.walk(Paths.get(dataFolder.getAbsolutePath()))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(p -> jsonFiles.add(p.toFile()));
        }

        return jsonFiles;
    }

    public static File getJsonFile(String name) throws IOException {
        String programPath = getProgramPath();
        File dataFolder = new File(programPath, "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        return new File(dataFolder, name + ".json");
    }

    public static String getProgramPath() {
        URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        return file.getParentFile().getPath();
    }
}
