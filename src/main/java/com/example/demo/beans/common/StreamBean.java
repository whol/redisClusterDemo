package com.example.demo.beans.common;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by alen on 17-10-10.
 */
public class StreamBean implements JsonObjectBean {

    private InputStream inputStream;

    public StreamBean(InputStream stream) {
        this.inputStream = stream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void write(OutputStream outputStream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);

        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        WritableByteChannel outputChannel = Channels.newChannel(outputStream);

        if (inputChannel.isOpen() && outputChannel.isOpen()) {
            while (inputChannel.read(buffer) != -1) {
                buffer.flip();
                outputChannel.write(buffer);
            }
        }
    }

    @Override
    public JSONObject toJSON() throws JsonFormatException {
        JSONObject result = new JSONObject();
        result.put("stream", inputStream != null ? inputStream.toString(): "<NULL>");
        return result;
    }

    @Override
    public String toJSONString() throws JsonFormatException {
        return toJSON().toString();
    }

}
