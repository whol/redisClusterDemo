package com.example.demo.common.json;

import com.example.demo.beans.common.JsonBean;
import com.example.demo.common.utils.ClassUtils;
import com.example.demo.common.utils.Constants;
import org.apache.http.util.Asserts;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-11-2.
 */
public class ObjectMapper {

    // 原始数据类型的映射表
    private static Map<String, Class<?>> _primitiveMappings = new HashMap<String, Class<?>>() {{
        put("int", Integer.class);
        put("byte", Byte.class);
        put("short", Short.class);
        put("float", Float.class);
        put("double", Double.class);
        put("long", Long.class);
        put("boolean", Boolean.class);
    }};

    private static HashMap<Class<?>, String> _primitiveParseMethods = new HashMap() {{
        put(Short.class, "parseShort");
        put(Integer.class, "parseInt");
        put(Long.class, "parseLong");
        put(Byte.class, "parseByte");
        put(Float.class, "parseFloat");
        put(Double.class, "parseDouble");
        put(Boolean.class, "parseBoolean");
    }};

    class MappingInfo {

        private JsonField mapping;
        private Field field;
        private String[] names;
        private Class<?> elementType;
        private String dateFormat;
        private Locale locale;

        public MappingInfo(Field field) {
            this.field = field;
            this.mapping = field.getAnnotation(JsonField.class);
            if (this.mapping == null) {
                this.names = new String[] { field.getName() };
                this.locale = Locale.ENGLISH;
                this.dateFormat = Constants.DEFAULT_DATE_FORMAT;
            } else {
                this.names = mapping.name();
                if (this.names.length <= 0) {
                    this.names = new String[] {field.getName()};
                }
                this.elementType = mapping.elementType().length > 0 ? mapping.elementType()[0] : null;
                this.dateFormat = mapping.dateFormat();
                this.locale = Locale.forLanguageTag(mapping.locale());
            }
        }

        public String[] getNames() {
            return names;
        }

        public Class<?> getElementType() {
            return elementType;
        }

        public Field getField() {
            return field;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public Locale getLocale() {
            return locale;
        }

        public boolean isJsonBean() {
            return JsonBean.class.isAssignableFrom(getField().getType());
        }
    }

    public ObjectMapper() {
    }

    public Object readJavaType(String jsonData, Class<? extends JsonBean<?>> typeClass) throws JsonException {
        Asserts.notNull(jsonData, "jsonData can not be null");
        Asserts.notNull(typeClass, "typeClass can not be null");

        if (isArrayData(jsonData)) {
            if (!List.class.isAssignableFrom(typeClass)) {
                throw new IllegalArgumentException("typeClass is not subclass of List type: " + typeClass);
            }
            return readList(new JsonArray(jsonData), new ArrayList(), typeClass);
        } else if (isObjectData(jsonData)) {
            return readObject(new JsonObject(jsonData), typeClass);
        } else {
            throw new UnsupportedOperationException("无效的JSON数据格式");
        }
    }

    public <T> T readObject(Mappable<?> jsonObject, Class<T> typeClass) throws JsonException {
        try {
            T instance = typeClass.newInstance();

            List<Field> fields = ClassUtils.getDeclaredFields(typeClass);
            for (Field field : fields) {
                MappingInfo mapping = new MappingInfo(field);
                MappedPair valuePair = getMappedPair(mapping, jsonObject);
                if (valuePair != null) {
                    field.setAccessible(true);
                    if (valuePair.isMultipart()) {
                        field.set(instance, valuePair.getValue());
                    } else {
                        try {
                            readField(instance, mapping, valuePair);
                        } catch (Exception e) {
                            throw new JsonMappingException("Conversion failed for field: " + field.getName(), e);
                        }
                    }
                }
            }
            return instance;
        } catch (Exception ex) {
            if (!(ex instanceof JsonException)) {
                throw new JsonMappingException(ex);
            } else {
                throw (JsonException)ex;
            }
        }
    }

    private MappedPair getMappedPair(MappingInfo mapping, Mappable<?> mappable) {
        String[] names = mapping.getNames();
        for (String name  : names) {
            if (mappable.contains(name)) {
                Object value = mappable.get(name);
                if (value != null) {
                    return transformValue(mapping, name,  value);
                } else {
                    return new MappedPair(name, value);
                }
            }
        }
        return null;
    }

    private MappedPair transformValue(MappingInfo mapping, String name, Object value) {
        boolean _isMultipart = value.getClass() == MultipartFile[].class;

        if (mapping.getField().getType() == MultipartFile.class) {
            // 文件對象
            MultipartFile[] files = (MultipartFile[]) value;
            value = files.length > 0 ? files[0] : null;
        } else if (value.getClass().isArray() && !_isMultipart) {
            Object[] array = (Object[])value;
            value = array.length > 0 ? array[0] : null;
        } else if (value instanceof JsonValue) {
            if (((JsonValue) value).isNull()) {
                value = null;
            } else {
                value = ((JsonValue) value).getValue();
            }
        }
        if (mapping.isJsonBean()) {
            value = value instanceof JSONObject ? value : new JSONObject(String.valueOf(value));
        }
        return new MappedPair(name, value, _isMultipart);
    }

    private <T> void readField(T instance, MappingInfo mapping, MappedPair valuePair) throws Exception {
        Field field = mapping.getField();
        Object value = valuePair.getValue();

        Class<?> fieldType = field.getType();

        if (value == null || value.getClass() == fieldType) {
            field.set(instance, value);
        } else {
            if (fieldType.isArray() || List.class == fieldType || List.class.isAssignableFrom(fieldType)) {
                if(mapping.getElementType() == null) {
                    throw new JsonMappingException("No element type specified for Collection/Array field");
                }
                List elements = readList(new JsonArray((JSONArray) value) , new ArrayList(), mapping.getElementType());

                field.set(instance, fieldType.isArray() ? elements.toArray((Object[]) Array.newInstance(mapping.getElementType(), elements.size())) : elements);
            } else if (Map.class == fieldType || Map.class.isAssignableFrom(fieldType)) {
                if (!(value instanceof JSONObject)) {
                    throw new JsonMappingException("不支持将`" + value.getClass() + "` 转为: " + fieldType);
                }

                Map dataMap = fieldType.isInterface() ? new HashMap() : (Map) fieldType.newInstance();

                JSONObject obj = (JSONObject)value;
                for (String key : obj.keySet()) {
                    dataMap.put(key, obj.get(key));
                }

                field.set(instance, dataMap);
            } else if (fieldType.isEnum()) {
                // 枚举类型，直接使用枚举类型的valueOf方法转换
                Method valueOf = fieldType.getMethod("valueOf", String.class);
                field.set(instance, valueOf.invoke(null, String.valueOf(value)));
            }  else {
                // 将原始类型转为原始类型对应的类类型
                if (_primitiveMappings.containsKey(fieldType.getSimpleName())) {
                    fieldType = _primitiveMappings.get(fieldType.getSimpleName());
                }
                // 使用原始类型的方法进行转换
                if (_primitiveParseMethods.containsKey(fieldType)) {
                    String methodName = _primitiveParseMethods.get(fieldType);
                    Method method = fieldType.getMethod(methodName, String.class);
                    field.set(instance, method.invoke(instance, String.valueOf(value)));
                } else if (fieldType == BigDecimal.class) {
                    field.set(instance, new BigDecimal(String.valueOf(value)));
                } else if (fieldType == BigInteger.class) {
                    field.set(instance, new BigInteger(String.valueOf(value)));
                } else if (Date.class == fieldType || Date.class.isAssignableFrom(fieldType)) {
                    field.set(instance, new SimpleDateFormat(mapping.getDateFormat(), mapping.getLocale()).parse(String.valueOf(value)));
                } else if (JsonBean.class.isAssignableFrom(fieldType)) {
                    field.set(instance, readObject(new JsonObject((JSONObject) value), fieldType));
                } else {
                    throw new JsonMappingException("不支持的类型转换：" + fieldType);
                }
            }
        }
    }

    private <T> List readList(MappableList<JsonValue<?>> jsonValues, List elements, Class<T> elementTypeClass) throws JsonException {
        try {
            for (int i = 0 ; i < jsonValues.length(); i++) {
                JsonValue<?> value = jsonValues.get(i);
                if (value instanceof JsonObject) {
                    elements.add(readObject((JsonObject)value, elementTypeClass));
                } else if (value instanceof JsonArray) {
                    elements.add(readList((JsonArray)value, new ArrayList(), elementTypeClass));
                } else {
                    elements.add(value.getValue());
                }
            }
            return elements;
        } catch (Exception ex) {
            if (!(ex instanceof JsonException)) {
                throw new JsonMappingException(ex);
            } else {
                throw (JsonException)ex;
            }
        }
    }

    private boolean isArrayData(String jsonData) {
        return jsonData.startsWith("[") && jsonData.endsWith("]");
    }

    private boolean isObjectData(String jsonData) {
        return jsonData.startsWith("{") && jsonData.endsWith("}");
    }

}
