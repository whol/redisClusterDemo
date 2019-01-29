package com.example.demo.common.http;

import com.example.demo.common.converter.AlienConvertMeta;
import com.example.demo.common.converter.AlienObjectConvertService;
import com.example.demo.common.converter.ConvertException;
import com.example.demo.common.converter.SwaggerJsonConverter;
import com.example.demo.common.utils.JSONUtils;
import com.example.demo.common.utils.TypeUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJacksonInputMessage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.charset.Charset;

/**
 *
 * 统一返回结果处理，将返回结果转为JSON对象
 *
 * Created by Zav Deng/dengzf@asiainfo.com on 17-3-7.
 */
public class JSONObjectHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private static final Logger LOGGER = Logger.getLogger(JSONObjectHttpMessageConverter.class);

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public AlienObjectConvertService alienConvertService = null;

    private ObjectMapper objectMapper = null;

    public JSONObjectHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8);
        this.objectMapper = Jackson2ObjectMapperBuilder.json()
                .build();
        this.alienConvertService = new AlienObjectConvertService(new AlienConvertMeta[] {
                new AlienConvertMeta(SwaggerJsonConverter.class, new String[]{"springfox.documentation.spring.web.json.Json"})
        });
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, new ParameteredInputMessage(inputMessage, DEFAULT_CHARSET));
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            if (inputMessage instanceof MappingJacksonInputMessage) {
                Class<?> deserializationView = ((MappingJacksonInputMessage) inputMessage).getDeserializationView();
                if (deserializationView != null) {
                    return this.objectMapper.readerWithView(deserializationView).forType(javaType).
                            readValue(inputMessage.getBody());
                }
            }
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read document: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void writeInternal(Object jsonObject, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (jsonObject != null) {
            // try alien object conversion
            if (alienConvertService.isConvertable(jsonObject)) {
                Object convertResult = null;
                try {
                    convertResult = alienConvertService.convert(jsonObject);
                } catch (ConvertException e) {
                    // fallback to original rendering
                    convertResult = jsonObject;
                }
                writeDataInternal(convertResult.toString().getBytes(DEFAULT_CHARSET), outputMessage);
            } else if (jsonObject instanceof JSONObject
                    || jsonObject instanceof JSONArray
                    || TypeUtils.isPrimitiveType(jsonObject.getClass())) {
                writeDataInternal(jsonObject.toString().getBytes(DEFAULT_CHARSET), outputMessage);
            } else {
                writeInternal(JSONUtils.toJSON(jsonObject), outputMessage);
            }
        }
    }

    private void writeDataInternal(byte[] data, HttpOutputMessage outputMessage) throws IOException {
        outputMessage.getHeaders().setContentLength(data.length);
        outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        outputMessage.getBody().write(data);
    }

    protected JavaType getJavaType(Type type, Class<?> contextClass) {
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        if (type instanceof TypeVariable && contextClass != null) {
            ResolvableType resolvedType = resolveVariable(
                    (TypeVariable<?>) type, ResolvableType.forClass(contextClass));
            if (resolvedType != ResolvableType.NONE) {
                return typeFactory.constructType(resolvedType.resolve());
            }
        }
        return typeFactory.constructType(type);
    }

    private ResolvableType resolveVariable(TypeVariable<?> typeVariable, ResolvableType contextType) {
        ResolvableType resolvedType;
        if (contextType.hasGenerics()) {
            resolvedType = ResolvableType.forType(typeVariable, contextType);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }
        resolvedType = resolveVariable(typeVariable, contextType.getSuperType());
        if (resolvedType.resolve() != null) {
            return resolvedType;
        }
        for (ResolvableType ifc : contextType.getInterfaces()) {
            resolvedType = resolveVariable(typeVariable, ifc);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }
        return ResolvableType.NONE;
    }
}
