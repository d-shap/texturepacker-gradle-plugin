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

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

import ru.d_shap.gradle.plugin.texturepacker.configuration.ExtensionConfiguration;

/**
 * TexturePacker gradle plugin.
 *
 * @author Dmitry Shapovalov
 */
public class TexturePackerGradlePlugin implements Plugin<Project> {

    static final String TASK_NAME = "texturePacker";

    static final String EXTENSION_NAME = "texturepacker";

    /**
     * Create new object.
     */
    public TexturePackerGradlePlugin() {
        super();
    }

    @Override
    public void apply(final Project project) {
        Task task = project.task(TASK_NAME);
        addDependencies(project, task);

        ExtensionConfiguration extensionConfiguration = getExtensionConfiguration(project);

        addTaskAction(task, extensionConfiguration);
        addProjectAction(project, task, extensionConfiguration);
    }

    private void addDependencies(final Project project, final Task task) {
        TaskContainer tasks = project.getTasks();
        addDependency(tasks, "processResources", task);
        addDependency(tasks, "compileJava", task);
        addMustRunAfter(tasks, "imageMagick", task);
    }

    private void addDependency(final TaskContainer tasks, final String otherTaskName, final Task task) {
        try {
            Task otherTask = tasks.getByName(otherTaskName);
            otherTask.dependsOn(task);
        } catch (UnknownTaskException ex) {
            // Ignore
        }
    }

    private void addMustRunAfter(final TaskContainer tasks, final String otherTaskName, final Task task) {
        try {
            Task otherTask = tasks.getByName(otherTaskName);
            task.mustRunAfter(otherTask);
        } catch (UnknownTaskException ex) {
            // Ignore
        }
    }

    private ExtensionConfiguration getExtensionConfiguration(final Project project) {
        ExtensionContainer extensions = project.getExtensions();
        return extensions.create(EXTENSION_NAME, ExtensionConfiguration.class, project);
    }

    private void addTaskAction(final Task task, final ExtensionConfiguration extensionConfiguration) {
        Action<Task> action = new TexturePackerGradleAction(extensionConfiguration);
        task.doLast(action);
    }

    private void addProjectAction(final Project project, final Task task, final ExtensionConfiguration extensionConfiguration) {
        Action<Project> action = new TexturePackerGradleConfiguration(task, extensionConfiguration);
        project.afterEvaluate(action);
    }

}
