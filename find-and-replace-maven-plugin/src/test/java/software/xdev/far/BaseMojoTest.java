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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;


/**
 * These behave more like integration tests than unit tests. They dynamically generate the folders/files, and then check
 * that the plugin is working as expected.
 */
public abstract class BaseMojoTest<M extends BaseMojo<?>>
{
	private final Supplier<M> mojoSupplier;
	
	protected M mojo;
	
	protected Path textTestFile;
	
	protected Path xmlTestFile;
	
	protected Path ymlTestFile;
	
	protected Path nonUtfTestFile;
	
	@TempDir
	protected Path runningTestsPath;
	
	protected BaseMojoTest(final Supplier<M> mojoSupplier)
	{
		this.mojoSupplier = mojoSupplier;
	}
	
	@BeforeEach
	void beforeEach()
	{
		this.textTestFile = this.copyFileIntoTestDir("test-file.txt");
		this.xmlTestFile = this.copyFileIntoTestDir("test-file.xml");
		this.ymlTestFile = this.copyFileIntoTestDir("test-file.yml");
		this.nonUtfTestFile = this.copyFileIntoTestDir("non-utf");
		
		this.mojo = this.mojoSupplier.get();
		this.mojo.baseDir = this.runningTestsPath.toString();
	}
	
	protected Path copyFileIntoTestDir(final String fileName)
	{
		try
		{
			final Path target = Paths.get(this.runningTestsPath.toString(), fileName);
			Files.copy(Objects.requireNonNull(this.getClass().getResourceAsStream("/" + fileName)), target);
			return target;
		}
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
	
	protected void executeMojoAssertDoesNotThrow()
	{
		assertDoesNotThrow(this.mojo::execute);
	}
	
	protected boolean fileContains(final File file, final String findValue)
	{
		return this.fileContains(file, findValue, Charset.defaultCharset());
	}
	
	protected boolean fileContains(final File file, final String findValue, final Charset charset)
	{
		try(final FileInputStream fis = new FileInputStream(file);
			final InputStreamReader isr = new InputStreamReader(fis, charset);
			final BufferedReader fileReader = new BufferedReader(isr))
		{
			return fileReader.lines().anyMatch(line -> line.contains(findValue));
		}
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
}
