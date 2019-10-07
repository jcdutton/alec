/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.alec.datasource.opennms.processors;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum StreamRecorder {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(StreamRecorder.class);
    private final BlockingQueue<DeleteRecord> situationDeletes = new ArrayBlockingQueue<>(1000);
    private final BlockingQueue<DeleteRecord> alarmDeletes = new ArrayBlockingQueue<>(1000);

    Path situationPath = Paths.get("/tmp", "situation.deletes");
    Path alarmPath = Paths.get("/tmp", "alarm.deletes");

    StreamRecorder() {
        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> process(alarmDeletes, alarmPath));
        executor.execute(() -> process(situationDeletes, situationPath));
    }

    public void recordSituationDelete(String key) {
        LOG.debug("Received situation delete for key {}", key);
        DeleteRecord deleteRecord = new DeleteRecord(key);

        if (!situationDeletes.offer(deleteRecord)) {
            LOG.warn("Failed to enqueue situation delete {}", deleteRecord);
        }
    }

    public void recordAlarmDelete(String key) {
        LOG.debug("Received alarm delete for key {}", key);
        DeleteRecord deleteRecord = new DeleteRecord(key);

        if (!alarmDeletes.offer(deleteRecord)) {
            LOG.warn("Failed to enqueue alarm delete {}", deleteRecord);
        }
    }

    private void process(BlockingQueue<DeleteRecord> queue, Path filePath) {
        while (true) {
            try {
                DeleteRecord deleteRecord = queue.take();

                try {
                    Files.write(filePath, (deleteRecord.toString() + "\n").getBytes(), CREATE, APPEND);
                } catch (IOException e) {
                    LOG.warn("Failed to write record {}", deleteRecord, e);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static class DeleteRecord {
        private static final DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        private final String key;
        private final Date timestamp;

        DeleteRecord(String key) {
            this.key = key;
            this.timestamp = new Date();
        }

        @Override
        public String toString() {
            return String.format("%s %s", formatter.format(timestamp), key);
        }
    }
}
