package tc.oc.server;

import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.joda.time.Duration;

public class Scheduler {
  private final Plugin plugin;
  private final BukkitScheduler bukkitScheduler;
  private final WeakHashMap<BukkitTask, Object> tasks = new WeakHashMap<>();
  private final WeakHashMap<Runnable, BukkitTask> tasksByRunnable = new WeakHashMap<>();
  private boolean cancelled;

  public Scheduler(Plugin plugin, BukkitScheduler scheduler) {
    this.plugin = plugin;
    this.bukkitScheduler = scheduler;
  }

  public Scheduler(Plugin plugin) {
    this(plugin, plugin.getServer().getScheduler());
  }

  private static long ticks(Duration duration) {
    return (duration.getMillis() + 49) / 50;
  }

  private BukkitTask register(final BukkitTask task) {
    if (cancelled) {
      // If the scheduler is cancelled, immediately cancel any task created,
      // but still return it for the sake of consistency.
      task.cancel();
    } else {
      this.tasks.put(task, null);
    }
    return task;
  }

  /**
   * Cancel all currently scheduled tasks and permanently disable the scheduler, so that any future
   * tasks will be cancelled immediately.
   */
  public void cancel() {
    for (BukkitTask task : this.tasks.keySet()) {
      task.cancel();
    }
    this.tasks.clear();
    this.cancelled = true;
  }

  public boolean isPending(BukkitTask task) {
    return this.bukkitScheduler.isQueued(task.getTaskId())
        || this.bukkitScheduler.isCurrentlyRunning(task.getTaskId());
  }

  public BukkitTask runTask(Runnable task) {
    return this.register(this.bukkitScheduler.runTask(this.plugin, task));
  }

  public BukkitTask runTaskLater(long delay, Runnable task) {
    return this.register(this.bukkitScheduler.runTaskLater(this.plugin, task, delay));
  }

  public BukkitTask runTaskLater(Duration delay, Runnable task) {
    return this.register(this.bukkitScheduler.runTaskLater(this.plugin, task, ticks(delay)));
  }

  public BukkitTask runTaskTimer(long interval, Runnable task) {
    return this.register(this.bukkitScheduler.runTaskTimer(this.plugin, task, 0L, interval));
  }

  public BukkitTask runTaskTimer(long delay, long interval, Runnable task) {
    return this.register(this.bukkitScheduler.runTaskTimer(this.plugin, task, delay, interval));
  }

  public BukkitTask runTaskTimer(Duration interval, Runnable task) {
    return this.register(this.bukkitScheduler.runTaskTimer(this.plugin, task, 0L, ticks(interval)));
  }

  public BukkitTask runTaskTimer(Duration delay, Duration interval, Runnable task) {
    return this.register(
        this.bukkitScheduler.runTaskTimer(this.plugin, task, ticks(delay), ticks(interval)));
  }

  /**
   * Run the given task only if there is no instance of that task's class already scheduled or
   * running.
   *
   * @return The handle of the newly scheduled task, if it was scheduled, otherwise the handle of
   *     the existing task.
   */
  public BukkitTask debounce(Runnable task) {
    for (Map.Entry<Runnable, BukkitTask> running : tasksByRunnable.entrySet()) {
      if (task.getClass().isInstance(running.getKey()) && isPending(running.getValue())) {
        return running.getValue();
      }
    }
    return runTask(task);
  }
}
