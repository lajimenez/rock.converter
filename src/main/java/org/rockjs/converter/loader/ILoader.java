package org.rockjs.converter.loader;

import java.util.List;

import org.rockjs.ConverterException;
import org.rockjs.converter.ConverterConfig;
import org.rockjs.model.Model;

/**
 * A loader is the responsible to load from a 3d format to a rock model
 * 
 * @author Luis Alberto Jiménez
 */
public interface ILoader {
    
    /**
     * Load a list of models from a 3d format
     * 
     * @param config
     *      configuration used to loaded the model
     *      
     * @return the model
     * 
     * @throws ConverterException
     */
    List<Model> loadModels(ConverterConfig config) throws ConverterException;
}
