package com.microalert.app.sensors

import com.microalert.app.sensors.model.SensorSample
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class DemoLevel(val label: String, val target: Float?) {
    AUTO("AUTO", null),
    NORMAL("NORMAL", 0f),
    PRECAUTION("PRECAUCIÓN", 0.33f),
    HIGH("ALTO", 0.42f),
    CRITICAL("CRÍTICO", 1f)
}

object DemoScenario {
    const val CALM_MS = 15_000L
    const val RAMP_MS = 45_000L
    const val HOLD_MS = 45_000L
    const val CYCLE_MS = CALM_MS + RAMP_MS + HOLD_MS
    const val WINDOW_MS = 12_000L
    const val STEP_MS = 4_000L
    const val INACTIVITY_SCALE = 60_000f / WINDOW_MS
    private const val MAX_STEP_PER_TICK = 0.08f

    @Volatile var level: DemoLevel = DemoLevel.AUTO
    @Volatile var current: Float = 0f
    @Volatile private var startMs: Long = 0L

    fun start(now: Long = System.currentTimeMillis()) {
        startMs = now
        current = 0f
        level = DemoLevel.AUTO
    }

    fun fatigueAt(elapsedMs: Long): Float {
        val t = elapsedMs % CYCLE_MS
        return when {
            t < CALM_MS -> 0f
            t < CALM_MS + RAMP_MS -> (t - CALM_MS).toFloat() / RAMP_MS
            else -> 1f
        }
    }

    fun tick(now: Long = System.currentTimeMillis()): Float {
        val target = level.target ?: fatigueAt(now - startMs)
        current += (target - current).coerceIn(-MAX_STEP_PER_TICK, MAX_STEP_PER_TICK)
        return current
    }

    fun reactionTimeMs(fatigue: Float): Long = (300 + 400 * fatigue).toLong()

    fun sessionSeconds(fatigue: Float): Float = 60f + 1440f * fatigue
}

class DemoSensorSimulator : SensorDataSource {

    private val _sensorDataStream = MutableStateFlow(SensorSample())
    override val sensorDataStream: Flow<SensorSample> = _sensorDataStream.asStateFlow()

    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun startSampling() {
        if (simulationJob?.isActive == true) return
        simulationJob = scope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val fatigue = DemoScenario.tick(now)

                val bpm = 74f - 18f * fatigue + (Random.nextFloat() * 2f - 1f)
                val motion = 1.6f * (1f - fatigue) + 0.05f
                val accelMag = 9.81f + (Random.nextFloat() * 2f - 1f) * motion
                val gyroMag = (0.01f + Random.nextFloat() * 0.06f * (1f - fatigue)).coerceAtLeast(0.005f)

                val baseRr = 60_000f / bpm
                val spread = 25f + 105f * fatigue
                val rr = List(4) { baseRr + (Random.nextFloat() * 2f - 1f) * spread }

                _sensorDataStream.value = SensorSample(
                    timestampMs = now,
                    heartRateBpm = bpm,
                    rIntervalsMs = rr,
                    accelX = 0.1f,
                    accelY = 9.8f,
                    accelZ = 0.2f,
                    accelMagnitude = accelMag,
                    gyroX = 0.01f,
                    gyroY = 0.02f,
                    gyroZ = 0.01f,
                    gyroMagnitude = gyroMag,
                    isHeartRateValid = true,
                    isAccelValid = true,
                    isGyroValid = true
                )
                delay(500L)
            }
        }
    }

    override fun stopSampling() {
        simulationJob?.cancel()
        simulationJob = null
    }

    override fun isHeartRateAvailable(): Boolean = true
    override fun isAccelerometerAvailable(): Boolean = true
    override fun isGyroscopeAvailable(): Boolean = true
    override fun isHrvAvailable(): Boolean = true
}
