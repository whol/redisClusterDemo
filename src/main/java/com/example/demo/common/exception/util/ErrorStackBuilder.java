package com.example.demo.common.exception.util;

import com.example.demo.common.exception.GeneralException;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2018 China Mobile
 * <pre>
 *   添加类说明
 * </pre>
 * Created by Zifeng Deng/dengzifeng@asiainfo.com on 18-3-14.
 *
 * @author Zifeng Deng
 * @since 1.0
 */
public class ErrorStackBuilder {

    private static ThreadLocal<String> currentExceptionClass = new ThreadLocal<>();

    static class ErrorStack {

        private String className;

        private String message;

        private ErrorStack parent;

        private ErrorStack causeBy;

        List<ErrorLocation> errorLocations = new ArrayList<>(0);

        public ErrorStack(String className, String message) {
            this.className = className;
            this.message = message;
        }

        public ErrorStack(String className, String message, ErrorStack parent) {
            this.parent = parent;
            this.className = className;
            this.message = message;
        }

        public String getClassName() {
            return className;
        }

        public String getMessage() {
            return message;
        }

        public ErrorStack getParent() {
            return parent;
        }

        public List<ErrorLocation> getErrorLocations() {
            return errorLocations;
        }

        public ErrorStack getCauseBy() {
            return causeBy;
        }

        public void setCauseBy(ErrorStack causeBy) {
            this.causeBy = causeBy;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.className)
                    .append(": ")
                    .append(message)
            .append("\n");
            for (ErrorLocation location : getErrorLocations()) {
                sb.append("\t at ").append(location.toString()).append("\n");
            }
            if (causeBy != null) {
                sb.append(" Cause by: ").append(causeBy.toString());
            }
            return sb.toString();
        }
    }

    static class FileLocation {
        private String filename;
        private int lineNo;

        public FileLocation(String filename, int lineNo) {
            this.filename = filename;
            this.lineNo = lineNo;
        }

        public String getFilename() {
            return filename;
        }

        public int getLineNo() {
            return lineNo;
        }

        public String toString() {
            return new StringBuilder(getFilename())
                    .append(":")
                    .append(getLineNo())
                    .toString();
        }
    }

    static class UnknownSource extends FileLocation {
        public UnknownSource() {
            super("Unknown Source", -1);
        }
    }

    static class ErrorLocation {
        private String className;
        private FileLocation location;

        public ErrorLocation(String className, FileLocation location) {
            this.className = className;
            this.location  = location;
        }

        public String getInvokedSignature() {
            return className;
        }

        public FileLocation getLocation() {
            return location;
        }

        public String toString() {
            return String.format("%s(%s)", getInvokedSignature(), getLocation());
        }
    }

    static class ErrorStackSource {

        static final int S_EXCEPTION = 0;
        static final int S_LOCATION  = 1;
        static final int S_CAUSE     = 2;
        static final int S_EOF       = 3;

        private char[] buffer;
        private int offset;
        private int currentState = S_EXCEPTION;

        public ErrorStackSource(String errorStack) {
            if (errorStack == null)
                throw new IllegalArgumentException("Invalid error stack: " + errorStack);
            this.buffer = errorStack.toCharArray();
            this.offset = 0;
        }

        public boolean hasMoreInput() {
            return this.offset < this.buffer.length;
        }

        public ErrorStack build() {

            ErrorStack rootCause = null;
            ErrorStack currentCause = null;

            loop: while (hasMoreInput()) {
                // skip whitespace
                while (hasMoreInput() && Character.isWhitespace(buffer[offset]))
                    offset++;

                if (!hasMoreInput()) {
                    return rootCause;
                }

                switch (currentState) {
                    case S_EXCEPTION: {
                        String className = readErrorClass();
                        String mesage = readErrorMessage();
                        currentState = S_LOCATION;
                        if (rootCause == null) {
                            currentCause = rootCause = new ErrorStack(className, mesage);
                        } else {
                            ErrorStack snapshot = new ErrorStack(className, mesage, currentCause);
                            currentCause.setCauseBy(snapshot);
                            currentCause = snapshot;
                        }
                    } break;
                    case S_LOCATION: {
                        currentState = readCauseLocation(currentCause);
                    } break;
                    case S_CAUSE: {
                        while (hasMoreInput()) {
                            char c = buffer[offset];
                            if (c == ':') {
                                offset++;
                                currentState = S_EXCEPTION;
                                continue loop;
                            }
                            offset++;
                        }
                    } break;
                    default: return rootCause;
                }
            }

            return rootCause;
        }

        private int readCauseLocation(ErrorStack parent) {
            String className = readClassName();
            FileLocation fileLocation = readFileLocation();
            parent.getErrorLocations().add(new ErrorLocation(className, fileLocation));
            if (expect("Cause", offset)) {
                return S_CAUSE;
            } else if (expect("at", offset)) {
                return S_LOCATION;
            } else {
                return S_EOF;
            }
        }

        private String readClassName() {
            StringBuilder sb = new StringBuilder();
            int state = 0;
            nextState: while (this.hasMoreInput()) {
                char c = buffer[this.offset];
                if (state == 0) {
                    if (Character.isWhitespace(c)) {
                        state = 1;
                        offset++;
                        continue nextState;
                    }
                    offset++;
                } else {
                    if (Character.isWhitespace(c))
                        continue;
                    if (c == '(') {
                        return sb.toString();
                    }
                    offset++;
                    sb.append(c);
                }
            }
            return null;
        }

        private FileLocation readFileLocation() {
            StringBuilder sb = new StringBuilder();
            String filename  = null;
            int state = 0;
            while (hasMoreInput()) {
                char c = buffer[offset];
                switch (state) {
                    case 0: // begin
                        if (c == '(') {
                            state = 1;
                            offset++;
                        }
                        break;
                    case 1: {
                        // File name
                        if (c == ')') {
                            offset++;
                            return new UnknownSource();
                        } else if (c == ':') {
                            state = 2;
                            offset++;
                            filename = sb.toString();
                            sb = new StringBuilder();
                        } else {
                            sb.append(c);
                            offset++;
                        }
                    } break;
                    case 2: {
                        if (c == ')') {
                            offset++;
                            return new FileLocation(filename, Integer.parseInt(sb.toString()));
                        }
                        offset++;
                        sb.append(c);
                    } break;
                    default: break;
                }
            }
            return new UnknownSource();
        }

        private String readErrorMessage() {
            StringBuilder sb = new StringBuilder();
            while (hasMoreInput()) {
                char c = buffer[offset];
                if (c == '\n') {
                    if (expect("at", offset)) {
                        // all message read
                        break;
                    }
                }
                offset++;
                sb.append(c);
            }
            return sb.toString();
        }

        private String readErrorClass() {
            StringBuilder sb = new StringBuilder();
            while (hasMoreInput()) {
                char c = buffer[offset++];
                if (c == ':') {
                    break;
                }
                sb.append(c);
                if (offset >= buffer.length){
                    break;
                }
            }
            return sb.toString();
        }

        private boolean expect(String lookup, int offset) {
            int i;
            int max = lookup.length();

            while (offset < buffer.length
                    && Character.isWhitespace(buffer[offset]))
                offset++;

            for (i = 0; i < max && offset < buffer.length; i++) {
                if (buffer[offset++] != lookup.charAt(i))
                    return false;
            }
            return max == i;
        }

    }

    public static ErrorStack build(String source, Charset charset) throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(source.getBytes(charset))) {
            return build(input, charset);
        }
    }

    public static ErrorStack build(File inputSource, Charset charset) throws IOException {
        try (FileInputStream input = new FileInputStream(inputSource)) {
            return build(input, charset);
        }
    }

    public static ErrorStack build(InputStream input, Charset charset) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            int nBytes;
            byte[] buffer = new byte[8192];
            while ((nBytes = input.read(buffer, 0, buffer.length)) != -1)
                output.write(buffer, 0, nBytes);
            return new ErrorStackSource(new String(output.toByteArray(), charset)).build();
        }
    }

    static class ErrorStackUtils {

        static Throwable createException(ErrorStack errorStack, Throwable parent) {
            Throwable error = null;
            try {
                Class<?> exceptionClass = Class.forName(errorStack.getClassName());
                error = (Throwable)exceptionClass.newInstance();
                setErrorField(error, "detailMessage", errorStack.getMessage());
            } catch (Throwable e) {
                currentExceptionClass.set(errorStack.getClassName());
                error = new GeneralException(ErrorConstants.SYSTEM.PRINCIPAL_CLASSNOTFOUND, errorStack.getClassName() + ":" + errorStack.getMessage());
            }
            if (parent != null) {
                setErrorField(parent, "cause", error);
            }
            error.setStackTrace(createStackTraceElements(errorStack));
            return error;
        }

        private static Field getField(String name, Class<?> targetType) throws Exception {
            if (targetType == null) {
                return null;
            } else {
                try {
                    return targetType.getDeclaredField(name);
                } catch (NoSuchFieldException e) {
                    return getField(name, targetType.getSuperclass());
                }
            }
        }

        private static StackTraceElement[] createStackTraceElements(ErrorStack errorStack) {
            int index = 0;
            StackTraceElement[] elements = new StackTraceElement[errorStack.getErrorLocations().size()];
            for (ErrorLocation location : errorStack.getErrorLocations()) {
                elements[index++] = createStackTraceElement(location);
            }
            return elements;
        }

        private static StackTraceElement createStackTraceElement(ErrorLocation location) {
            String signature = location.getInvokedSignature();
            String filename = location.getLocation().getFilename();
            int lineNo = location.getLocation().getLineNo();

            int offset = signature.lastIndexOf(".");
            if (offset != -1) {
                String declaredClassName = signature.substring(0, offset);
                String methodName = signature.substring(offset + 1);
                return new StackTraceElement(declaredClassName, methodName, filename, lineNo);
            } else {
                return new StackTraceElement(signature, signature, filename, lineNo);
            }
        }

        private static void setErrorField(Throwable error, String fieldName, Object value) {
            try {
                Field field  = getField(fieldName, error.getClass());
                field.setAccessible(true);
                field.set(error, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Throwable rebuildException(ErrorStack errorStack) {
        Throwable root = ErrorStackUtils.createException(errorStack, null);
        createErrorObject(errorStack.getCauseBy(), root);
        return root;
    }

    private static void createErrorObject(ErrorStack errorStack, Throwable parent) {
        if (errorStack != null ) {
            Throwable current = ErrorStackUtils.createException(errorStack, parent);
            createErrorObject(errorStack.getCauseBy(), current);
        }
    }

    public static String getCurrentExceptionClass() {
        return currentExceptionClass.get();
    }
}
