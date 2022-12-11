/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.support.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;

/**
 * CronTaskRegistrar
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022-02-11 23:29
 **/
public class TaskSchedulerRegistrarHelp implements DisposableBean {

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>(16);

    private final TaskScheduler                   taskScheduler;

    public TaskSchedulerRegistrarHelp(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    public void addCronTask(String taskId, Runnable task, String cronExpression) {
        addCronTask(taskId, new CronTask(task, cronExpression));
    }

    private void addCronTask(String taskId, CronTask cronTask) {
        if (cronTask != null) {
            if (this.scheduledTasks.containsKey(taskId)) {
                removeCronTask(taskId);
            }
            this.scheduledTasks.put(taskId, scheduleCronTask(cronTask));
        }
    }

    public void removeCronTask(String taskId) {
        ScheduledFuture<?> scheduledTask = this.scheduledTasks.remove(taskId);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
    }

    private ScheduledFuture<?> scheduleCronTask(CronTask cronTask) {
        return this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
    }

    @Override
    public void destroy() {
        for (ScheduledFuture<?> task : this.scheduledTasks.values()) {
            task.cancel(true);
        }
        this.scheduledTasks.clear();
    }
}
