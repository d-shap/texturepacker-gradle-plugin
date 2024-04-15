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

/**
 * The parameters configuration.
 *
 * @author Dmitry Shapovalov
 */
public class ParametersConfiguration {

    private final List<Parameter> _parameters;

    /**
     * Create new object.
     */
    public ParametersConfiguration() {
        super();
        _parameters = new ArrayList<>();
    }

    /**
     * Get the parameters.
     *
     * @return the parameters.
     */
    public List<Parameter> getParameters() {
        return _parameters;
    }

    /**
     * Add an arbitrary parameter.
     *
     * @param name the name.
     * @param args the args.
     */
    public void methodMissing(final String name, final Object args) {
        Parameter parameter = new Parameter(name, args);
        _parameters.add(parameter);
    }

}