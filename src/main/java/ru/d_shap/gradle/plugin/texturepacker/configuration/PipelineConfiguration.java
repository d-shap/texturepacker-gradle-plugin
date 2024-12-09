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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.model.ObjectFactory;

import groovy.lang.Closure;

/**
 * The pipeline configuration.
 *
 * @author Dmitry Shapovalov
 */
public class PipelineConfiguration {

    private final Project _project;

    private final String _name;

    private File _sourceDir;

    private String _include;

    private List<String> _includes;

    private String _exclude;

    private List<String> _excludes;

    private File _destinationDir;

    private Closure<?> _sheetNameClosure;

    private Closure<?> _dataNameClosure;

    private final ParametersConfiguration _parametersConfiguration;

    /**
     * Create new object.
     *
     * @param project the project.
     * @param name    the pipeline name.
     */
    @Inject
    public PipelineConfiguration(final Project project, final String name) {
        super();
        _project = project;
        _name = name;
        _sourceDir = null;
        _include = null;
        _includes = null;
        _exclude = null;
        _excludes = null;
        _destinationDir = null;
        _sheetNameClosure = null;
        _dataNameClosure = null;
        ObjectFactory objects = _project.getObjects();
        _parametersConfiguration = objects.newInstance(ParametersConfiguration.class);
    }

    /**
     * Get the pipeline name.
     *
     * @return the pipeline name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Get the source directory.
     *
     * @return the source directory.
     */
    public File getSourceDir() {
        return _sourceDir;
    }

    /**
     * Set the source directory.
     *
     * @param sourceDir the source directory.
     */
    public void src(final String sourceDir) {
        ConfigurableFileTree fileTree = _project.fileTree(sourceDir);
        File sourceFile = fileTree.getDir();
        _sourceDir = sourceFile.getAbsoluteFile();
    }

    /**
     * Get the source directory to include.
     *
     * @return the source directory to include.
     */
    public String getInclude() {
        return _include;
    }

    /**
     * Set the source directory to include.
     *
     * @param include the source directory to include.
     */
    public void include(final String include) {
        _include = include;
    }

    /**
     * Get the source directories to include.
     *
     * @return the source directories to include.
     */
    public List<String> getIncludes() {
        return _includes;
    }

    /**
     * Set the source directories to include.
     *
     * @param includes the source directories to include.
     */
    public void includes(final String... includes) {
        if (includes == null) {
            _includes = null;
        } else {
            _includes = Arrays.asList(includes);
        }
    }

    /**
     * Get the source directory to exclude.
     *
     * @return the source directory to exclude.
     */
    public String getExclude() {
        return _exclude;
    }

    /**
     * Set the source directory to exclude.
     *
     * @param exclude the source directory to exclude.
     */
    public void exclude(final String exclude) {
        _exclude = exclude;
    }

    /**
     * Get the source directories to exclude.
     *
     * @return the source directories to exclude.
     */
    public List<String> getExcludes() {
        return _excludes;
    }

    /**
     * Set the source directories to exclude.
     *
     * @param excludes the source directories to exclude.
     */
    public void excludes(final String... excludes) {
        if (excludes == null) {
            _excludes = null;
        } else {
            _excludes = Arrays.asList(excludes);
        }
    }

    /**
     * Get the destination directory.
     *
     * @return the destination directory.
     */
    public File getDestinationDir() {
        return _destinationDir;
    }

    /**
     * Set the destination directory.
     *
     * @param destinationDir the destination directory.
     */
    public void dst(final String destinationDir) {
        File buildDir = _project.getBuildDir();
        File buildFile = buildDir.getAbsoluteFile();
        _destinationDir = new File(buildFile, destinationDir);
    }

    /**
     * Get the sheet name closure.
     *
     * @return the sheet name closure.
     */
    public Closure<?> getSheetNameClosure() {
        return _sheetNameClosure;
    }

    /**
     * Set the sheet name closure.
     *
     * @param closure the closure.
     */
    public void sheet(final Closure<?> closure) {
        _sheetNameClosure = closure;
    }

    /**
     * Get the data name closure.
     *
     * @return the data name closure.
     */
    public Closure<?> getDataNameClosure() {
        return _dataNameClosure;
    }

    /**
     * Set the data name closure.
     *
     * @param closure the closure.
     */
    public void data(final Closure<?> closure) {
        _dataNameClosure = closure;
    }

    /**
     * Get the parameters configuration.
     *
     * @return the parameters configuration.
     */
    public ParametersConfiguration getParameterConfiguration() {
        return _parametersConfiguration;
    }

    /**
     * Set the parameters configuration.
     *
     * @param closure the closure.
     */
    public void parameters(final Closure<? super ParametersConfiguration> closure) {
        closure.setDelegate(_parametersConfiguration);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
    }

}
