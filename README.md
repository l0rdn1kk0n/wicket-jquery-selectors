wicket-jquery-selectors
=======================

Utility library for working with JQuery and Apache Wicket.

## Usage

The following example uses the [bootstrap typeahead](http://getbootstrap.com/2.3.2/javascript.html#typeahead) jquery plugin:

### Config classes

Create a subclass of `AbstractConfig` to build the options object for a JQuery plugin. You can omit
this step if the JQuery plugin doesn't need any options:

```java

    import de.agilecoders.wicket.jquery.AbstractConfig;
    import de.agilecoders.wicket.jquery.IDataSource;

    public class TypeaheadConfig extends AbstractConfig {

        private static final IKey<IDataSource> Source = newKey("source", null);
        private static final IKey<Integer> MinLength = newKey("minLength", 1);

        public TypeaheadConfig withDataSource(final IDataSource<?> value) {
            put(Source, value);
            return this;
        }

        public TypeaheadConfig withMinLength(final int value) {
            put(MinLength, value);
            return this;
        }
    }

```

### JQuery method chaining

```java

    import static de.agilecoders.wicket.jquery.JQuery.$;

    public class Typeahead<T> extends TextField<T> {

        @Override
        public void renderHead(final IHeaderResponse response) {
            super.renderHead(response);

            // setup the options
            TypeaheadConfig config = new TypeaheadConfig();
            config.withDataSource(dataSource);
            config.withMinLength(3);

            // render the header contribution
            response.render($(this).chain("typeahead", config).asDomReadyScript());
        }
    }

```

The above code will generate the following javascript:


```javascript

    $('#wicketComponentId').typeahead({"dataSource" : "<wicket-ajax-callback>", "minLength": 3});

```