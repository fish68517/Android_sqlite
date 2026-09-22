package com.example.sensecheck.checkin;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.sensecheck.util.PermissionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EnvironmentCollector {
    public interface Callback {
        void onCollected(EnvironmentSnapshot snapshot);
    }

    private static final long SCAN_TIMEOUT_MILLIS = 6_000L;

    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final EnvironmentSnapshot snapshot = new EnvironmentSnapshot();
    private final Set<String> bluetoothAddresses = new HashSet<>();

    private Callback callback;
    private boolean locationDone;
    private boolean bluetoothDone;
    private boolean wifiDirectDone;
    private boolean delivered;
    private LocationManager locationManager;
    private BluetoothLeScanner bluetoothLeScanner;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiP2pChannel;
    private BroadcastReceiver wifiP2pReceiver;
    private boolean wifiP2pReceiverRegistered;

    public EnvironmentCollector(Context context) {
        this.context = context.getApplicationContext();
    }

    public void collect(Callback callback) {
        this.callback = callback;
        collectWifi();
        collectLocation();
        collectBluetooth();
        collectWifiDirect();
        handler.postDelayed(this::forceFinish, SCAN_TIMEOUT_MILLIS);
    }

    @SuppressWarnings("deprecation")
    private void collectWifi() {
        if (!PermissionUtils.hasLocation(context)) {
            snapshot.setWifiCount(0);
            return;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return;
        }
        try {
            wifiManager.startScan();
            List<android.net.wifi.ScanResult> results = wifiManager.getScanResults();
            snapshot.setWifiCount(results == null ? 0 : results.size());
        } catch (SecurityException ignored) {
            snapshot.setWifiCount(0);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void collectLocation() {
        if (!PermissionUtils.hasLocation(context)) {
            locationDone = true;
            tryFinish();
            return;
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            locationDone = true;
            tryFinish();
            return;
        }

        Location lastLocation = bestLastKnownLocation(locationManager);
        if (lastLocation != null) {
            snapshot.setLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), lastLocation.getAccuracy());
        }

        String provider = null;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        if (provider == null) {
            locationDone = true;
            tryFinish();
            return;
        }

        try {
            locationManager.requestSingleUpdate(provider, locationListener, Looper.getMainLooper());
        } catch (IllegalArgumentException | SecurityException exception) {
            locationDone = true;
            tryFinish();
        }
    }

    @SuppressWarnings("MissingPermission")
    private Location bestLastKnownLocation(LocationManager manager) {
        Location best = null;
        for (String provider : manager.getProviders(true)) {
            try {
                Location value = manager.getLastKnownLocation(provider);
                if (value != null && (best == null || value.getTime() > best.getTime())) {
                    best = value;
                }
            } catch (SecurityException ignored) {
                return best;
            }
        }
        return best;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            snapshot.setLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy());
            locationDone = true;
            tryFinish();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @SuppressWarnings("MissingPermission")
    private void collectBluetooth() {
        if (!PermissionUtils.hasBluetoothScan(context)) {
            bluetoothDone = true;
            tryFinish();
            return;
        }
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager == null ? null : manager.getAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            bluetoothDone = true;
            tryFinish();
            return;
        }

        if (PermissionUtils.hasBluetoothConnect(context)) {
            try {
                adapter.getBondedDevices().forEach(device -> bluetoothAddresses.add(device.getAddress()));
            } catch (SecurityException ignored) {
                // 动态扫描结果仍可继续使用。
            }
        }

        bluetoothLeScanner = adapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            bluetoothDone = true;
            snapshot.setBluetoothCount(bluetoothAddresses.size());
            tryFinish();
            return;
        }
        try {
            bluetoothLeScanner.startScan(scanCallback);
            handler.postDelayed(this::finishBluetooth, SCAN_TIMEOUT_MILLIS - 500L);
        } catch (SecurityException | IllegalStateException exception) {
            finishBluetooth();
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result != null && result.getDevice() != null) {
                try {
                    bluetoothAddresses.add(result.getDevice().getAddress());
                } catch (SecurityException ignored) {
                    // 权限变化时忽略单个结果。
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            if (results != null) {
                for (ScanResult result : results) {
                    onScanResult(0, result);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            finishBluetooth();
        }
    };

    @SuppressWarnings("MissingPermission")
    private void finishBluetooth() {
        if (bluetoothDone) {
            return;
        }
        bluetoothDone = true;
        if (bluetoothLeScanner != null) {
            try {
                bluetoothLeScanner.stopScan(scanCallback);
            } catch (SecurityException | IllegalStateException ignored) {
                // 扫描已由系统停止。
            }
        }
        snapshot.setBluetoothCount(bluetoothAddresses.size());
        tryFinish();
    }

    @SuppressWarnings("MissingPermission")
    private void collectWifiDirect() {
        if (!PermissionUtils.hasLocation(context) || !PermissionUtils.hasNearbyWifi(context)) {
            wifiDirectDone = true;
            tryFinish();
            return;
        }
        wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            wifiDirectDone = true;
            tryFinish();
            return;
        }
        wifiP2pChannel = wifiP2pManager.initialize(context, Looper.getMainLooper(), null);
        if (wifiP2pChannel == null) {
            wifiDirectDone = true;
            tryFinish();
            return;
        }

        wifiP2pReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context receiverContext, Intent intent) {
                if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(intent.getAction())) {
                    try {
                        wifiP2pManager.requestPeers(wifiP2pChannel, EnvironmentCollector.this::onPeersAvailable);
                    } catch (SecurityException exception) {
                        finishWifiDirect(0);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(wifiP2pReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(wifiP2pReceiver, filter);
        }
        wifiP2pReceiverRegistered = true;

        try {
            wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // 等待系统广播返回设备列表。
                }

                @Override
                public void onFailure(int reason) {
                    finishWifiDirect(0);
                }
            });
            handler.postDelayed(() -> finishWifiDirect(snapshot.getWifiDirectCount()),
                    SCAN_TIMEOUT_MILLIS - 300L);
        } catch (SecurityException exception) {
            finishWifiDirect(0);
        }
    }

    private void onPeersAvailable(WifiP2pDeviceList peers) {
        finishWifiDirect(peers == null ? 0 : peers.getDeviceList().size());
    }

    private void finishWifiDirect(int count) {
        if (wifiDirectDone) {
            return;
        }
        wifiDirectDone = true;
        snapshot.setWifiDirectCount(count);
        unregisterWifiP2pReceiver();
        tryFinish();
    }

    private void tryFinish() {
        if (locationDone && bluetoothDone && wifiDirectDone) {
            deliver();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void forceFinish() {
        locationDone = true;
        finishBluetooth();
        finishWifiDirect(snapshot.getWifiDirectCount());
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException ignored) {
                // 权限被撤销时忽略。
            }
        }
        deliver();
    }

    private void unregisterWifiP2pReceiver() {
        if (!wifiP2pReceiverRegistered) {
            return;
        }
        wifiP2pReceiverRegistered = false;
        try {
            context.unregisterReceiver(wifiP2pReceiver);
        } catch (IllegalArgumentException ignored) {
            // 接收器已经注销。
        }
    }

    private void deliver() {
        if (delivered) {
            return;
        }
        delivered = true;
        handler.removeCallbacksAndMessages(null);
        unregisterWifiP2pReceiver();
        if (callback != null) {
            callback.onCollected(snapshot);
        }
    }
}
