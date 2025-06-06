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
package io.seata.server.event;

import io.seata.core.event.EventBus;
import io.seata.core.event.GuavaEventBus;

/**
 * Manager hold the singleton event bus instance.
 *
 * @author zhengyangyong
 */
public class EventBusManager {
    public static EventBus get() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static EventBus INSTANCE = new GuavaEventBus("tc",true);
    }
}
