package com.example.relayservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class PropertyService {

    @Autowired
    private ConfigurableEnvironment environment;

    /**
     * Updates a property in the environment.
     *
     * @param key The property key to update.
     * @param value The new value for the property.
     */
    public void updateProperty(String key, String value) {
        MutablePropertySources propertySources = environment.getPropertySources();
        Properties props = new Properties();
        props.put(key, value);
        PropertiesPropertySource propertySource = new PropertiesPropertySource("dynamicProps", props);
        propertySources.addFirst(propertySource);
    }

    /**
     * Retrieves a property value from the environment.
     *
     * @param key The property key to retrieve.
     * @return The current value of the property.
     */
    public String getProperty(String key) {
        return environment.getProperty(key);
    }
}
