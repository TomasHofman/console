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
package org.jboss.hal.core.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import elemental.client.Browser;
import elemental.dom.Element;
import elemental.html.HTMLCollection;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.gwt.elemento.core.IsElement;
import org.jboss.gwt.flow.Async;
import org.jboss.gwt.flow.Control;
import org.jboss.gwt.flow.Function;
import org.jboss.gwt.flow.FunctionContext;
import org.jboss.gwt.flow.Outcome;
import org.jboss.gwt.flow.Progress;
import org.jboss.hal.ballroom.Attachable;
import org.jboss.hal.core.finder.ColumnRegistry.LookupCallback;
import org.jboss.hal.core.finder.FinderColumn.RefreshMode;
import org.jboss.hal.core.ui.Skeleton;
import org.jboss.hal.meta.security.SecurityContext;
import org.jboss.hal.meta.security.SecurityContextAware;
import org.jboss.hal.resources.IdBuilder;
import org.jboss.hal.spi.Footer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static elemental.css.CSSStyleDeclaration.Unit.PX;
import static java.lang.Math.min;
import static org.jboss.hal.resources.CSS.column;
import static org.jboss.hal.resources.CSS.finder;
import static org.jboss.hal.resources.CSS.finderPreview;
import static org.jboss.hal.resources.CSS.row;
import static org.jboss.hal.resources.Ids.FINDER;

/**
 * The one and only finder which is shared across all different top level categories in HAL. The same finder instance
 * gets injected into the different top level presenters. Only the columns will change when navigating between the
 * different places
 *
 * @author Harald Pehl
 */
public class Finder implements IsElement, SecurityContextAware, Attachable {

    /**
     * Function used in {@link #select(String, FinderPath, Runnable)} to select one segment in a finder path.
     */
    private class SelectFunction implements Function<FunctionContext> {

        private final FinderSegment segment;
        private final Element columnElement;

        private SelectFunction(final FinderSegment segment, final Element columnElement) {
            this.segment = segment;
            this.columnElement = columnElement;
        }

        @Override
        public void execute(final Control<FunctionContext> control) {
            if (columnElement != null &&
                    columns.containsKey(columnElement.getId()) &&
                    segment.getKey().equals(columnElement.getId())) {
                // column is already in place just select the item
                FinderColumn finderColumn = columns.get(columnElement.getId());
                selectItem(finderColumn, control);

            } else {
                // append the column
                appendColumn(segment.getKey(), new AsyncCallback<FinderColumn>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        control.abort();
                    }

                    @Override
                    public void onSuccess(final FinderColumn finderColumn) {
                        selectItem(finderColumn, control);
                    }
                });
            }
        }

        private void selectItem(FinderColumn finderColumn, Control<FunctionContext> control) {
            if (finderColumn.contains(segment.getValue())) {
                finderColumn.markSelected(segment.getValue());
                updateContext();
                control.getContext().push(finderColumn);
                control.proceed();
            } else {
                logger.error("Unable to select item '{}'", segment.getValue()); //NON-NLS
                control.abort();
            }
        }
    }


    static final String DATA_BREADCRUMB = "breadcrumb";
    /**
     * The maximum number of visible columns. If there are more columns, the first column is hidden when column
     * {@code MAX_VISIBLE_COLUMNS + 1} is shown.
     */
    private static final int MAX_VISIBLE_COLUMNS = 4;

    private static final int MAX_COLUMNS = 12;
    private static final String PREVIEW_COLUMN = "previewColumn";
    private static final Logger logger = LoggerFactory.getLogger(Finder.class);

    private final EventBus eventBus;
    private final PlaceManager placeManager;
    private final ColumnRegistry columnRegistry;
    private final Provider<Progress> progress;
    private final String id;
    private final FinderContext context;
    private final LinkedHashMap<String, FinderColumn> columns;
    private final Map<String, String> initialColumnsByToken;
    private final Map<String, PreviewContent> initialPreviewsByToken;
    private final Element root;
    private final Element previewColumn;


    // ------------------------------------------------------ ui

    @Inject
    public Finder(final EventBus eventBus,
            final PlaceManager placeManager,
            final ColumnRegistry columnRegistry,
            @Footer final Provider<Progress> progress) {
        this.eventBus = eventBus;
        this.placeManager = placeManager;
        this.columnRegistry = columnRegistry;
        this.progress = progress;

        this.id = FINDER;
        this.context = new FinderContext();
        this.columns = new LinkedHashMap<>();
        this.initialColumnsByToken = new HashMap<>();
        this.initialPreviewsByToken = new HashMap<>();

        // @formatter:off
        Elements.Builder builder = new Elements.Builder()
            .div().id(this.id).css(row, finder)
                .div()
                    .id(IdBuilder.build(id, "preview"))
                    .css(finderPreview, column(12))
                    .rememberAs(PREVIEW_COLUMN)
                .end()
            .end();
        // @formatter:on

        root = builder.build();
        previewColumn = builder.referenceFor(PREVIEW_COLUMN);
    }

    @Override
    public Element asElement() {
        return root;
    }

    @Override
    public void attach() {
        Browser.getWindow().setOnresize(event -> adjustHeight());
        adjustHeight();
    }

    private void adjustHeight() {
        int window = Browser.getWindow().getInnerHeight();
        int navigation = Skeleton.navigationHeight();
        int footer = Skeleton.footerHeight();
        if (navigation > 0 && footer > 0) {
            int finder = window - navigation - footer;
            root.getStyle().setHeight(finder, PX);
        }
    }

    private FinderColumn initialColumn() {
        String columnId = initialColumnsByToken.get(context.getToken());
        if (columnId != null) {
            return columns.get(columnId);
        }
        return null;
    }

    private void resizePreview() {
        int columns = root.getChildren().length() - 1;
        int previewSize = MAX_COLUMNS - 2 * min(columns, MAX_VISIBLE_COLUMNS);
        previewColumn.setClassName(finderPreview + " " + column(previewSize));
    }


    // ------------------------------------------------------ internal API

    void appendColumn(String columnId, AsyncCallback<FinderColumn> callback) {
        columnRegistry.lookup(columnId, new LookupCallback() {
            @Override
            public void found(final FinderColumn column) {
                appendColumn(column, callback);
            }

            @Override
            public void error(final String failure) {
                logger.error(failure);
                if (callback != null) {
                    callback.onFailure(new RuntimeException(failure));
                }
            }
        });
    }

    private void appendColumn(FinderColumn<?> column, AsyncCallback<FinderColumn> callback) {
        column.resetSelection();
        columns.put(column.getId(), column);
        showHideColumns();

        root.insertBefore(column.asElement(), previewColumn);
        column.setItems(callback);
        resizePreview();
    }

    private void reduceAll() {
        for (Iterator<Element> iterator = Elements.children(root).iterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            if (element == previewColumn) {
                break;
            }
            columns.remove(element.getId());
            iterator.remove();
        }
    }

    void reduceTo(FinderColumn<?> column) {
        boolean removeFromHere = false;
        for (Iterator<Element> iterator = Elements.children(root).iterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            if (element == column.asElement()) {
                removeFromHere = true;
                continue;
            }
            if (element == previewColumn) {
                break;
            }
            if (removeFromHere) {
                columns.remove(element.getId());
                iterator.remove();
            }
        }
        showHideColumns();
        resizePreview();
    }

    private void showHideColumns() {
        int index = 0;
        int hideUntilHere = columns.size() - MAX_VISIBLE_COLUMNS;
        for (FinderColumn column : columns.values()) {
            Elements.setVisible(column.asElement(), index >= hideUntilHere);
            column.markHiddenColumns(false);
            index++;
        }
        if (hideUntilHere > 0) {
            for (FinderColumn column : columns.values()) {
                if (Elements.isVisible(column.asElement())) {
                    column.markHiddenColumns(true);
                    break;
                }
            }
        }
    }

    void updateContext() {
        context.getPath().clear();

        for (Element columnElement : Elements.children(root)) {
            if (columnElement == previewColumn) {
                break;
            }
            String key = columnElement.getId();
            FinderColumn column = columns.get(key);
            context.getPath().append(column);
        }
        eventBus.fireEvent(new FinderContextEvent(context));
    }

    void updateHistory() {
        // only finder tokens of the same type please
        PlaceRequest current = placeManager.getCurrentPlaceRequest();
        if (context.getToken().equals(current.getNameToken())) {
            PlaceRequest update = context.toPlaceRequest();
            if (!update.equals(current)) {
                logger.debug("Update history: {}", "#" + context.getToken() +
                        (context.getPath().isEmpty() ? "" : ";path=" + context.getPath()));
                placeManager.updateHistory(update, true);
            }
        }
    }

    void selectColumn(String columnId) {
        FinderColumn finderColumn = columns.get(columnId);
        finderColumn.asElement().focus();
    }

    void selectPreviousColumn(final String columnId) {
        List<String> columnIds = new ArrayList<>(columns.keySet());
        int index = 0;
        for (String id : columnIds) {
            if (id.equals(columnId)) {
                break;
            }
            index++;
        }
        if (index > 0 && index < columnIds.size()) {
            String previousId = columnIds.get(index - 1);
            selectColumn(previousId);
            FinderColumn previousColumn = columns.get(previousId);
            if (previousColumn != null) {
                FinderRow selectedRow = previousColumn.selectedRow();
                if (selectedRow != null) {
                    selectedRow.click();
                }
            }
        }
    }

    int columns() {
        return columns.size();
    }

    void showPreview(PreviewContent preview) {
        Elements.removeChildrenFrom(previewColumn);
        if (preview != null) {
            for (Element element : preview.asElements()) {
                previewColumn.appendChild(element);
            }
            preview.attach();
            preview.onReset();
        }
    }

    private void clearPreview() {
        Elements.removeChildrenFrom(previewColumn);
    }

    void showInitialPreview() {
        PreviewContent previewContent = initialPreviewsByToken.get(context.getToken());
        if (previewContent != null) {
            showPreview(previewContent);
        }
    }


    // ------------------------------------------------------ public interface

    /**
     * Resets the finder to its initial state by showing the initial column and preview.
     */
    public void reset(final String token, final String initialColumn, final PreviewContent initialPreview,
            AsyncCallback<FinderColumn> callback) {
        initialColumnsByToken.put(token, initialColumn);
        initialPreviewsByToken.put(token, initialPreview);

        columns.clear();
        while (root.getFirstChild() != previewColumn) {
            root.removeChild(root.getFirstChild());
        }
        context.reset(token);
        appendColumn(initialColumn, callback);
        selectColumn(initialColumn);
        for (FinderColumn column : columns.values()) {
            Elements.setVisible(column.asElement(), true);
            column.markHiddenColumns(false);
        }
        showPreview(initialPreview);
        updateHistory();
    }

    /**
     * Selects the columns as specified in the finder path. Please note that this might be a complex and long running
     * operation since each segment in the finder path is turned into a function. The function will load and initialize
     * the column and select the item as specified in the segment.
     * <p>
     * If the path is empty, the fallback operation is executed.
     */
    public void select(final String token, final FinderPath path, final Runnable fallback) {
        if (path.isEmpty()) {
            fallback.run();

        } else {
            if (!token.equals(context.getToken())) {
                context.reset(token);
                reduceAll();

            } else {
                // clear the preview right away, otherwise the previous (wrong) preview would be visible until all
                // select functions have been finished
                clearPreview();

                // Find the last common column between the new and the current path
                String match = null;
                FinderPath newPath = path.reversed();
                FinderPath currentPath = context.getPath().reversed();
                for (FinderSegment newSegment : newPath) {
                    for (FinderSegment currentSegment : currentPath) {
                        if (newSegment.getKey().equals(currentSegment.getKey())) {
                            match = newSegment.getKey();
                            break;
                        }
                    }
                    if (match != null) {
                        break;
                    }
                }
                FinderColumn lastCommonColumn = match != null ? columns.get(match) : initialColumn();
                if (lastCommonColumn != null) {
                    reduceTo(lastCommonColumn);
                }
            }

            int index = 0;
            Function[] functions = new Function[path.size()];
            HTMLCollection columns = root.getChildren();
            for (FinderSegment segment : path) {
                Element column = index < columns.getLength() ? (Element) columns.item(index) : null;
                functions[index] = new SelectFunction(new FinderSegment(segment.getKey(), segment.getValue()),
                        column); // work with a copy of segment!
                index++;
            }
            new Async<FunctionContext>(progress.get()).waterfall(new FunctionContext(), new Outcome<FunctionContext>() {
                @Override
                public void onFailure(final FunctionContext context) {
                    if (Finder.this.context.getPath().isEmpty()) {
                        fallback.run();

                    } else if (!context.emptyStack()) {
                        FinderColumn column = context.pop();
                        processLastColumnSelection(column);
                    }
                }

                @Override
                public void onSuccess(final FunctionContext context) {
                    FinderColumn column = context.pop();
                    processLastColumnSelection(column);
                }

                private void processLastColumnSelection(FinderColumn column) {
                    selectColumn(column.getId());
                    column.refresh(RefreshMode.RESTORE_SELECTION);
                }
            }, functions);
        }
    }

    @Override
    public void onSecurityContextChange(final SecurityContext securityContext) {
        for (FinderColumn column : columns.values()) {
            column.onSecurityContextChange(securityContext);
        }
    }

    public String getId() {
        return id;
    }

    public FinderColumn getColumn(String columnId) {
        return columns.get(columnId);
    }

    public FinderContext getContext() {
        return context;
    }
}