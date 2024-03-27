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
package software.xdev.far.filecontents;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import software.xdev.far.BaseMojo;
import software.xdev.far.ExecData;


/**
 * Allows replacing file contents
 */
@Mojo(name = "file-contents", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class FileContentsMojo extends BaseMojo<FileContentsExecData>
{
	
	/**
	 * Specify file encoding during file-contents replacement
	 * <p>
	 * Default set to Charset.defaultCharset();
	 * </p>
	 */
	@Parameter(property = "encoding")
	protected String encoding;
	
	/**
	 * Specify to match line based or otherwise match the whole document.
	 * <p>
	 * When this value is set to <code>false</code> more memory may be required as the whole file is processed at once.
	 * </p>
	 */
	@Parameter(property = "replaceLineBased", defaultValue = "true")
	protected boolean replaceLineBased = true;
	
	public FileContentsMojo()
	{
		super(FileContentsProcessor::new);
	}
	
	@Override
	protected FileContentsExecData enrichData(final ExecData data)
	{
		return new FileContentsExecData(
			data,
			Optional.ofNullable(this.encoding)
				.map(e -> {
					try
					{
						return Charset.forName(e);
					}
					catch(final Exception ex)
					{
						return null;
					}
				})
				.orElseGet(Charset::defaultCharset),
			this.replaceLineBased
		);
	}
	
	@Override
	protected Pattern compileFindRegex()
	{
		return !this.replaceLineBased
			? Pattern.compile(this.findRegex, Pattern.MULTILINE)
			: super.compileFindRegex();
	}
	
	public void setEncoding(final String encoding)
	{
		this.encoding = encoding;
	}
	
	public void setReplaceLineBased(final boolean replaceLineBased)
	{
		this.replaceLineBased = replaceLineBased;
	}
}
