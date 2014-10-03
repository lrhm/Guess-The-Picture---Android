package ir.treeco.aftabe.utils;

public class LoadingManager {
    private static int tasksCount = 0;

    public static void startTask(final TaskStartedListener taskStartedListener) {
        boolean wasZero = tasksCount == 0;
        tasksCount++;

        if (wasZero && someTaskStartedListener != null) {
            someTaskStartedListener.someTaskStarted(new TaskCallback() {
                @Override
                public void done() {
                    if (taskStartedListener != null)
                        taskStartedListener.taskStarted();
                }
            });
        } else {
            if (taskStartedListener != null)
                taskStartedListener.taskStarted();
        }
    }

    public static void endTask() {
        tasksCount--;
        if (tasksCount == 0 && tasksFinishedListener != null)
            tasksFinishedListener.tasksFinished();
        if (tasksCount < 0)
            tasksCount = 0;
    }

    static TasksFinishedListener tasksFinishedListener = null;
    static SomeTaskStartedListener someTaskStartedListener = null;

    public static void setTasksFinishedListener(TasksFinishedListener tasksFinishedListener) {
        LoadingManager.tasksFinishedListener = tasksFinishedListener;
    }

    public static void setSomeTaskStartedListener(SomeTaskStartedListener someTaskStartedListener) {
        LoadingManager.someTaskStartedListener = someTaskStartedListener;
    }
}
