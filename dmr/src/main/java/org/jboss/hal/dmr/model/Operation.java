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
package org.jboss.hal.dmr.model;

import org.jboss.hal.dmr.ModelNode;
import org.jetbrains.annotations.NonNls;

import static org.jboss.hal.dmr.ModelDescriptionConstants.*;

/**
 * @author Harald Pehl
 */
public class Operation extends ModelNode {

    public static class Builder {

        private final String name;
        private final ResourceAddress address;
        private ModelNode parameter;
        private String role;

        public Builder(final String name, final ResourceAddress address) {
            this.address = address;
            this.name = name;
            this.parameter = new ModelNode();
        }

        public Builder param(String name, boolean value) {
            parameter.get(name).set(value);
            return this;
        }

        public Builder param(String name, int value) {
            parameter.get(name).set(value);
            return this;
        }

        public Builder param(String name, long value) {
            parameter.get(name).set(value);
            return this;
        }

        public Builder param(String name, double value) {
            parameter.get(name).set(value);
            return this;
        }

        public Builder param(String name, @NonNls String value) {
            parameter.get(name).set(value);
            return this;
        }

        public Builder payload(ModelNode payload) {
            parameter = payload;
            return this;
        }

        public Builder runAs(String role) {
            this.role = role;
            return this;
        }

        public Operation build() {
            return new Operation(name, address, parameter, role);
        }
    }


    private Operation(final ModelNode modelNode) {
        set(modelNode);
    }

    Operation(final String name, final ResourceAddress address, final ModelNode parameter,
            final String role) {
        this(parameter);
        get(OP).set(name);
        get(ADDRESS).set(address);
        if (role != null && !name.equals(WHOAMI)) {
            // otherwise we get the replacement role
            get(OPERATION_HEADERS).get(ROLES).set(role);
        }
    }

    @Override
    public Operation clone() {
        return new Operation(super.clone());
    }
}