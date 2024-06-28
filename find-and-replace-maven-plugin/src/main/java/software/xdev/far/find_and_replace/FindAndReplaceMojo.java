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
package software.xdev.far.find_and_replace;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import software.xdev.far.BaseMojo;
import software.xdev.far.ExecData;


/**
 * @deprecated Only exists for compatibility reasons.
 * Use the other goals instead
 */
@Deprecated
@Mojo(name = "find-and-replace", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class FindAndReplaceMojo extends BaseMojo<FindAndReplaceExecData>
{
	private static final String FILE_CONTENTS = "file-contents";
	private static final String FILENAMES = "filenames";
	private static final String DIRECTORY_NAMES = "directory-names";
	
	/**
	 * A CSV of what type of replacement(s) being done. Valid values are: file-contents filenames directory-names
	 * <p>
	 * file-contents will replace the find regex within a file. filenames will replace the find regex within a file's
	 * name. directory-names will replace the find regex within a directory's name
	 * <p>
	 * To run the find and replace for multiple types, pass them as a CSV: file-contents,filenames,directory-names
	 */
	@Parameter(property = "replacementType", required = true)
	protected  String replacementType;
	
	/**
	 * Specify file encoding during file-contents replacement
	 * <p>
	 * Default set to Charset.defaultCharset();
	 * </p>
	 */
	@Parameter(property = "encoding")
	protected String encoding;
	
	public FindAndReplaceMojo()
	{
		super(FindAndReplaceProcessor::new);
	}
	
	@Override
	protected FindAndReplaceExecData enrichData(final ExecData data)
	{
		this.getLog().warn("The find-and-replace goal is deprecated and only exists for compatibility reasons."
			+ " Please pick a more specific goal.");
		
		return new FindAndReplaceExecData(
			data,
			FILE_CONTENTS.equals(this.replacementType),
			FILENAMES.equals(this.replacementType),
			DIRECTORY_NAMES.equals(this.replacementType),
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
				.orElseGet(Charset::defaultCharset)
		);
	}
	
	public void setEncoding(final String encoding)
	{
		this.encoding = encoding;
	}
}
