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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import software.xdev.far.BaseMojoTest;


class DirectoryNamesMojoTest extends BaseMojoTest<DirectoryNamesMojo>
{
	public DirectoryNamesMojoTest()
	{
		super(DirectoryNamesMojo::new);
	}
	
	@Test
	void testDirectoryNames() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.mojo.setFindRegex("-");
		this.mojo.setReplaceValue("_");
		
		this.executeMojoAssertDoesNotThrow();
		
		final Path expectedFirstDirPath = Paths.get(this.runningTestsPath.toString(), "test_top_directory");
		final Path expectedSecondDirPath = Paths.get(expectedFirstDirPath.toString(), secondDirName);
		
		assertTrue(Files.exists(expectedFirstDirPath));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.mojo.setFindRegex("-");
		this.mojo.setReplaceValue("_");
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		final Path expectedFirstDirPath = Paths.get(this.runningTestsPath.toString(), "test_top-directory");
		final Path expectedSecondDirPath = Paths.get(expectedFirstDirPath.toString(), secondDirName);
		
		assertTrue(Files.exists(expectedFirstDirPath));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesRecursive() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.mojo.setFindRegex("-");
		this.mojo.setReplaceValue("_");
		this.mojo.setRecursive(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		final Path expectedFirstDirPath = Paths.get(this.runningTestsPath.toString(), "test_top_directory");
		final Path expectedSecondDirPath = Paths.get(expectedFirstDirPath.toString(), "test_sub_directory");
		
		assertTrue(Files.exists(expectedFirstDirPath));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesRecursiveReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.mojo.setFindRegex("-");
		this.mojo.setReplaceValue("_");
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		final Path expectedFirstDirPath = Paths.get(this.runningTestsPath.toString(), "test_top-directory");
		final Path expectedSecondDirPath = Paths.get(expectedFirstDirPath.toString(), "test_sub-directory");
		
		assertTrue(Files.exists(expectedFirstDirPath));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesRecursiveExclusions() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.mojo.setFindRegex("-");
		this.mojo.setReplaceValue("_");
		this.mojo.setExclusions("-top-");
		this.mojo.setRecursive(true);
		
		this.executeMojoAssertDoesNotThrow();
		
		final Path expectedSecondDirPath = Paths.get(firstDir.toString(), "test_sub_directory");
		
		assertTrue(Files.exists(firstDir));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesRecursiveExclusionsReplaceFirst() throws IOException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.mojo.setFindRegex("-");
		this.mojo.setReplaceValue("_");
		this.mojo.setExclusions("-top-");
		this.mojo.setRecursive(true);
		this.mojo.setReplaceAll(false);
		
		this.executeMojoAssertDoesNotThrow();
		
		final Path expectedSecondDirPath = Paths.get(firstDir.toString(), "test_sub-directory");
		
		assertTrue(Files.exists(firstDir));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
}
