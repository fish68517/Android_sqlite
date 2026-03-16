package com.hakimi.local;

public class LocalResult<T> {

    private final boolean success;
    private final String message;
    private final T data;

    private LocalResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> LocalResult<T> success(T data) {
        return new LocalResult<>(true, "", data);
    }

    public static <T> LocalResult<T> fail(String message) {
        return new LocalResult<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
