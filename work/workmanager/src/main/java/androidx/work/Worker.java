/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.work;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.concurrent.TimeUnit;

/**
 * The basic object that performs work.  Worker classes are instantiated at runtime by
 * {@link WorkManager} and the {@link #doWork()} method is called on a background thread.  In case
 * the work is preempted for any reason, the same instance of Worker is not reused.  This means
 * that {@link #doWork()} is called exactly once per Worker instance.
 */
public abstract class Worker extends NonBlockingWorker {

    // TODO(rahulrav@) Move this to a NonBlockingWorker once we are ready to expose it.
    /**
     * The result of the Worker's computation that is returned in the {@link #doWork()} method.
     */
    public enum Result {
        /**
         * Used to indicate that the work completed successfully.  Any work that depends on this
         * can be executed as long as all of its other dependencies and constraints are met.
         */
        SUCCESS,

        /**
         * Used to indicate that the work completed with a permanent failure.  Any work that depends
         * on this will also be marked as failed and will not be run.
         */
        FAILURE,

        /**
         * Used to indicate that the work encountered a transient failure and should be retried with
         * backoff specified in
         * {@link WorkRequest.Builder#setBackoffCriteria(BackoffPolicy, long, TimeUnit)}.
         */
        RETRY
    }

    /**
     * Override this method to do your actual background processing.
     */
    @WorkerThread
    public abstract @NonNull Result doWork();

    @Override
    public void onStartWork(@NonNull WorkFinishedCallback callback) {
        Result result = doWork();
        callback.onWorkFinished(result);
    }
}
