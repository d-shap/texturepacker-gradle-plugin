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

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.Task;

import ru.d_shap.gradle.plugin.texturepacker.configuration.ExtensionConfiguration;
import ru.d_shap.gradle.plugin.texturepacker.configuration.Parameter;

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
        File destinationDir = _extensionConfiguration.getDestinationDir();
        ensureDestinationDirExists(destinationDir);
        File sourceDir = _extensionConfiguration.getSourceDir();
        File[] sourceFiles = sourceDir.listFiles();
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

    private void ensureDestinationDirExists(final File destinationDir) {
        File parentFile = destinationDir.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
    }

    private void processSourceDir(final File sourceDir, final File destinationDir) {
        if (Logger.isWarnEnabled()) {
            Logger.warn(COMMAND);
            Logger.warn(sourceDir.getAbsolutePath());
            Logger.warn(destinationDir.getAbsolutePath());
            for (Parameter parameter : _extensionConfiguration.getParameterConfiguration().getParameters()) {
                Logger.warn("--" + parameter.getName());
                for (String str : parameter.getArgs()) {
                    Logger.warn(str);
                }
            }
        }
    }

}
