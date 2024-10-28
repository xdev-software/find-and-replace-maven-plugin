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

import software.xdev.far.ExecData;


/**
 * @deprecated Only exists for compatibility reasons
 */
@Deprecated
public class FindAndReplaceExecData extends ExecData
{
	private final boolean processFileContents;
	private final boolean processFilenames;
	private final boolean processDirectoryNames;
	
	private final Charset charset;
	
	
	public FindAndReplaceExecData(
		final ExecData other,
		final boolean processFileContents,
		final boolean processFilenames,
		final boolean processDirectoryNames,
		final Charset charset)
	{
		super(other);
		this.processFileContents = processFileContents;
		this.processFilenames = processFilenames;
		this.processDirectoryNames = processDirectoryNames;
		this.charset = charset;
	}
	
	public boolean isProcessFileContents()
	{
		return this.processFileContents;
	}
	
	public boolean isProcessFilenames()
	{
		return this.processFilenames;
	}
	
	public boolean isProcessDirectoryNames()
	{
		return this.processDirectoryNames;
	}
	
	public Charset getCharset()
	{
		return this.charset;
	}
}
