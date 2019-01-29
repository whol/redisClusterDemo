package com.example.demo.common.domain;

import com.example.demo.beans.common.JsonBean;
import com.example.demo.beans.common.JsonConvertException;
import com.example.demo.common.collections.DataMap;
import com.example.demo.common.json.Mappable;
import com.example.demo.common.utils.JSONUtils;
import com.example.demo.common.utils.JsonKeyConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统一入参对象
 * Created by Zav Deng/dengzf@asiainfo.com on 17-10-11.
 */
public class ServiceInput implements Mappable<Object>, Serializable {

    private final HttpServletRequest request;

    private final MediaType mediaType;

    private DataMap<Object> inputData;

    private boolean isReadableStream = false;

    private Object cachedObject;

    public ServiceInput(HttpServletRequest request) {
        this.inputData = new DataMap<>();
        this.request   = request;

        Map<String, String[]> params = request.getParameterMap();
        for (String key : params.keySet()) {
            inputData.put(key, params.get(key));
        }

        this.mediaType = MediaType.valueOf(request.getContentType());

        if (mediaType.isCompatibleWith(MediaType.MULTIPART_FORM_DATA)
                && request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> files = multipartRequest.getFileMap();
            for (String key : files.keySet()) {
                inputData.put(key, new MultipartFile[] { files.get(key) });
            }
        } else if (!mediaType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            isReadableStream = true;
        }
    }

    @Override
    public Object get(String key) {
        return inputData.get(key);
    }

    public boolean contains(String fieldName) {
        return inputData.containsKey(fieldName);
    }

    public MultipartFile getFile(String fieldName) {
        return getFirst(fieldName, MultipartFile.class);
    }

    public MultipartFile[] getFiles(String fieldName) {
        return get(fieldName, MultipartFile.class);
    }

    public List<MultipartFile> getFiles() {
        List<MultipartFile> files = new ArrayList<>();
        for (String key : this.inputData.keySet()) {
            Object value = inputData.get(key);
            if (value != null && value.getClass() == MultipartFile.class) {
                MultipartFile[] multipartFiles = (MultipartFile[]) value;
                for (MultipartFile file : multipartFiles)
                    files.add(file);
            }
        }
        return files;
    }

    public <T> T getFirst(String fieldName, Class<T> typeClass) {
        T[] values = get(fieldName, typeClass);
        return values != null && values.length > 0 ? values[0] : null;
    }

    public <T> T[] get(String fieldName, Class<T> typeClass) {
        if (typeClass == String.class || typeClass == MultipartFile.class) {
            return (T[])inputData.get(fieldName);
        }
        return null;
    }

    public Date getDate(String fieldName, String dateFormat) throws ParseException {
        if (!contains(fieldName))
            throw new NoSuchElementException("No such field: " + fieldName);
        return new SimpleDateFormat(dateFormat).parse(getString(fieldName));
    }

    public String getString(String fieldName) {
        return contains(fieldName) ? getFirst(fieldName, String.class) : null;
    }

    public long getLong(String fieldName, long defaultValue) {
        String strval = getString(fieldName);
        try {
            return Long.parseLong(strval);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getHeader(String headerName) {
        return request.getHeader(headerName);
    }

    public HeaderMap getHeaderMap() {
        HeaderMap headerMap = new HeaderMap();

        Enumeration<String> headerNamesE = request.getHeaderNames();
        while (headerNamesE.hasMoreElements()) {
            String headerName = headerNamesE.nextElement();

            Enumeration<String> headers  = request.getHeaders(headerName);
            HeaderValues headerList = new HeaderValues();
            while (headers.hasMoreElements()) {
                headerList.add(headers.nextElement());
            }

            headerMap.put(headerName, headerList);
        }
        return headerMap;
    }

    public <T extends JsonBean> T toEntity(Class<T> typeClass, Charset charset) throws IOException {
        try {
            return JSONUtils.toJavaBean((JSONObject) toJSON(charset), typeClass);
        } catch (JsonConvertException e) {
            throw new IOException(e);
        }
    }

    public <T> T toJSON(Charset charset) throws IOException {
        return toJSON(null, charset);
    }

    public <T> T toJSON(JsonKeyConverter keyConverter, Charset charset) throws IOException {
        if (cachedObject == null) {
            String data = readFully(charset);
            try {
                if (data.trim().startsWith("[")) {
                    cachedObject = new JSONArray(data);
                } else {
                    cachedObject = new JSONObject(data);
                    for (String key : inputData.keySet()) {
                        Object value = inputData.get(key);
                        if (value != null && value.getClass() == String[].class) {
                            String[] values = (String[])value;
                            if (values.length == 1) {
                                ((JSONObject) cachedObject).put(keyConverter == null ? key : keyConverter.convert(key), values[0]);
                            } else if (values.length > 1) {
                                ((JSONObject) cachedObject).put(keyConverter == null ? key : keyConverter.convert(key), values);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }
        return (T)cachedObject;
    }

    private String readFully(Charset charset) throws IOException {
        if (!isReadableStream) {
            return "{}";
        } else {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                try (InputStream input = request.getInputStream()) {
                    byte[] buffer = new byte[8192];

                    int nBytes    = input.read(buffer);
                    while (nBytes != -1) {
                        outputStream.write(buffer, 0, nBytes);
                        nBytes = input.read(buffer);
                    }
                }
                return new String(outputStream.toByteArray(), charset);
            }
        }
    }

}
