///////////////////////////////////////////////////////////////////////////////////////////////////
// TexturePacker Gradle Plugin is a plugin to call TexturePacker CLI.
// Copyright (C) 2024 Dmitry Shapovalov.
//
// This file is part of TexturePacker Gradle Plugin.
//
// TexturePacker Gradle Plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// TexturePacker Gradle Plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.
///////////////////////////////////////////////////////////////////////////////////////////////////
package ru.d_shap.gradle.plugin.texturepacker.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;

import groovy.lang.Closure;

/**
 * The extension configuration.
 *
 * @author Dmitry Shapovalov
 */
public class ExtensionConfiguration {

    private final Project _project;

    private final List<PipelineConfiguration> _pipelineConfigurations;

    /**
     * Create new object.
     *
     * @param project the project.
     */
    @Inject
    public ExtensionConfiguration(final Project project) {
        super();
        _project = project;
        _pipelineConfigurations = new ArrayList<>();
    }

    /**
     * Get the pipeline configurations.
     *
     * @return the pipeline configurations.
     */
    public List<PipelineConfiguration> getPipelineConfigurations() {
        return _pipelineConfigurations;
    }

    /**
     * Add the pipeline configuration.
     *
     * @param name the pipeline name.
     * @param args the pipeline args.
     */
    public void methodMissing(final String name, final Object args) {
        if (args instanceof Object[] && ((Object[]) args).length == 1 && ((Object[]) args)[0] instanceof Closure) {
            ObjectFactory objects = _project.getObjects();
            PipelineConfiguration pipelineConfiguration = objects.newInstance(PipelineConfiguration.class, _project, name);

            Closure<?> closure = (Closure<?>) ((Object[]) args)[0];
            closure.setDelegate(pipelineConfiguration);
            closure.setResolveStrategy(Closure.DELEGATE_ONLY);
            closure.call();

            _pipelineConfigurations.add(pipelineConfiguration);
        } else {
            throw new InvalidUserDataException("Wrong extension configuration");
        }
    }

}
