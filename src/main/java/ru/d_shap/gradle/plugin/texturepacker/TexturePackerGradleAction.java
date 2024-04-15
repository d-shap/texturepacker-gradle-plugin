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
import org.gradle.api.Task;

import ru.d_shap.gradle.plugin.texturepacker.configuration.ExtensionConfiguration;
import ru.d_shap.gradle.plugin.texturepacker.configuration.Parameter;
import ru.d_shap.gradle.plugin.texturepacker.configuration.ParametersConfiguration;

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
        if (Logger.isWarnEnabled()) {
            Logger.warn("Start processing images with TexturePacker");
        }
        File sourceDir = _extensionConfiguration.getSourceDir();
        File[] sourceFiles = sourceDir.listFiles();
        File destinationDir = _extensionConfiguration.getDestinationDir();
        destinationDir.mkdirs();
        if (sourceFiles != null) {
            for (File sourceFile : sourceFiles) {
                if (sourceFile.isDirectory()) {
                    processSourceDir(sourceFile, destinationDir);
                }
            }
        }
        if (Logger.isWarnEnabled()) {
            Logger.warn("Finish processing images with TexturePacker");
        }
    }

    private void processSourceDir(final File sourceDir, final File destinationDir) {
        CommandLine commandLine = createCommandLine(sourceDir);
        executeCommandLine(commandLine, destinationDir);
    }

    private CommandLine createCommandLine(final File sourceDir) {
        CommandLine commandLine = new CommandLine(COMMAND);

        String sourceDirName = sourceDir.getName();
        Closure<?> sheetNameClosure = _extensionConfiguration.getSheetNameClosure();
        String sheetName = callClosure(sourceDirName, sheetNameClosure);
        commandLine.addArgument("--sheet");
        commandLine.addArgument(sheetName);
        Closure<?> dataNameClosure = _extensionConfiguration.getDataNameClosure();
        String dataName = callClosure(sourceDirName, dataNameClosure);
        commandLine.addArgument("--data");
        commandLine.addArgument(dataName);

        ParametersConfiguration parametersConfiguration = _extensionConfiguration.getParameterConfiguration();
        List<Parameter> parameters = parametersConfiguration.getParameters();
        for (Parameter parameter : parameters) {
            commandLine.addArgument("--" + parameter.getName());
            String[] args = parameter.getArgs();
            for (String arg : args) {
                commandLine.addArgument(arg);
            }
        }

        commandLine.addArgument(sourceDir.getAbsolutePath());

        if (Logger.isInfoEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append(commandLine.getExecutable());
            for (String argument : commandLine.getArguments()) {
                builder.append(' ').append(argument);
            }
            Logger.info(builder.toString());
        }

        return commandLine;
    }

    private String callClosure(final String sourceDirName, final Closure<?> closure) {
        Object result = closure.call(sourceDirName);
        if (result instanceof String) {
            return (String) result;
        } else {
            return result.toString();
        }
    }

    private void executeCommandLine(final CommandLine commandLine, final File destinationDir) {
        try {
            DefaultExecutor executor = DefaultExecutor.builder().setWorkingDirectory(destinationDir).get();
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
        } catch (IOException ex) {
            if (Logger.isErrorEnabled()) {
                Logger.error("Exception in TexturePacker execution", ex);
            }
        }
    }

}
