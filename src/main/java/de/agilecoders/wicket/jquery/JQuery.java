package de.agilecoders.wicket.jquery;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Function;
import de.agilecoders.wicket.jquery.util.Generics2;
import de.agilecoders.wicket.jquery.util.Json;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;

import java.util.List;

import static de.agilecoders.wicket.jquery.util.Strings2.escapeMarkupId;
import static de.agilecoders.wicket.jquery.util.Strings2.nullToEmpty;

/**
 * The Jquery class helps to keep all javascript jquery scripts type safe.
 *
 * @author miha
 */
public class JQuery implements IClusterable {

    /**
     * creates a quoted attribute
     *
     * @param value the selector
     * @return quoted attribute
     */
    public static Attr quoted(final CharSequence value) {
        if (!Strings.isEmpty(value)) {
            return new Attr.Quoted(value);
        } else {
            return Attr.nullValue();
        }
    }

    /**
     * creates a plain attribute without quotes
     *
     * @param value the selector
     * @return plain attribute
     */
    public static Attr plain(final CharSequence value) {
        if (!Strings.isEmpty(value)) {
            return new Attr.Plain(value);
        } else {
            return Attr.nullValue();
        }
    }

    /**
     * Function that maps an {@link IFunction} to its string representation.
     */
    private static final Function<IFunction, String> FUNCTION_TRANSFORMER = new Function<IFunction, String>() {
        @Override
        public String apply(final IFunction function) {
            return function != null ? function.build() : null;
        }
    };

    /**
     * helper method to allow a jquery like code style. The selector will be quoted.
     * <p/>
     * <pre>
     *     JQuery.$("selector") // = $('selector')
     * </pre>
     *
     * @param selector The jquery selector
     * @return new Jquery instance
     */
    public static JQuery $(final CharSequence selector) {
        return new JQuery(quoted(selector));
    }

    /**
     * helper method to allow a jquery like code style
     * <p/>
     * <pre>
     *     JQuery.$(plain("document")) // = $(document)
     * </pre>
     *
     * @param selector The jquery selector
     * @return new Jquery instance
     */
    public static JQuery $(final Attr selector) {
        return new JQuery(selector);
    }

    /**
     * helper method to allow a jquery like code style
     *
     * @param component The markup id of given component is used as jquery selector
     * @return new Jquery instance
     */
    public static JQuery $(final Component component) {
        return $(toIdSelector(component));
    }

    /**
     * creates an id selector attribute. The markup id will be escaped ({@link de.agilecoders.wicket.jquery.util.Strings2#escapeMarkupId(CharSequence)})
     *
     * @param markupId the markup id
     * @return id selector as Attr
     */
    public static Attr toIdSelector(CharSequence markupId) {
        return quoted("#" + escapeMarkupId(markupId));
    }

    /**
     * creates an id selector attribute. The markup id will be escaped ({@link de.agilecoders.wicket.jquery.util.Strings2#escapeMarkupId(CharSequence)})
     *
     * @param component the component to extract markup id
     * @return id selector as Attr
     */
    public static Attr toIdSelector(Component component) {
        return toIdSelector(Args.notNull(component, "component").getMarkupId(true));
    }

    /**
     * helper method to allow a jquery like code style
     *
     * @param component          The markup id of given component is used as jquery selector
     * @param additionalSelector an additional initial selector
     * @return new Jquery instance
     */
    public static JQuery $(final Component component, final CharSequence... additionalSelector) {
        final List<Attr> selectors = Generics2.newArrayList(toIdSelector(component));

        if (additionalSelector != null) {
            for (CharSequence selector : additionalSelector) {
                selectors.add(quoted(selector));
            }
        }

        return $(Generics2.join(selectors, ' '));
    }

    /**
     * helper method to allow a jquery like code style
     *
     * @param component          The markup id of given component is used as jquery selector
     * @param additionalSelector an additional initial selector
     * @return new Jquery instance
     */
    public static JQuery $(final Component component, final Attr... additionalSelector) {
        final List<Attr> selector = Generics2.newArrayList(toIdSelector(component));

        if (additionalSelector != null) {
            selector.addAll(Generics2.newArrayList(additionalSelector));
        }

        return $(Generics2.join(selector, ' '));
    }

    private final Attr selector;
    private final List<IFunction> functions;

    /**
     * Construct.
     *
     * @param selector the selector to use.
     */
    private JQuery(final Attr selector) {
        this.selector = selector;
        this.functions = Generics2.newArrayList();
    }

    /**
     * adds a chained function to this jquery instance
     *
     * @param function the function to add
     * @return this instance for chaining
     */
    public JQuery chain(final IFunction function) {
        functions.add(function);
        return this;
    }

    public JQuery on(CharSequence events, JavaScriptInlineFunction handler) {
        return chain(OnJqueryFunction.on(events, handler));
    }


    public JQuery on(CharSequence events, CharSequence selector, JavaScriptInlineFunction handler) {
        return on(quoted(events), quoted(selector), handler);
    }

    /**
     * Attach an event handler function for one or more events to the selected elements.
     *
     * @param events   the events to listen to
     * @param selector the
     * @param handler
     * @return
     */
    public JQuery on(Attr events, Attr selector, JavaScriptInlineFunction handler) {
        return chain(OnJqueryFunction.on(events, selector, handler));
    }

    public JQuery on(Attr events, JavaScriptInlineFunction handler) {
        return on(events, Attr.nullValue(), handler);
    }

    /**
     * For each element in the set, get the first element that matches the selector by testing the element itself and
     * traversing up through its ancestors in the DOM tree.
     *
     * @param selector the selector to use (will be quoted).
     * @return this instance for chaining
     */
    public JQuery closest(CharSequence selector) {
        return closest(quoted(selector));
    }

    /**
     * For each element in the set, get the first element that matches the selector by testing the element itself and
     * traversing up through its ancestors in the DOM tree.
     *
     * @param selector the selector to use.
     * @return this instance for chaining
     */
    public JQuery closest(Attr selector) {
        return chain(ClosestJqueryFunction.closest(selector));
    }

    /**
     * adds a chained function to this jquery instance
     *
     * @param functionName the function to add
     * @return this instance for chaining
     */
    public JQuery chain(final CharSequence functionName) {
        functions.add(new SimpleFunction(functionName));
        return this;
    }

    /**
     * @return this jquery chain as string and a semicolon as last char
     */
    public String get() {
        return build() + ";";
    }

    /**
     * @return this jquery chain as string but doesn't close chain with semicolon.
     */
    public String build() {
        return "$(" + selector + ")" + createFunctionString();
    }

    /**
     * @return this jquery script as {@link OnDomReadyHeaderItem} instance
     */
    public OnDomReadyHeaderItem asDomReadyScript() {
        return OnDomReadyHeaderItem.forScript(get());
    }

    /**
     * @return all functions as joined string.
     */
    private String createFunctionString() {
        return functions.isEmpty() ? "" : "." + Generics2.join(Generics2.transform(functions, FUNCTION_TRANSFORMER), '.');
    }

    /**
     * adds a chained function to this jquery instance
     *
     * @param functionName the function to add
     * @param config       the function configuration
     * @return this instance for chaining
     */
    public JQuery chain(final CharSequence functionName, final AbstractConfig config) {
        functions.add(new ConfigurableFunction(functionName, config));
        return this;
    }

    /**
     * simple class to represent a javascript function.
     */
    public static class JavaScriptInlineFunction extends AbstractFunction {
        private final String functionBody;

        /**
         * Construct.
         *
         * @param functionBody the function body as string
         */
        public JavaScriptInlineFunction(final String functionBody) {
            super("function");

            this.functionBody = nullToEmpty(functionBody);
        }

        @Override
        public String build() {
            return super.build() + "{" + functionBody + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof JavaScriptInlineFunction) {
                return functionBody.equals(((JavaScriptInlineFunction) o).functionBody);
            } else if (o instanceof String) {
                return functionBody.equals(o);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return functionBody.hashCode();
        }

        @Override
        public String toString() {
            return functionBody;
        }

        /**
         * @param value The value to stringify
         * @return string representation of given value
         */
        public static String toString(final JavaScriptInlineFunction value) {
            return value != null ? value.toString() : "null";
        }
    }

    /**
     * a simple function without params and body
     */
    public static final class SimpleFunction extends AbstractFunction {
        /**
         * Construct.
         *
         * @param functionName The function name of this {@link IFunction} implementation
         */
        protected SimpleFunction(final CharSequence functionName) {
            super(functionName);
        }
    }

    /**
     * a configurable function with one parameter (an {@link AbstractConfig}) and without body
     */
    public static final class ConfigurableFunction extends AbstractFunction {

        /**
         * Construct.
         *
         * @param functionName The function name of this {@link IFunction} implementation
         * @param config       the function configuration
         */
        protected ConfigurableFunction(final CharSequence functionName, final AbstractConfig config) {
            super(functionName);

            if (!config.isEmpty()) {
                addParameter(config.toJsonString());
            }
        }
    }

    /**
     * java abstraction of jquery each function
     */
    public static final class EachJqueryFunction extends AbstractFunction {

        /**
         * creates a new {@link EachJqueryFunction} instance that holds a given inline function.
         *
         * @param function The inline function to execute for each element
         * @return new {@link EachJqueryFunction} instance
         */
        public static EachJqueryFunction each(final JavaScriptInlineFunction function) {
            return new EachJqueryFunction(function);
        }

        /**
         * Construct.
         */
        protected EachJqueryFunction(final JavaScriptInlineFunction function) {
            super("each");

            addParameter(toParameterValue(function));
        }
    }

    /**
     * java abstraction of jquery closest function
     */
    public static final class ClosestJqueryFunction extends AbstractFunction {

        /**
         * creates a new {@link ClosestJqueryFunction} instance
         *
         * @param selector The CSS selector to use the closest parent
         * @return new {@link ClosestJqueryFunction} instance
         */
        public static ClosestJqueryFunction closest(final Attr selector) {
            return new ClosestJqueryFunction(selector);
        }

        /**
         * creates a new {@link ClosestJqueryFunction} instance
         *
         * @param selector The CSS selector to use the closest parent
         * @return new {@link ClosestJqueryFunction} instance
         */
        public static ClosestJqueryFunction closest(final CharSequence selector) {
            return closest(quoted(selector));
        }

        /**
         * Construct.
         */
        protected ClosestJqueryFunction(final Attr selector) {
            super("closest");

            addParameter(selector);
        }
    }

    /**
     * java abstraction of JQuery <em>on</em> function. Attach an event handler function for one or more events to the
     * selected elements.
     */
    public static final class OnJqueryFunction extends AbstractFunction {

        /**
         * creates a new {@link OnJqueryFunction} instance
         *
         * @param events The CSS selector for event delegation
         * @return new {@link OnJqueryFunction} instance
         */
        public static OnJqueryFunction on(final Attr events, JavaScriptInlineFunction handler) {
            return new OnJqueryFunction(events, Attr.nullValue(), null, handler);
        }

        /**
         * creates a new {@link OnJqueryFunction} instance
         *
         * @param events The CSS selector for event delegation
         * @return new {@link OnJqueryFunction} instance
         */
        public static OnJqueryFunction on(final CharSequence events, JavaScriptInlineFunction handler) {
            return new OnJqueryFunction(quoted(events), Attr.nullValue(), null, handler);
        }

        /**
         * creates a new {@link OnJqueryFunction} instance
         *
         * @param selector The CSS selector for event delegation
         * @return new {@link OnJqueryFunction} instance
         */
        public static OnJqueryFunction on(final Attr events, final Attr selector, JavaScriptInlineFunction handler) {
            return new OnJqueryFunction(events, selector, null, handler);
        }

        /**
         * creates a new {@link OnJqueryFunction} instance
         *
         * @param selector The CSS selector for event delegation
         * @return new {@link OnJqueryFunction} instance
         */
        public static OnJqueryFunction on(final CharSequence events, final CharSequence selector, JavaScriptInlineFunction handler) {
            return new OnJqueryFunction(quoted(events), quoted(selector), null, handler);
        }

        /**
         * Construct.
         *
         * @param events   One or more space-separated event types and optional namespaces, such as "click" or "keydown.myPlugin".
         * @param selector A selector string to filter the descendants of the selected elements that trigger the event.
         *                 If the selector is null or omitted, the event is always triggered when it reaches the selected element.
         * @param data     Data to be passed to the handler in event.data when an event is triggered.
         * @param handler  A function to execute when the event is triggered. The value false is also allowed as a shorthand for
         *                 a function that simply does return false.
         */
        // TODO Add support for 'data' parameter
        protected OnJqueryFunction(final Attr events, final Attr selector, final CharSequence data, final JavaScriptInlineFunction handler) {
            super("on");

            addParameter(events);

            if (!Strings.isEmpty(selector) && !(selector instanceof Attr.NullValue)) {
                addParameter(selector);
            }

            handler.addParameter("evt");

            if (data != null && !(data instanceof Attr.NullValue)) {
                handler.addParameter(data);
            }

            addParameter(toParameterValue(handler));
        }
    }

    /**
     * A simple implementation of {@link IFunction} that allows you to chain
     * function parameters in a javascript safe way.
     */
    public static abstract class AbstractFunction implements IFunction {
        private final CharSequence functionName;
        private final List<CharSequence> parameters;

        /**
         * Construct.
         *
         * @param functionName The function name of this {@link IFunction} implementation
         */
        protected AbstractFunction(final CharSequence functionName) {
            this.functionName = functionName;
            this.parameters = Generics2.newArrayList();
        }

        @Override
        public String build() {
            return functionName + "(" + buildParameters() + ")";
        }

        /**
         * @return the separator
         */
        protected final char getSeparator() {
            return ',';
        }

        /**
         * @return a joined list of parameters as string
         */
        protected String buildParameters() {
            return Generics2.join(parameters, getSeparator());
        }

        /**
         * adds a new parameter to parameter list
         *
         * @param parameter The parameter to add
         */
        protected final void addParameter(final CharSequence parameter) {
            parameters.add(parameter);
        }

        /**
         * transform given value to a javascript parameter value
         *
         * @param value The value to transform
         * @return value as string
         */
        protected final CharSequence toParameterValue(final Object value) {
            if (value instanceof Long) {
                return toParameterValue((Long) value);
            } else if (value instanceof Integer) {
                return toParameterValue((Integer) value);
            } else if (value instanceof Boolean) {
                return toParameterValue((Boolean) value);
            } else if (value instanceof Float) {
                return toParameterValue((Float) value);
            } else if (value instanceof JavaScriptInlineFunction) {
                return toParameterValue((JavaScriptInlineFunction) value);
            } else if (value instanceof Duration) {
                return String.valueOf(((Duration) value).getMilliseconds());
            } else if (value instanceof Attr) {
                return value.toString();
            } else if (value instanceof JsonNode) {
                return Json.stringify(value);
            }

            return value != null ? "'" + String.valueOf(value) + "'" : Attr.nullValue();
        }

        /**
         * transform given value to a javascript parameter value
         *
         * @param value The value to transform
         * @return value as string
         */
        protected final CharSequence toParameterValue(final JavaScriptInlineFunction value) {
            return value != null ? value.build() : Attr.nullValue();
        }

        /**
         * transform given value to a javascript parameter value
         *
         * @param value The value to transform
         * @return value as string
         */
        protected final CharSequence toParameterValue(final Long value) {
            return value != null ? Long.toString(value) : Attr.nullValue();
        }

        /**
         * transform given value to a javascript parameter value
         *
         * @param value The value to transform
         * @return value as string
         */
        protected final CharSequence toParameterValue(final Integer value) {
            return value != null ? Integer.toString(value) : Attr.nullValue();
        }

        /**
         * transform given value to a javascript parameter value
         *
         * @param value The value to transform
         * @return value as string
         */
        protected final CharSequence toParameterValue(final Float value) {
            return value != null ? Float.toString(value) : Attr.nullValue();
        }

        /**
         * transform given value to a javascript parameter value
         *
         * @param value The value to transform
         * @return value as string
         */
        protected final CharSequence toParameterValue(final Boolean value) {
            return value != null ? Boolean.toString(value) : Attr.nullValue();
        }
    }
}