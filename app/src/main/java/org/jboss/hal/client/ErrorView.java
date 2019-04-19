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
package org.jboss.hal.client;

import javax.annotation.PostConstruct;

import org.jboss.gwt.elemento.template.Templated;
import org.jboss.hal.config.Environment;
import org.jboss.hal.core.mvp.HalViewImpl;
import org.jboss.hal.resources.Resources;

@Templated
public abstract class ErrorView extends HalViewImpl implements ErrorPresenter.MyView {

    // @formatter:off
    public static ErrorView create(Environment environment, Resources resources) {
        return new Templated_ErrorView(environment, resources);
    }

    public abstract Environment environment();
    public abstract Resources resources();
    // @formatter:on

    @PostConstruct
    void init() {

    }
}
