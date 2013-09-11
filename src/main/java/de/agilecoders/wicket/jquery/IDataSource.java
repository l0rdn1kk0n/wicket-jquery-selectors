package de.agilecoders.wicket.jquery;

import org.apache.wicket.util.io.IClusterable;

import java.util.List;

/**
 * Simple data source for e.g. {@link Typeahead}
 *
 * @author miha
 */
public interface IDataSource<T> extends IClusterable {

    /**
     * @return a list of data of type T
     */
    List<T> load();

}
