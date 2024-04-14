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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

import ru.d_shap.gradle.plugin.texturepacker.configuration.ExtensionConfiguration;

/**
 * TexturePacker gradle plugin.
 *
 * @author Dmitry Shapovalov
 */
public class TexturePackerGradlePlugin implements Plugin<Project> {

    static final String TASK_NAME = "texturepacker";

    static final String EXTENSION_NAME = "texturePacker";

    /**
     * Create new object.
     */
    public TexturePackerGradlePlugin() {
        super();
    }

    @Override
    public void apply(final Project project) {
        ExtensionContainer extensionContainer = project.getExtensions();
        ExtensionConfiguration extensionConfiguration = extensionContainer.create(EXTENSION_NAME, ExtensionConfiguration.class);
        Task texturePackerTask = project.task(TASK_NAME);
        TexturePackerGradleAction texturePackerAction = new TexturePackerGradleAction(extensionConfiguration);
        texturePackerTask.doLast(texturePackerAction);

        TaskContainer tasks = project.getTasks();
        Task processResourcesTask = tasks.getByName("processResources");
        processResourcesTask.dependsOn(texturePackerTask);
        Task compileJavaTask = tasks.getByName("compileJava");
        compileJavaTask.dependsOn(texturePackerTask);
    }

}
