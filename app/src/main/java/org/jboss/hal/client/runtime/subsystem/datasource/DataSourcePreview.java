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
package org.jboss.hal.client.runtime.subsystem.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental2.dom.HTMLElement;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.hal.ballroom.Alert;
import org.jboss.hal.ballroom.EmptyState;
import org.jboss.hal.ballroom.chart.Utilization;
import org.jboss.hal.config.Environment;
import org.jboss.hal.core.datasource.DataSource;
import org.jboss.hal.core.finder.FinderPath;
import org.jboss.hal.core.finder.FinderPathFactory;
import org.jboss.hal.core.finder.PreviewContent;
import org.jboss.hal.core.mvp.Places;
import org.jboss.hal.core.runtime.server.Server;
import org.jboss.hal.core.runtime.server.ServerActions;
import org.jboss.hal.dmr.Composite;
import org.jboss.hal.dmr.CompositeResult;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.ModelNodeHelper;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.security.Constraint;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Icons;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;

import static elemental2.dom.DomGlobal.document;
import static org.jboss.gwt.elemento.core.Elements.a;
import static org.jboss.gwt.elemento.core.Elements.h;
import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.meta.StatementContext.Tuple.SELECTED_HOST;
import static org.jboss.hal.meta.StatementContext.Tuple.SELECTED_SERVER;
import static org.jboss.hal.resources.CSS.fontAwesome;
import static org.jboss.hal.resources.CSS.hidden;
import static org.jboss.hal.resources.CSS.underline;

/**
 * TODO The empty state action to enable statistics makes only sense in standalone mode or w/o RBAC enabled.
 * TODO Otherwise we'd need to have the metadata to resolve the constraint
 * TODO "writable(profile=[*]/subsystem=datasources/data-source=*@enabled)"
 */
class DataSourcePreview extends PreviewContent<DataSource> {

    private final Server server;
    private final DataSource dataSource;
    private final Environment environment;
    private final Dispatcher dispatcher;
    private final StatementContext statementContext;
    private final ResourceAddress dataSourceAddress;

    private final EmptyState fromDeployment;
    private final EmptyState noStatistics;
    private final Alert needsReloadWarning;
    private final Alert needsRestartWarning;
    private final Alert disabledWarning;
    private final HTMLElement refresh;
    private final HTMLElement poolHeader;
    private final Utilization activeConnections;
    private final Utilization maxUsedConnections;
    private final HTMLElement cacheHeader;
    private final Utilization hitCount;
    private final Utilization missCount;

    DataSourcePreview(DataSourceColumn column,
            Server server,
            DataSource dataSource,
            Environment environment,
            Dispatcher dispatcher,
            StatementContext statementContext,
            ServerActions serverActions,
            FinderPathFactory finderPathFactory,
            Places places,
            Resources resources) {

        super(dataSource.getName(), dataSource.isXa() ? Names.XA_DATASOURCE : Names.DATASOURCE);
        this.server = server;
        this.dataSource = dataSource;
        this.environment = environment;
        this.dispatcher = dispatcher;
        this.statementContext = statementContext;
        this.dataSourceAddress = column.dataSourceAddress(dataSource);

        if (dataSource.fromDeployment()) {
            FinderPath path = finderPathFactory.deployment(dataSource.getDeployment());
            PlaceRequest placeRequest = places.finderPlace(NameTokens.DEPLOYMENTS, path).build();
            Elements.removeChildrenFrom(getLeadElement());
            getLeadElement().appendChild(
                    document.createTextNode(dataSource.isXa() ? Names.XA_DATASOURCE : Names.DATASOURCE + " @ "));
            getLeadElement().appendChild(a(places.historyToken(placeRequest))
                    .textContent(dataSource.getPath())
                    .title(resources.messages().goTo(Names.DEPLOYMENTS))
                    .asElement());
        }

        fromDeployment = new EmptyState.Builder(Ids.DATA_SOURCE_RUNTIME_STATISTICS_NOT_AVAILABLE,
                resources.constants().statisticsNotAvailableHeader())
                .description(resources.messages().dataSourceStatisticsFromDeployment())
                .icon(fontAwesome("line-chart"))
                .build();
        noStatistics = new EmptyState.Builder(Ids.DATA_SOURCE_RUNTIME_STATISTICS_NOT_ENABLED,
                resources.constants().statisticsDisabledHeader())
                .description(resources.messages().dataSourceStatisticsDisabled(dataSource.getName()))
                .icon(fontAwesome("line-chart"))
                .primaryAction(resources.constants().enableStatistics(), () -> column.enableStatistics(dataSource),
                        Constraint.writable(column.dataSourceConfigurationTemplate(dataSource), ENABLED))
                .build();

        Constraint reloadConstraint = environment.isStandalone()
                ? Constraint.executable(AddressTemplate.of("/"), RELOAD)
                : Constraint.executable(AddressTemplate.of("/{selected.host}/server-config=*"), RELOAD);
        Constraint restartConstraint = environment.isStandalone()
                ? Constraint.executable(AddressTemplate.of("/"), RELOAD)
                : Constraint.executable(AddressTemplate.of("/{selected.host}/server-config=*"), RESTART);

        needsReloadWarning = new Alert(Icons.WARNING,
                new SafeHtmlBuilder()
                        .append(resources.messages().serverNeedsReload(server.getName()))
                        .appendEscaped(" ")
                        .append(resources.messages().staleStatistics())
                        .toSafeHtml(),
                resources.constants().reload(), event -> serverActions.reload(server),
                reloadConstraint);

        needsRestartWarning = new Alert(Icons.WARNING,
                new SafeHtmlBuilder()
                        .append(resources.messages().serverNeedsRestart(server.getName()))
                        .appendEscaped(" ")
                        .append(resources.messages().staleStatistics())
                        .toSafeHtml(),
                resources.constants().restart(), event -> serverActions.restart(server),
                restartConstraint);

        disabledWarning = new Alert(Icons.WARNING,
                resources.messages().dataSourceDisabledNoStatistics(dataSource.getName()),
                resources.constants().enable(), event -> column.enableDataSource(dataSource),
                Constraint.writable(column.dataSourceConfigurationTemplate(dataSource), STATISTICS_ENABLED));

        activeConnections = new Utilization(resources.constants().active(), resources.constants().connections(),
                environment.isStandalone(), true);
        maxUsedConnections = new Utilization(resources.constants().maxUsed(), resources.constants().connections(),
                environment.isStandalone(), true);
        hitCount = new Utilization(resources.constants().hitCount(), resources.constants().count(),
                environment.isStandalone(), false);
        missCount = new Utilization(resources.constants().missCount(), resources.constants().count(),
                environment.isStandalone(), false);

        getHeaderContainer().appendChild(refresh = refreshLink(() -> update(null)));
        previewBuilder()
                .add(fromDeployment)
                .add(noStatistics)
                .add(needsReloadWarning)
                .add(needsRestartWarning)
                .add(disabledWarning)
                .add(poolHeader = h(2).css(underline).textContent(resources.constants().connectionPool()).asElement())
                .add(activeConnections)
                .add(maxUsedConnections)
                .add(cacheHeader = h(2).css(underline)
                        .textContent(resources.constants().preparedStatementCache())
                        .asElement())
                .add(hitCount)
                .add(missCount);

        // to prevent flickering we initially hide everything
        Elements.setVisible(fromDeployment.asElement(), false);
        Elements.setVisible(noStatistics.asElement(), false);
        needsReloadWarning.asElement().classList.add(hidden);
        needsRestartWarning.asElement().classList.add(hidden);
        disabledWarning.asElement().classList.add(hidden);
    }

    @Override
    @SuppressWarnings("HardCodedStringLiteral")
    public void update(DataSource ds) {

        // if the data source is from a deployment we don't need to refresh
        if (ds != null && ds.fromDeployment()) {
            Elements.setVisible(fromDeployment.asElement(), ds.fromDeployment());
            Elements.setVisible(noStatistics.asElement(), false);
            Elements.setVisible(refresh, false);
            Elements.setVisible(poolHeader, false);
            Elements.setVisible(activeConnections.asElement(), false);
            Elements.setVisible(maxUsedConnections.asElement(), false);
            Elements.setVisible(cacheHeader, false);
            Elements.setVisible(hitCount.asElement(), false);
            Elements.setVisible(missCount.asElement(), false);

        } else {
            List<Operation> operations = new ArrayList<>();
            Elements.setVisible(fromDeployment.asElement(), false);
            if (environment.isStandalone()) {
                operations.add(new Operation.Builder(ResourceAddress.root(), READ_RESOURCE_OPERATION)
                        .param(INCLUDE_RUNTIME, true)
                        .param(ATTRIBUTES_ONLY, true)
                        .build());
            } else {
                ResourceAddress address = AddressTemplate.of(SELECTED_HOST, SELECTED_SERVER)
                        .resolve(statementContext);
                operations.add(new Operation.Builder(address, READ_RESOURCE_OPERATION)
                        .param(INCLUDE_RUNTIME, true)
                        .param(ATTRIBUTES_ONLY, true)
                        .build());
            }
            if (ds == null) {
                operations.add(new Operation.Builder(dataSourceAddress, READ_RESOURCE_OPERATION)
                        .param(INCLUDE_RUNTIME, true)
                        .param(RECURSIVE, true)
                        .build());
            }

            dispatcher.execute(new Composite(operations), (CompositeResult result) -> {
                server.addServerAttributes(result.step(0).get(RESULT));
                if (ds == null) {
                    dataSource.update(result.step(1).get(RESULT));
                }

                boolean statisticsEnabled = dataSource.isStatisticsEnabled();
                Elements.setVisible(noStatistics.asElement(), !statisticsEnabled);
                Elements.setVisible(refresh, statisticsEnabled);
                Elements.setVisible(poolHeader, statisticsEnabled);
                Elements.setVisible(activeConnections.asElement(), statisticsEnabled);
                Elements.setVisible(maxUsedConnections.asElement(), statisticsEnabled);
                Elements.setVisible(cacheHeader, statisticsEnabled);
                Elements.setVisible(hitCount.asElement(), statisticsEnabled);
                Elements.setVisible(missCount.asElement(), statisticsEnabled);

                // Do not simply hide the links, but add the hidden CSS class.
                // Important when constraints for the links are processed later.
                needsReloadWarning.asElement().classList.add(hidden);
                needsRestartWarning.asElement().classList.add(hidden);
                disabledWarning.asElement().classList.add(hidden);
                if (statisticsEnabled) {
                    if (!dataSource.isEnabled()) {
                        disabledWarning.asElement().classList.remove(hidden);
                    } else {
                        Elements.toggle(needsReloadWarning.asElement(), hidden, !server.needsReload());
                        Elements.toggle(needsRestartWarning.asElement(), hidden, !server.needsRestart());
                    }

                    // pool statistics
                    ModelNode pool = ModelNodeHelper.failSafeGet(dataSource, "statistics/pool");
                    if (pool.isDefined()) {
                        int available = pool.get("AvailableCount").asInt(0);
                        int active = pool.get("ActiveCount").asInt(0);
                        int maxUsed = pool.get("MaxUsedCount").asInt(0);
                        activeConnections.update(active, available);
                        maxUsedConnections.update(maxUsed, available);
                    } else {
                        activeConnections.update(0, 0);
                        maxUsedConnections.update(0, 0);
                    }

                    // jdbc statistics
                    ModelNode jdbc = ModelNodeHelper.failSafeGet(dataSource, "statistics/jdbc");
                    if (jdbc.isDefined()) {
                        long accessed = jdbc.get("PreparedStatementCacheAccessCount").asLong(0);
                        long hit = jdbc.get("PreparedStatementCacheHitCount").asLong(0);
                        long missed = jdbc.get("PreparedStatementCacheMissCount").asLong(0);
                        hitCount.update(hit, accessed);
                        missCount.update(missed, accessed);
                    } else {
                        hitCount.update(0, 0);
                        missCount.update(0, 0);
                    }
                }
            });
        }
    }
}
