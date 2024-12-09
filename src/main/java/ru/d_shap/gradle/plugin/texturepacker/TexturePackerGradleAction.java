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

            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    if (sourceFile.isDirectory()) {
                        processSourceDir(pipelineConfiguration, sourceFile, destinationDir);
                    }
                }
            }
        }
        if (Logger.isInfoEnabled()) {
            Logger.info("Finish processing images with TexturePacker");
        }
    }

    private void processSourceDir(final PipelineConfiguration pipelineConfiguration, final File sourceDir, final File destinationDir) {
        String sourceDirName = sourceDir.getName();
        Closure<?> sheetNameClosure = pipelineConfiguration.getSheetNameClosure();
        if (sheetNameClosure == null) {
            throw new InvalidUserDataException("sheet property must be defined");
        }
        String sheetAbsolutePath = getAbsolutePath(sourceDirName, sheetNameClosure, destinationDir);
        Closure<?> dataNameClosure = pipelineConfiguration.getDataNameClosure();
        if (dataNameClosure == null) {
            throw new InvalidUserDataException("data property must be defined");
        }
        String dataAbsolutePath = getAbsolutePath(sourceDirName, dataNameClosure, destinationDir);
        String sourceDirAbsolutePath = sourceDir.getAbsolutePath();
        CommandLine commandLine = createCommandLine(pipelineConfiguration, sheetAbsolutePath, dataAbsolutePath, sourceDirAbsolutePath);
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

    private CommandLine createCommandLine(final PipelineConfiguration pipelineConfiguration, final String sheetAbsolutePath, final String dataAbsolutePath, final String sourceDirAbsolutePath) {
        CommandLine commandLine = new CommandLine(COMMAND);

        commandLine.addArgument("--sheet");
        commandLine.addArgument(sheetAbsolutePath);
        commandLine.addArgument("--data");
        commandLine.addArgument(dataAbsolutePath);

        ParametersConfiguration parametersConfiguration = pipelineConfiguration.getParameterConfiguration();
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
            if (outputStr.length() > 0 && Logger.isInfoEnabled()) {
                Logger.info(outputStr);
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
