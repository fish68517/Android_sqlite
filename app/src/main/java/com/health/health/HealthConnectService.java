package com.Health.health;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.aggregate.AggregateMetric;
import androidx.health.connect.client.aggregate.AggregationResult;
import androidx.health.connect.client.aggregate.AggregationResultGroupedByPeriod;
import androidx.health.connect.client.records.HeartRateRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.records.metadata.DataOrigin;
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.response.ReadRecordsResponse;
import androidx.health.connect.client.time.TimeRangeFilter;

import com.Health.utils.HealthDebugLogger;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;

public class HealthConnectService {

    private static final String TAG = "HealthConnectService";

    public static final String PERMISSION_READ_STEPS = "android.permission.health.READ_STEPS";
    public static final String PERMISSION_READ_HEART_RATE = "android.permission.health.READ_HEART_RATE";

    private final Context appContext;

    public HealthConnectService(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public int getSdkStatus() {
        int status = HealthConnectClient.sdkStatus(appContext, HealthConstants.HEALTH_CONNECT_PACKAGE);
        HealthDebugLogger.d(TAG, "getSdkStatus=" + status);
        return status;
    }

    public boolean isAvailable() {
        return getSdkStatus() == HealthConnectClient.SDK_AVAILABLE;
    }

    public Intent buildProviderInstallIntent() {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + HealthConstants.HEALTH_CONNECT_PACKAGE));
        marketIntent.setPackage("com.android.vending");
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return marketIntent;
    }

    public Set<String> buildRequestedPermissions(boolean includeBackground) {
        LinkedHashSet<String> permissions = new LinkedHashSet<>();
        permissions.add(PERMISSION_READ_STEPS);
        permissions.add(PERMISSION_READ_HEART_RATE);
        return permissions;
    }

    public boolean hasBasicPermissions() {
        return getGrantedPermissions().containsAll(buildRequestedPermissions(false));
    }

    public boolean hasBackgroundPermission() {
        return false;
    }

    public boolean isBackgroundReadSupported() {
        return false;
    }

    public Set<String> getGrantedPermissions() {
        if (!isAvailable()) {
            return Collections.emptySet();
        }
        try {
            Set<String> permissions = runBlocking(continuation ->
                    getClient().getPermissionController().getGrantedPermissions(continuation));
            HealthDebugLogger.d(TAG, "getGrantedPermissions success. count=" + permissions.size()
                    + ", permissions=" + permissions);
            return permissions;
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            HealthDebugLogger.w(TAG, "getGrantedPermissions interrupted");
            return Collections.emptySet();
        } catch (Exception ignored) {
            HealthDebugLogger.e(TAG, "getGrantedPermissions failed", ignored);
            return Collections.emptySet();
        }
    }

    public HealthConnectReadResult readLatestData(boolean allowBackgroundRead) throws Exception {
        HealthConnectReadResult result = new HealthConnectReadResult();
        result.available = isAvailable();
        if (!result.available) {
            result.message = "Health Connect unavailable";
            HealthDebugLogger.w(TAG, "readLatestData aborted because Health Connect is unavailable");
            return result;
        }

        Set<String> grantedPermissions = getGrantedPermissions();
        result.permissionsGranted = grantedPermissions.containsAll(buildRequestedPermissions(false));
        result.backgroundPermissionGranted = false;

        if (!result.permissionsGranted) {
            result.message = "Health permissions missing";
            HealthDebugLogger.w(TAG, "readLatestData aborted because permissions are missing");
            return result;
        }

        HealthConnectClient client = getClient();
        result.stepSamples.addAll(readDailySteps(client));
        result.heartRateDailySamples.addAll(readDailyHeartRate(client));
        result.latestHeartRate = readLatestHeartRate(client);
        result.message = "Health Connect sync ready";
        HealthDebugLogger.i(TAG, "readLatestData success. stepSampleCount=" + result.stepSamples.size()
                + ", heartRateDailyCount=" + result.heartRateDailySamples.size()
                + ", latestHeartRate=" + describeSample(result.latestHeartRate));
        return result;
    }

    private HealthConnectClient getClient() {
        return HealthConnectClient.getOrCreate(appContext);
    }

    private List<DailySample> readDailySteps(HealthConnectClient client) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Set<AggregateMetric<?>> metrics = new LinkedHashSet<>();
        metrics.add(StepsRecord.COUNT_TOTAL);
        AggregateGroupByPeriodRequest request = new AggregateGroupByPeriodRequest(
                metrics,
                TimeRangeFilter.between(startInstant, endInstant),
                Period.ofDays(1),
                Collections.<DataOrigin>emptySet()
        );

        List<AggregationResultGroupedByPeriod> response = runBlocking(
                continuation -> client.aggregateGroupByPeriod(request, continuation));
        List<DailySample> samples = new ArrayList<>();
        for (AggregationResultGroupedByPeriod bucket : response) {
            AggregationResult aggregationResult = bucket.getResult();
            Long count = aggregationResult.get(StepsRecord.COUNT_TOTAL);
            if (count == null) {
                continue;
            }
            DailySample sample = new DailySample();
            sample.sampleDay = bucket.getStartTime().toLocalDate().toString();
            sample.sampleTime = bucket.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            sample.valuePrimary = count.doubleValue();
            sample.unit = "steps";
            sample.sourceType = HealthConstants.SOURCE_HEALTH_CONNECT;
            samples.add(sample);
        }
        HealthDebugLogger.d(TAG, "readDailySteps finished. sampleCount=" + samples.size());
        return samples;
    }

    private List<DailySample> readDailyHeartRate(HealthConnectClient client) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Set<AggregateMetric<?>> metrics = new LinkedHashSet<>();
        metrics.add(HeartRateRecord.BPM_AVG);
        AggregateGroupByPeriodRequest request = new AggregateGroupByPeriodRequest(
                metrics,
                TimeRangeFilter.between(startInstant, endInstant),
                Period.ofDays(1),
                Collections.<DataOrigin>emptySet()
        );

        List<AggregationResultGroupedByPeriod> response = runBlocking(
                continuation -> client.aggregateGroupByPeriod(request, continuation));
        List<DailySample> samples = new ArrayList<>();
        for (AggregationResultGroupedByPeriod bucket : response) {
            AggregationResult aggregationResult = bucket.getResult();
            Long bpm = aggregationResult.get(HeartRateRecord.BPM_AVG);
            if (bpm == null) {
                continue;
            }
            DailySample sample = new DailySample();
            sample.sampleDay = bucket.getStartTime().toLocalDate().toString();
            sample.sampleTime = bucket.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            sample.valuePrimary = bpm.doubleValue();
            sample.unit = "bpm";
            sample.sourceType = HealthConstants.SOURCE_HEALTH_CONNECT;
            samples.add(sample);
        }
        HealthDebugLogger.d(TAG, "readDailyHeartRate finished. sampleCount=" + samples.size());
        return samples;
    }

    private DailySample readLatestHeartRate(HealthConnectClient client) throws Exception {
        Instant endInstant = Instant.now();
        Instant startInstant = endInstant.minus(Duration.ofDays(3));
        ReadRecordsRequest<HeartRateRecord> request = new ReadRecordsRequest<>(
                JvmClassMappingKt.getKotlinClass(HeartRateRecord.class),
                TimeRangeFilter.between(startInstant, endInstant),
                Collections.<DataOrigin>emptySet(),
                false,
                50,
                null
        );
        ReadRecordsResponse<HeartRateRecord> response = runBlocking(
                continuation -> client.readRecords(request, continuation));
        HeartRateRecord.Sample latestSample = null;
        for (HeartRateRecord record : response.getRecords()) {
            for (HeartRateRecord.Sample sample : record.getSamples()) {
                if (latestSample == null || sample.getTime().isAfter(latestSample.getTime())) {
                    latestSample = sample;
                }
            }
        }
        if (latestSample == null) {
            HealthDebugLogger.w(TAG, "readLatestHeartRate found no heart rate samples");
            return null;
        }
        DailySample sample = new DailySample();
        sample.sampleDay = LocalDateTime.ofInstant(latestSample.getTime(), ZoneId.systemDefault())
                .toLocalDate().toString();
        sample.sampleTime = latestSample.getTime().toEpochMilli();
        sample.valuePrimary = (double) latestSample.getBeatsPerMinute();
        sample.unit = "bpm";
        sample.sourceType = HealthConstants.SOURCE_HEALTH_CONNECT;
        HealthDebugLogger.d(TAG, "readLatestHeartRate success. sample=" + describeSample(sample));
        return sample;
    }

    private <T> T runBlocking(HealthConnectCall<T> call) throws InterruptedException {
        return BuildersKt.runBlocking(
                EmptyCoroutineContext.INSTANCE,
                new Function2<CoroutineScope, Continuation<? super T>, Object>() {
                    @Override
                    public Object invoke(CoroutineScope coroutineScope, Continuation<? super T> continuation) {
                        return call.invoke(continuation);
                    }
                });
    }

    private interface HealthConnectCall<T> {
        Object invoke(Continuation<? super T> continuation);
    }

    private String describeSample(DailySample sample) {
        if (sample == null) {
            return "null";
        }
        return "primary=" + sample.valuePrimary
                + ",secondary=" + sample.valueSecondary
                + ",unit=" + sample.unit
                + ",source=" + sample.sourceType
                + ",sampleDay=" + sample.sampleDay
                + ",sampleTime=" + sample.sampleTime;
    }

    public static class DailySample {
        public String sampleDay;
        public long sampleTime;
        public Double valuePrimary;
        public Double valueSecondary;
        public String unit;
        public String sourceType;
    }

    public static class HealthConnectReadResult {
        public boolean available;
        public boolean permissionsGranted;
        public boolean backgroundPermissionGranted;
        public String message;
        public final List<DailySample> stepSamples = new ArrayList<>();
        public final List<DailySample> heartRateDailySamples = new ArrayList<>();
        public DailySample latestHeartRate;
    }
}
