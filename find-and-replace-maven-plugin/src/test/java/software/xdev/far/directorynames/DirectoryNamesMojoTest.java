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
