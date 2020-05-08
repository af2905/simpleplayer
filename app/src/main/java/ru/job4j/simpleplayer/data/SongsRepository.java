package ru.job4j.simpleplayer.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ru.job4j.simpleplayer.R;

public class SongsRepository {
    private static SongsRepository instance;

    public static SongsRepository getInstance() {
        if (instance == null) {
            instance = new SongsRepository();
        }
        return instance;
    }

    public static final Field[] RAW_FIELDS = R.raw.class.getFields();

    public Map<Integer, String> getDataFromFields(Field[] fields) {
        Map<Integer, String> map = new HashMap<>();
        for (Field field : fields) {
            try {
                map.put(field.getInt(field), field.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
