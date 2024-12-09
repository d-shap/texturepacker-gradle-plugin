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
package ru.d_shap.gradle.plugin.texturepacker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Task;

import ru.d_shap.gradle.plugin.texturepacker.configuration.ExtensionConfiguration;
import ru.d_shap.gradle.plugin.texturepacker.configuration.Parameter;
import ru.d_shap.gradle.plugin.texturepacker.configuration.ParametersConfiguration;
import ru.d_shap.gradle.plugin.texturepacker.configuration.PipelineConfiguration;

import groovy.lang.Closure;

/**
 * TexturePacker gradle action.
 *
 * @author Dmitry Shapovalov
 */
public class TexturePackerGradleAction implements Action<Task> {

    private static final String COMMAND = "TexturePacker";

    private final ExtensionConfiguration _extensionConfiguration;

    /**
     * Create new object.
     *
     * @param extensionConfiguration the extension configuration.
     */
    public TexturePackerGradleAction(final ExtensionConfiguration extensionConfiguration) {
        super();
        _extensionConfiguration = extensionConfiguration;
    }

    @Override
    public void execute(final Task task) {
        if (Logger.isInfoEnabled()) {
            Logger.info("Start processing images with TexturePacker");
        }
        List<PipelineConfiguration> pipelineConfigurations = _extensionConfiguration.getPipelineConfigurations();
        for (PipelineConfiguration pipelineConfiguration : pipelineConfigurations) {
            File sourceDir = pipelineConfiguration.getSourceDir();
            if (sourceDir == null) {
                throw new InvalidUserDataException("src property must be defined");
            }
            File[] sourceFiles = sourceDir.listFiles();

            File destinationDir = pipelineConfiguration.getDestinationDir();
            if (destinationDir == null) {
                throw new InvalidUserDataException("dst property must be defined");
            }
            destinationDir.mkdirs();

            String include = pipelineConfiguration.getInclude();
            List<String> includes = pipelineConfiguration.getIncludes();
            String exclude = pipelineConfiguration.getExclude();
            List<String> excludes = pipelineConfiguration.getExcludes();
            checkConfigurationValid(include, includes, exclude, excludes);

            Closure<?> sheetNameClosure = pipelineConfiguration.getSheetNameClosure();
            if (sheetNameClosure == null) {
                throw new InvalidUserDataException("Property sheet is udefined");
            }
            Closure<?> dataNameClosure = pipelineConfiguration.getDataNameClosure();
            if (dataNameClosure == null) {
                throw new InvalidUserDataException("Property data is udefined");
            }

            ParametersConfiguration parametersConfiguration = pipelineConfiguration.getParameterConfiguration();

            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    if (shouldProcessSourceDir(sourceFile, include, includes, exclude, excludes)) {
                        processSourceDir(sourceFile, destinationDir, sheetNameClosure, dataNameClosure, parametersConfiguration);
                    }
                }
            }
        }
        if (Logger.isInfoEnabled()) {
            Logger.info("Finish processing images with TexturePacker");
        }
    }

    private void checkConfigurationValid(final String include, final List<String> includes, final String exclude, final List<String> excludes) {
        if (include == null && (includes == null || includes.isEmpty()) && exclude == null && (excludes == null || excludes.isEmpty())) {
            return;
        }
        if (include != null && includes != null && !includes.isEmpty()) {
            throw new InvalidUserDataException("Configuration can't have both include and includes");
        }
        if (exclude != null && excludes != null && !excludes.isEmpty()) {
            throw new InvalidUserDataException("Configuration can't have both exclude and excludes");
        }
        if (include != null && (exclude != null || excludes != null && !excludes.isEmpty())) {
            throw new InvalidUserDataException("Configuration can't have both include and exclude");
        }
        if (excludes != null && !excludes.isEmpty() && (exclude != null || excludes != null && !excludes.isEmpty())) {
            throw new InvalidUserDataException("Configuration can't have both include and exclude");
        }
        if (exclude != null && (include != null || includes != null && !includes.isEmpty())) {
            throw new InvalidUserDataException("Configuration can't have both include and exclude");
        }
        if (excludes != null && !excludes.isEmpty() && (include != null || includes != null && !includes.isEmpty())) {
            throw new InvalidUserDataException("Configuration can't have both include and exclude");
        }
    }

    private boolean shouldProcessSourceDir(final File sourceFile, final String include, final List<String> includes, final String exclude, final List<String> excludes) {
        if (!sourceFile.isDirectory()) {
            return false;
        }

        String name = sourceFile.getName();

        if (name.equals(include)) {
            return true;
        }
        if (includes != null && includes.contains(name)) {
            return true;
        }

        if (name.equals(exclude)) {
            return false;
        }
        if (excludes != null && excludes.contains(name)) {
            return false;
        }

        return true;
    }

    private void processSourceDir(final File sourceDir, final File destinationDir, final Closure<?> sheetNameClosure, final Closure<?> dataNameClosure, final ParametersConfiguration parametersConfiguration) {
        String sourceDirName = sourceDir.getName();
        String sheetAbsolutePath = getAbsolutePath(sourceDirName, sheetNameClosure, destinationDir);
        String dataAbsolutePath = getAbsolutePath(sourceDirName, dataNameClosure, destinationDir);
        String sourceDirAbsolutePath = sourceDir.getAbsolutePath();
        CommandLine commandLine = createCommandLine(sheetAbsolutePath, dataAbsolutePath, parametersConfiguration, sourceDirAbsolutePath);
        executeCommandLine(commandLine, sheetAbsolutePath, dataAbsolutePath, sourceDirAbsolutePath);
    }

    private String getAbsolutePath(final String sourceDirName, final Closure<?> closure, final File destinationDir) {
        Object callResult = closure.call(sourceDirName);
        String fileName;
        if (callResult instanceof String) {
            fileName = (String) callResult;
        } else {
            fileName = callResult.toString();
        }
        File file = new File(destinationDir, fileName);
        return file.getAbsolutePath();
    }

    private CommandLine createCommandLine(final String sheetAbsolutePath, final String dataAbsolutePath, final ParametersConfiguration parametersConfiguration, final String sourceDirAbsolutePath) {
        CommandLine commandLine = new CommandLine(COMMAND);

        commandLine.addArgument("--sheet");
        commandLine.addArgument(sheetAbsolutePath);
        commandLine.addArgument("--data");
        commandLine.addArgument(dataAbsolutePath);

        List<Parameter> parameters = parametersConfiguration.getParameters();
        for (Parameter parameter : parameters) {
            commandLine.addArgument("--" + parameter.getName());
            String[] args = parameter.getArgs();
            for (String arg : args) {
                commandLine.addArgument(arg);
            }
        }

        commandLine.addArgument(sourceDirAbsolutePath);

        if (Logger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append(commandLine.getExecutable());
            for (String argument : commandLine.getArguments()) {
                builder.append(' ').append(argument);
            }
            Logger.debug(builder.toString());
        }

        return commandLine;
    }

    private void executeCommandLine(final CommandLine commandLine, final String sheetAbsolutePath, final String dataAbsolutePath, final String sourceDirAbsolutePath) {
        try {
            DefaultExecutor executor = DefaultExecutor.builder().get();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
            ExecuteStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorOutputStream);
            executor.setStreamHandler(streamHandler);
            executor.execute(commandLine);

            String outputStr = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            if (outputStr.length() > 0 && Logger.isDebugEnabled()) {
                Logger.debug(outputStr);
            }
            String errorOutputStr = new String(errorOutputStream.toByteArray(), StandardCharsets.UTF_8);
            if (errorOutputStr.length() > 0 && Logger.isErrorEnabled()) {
                Logger.error(errorOutputStr);
            }
            if (Logger.isInfoEnabled()) {
                Logger.info("Directory " + sourceDirAbsolutePath + " is processed");
                Logger.info("File " + sheetAbsolutePath + " is created");
                Logger.info("File " + dataAbsolutePath + " is created");
            }
        } catch (IOException ex) {
            if (Logger.isErrorEnabled()) {
                Logger.error("Exception in TexturePacker execution", ex);
            }
        }
    }

}
