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
package software.xdev.far.directorynames;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import software.xdev.far.BaseMojo;
import software.xdev.far.ExecData;


@Mojo(name = "directory-names", defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class DirectoryNamesMojo extends BaseMojo<ExecData>
{
	public DirectoryNamesMojo()
	{
		super(DirectoryNamesProcessor::new);
	}
	
	@Override
	protected ExecData enrichData(final ExecData data)
	{
		return data;
	}
}
