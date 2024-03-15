/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.far;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


public abstract class BaseMojo<D extends ExecData> extends AbstractMojo
{
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	MavenProject project;
	
	/**
	 * The base directory from which to perform the find and replace. This is relative to the location of the pom.
	 */
	@Parameter(property = "baseDir", defaultValue = "${basedir}")
	String baseDir;
	
	/**
	 * Whether the find and replace is recursive from the baseDir.
	 */
	@Parameter(property = "recursive", defaultValue = "false")
	boolean recursive;
	
	/**
	 * The regex string to find.
	 */
	@Parameter(property = "findRegex", required = true)
	String findRegex;
	
	/**
	 * The value to replace the matching findRegex with.
	 */
	@Parameter(property = "replaceValue")
	String replaceValue;
	
	/**
	 * A CSV of the file types to search in. For example for the value: .xml Only files ending with .xml will be
	 * renamed.
	 * <p>
	 * For the value: .xml,.properties Only files ending with .xml,.properties will be renamed.
	 * <p>
	 * Ignored for directories.
	 */
	@Parameter(property = "fileMask")
	String fileMask;
	
	/**
	 * Regex filenames/directory-names to exclude.
	 */
	@Parameter(property = "exclusions")
	String exclusions;
	
	/**
	 * Skip execution of the plugin.
	 */
	@Parameter(property = "skip", defaultValue = "false")
	boolean skip;
	
	/**
	 * Whether the find and replace maven plugin replaces all matches or just the first match.
	 */
	@Parameter(property = "replaceAll", defaultValue = "true")
	boolean replaceAll = true;
	
	protected final Consumer<D> executeInternal;
	
	protected BaseMojo(final Consumer<D> executeInternal)
	{
		this.executeInternal = Objects.requireNonNull(executeInternal);
	}
	
	@Override
	public void execute()
	{
		if(this.skip)
		{
			this.getLog().warn("Skipping execution of find-and-replace-maven-plugin.");
			return;
		}
		
		this.executeInternal.accept(this.enrichData(this.createDefaultData()));
	}
	
	protected ExecData createDefaultData()
	{
		return new ExecData(
			this.getLog(),
			this.project == null || this.baseDir.equals(this.project.getBasedir().getAbsolutePath())
				? Paths.get(this.baseDir)
				: Paths.get(this.project.getBasedir().getAbsolutePath(), this.baseDir),
			this.recursive,
			Pattern.compile(this.findRegex),
			Optional.ofNullable(this.replaceValue).orElse(""),
			Optional.ofNullable(this.fileMask)
				.map(s -> List.of(s.split(",")))
				.orElseGet(List::of),
			Optional.ofNullable(this.exclusions)
				.map(Pattern::compile)
				.map(List::of)
				.orElseGet(List::of),
			this.replaceAll);
	}
	
	protected abstract D enrichData(ExecData data);
	
	public void setProject(final MavenProject project)
	{
		this.project = project;
	}
	
	public void setBaseDir(final String baseDir)
	{
		this.baseDir = baseDir;
	}
	
	public void setRecursive(final boolean recursive)
	{
		this.recursive = recursive;
	}
	
	public void setFindRegex(final String findRegex)
	{
		this.findRegex = findRegex;
	}
	
	public void setReplaceValue(final String replaceValue)
	{
		this.replaceValue = replaceValue;
	}
	
	public void setFileMask(final String fileMask)
	{
		this.fileMask = fileMask;
	}
	
	public void setExclusions(final String exclusions)
	{
		this.exclusions = exclusions;
	}
	
	public void setSkip(final boolean skip)
	{
		this.skip = skip;
	}
	
	public void setReplaceAll(final boolean replaceAll)
	{
		this.replaceAll = replaceAll;
	}
}
