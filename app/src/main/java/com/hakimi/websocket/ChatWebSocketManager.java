package com.hakimi.websocket;

import android.util.Log;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * WebSocket管理类
 * 
 * @author hakimi
 */
public class ChatWebSocketManager {

    private static final String TAG = "ChatWebSocketManager";
    private static ChatWebSocketManager instance;
    private WebSocketClient webSocketClient;
    private WebSocketListener listener;
    private boolean isConnected = false;

    private ChatWebSocketManager() {
    }

    public static ChatWebSocketManager getInstance() {
        if (instance == null) {
            synchronized (ChatWebSocketManager.class) {
                if (instance == null) {
                    instance = new ChatWebSocketManager();
                }
            }
        }
        return instance;
    }

    /**
     * 连接WebSocket
     */
    public void connect(String serverUrl, WebSocketListener listener) {
        this.listener = listener;
        try {
            URI uri = URI.create(serverUrl);
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "WebSocket连接成功");
                    isConnected = true;
                    if (listener != null) {
                        listener.onOpen();
                    }
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "收到消息: " + message);
                    if (listener != null) {
                        listener.onMessage(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocket连接关闭: " + reason);
                    isConnected = false;
                    if (listener != null) {
                        listener.onClose(code, reason, remote);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "WebSocket错误", ex);
                    isConnected = false;
                    if (listener != null) {
                        listener.onError(ex);
                    }
                }
            };
            webSocketClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "连接WebSocket失败", e);
            if (listener != null) {
                listener.onError(e);
            }
        }
    }

    /**
     * 发送消息
     */
    public void sendMessage(Object message) {
        if (webSocketClient != null && isConnected) {
            String json = new Gson().toJson(message);
            webSocketClient.send(json);
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
            isConnected = false;
        }
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return isConnected && webSocketClient != null;
    }

    /**
     * WebSocket监听器
     */
    public interface WebSocketListener {
        void onOpen();

        void onMessage(String message);

        void onClose(int code, String reason, boolean remote);

        void onError(Exception ex);
    }
}

