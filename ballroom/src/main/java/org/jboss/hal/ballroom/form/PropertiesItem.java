/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.hal.ballroom.form;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import elemental.client.Browser;
import elemental.dom.Element;
import elemental.html.TextAreaElement;
import org.jboss.hal.ballroom.form.PropertiesBridge.Bridge;
import org.jboss.hal.ballroom.js.JsHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.jboss.hal.resources.CSS.properties;

/**
 * @author Harald Pehl
 */
public class PropertiesItem extends AbstractFormItem<Map<String, String>> {

    private PropertiesElement propertiesElement;

    public PropertiesItem(final String name, final String label) {
        super(name, label);
    }

    @Override
    protected InputElement<Map<String, String>> newInputElement() {
        propertiesElement = new PropertiesElement();
        propertiesElement.setClassName(properties);
        Bridge.element(propertiesElement.asElement()).onChange((field, editor, tags) -> {
            Map<String, String> value = asProperties(JsHelper.asList(tags));
            setModified(true);
            setUndefined(value.isEmpty());
            signalChange(value);
        });
        return propertiesElement;
    }

    @Override
    public boolean supportsExpressions() {
        return false;
    }

    public void setProperties(final Map<String, String> properties) {
        propertiesElement.setValue(properties);
    }


    static class PropertiesElement extends InputElement<Map<String, String>> {

        final TextAreaElement element;

        PropertiesElement() {
            element = Browser.getDocument().createTextAreaElement();
        }

        @Override
        public Map<String, String> getValue() {
            return asProperties(Bridge.element(asElement()).getTags());
        }

        @Override
        public void setValue(final Map<String, String> value) {
            Bridge.element(asElement()).setTags(asTags(value));
        }

        @Override
        public void clearValue() {
            Bridge.element(asElement()).removeAll();
        }

        @Override
        public int getTabIndex() {
            return element.getTabIndex();
        }

        @Override
        public void setAccessKey(final char c) {
            element.setAccessKey(String.valueOf(c));
        }

        @Override
        public void setFocus(final boolean b) {
            if (b) {
                element.focus();
            } else {
                element.blur();
            }
        }

        @Override
        public void setTabIndex(final int i) {
            element.setTabIndex(i);
        }

        @Override
        public boolean isEnabled() {
            return !element.isDisabled();
        }

        @Override
        public void setEnabled(final boolean b) {
            element.setDisabled(!b);
        }

        @Override
        public void setName(final String s) {
            element.setName(s);
        }

        @Override
        public String getName() {
            return element.getName();
        }

        @Override
        public String getText() {
            return Joiner.on(',').join(asTags(getValue()));
        }

        @Override
        public void setText(final String s) {
            // not supported
        }

        @Override
        public Element asElement() {
            return element;
        }
    }

    private static Map<String, String> asProperties(final List<String> tags) {
        if (tags.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> properties = new HashMap<>();
        Splitter splitter = Splitter.on('=').omitEmptyStrings().trimResults().limit(2);
        for (String tag : tags) {
            Iterable<String> split = splitter.split(tag);
            if (Iterables.size(split) == 2) {
                Iterator<String> iterator = split.iterator();
                properties.put(split.iterator().next(), iterator.next());
            }
        }
        return properties;
    }

    private static List<String> asTags(final Map<String, String> properties) {
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> tags = new ArrayList<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            tags.add(entry.getKey() + "=" + entry.getValue());
        }
        return tags;
    }
}