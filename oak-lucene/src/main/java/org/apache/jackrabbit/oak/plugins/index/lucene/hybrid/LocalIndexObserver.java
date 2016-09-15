/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.jackrabbit.oak.plugins.index.lucene.hybrid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.jackrabbit.oak.spi.commit.CommitContext;
import org.apache.jackrabbit.oak.spi.commit.CommitInfo;
import org.apache.jackrabbit.oak.spi.commit.Observer;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.stats.MeterStats;
import org.apache.jackrabbit.oak.stats.StatisticsProvider;
import org.apache.jackrabbit.oak.stats.StatsOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalIndexObserver implements Observer{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DocumentQueue docQueue;
    private final MeterStats added;
    private final MeterStats dropped;

    public LocalIndexObserver(DocumentQueue docQueue, StatisticsProvider sp) {
        this.docQueue = docQueue;
        this.added = sp.getMeter("HYBRID_ADDED", StatsOptions.DEFAULT);
        this.dropped = sp.getMeter("HYBRID_DROPPED", StatsOptions.DEFAULT);
    }

    @Override
    public void contentChanged(@Nonnull NodeState root, @Nullable CommitInfo info) {
        //TODO [hybrid] Do external diff?
        if (info == null){
           return;
        }

        CommitContext commitContext = (CommitContext) info.getInfo().get(CommitContext.NAME);
        //Commit done internally i.e. one not using Root/Tree API
        if (commitContext == null){
            return;
        }

        LuceneDocumentHolder holder = (LuceneDocumentHolder) commitContext.get(LuceneDocumentHolder.NAME);
        //Nothing to be indexed
        if (holder == null){
            return;
        }

        int addedCount = 0, droppedCount = 0;
        for (LuceneDoc doc : holder.getNRTIndexedDocs()){
            if (docQueue.add(doc)) {
                addedCount++;
            } else {
                droppedCount++;
            }
        }

        added.mark(addedCount);
        dropped.mark(droppedCount);
        if (droppedCount > 0){
            //TODO Ensure that log do not flood
            log.warn("Dropped [{}] docs from indexing as queue is full", droppedCount);
        }
    }
}
