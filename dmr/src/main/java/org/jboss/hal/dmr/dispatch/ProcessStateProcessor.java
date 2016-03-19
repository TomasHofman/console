/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.dmr.dispatch;

import org.jboss.hal.dmr.ModelNode;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public interface ProcessStateProcessor {

    String RESPONSE_HEADERS = "response-headers";
    String PROCESS_STATE = "process-state";
    String RESTART_REQUIRED = "restart-required";
    String RELOAD_REQUIRED = "reload-required";

    boolean accepts(ModelNode response);

    ProcessState process(ModelNode response);

    ProcessStateProcessor NOOP = new ProcessStateProcessor() {
        @Override
        public boolean accepts(final ModelNode response) {
            return false;
        }

        @Override
        public ProcessState process(final ModelNode response) {
            return ProcessState.EMPTY;
        }
    };
}