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

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.hal.core.mvp.ApplicationPresenter;
import org.jboss.hal.core.mvp.HasPresenter;
import org.jboss.hal.core.mvp.PatternFlyView;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.dmr.model.Operation;
import org.jboss.hal.dmr.model.ResourceAddress;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.StatementContext;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.spi.Requires;

import javax.inject.Inject;
import java.util.Map;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.READ_RESOURCE_OPERATION;

/**
 * @author Harald Pehl
 */
@SuppressWarnings("SpellCheckingInspection")
public class InterfacePresenter extends
        ApplicationPresenter<InterfacePresenter.MyView, InterfacePresenter.MyProxy> {

    // @formatter:off
    @ProxyCodeSplit
    @Requires(ROOT_ADDRESS)
    @NameToken(NameTokens.INTERFACE)
    public interface MyProxy extends ProxyPlace<InterfacePresenter> {}

    public interface MyView extends PatternFlyView, HasPresenter<InterfacePresenter> {
        void update(ModelNode interfce);
    }
    // @formatter:on


    static final String ROOT_ADDRESS = "/interface=*";
    static final AddressTemplate ROOT_TEMPLATE = AddressTemplate.of(ROOT_ADDRESS);

    private final Dispatcher dispatcher;
    private final StatementContext statementContext;
    private String interfce;

    @Inject
    public InterfacePresenter(final EventBus eventBus,
            final MyView view,
            final MyProxy proxy,
            final Dispatcher dispatcher,
            final StatementContext statementContext) {
        super(eventBus, view, proxy);
        this.dispatcher = dispatcher;
        this.statementContext = statementContext;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(final PlaceRequest request) {
        interfce = request.getParameter(NAME, null);
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadInterface();
    }

    private void loadInterface() {
        ResourceAddress address = ROOT_TEMPLATE.resolve(statementContext, interfce);
        Operation operation = new Operation.Builder(READ_RESOURCE_OPERATION, address).build();
        dispatcher.execute(operation, result -> getView().update(result));
    }

    public void saveInterface(final Map<String, Object> changedValues) {
        loadInterface();
    }
}