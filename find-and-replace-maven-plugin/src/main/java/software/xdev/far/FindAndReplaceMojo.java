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

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import software.xdev.far.tasks.ProcessFilesTask;


/**
 * The find and replace maven plugin will find a regex string in filenames, file contents, and directory names and
 * replace it with a given value.
 */
@Mojo(name = "find-and-replace", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class FindAndReplaceMojo extends AbstractMojo
{
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;
	
	/**
	 * The base directory from which to perform the find and replace. This is relative to the location of the pom.
	 *
	 * @parameter baseDir
	 */
	@Parameter(property = "baseDir", defaultValue = "${basedir}")
	private String baseDir;
	
	/**
	 * Whether the find and replace is recursive from the baseDir.
	 *
	 * @parameter recursive
	 */
	@Parameter(property = "recursive", defaultValue = "false")
	private boolean recursive;
	
	/**
	 * A CSV of what type of replacement(s) being done. Valid values are: file-contents filenames directory-names
	 * <p>
	 * file-contents will replace the find regex within a file. filenames will replace the find regex within a file's
	 * name. directory-names will replace the find regex within a directory's name
	 * <p>
	 * To run the find and replace for multiple types, pass them as a CSV: file-contents,filenames,directory-names
	 *
	 * @parameter replacementType
	 */
	@Parameter(property = "replacementType", required = true)
	private String replacementType;
	
	/**
	 * The regex string to find.
	 *
	 * @parameter findRegex
	 */
	@Parameter(property = "findRegex", required = true)
	private String findRegex;
	
	/**
	 * The value to replace the matching findRegex with.
	 *
	 * @parameter replaceValue
	 */
	@Parameter(property = "replaceValue", required = true, defaultValue = "")
	private String replaceValue;
	
	/**
	 * A CSV of the file types to search in. For example for the value: .xml Only files ending with .xml will be
	 * renamed.
	 * <p>
	 * For the value: .xml,.properties Only files ending with .xml,.properties will be renamed.
	 * <p>
	 * Ignored for directories.
	 *
	 * @parameter fileMask
	 */
	@Parameter(property = "fileMask")
	private String fileMask;
	
	/**
	 * Regex filenames/directory-names to exclude.
	 *
	 * @parameter exclusions
	 */
	@Parameter(property = "exclusions")
	private String exclusions;
	
	/**
	 * Skip execution of the plugin.
	 *
	 * @parameter skip
	 */
	@Parameter(property = "skip", defaultValue = "false")
	private boolean skip;
	
	/**
	 * Specify file encoding during file-contents replacement
	 * <p>
	 * Default set to Charset.defaultCharset();
	 * </p>
	 *
	 * @parameter encoding
	 */
	@Parameter(property = "encoding")
	private String encoding;
	
	/**
	 * Whether the find and replace maven plugin replaces all matches or just the first match.
	 *
	 * @parameter replaceAll
	 */
	@Parameter(property = "replaceAll", defaultValue = "true")
	private boolean replaceAll;
	
	private Charset charset = Charset.defaultCharset();
	
	private static final String FILE_CONTENTS = "file-contents";
	private static final String FILENAMES = "filenames";
	private static final String DIRECTORY_NAMES = "directory-names";
	
	private Path baseDirPath;
	
	private final List<String> validReplacementTypes = Arrays.asList(FILE_CONTENTS, FILENAMES, DIRECTORY_NAMES);
	private boolean processFileContents = false;
	private boolean processFilenames = false;
	private boolean processDirectoryNames = false;
	
	private List<String> fileMaskList = new ArrayList<>();
	private final List<Pattern> exclusionsList = new ArrayList<>();
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		if(this.skip)
		{
			this.getLog().warn("Skipping execution of find-and-replace-maven-plugin.");
			return;
		}
		
		this.setup();
		
		this.getLog().info("Executing find-and-replace maven plugin with options: " + this);
		
		try
		{
			ProcessFilesTask.process(
				this.getLog(),
				this.baseDirPath,
				this.recursive,
				Pattern.compile(this.findRegex),
				this.replaceValue,
				this.fileMaskList,
				this.exclusionsList,
				this.processFileContents,
				this.processFilenames,
				this.processDirectoryNames,
				this.replaceAll,
				this.charset);
		}
		catch(final Exception e)
		{
			throw new MojoFailureException("Unable to process files.", e);
		}
	}
	
	private void setup() throws MojoExecutionException
	{
		this.setupReplacementTypes();
		
		this.setupFileMasks();
		
		this.setupExclusions();
		
		this.setupBaseDir();
		
		this.setupEncoding();
	}
	
	private void setupReplacementTypes() throws MojoExecutionException
	{
		final String[] replacementTypeList = this.replacementType.split(",");
		
		final String logMessage = "Mode set to ";
		
		for(final String replacementTypeVal : replacementTypeList)
		{
			if(!this.validReplacementTypes.contains(replacementTypeVal))
			{
				throw new MojoExecutionException("Invalid replacementType specified: " + replacementTypeVal);
			}
			
			if(FILE_CONTENTS.equals(replacementTypeVal))
			{
				this.getLog().info(logMessage + FILE_CONTENTS);
				this.processFileContents = true;
			}
			
			if(FILENAMES.equals(replacementTypeVal))
			{
				this.getLog().info(logMessage + FILENAMES);
				this.processFilenames = true;
			}
			
			if(DIRECTORY_NAMES.equals(replacementTypeVal))
			{
				this.getLog().info(logMessage + DIRECTORY_NAMES);
				this.processDirectoryNames = true;
			}
		}
	}
	
	private void setupFileMasks()
	{
		if(this.fileMask != null && !this.fileMask.isEmpty())
		{
			this.fileMaskList = Arrays.asList(this.fileMask.split(","));
			this.getLog().info("fileMasks set to: " + this.fileMaskList);
		}
	}
	
	private void setupEncoding()
	{
		if(this.encoding != null && !this.encoding.isEmpty())
		{
			try
			{
				this.charset = Charset.forName(this.encoding);
				this.getLog().info("encoding set to: " + this.charset);
			}
			catch(final Exception e)
			{
				this.getLog().warn("Invalid encoding value " + this.encoding + ". Using default charset.");
			}
		}
	}
	
	private void setupExclusions()
	{
		if(this.exclusions != null && !this.exclusions.isEmpty())
		{
			this.getLog().info("Compiling regex for exclusions: " + this.exclusions);
			this.exclusionsList.add(Pattern.compile(this.exclusions));
		}
	}
	
	private void setupBaseDir()
	{
		if(this.project == null)
		{
			this.baseDirPath = Paths.get(this.baseDir);
		}
		else if(this.baseDir.equals(this.project.getBasedir().getAbsolutePath()))
		{
			this.baseDirPath = Paths.get(this.baseDir);
		}
		else
		{
			this.baseDirPath = Paths.get(this.project.getBasedir().getAbsolutePath(), this.baseDir);
		}
		
		this.getLog().info("baseDir set to: " + this.baseDirPath.toString());
	}
	
	@Override
	public String toString()
	{
		return "FindAndReplaceMojo{" + "baseDir='" + this.baseDir + '\''
			+ ", recursive=" + this.recursive
			+ ", replacementType='" + this.replacementType + '\''
			+ ", findRegex='" + this.findRegex + '\''
			+ ", replaceValue='" + this.replaceValue + '\''
			+ ", fileMask='" + this.fileMask + '\''
			+ ", exclusions='" + this.exclusions + '\''
			+ ", skip=" + this.skip
			+ ", baseDirPath=" + this.baseDirPath
			+ ", processFileContents=" + this.processFileContents
			+ ", processFilenames=" + this.processFilenames
			+ ", processDirectoryNames=" + this.processDirectoryNames
			+ ", fileMaskList=" + this.fileMaskList
			+ ", exclusionsList=" + this.exclusionsList
			+ ", encoding=" + this.encoding
			+ ", replaceAll=" + this.replaceAll
			+ '}';
	}
}
