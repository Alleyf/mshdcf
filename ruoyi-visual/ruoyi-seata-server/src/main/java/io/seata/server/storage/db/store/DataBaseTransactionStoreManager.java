/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.server.storage.db.store;

import io.seata.common.exception.StoreException;
import io.seata.common.loader.EnhancedServiceLoader;
import io.seata.common.util.CollectionUtils;
import io.seata.common.util.StringUtils;
import io.seata.config.Configuration;
import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.core.model.GlobalStatus;
import io.seata.core.store.BranchTransactionDO;
import io.seata.core.store.GlobalTransactionDO;
import io.seata.core.store.LogStore;
import io.seata.core.store.db.DataSourceProvider;
import io.seata.server.session.GlobalSession;
import io.seata.server.session.SessionCondition;
import io.seata.server.storage.SessionConverter;
import io.seata.server.store.AbstractTransactionStoreManager;
import io.seata.server.store.SessionStorable;
import io.seata.server.store.TransactionStoreManager;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

import static io.seata.common.DefaultValues.DEFAULT_QUERY_LIMIT;

/**
 * The type Database transaction store manager.
 *
 * @author zhangsen
 */
public class DataBaseTransactionStoreManager extends AbstractTransactionStoreManager
    implements TransactionStoreManager {

    /**
     * The constant CONFIG.
     */
    protected static final Configuration CONFIG = ConfigurationFactory.getInstance();
    private static volatile DataBaseTransactionStoreManager instance;
    /**
     * The Log store.
     */
    protected LogStore logStore;

    /**
     * The Log query limit.
     */
    protected int logQueryLimit;

    /**
     * Instantiates a new Database transaction store manager.
     */
    private DataBaseTransactionStoreManager() {
        logQueryLimit = CONFIG.getInt(ConfigurationKeys.STORE_DB_LOG_QUERY_LIMIT, DEFAULT_QUERY_LIMIT);
        String datasourceType = CONFIG.getConfig(ConfigurationKeys.STORE_DB_DATASOURCE_TYPE);
        //init dataSource
        DataSource logStoreDataSource = EnhancedServiceLoader.load(DataSourceProvider.class, datasourceType).provide();
        logStore = new LogStoreDataBaseDAO(logStoreDataSource);
    }

    /**
     * Get the instance.
     */
    public static DataBaseTransactionStoreManager getInstance() {
        if (instance == null) {
            synchronized (DataBaseTransactionStoreManager.class) {
                if (instance == null) {
                    instance = new DataBaseTransactionStoreManager();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean writeSession(LogOperation logOperation, SessionStorable session) {
        if (LogOperation.GLOBAL_ADD.equals(logOperation)) {
            return logStore.insertGlobalTransactionDO(SessionConverter.convertGlobalTransactionDO(session));
        } else if (LogOperation.GLOBAL_UPDATE.equals(logOperation)) {
            return logStore.updateGlobalTransactionDO(SessionConverter.convertGlobalTransactionDO(session));
        } else if (LogOperation.GLOBAL_REMOVE.equals(logOperation)) {
            return logStore.deleteGlobalTransactionDO(SessionConverter.convertGlobalTransactionDO(session));
        } else if (LogOperation.BRANCH_ADD.equals(logOperation)) {
            return logStore.insertBranchTransactionDO(SessionConverter.convertBranchTransactionDO(session));
        } else if (LogOperation.BRANCH_UPDATE.equals(logOperation)) {
            return logStore.updateBranchTransactionDO(SessionConverter.convertBranchTransactionDO(session));
        } else if (LogOperation.BRANCH_REMOVE.equals(logOperation)) {
            return logStore.deleteBranchTransactionDO(SessionConverter.convertBranchTransactionDO(session));
        } else {
            throw new StoreException("Unknown LogOperation:" + logOperation.name());
        }
    }

    /**
     * Read session global session.
     *
     * @param transactionId the transaction id
     * @return the global session
     */
    public GlobalSession readSession(Long transactionId) {
        //global transaction
        GlobalTransactionDO globalTransactionDO = logStore.queryGlobalTransactionDO(transactionId);
        if (globalTransactionDO == null) {
            return null;
        }
        //branch transactions
        List<BranchTransactionDO> branchTransactionDOs = logStore.queryBranchTransactionDO(
            globalTransactionDO.getXid());
        return getGlobalSession(globalTransactionDO, branchTransactionDOs);
    }

    /**
     * Read session global session.
     *
     * @param xid the xid
     * @return the global session
     */
    @Override
    public GlobalSession readSession(String xid) {
        return this.readSession(xid, true);
    }

    /**
     * Read session global session.
     *
     * @param xid the xid
     * @param withBranchSessions the withBranchSessions
     * @return the global session
     */
    @Override
    public GlobalSession readSession(String xid, boolean withBranchSessions) {
        //global transaction
        GlobalTransactionDO globalTransactionDO = logStore.queryGlobalTransactionDO(xid);
        if (globalTransactionDO == null) {
            return null;
        }
        //branch transactions
        List<BranchTransactionDO> branchTransactionDOs = null;
        //reduce rpc with db when branchRegister and getGlobalStatus
        if (withBranchSessions) {
            branchTransactionDOs = logStore.queryBranchTransactionDO(globalTransactionDO.getXid());
        }
        return getGlobalSession(globalTransactionDO, branchTransactionDOs);
    }

    @Override
    public List<GlobalSession> readSortByTimeoutBeginSessions(boolean withBranchSessions) {
        return readSession(new GlobalStatus[] {GlobalStatus.Begin}, withBranchSessions);
    }

    /**
     * Read session list.
     *
     * @param statuses the statuses
     * @return the list
     */
    @Override
    public List<GlobalSession> readSession(GlobalStatus[] statuses, boolean withBranchSessions) {
        int[] states = new int[statuses.length];
        for (int i = 0; i < statuses.length; i++) {
            states[i] = statuses[i].getCode();
        }
        //global transaction
        List<GlobalTransactionDO> globalTransactionDOs = logStore.queryGlobalTransactionDO(states, logQueryLimit);
        Map<String, List<BranchTransactionDO>> branchTransactionDOsMap = Collections.emptyMap();
        if (CollectionUtils.isNotEmpty(globalTransactionDOs)) {
            List<String> xids =
                globalTransactionDOs.stream().map(GlobalTransactionDO::getXid).collect(Collectors.toList());
            if (withBranchSessions) {
                List<BranchTransactionDO> branchTransactionDOs = logStore.queryBranchTransactionDO(xids);
                branchTransactionDOsMap = branchTransactionDOs.stream().collect(
                    Collectors.groupingBy(BranchTransactionDO::getXid, LinkedHashMap::new, Collectors.toList()));
            }
        }
        Map<String, List<BranchTransactionDO>> finalBranchTransactionDOsMap = branchTransactionDOsMap;
        return globalTransactionDOs.stream()
            .map(globalTransactionDO -> getGlobalSession(globalTransactionDO,
                    finalBranchTransactionDOsMap.get(globalTransactionDO.getXid()), withBranchSessions))
            .collect(Collectors.toList());
    }

    @Override
    public List<GlobalSession> readSession(SessionCondition sessionCondition) {
        if (StringUtils.isNotBlank(sessionCondition.getXid())) {
            GlobalSession globalSession = readSession(sessionCondition.getXid());
            if (globalSession != null) {
                List<GlobalSession> globalSessions = new ArrayList<>();
                globalSessions.add(globalSession);
                return globalSessions;
            }
        } else if (sessionCondition.getTransactionId() != null) {
            GlobalSession globalSession = readSession(sessionCondition.getTransactionId());
            if (globalSession != null) {
                List<GlobalSession> globalSessions = new ArrayList<>();
                globalSessions.add(globalSession);
                return globalSessions;
            }
        } else if (CollectionUtils.isNotEmpty(sessionCondition.getStatuses())) {
            return readSession(sessionCondition.getStatuses(), !sessionCondition.isLazyLoadBranch());
        }
        return null;
    }

    private GlobalSession getGlobalSession(GlobalTransactionDO globalTransactionDO,
        List<BranchTransactionDO> branchTransactionDOs) {
        return getGlobalSession(globalTransactionDO, branchTransactionDOs, true);
    }

    private GlobalSession getGlobalSession(GlobalTransactionDO globalTransactionDO,
        List<BranchTransactionDO> branchTransactionDOs, boolean withBranchSessions) {
        GlobalSession globalSession = SessionConverter.convertGlobalSession(globalTransactionDO, !withBranchSessions);
        // branch transactions
        if (CollectionUtils.isNotEmpty(branchTransactionDOs)) {
            for (BranchTransactionDO branchTransactionDO : branchTransactionDOs) {
                globalSession.add(SessionConverter.convertBranchSession(branchTransactionDO));
            }
        }
        return globalSession;
    }

    /**
     * Sets log store.
     *
     * @param logStore the log store
     */
    public void setLogStore(LogStore logStore) {
        this.logStore = logStore;
    }

    /**
     * Sets log query limit.
     *
     * @param logQueryLimit the log query limit
     */
    public void setLogQueryLimit(int logQueryLimit) {
        this.logQueryLimit = logQueryLimit;
    }
}
