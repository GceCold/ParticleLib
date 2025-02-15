package top.zoyn.particlelib.pobject;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import top.zoyn.particlelib.ParticleLib;
import top.zoyn.particlelib.utils.matrix.Matrix;

import java.awt.*;

/**
 * 表示一个特效对象
 *
 * @author Zoyn IceCold
 */
public abstract class ParticleObject {

    private Location origin;

    private ShowType showType = ShowType.NONE;
    private BukkitTask task;
    private long period;
    private boolean running = false;

    private Particle particle = Particle.VILLAGER_HAPPY;
    private int count = 1;
    private double offsetX = 0;
    private double offsetY = 0;
    private double offsetZ = 0;
    private double extra = 0;
    private Object data = null;

    public abstract void show();

    public void alwaysShow() {
        turnOffTask();

        // 此处的延迟 2tick 是为了防止turnOffTask还没把特效给关闭时的缓冲
        Bukkit.getScheduler().runTaskLater(ParticleLib.getInstance(), () -> {
            running = true;
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!running) {
                        return;
                    }
                    show();
                }
            }.runTaskTimer(ParticleLib.getInstance(), 0L, period);

            setShowType(ShowType.ALWAYS_SHOW);
        }, 2L);
    }

    public void alwaysShowAsync() {
        turnOffTask();

        // 此处的延迟 2tick 是为了防止turnOffTask还没把特效给关闭时的缓冲
        Bukkit.getScheduler().runTaskLater(ParticleLib.getInstance(), () -> {
            running = true;
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!running) {
                        return;
                    }
                    show();
                }
            }.runTaskTimerAsynchronously(ParticleLib.getInstance(), 0L, period);

            setShowType(ShowType.ALWAYS_SHOW_ASYNC);
        }, 2L);
    }

    public void alwaysPlay() {
        if (!(this instanceof Playable)) {
            try {
                throw new NoSuchMethodException("该对象不支持播放!");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        Playable playable = (Playable) this;
        turnOffTask();

        // 此处的延迟 2tick 是为了防止turnOffTask还没把特效给关闭时的缓冲
        Bukkit.getScheduler().runTaskLater(ParticleLib.getInstance(), () -> {
            running = true;
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!running) {
                        return;
                    }
                    playable.playNextPoint();
                }
            }.runTaskTimer(ParticleLib.getInstance(), 0L, period);

            setShowType(ShowType.ALWAYS_PLAY);
        }, 2L);
    }

    public void alwaysPlayAsync() {
        if (!(this instanceof Playable)) {
            try {
                throw new NoSuchMethodException("该对象不支持播放!");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        Playable playable = (Playable) this;
        turnOffTask();

        // 此处的延迟 2tick 是为了防止turnOffTask还没把特效给关闭时的缓冲
        Bukkit.getScheduler().runTaskLater(ParticleLib.getInstance(), () -> {
            running = true;
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!running) {
                        return;
                    }
                    playable.playNextPoint();
                }
            }.runTaskTimerAsynchronously(ParticleLib.getInstance(), 0L, period);

            setShowType(ShowType.ALWAYS_PLAY_ASYNC);
        }, 2L);
    }

    public void turnOffTask() {
        if (task != null) {
            running = false;
            task.cancel();
            setShowType(ShowType.NONE);
        }
    }

    /**
     * 表示该特效对象所拥有的矩阵
     */
    private Matrix matrix;

    public void addMatrix(Matrix matrix) {
        if (this.matrix == null) {
            setMatrix(matrix);
            return;
        }
        this.matrix = matrix.multiply(this.matrix);
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public void removeMatrix() {
        matrix = null;
    }

    public boolean hasMatrix() {
        return matrix != null;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public ShowType getShowType() {
        return showType;
    }

    public void setShowType(ShowType showType) {
        this.showType = showType;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public double getExtra() {
        return extra;
    }

    public void setExtra(double extra) {
        this.extra = extra;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 通过给定一个坐标就可以使用已经指定的参数来播放粒子
     *
     * @param location 坐标
     */
    public void spawnParticle(Location location) {
        Location showLocation = location;
        if (hasMatrix()) {
            Vector vector = location.clone().subtract(origin).toVector();
            Vector changed = matrix.applyVector(vector);

            showLocation = origin.clone().add(changed);
        }
        location.getWorld().spawnParticle(particle, showLocation, count, offsetX, offsetY, offsetZ, extra, data);
    }

    /**
     * 播放彩色粒子
     * @param location 坐标
     * @param color 颜色
     */
    public void spawnParticle(Location location,Color color) {
        Location showLocation = location;
        if (hasMatrix()) {
            Vector vector = location.clone().subtract(origin).toVector();
            Vector changed = matrix.applyVector(vector);

            showLocation = origin.clone().add(changed);
        }
        if (isNewer()){
            Particle.DustOptions dust = new Particle.DustOptions(color, 1);
            location.getWorld().spawnParticle(Particle.REDSTONE, showLocation.getX(), showLocation.getY(), showLocation.getZ(), 0,0, 0, 0,1,dust);
        }else {
            location.getWorld().spawnParticle(Particle.REDSTONE, showLocation.getX(), showLocation.getY(), showLocation.getZ(), 0,color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f,1);
        }
    }

    public static boolean isNewer(){
        String bukkitVersion = Bukkit.getBukkitVersion();
        return !bukkitVersion.contains("1.6") && !bukkitVersion.contains("1.7") && !bukkitVersion.contains("1.8") && !bukkitVersion.contains("1.9") && !bukkitVersion.contains("1.10") && !bukkitVersion.contains("1.11") && !bukkitVersion.contains("1.12");
    }

}