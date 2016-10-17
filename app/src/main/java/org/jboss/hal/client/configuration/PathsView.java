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
package org.jboss.hal.client.configuration;

import java.util.List;

import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.ballroom.table.DataTable;
import org.jboss.hal.core.mbui.MbuiContext;
import org.jboss.hal.core.mbui.MbuiViewImpl;
import org.jboss.hal.dmr.model.NamedNode;
import org.jboss.hal.spi.MbuiElement;
import org.jboss.hal.spi.MbuiView;

import static org.jboss.hal.ballroom.table.Api.RefreshMode.RESET;

/**
 * TODO Add support for static suggestion handler for form field 'relative-to'
 * TODO Add support for adding a validation handler fdr form-item 'name'
 *
 * @author Harald Pehl
 */
@MbuiView
public abstract class PathsView extends MbuiViewImpl<PathsPresenter> implements PathsPresenter.MyView {

    public static PathsView create(final MbuiContext mbuiContext) {
        return new Mbui_PathsView(mbuiContext);
    }

    @MbuiElement("path-table") DataTable<NamedNode> table;
    @MbuiElement("path-form") Form<NamedNode> form;

    PathsView(final MbuiContext mbuiContext) {
        super(mbuiContext);
    }

    @Override
    public void update(final List<NamedNode> paths) {
        table.api().clear().add(paths).refresh(RESET);
        form.clear(); // TODO restore selection, set selection to newly created item or clear selection after remove
    }
}
