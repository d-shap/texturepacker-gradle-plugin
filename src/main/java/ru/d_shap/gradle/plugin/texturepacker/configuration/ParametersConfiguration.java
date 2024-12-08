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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * The parameters configuration.
 *
 * @author Dmitry Shapovalov
 */
public class ParametersConfiguration {

    private static final Set<String> EXCLUDED_ARGS;

    static {
        Set<String> set = new HashSet<>();
        set.add("data");
        set.add("sheet");

        EXCLUDED_ARGS = Collections.unmodifiableSet(set);
    }

    private final List<Parameter> _parameters;

    /**
     * Create new object.
     */
    @Inject
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
        String arg = name.replace('_', '-');
        if (EXCLUDED_ARGS.contains(arg)) {
            return;
        }
        String[] stringArgs = getStringArgs(args);
        Parameter parameter = new Parameter(arg, stringArgs);
        _parameters.add(parameter);
    }

    private String[] getStringArgs(final Object object) {
        if (object instanceof Object[]) {
            String[] result = new String[((Object[]) object).length];
            for (int i = 0; i < ((Object[]) object).length; i++) {
                result[i] = getString(((Object[]) object)[i]);
            }
            return result;
        } else {
            return new String[0];
        }
    }

    private String getString(final Object object) {
        if (object instanceof String) {
            return (String) object;
        } else {
            return object.toString();
        }
    }

}
