package stas.batura.radioproject.download;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import javax.inject.Inject;

import stas.batura.radioproject.R;

public class PodcastDownloadService extends DownloadService {

    private static final String TAG = "PodcastDownloadService";

    @Inject
    DownloadManager downloadManager;

    @Inject
    DownloadNotificationHelper notificationHelper;

    private static final int JOB_ID = 1;
    private static final int FOREGROUND_NOTIFICATION_ID = 1;
    public static final String DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel";

    public PodcastDownloadService() {
        super(
                FOREGROUND_NOTIFICATION_ID,
                DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                DOWNLOAD_NOTIFICATION_CHANNEL_ID,
                R.string.exo_download_notification_channel_name
        );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    @NonNull
    protected DownloadManager getDownloadManager() {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        downloadManager.addListener(
                new TerminalStateNotificationHelper(
                        this, notificationHelper, FOREGROUND_NOTIFICATION_ID + 1));
        return downloadManager;
    }

    @Override
    protected PlatformScheduler getScheduler() {
        return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
    }

    @Override
    @NonNull
    protected Notification getForegroundNotification(@NonNull List<Download> downloads) {
        return notificationHelper
                .buildProgressNotification(
                        this,
                        R.drawable.ic_notifications_black_24dp,
                        /* message= */ null,
                        "Downl",
                        downloads);
    }

    /**
     * Creates and displays notifications for downloads when they complete or fail.
     *
     * <p>This helper will outlive the lifespan of a single instance of {@link PodcastDownloadService}.
     * It is static to avoid leaking the first {@link PodcastDownloadService} instance.
     */
    private static final class TerminalStateNotificationHelper implements DownloadManager.Listener, stas.batura.radioproject.download.TerminalStateNotificationHelper {

        private final Context context;
        private final DownloadNotificationHelper notificationHelper;

        private int nextNotificationId;

        public TerminalStateNotificationHelper(
                Context context, DownloadNotificationHelper notificationHelper, int firstNotificationId) {
            this.context = context.getApplicationContext();
            this.notificationHelper = notificationHelper;
            nextNotificationId = firstNotificationId;
        }

        @Override
        public void onDownloadChanged(DownloadManager downloadManager, Download download, @Nullable Exception finalException) {
            Notification notification;
            if (download.state == Download.STATE_COMPLETED) {
                notification =
                        notificationHelper.buildDownloadCompletedNotification(
                                context,
                                R.drawable.ic_pause_black_24dp,
                                /* contentIntent= */ null,
                                Util.fromUtf8Bytes(download.request.data));
            } else if (download.state == Download.STATE_FAILED) {
                notification =
                        notificationHelper.buildDownloadFailedNotification(
                                context,
                                R.drawable.ic_baseline_cloud_download_24,
                                /* contentIntent= */ null,
                                Util.fromUtf8Bytes(download.request.data));
            } else {
                return;
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification);
        }
    }

    //        @Override
//        public void onDownloadChanged(
//                DownloadManager downloadManager, Download download) {
//            Notification notification;
//            if (download.state == Download.STATE_COMPLETED) {
//                notification =
//                        notificationHelper.buildDownloadCompletedNotification(
//                                context,
//                                R.drawable.ic_pause_black_24dp,
//                                /* contentIntent= */ null,
//                                Util.fromUtf8Bytes(download.request.data));
//            } else if (download.state == Download.STATE_FAILED) {
//                notification =
//                        notificationHelper.buildDownloadFailedNotification(
//                                context,
//                                R.drawable.ic_baseline_cloud_download_24,
//                                /* contentIntent= */ null,
//                                Util.fromUtf8Bytes(download.request.data));
//            } else {
//                return;
//            }
//            NotificationUtil.setNotification(context, nextNotificationId++, notification);
//        }

}
