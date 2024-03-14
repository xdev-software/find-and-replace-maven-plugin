package software.xdev.far;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * These behave more like integration tests than unit tests. They dynamically generate the folders/files, and then check
 * that the plugin is working as expected.
 */
class FindAndReplaceMojoTest
{
	private final FindAndReplaceMojo findAndReplaceMojo = new FindAndReplaceMojo();
	
	private Path runningTestsPath;
	
	private Path textTestFile;
	
	private Path xmlTestFile;
	
	private Path ymlTestFile;
	
	private Path nonUtfTestFile;
	
	@BeforeEach
	void setUpAll() throws NoSuchFieldException, IllegalAccessException, IOException, URISyntaxException
	{
		final URL integration = ClassLoader.getSystemResource("integration");
		final Path integrationFolder = Paths.get(Objects.requireNonNull(
			integration.toURI()));
		this.runningTestsPath = Paths.get(integrationFolder.toString(), "runner");
		
		Files.createDirectory(this.runningTestsPath);
		
		this.textTestFile = Files.copy(
			Paths.get(integrationFolder.toString(), "test-file.txt"),
			Paths.get(this.runningTestsPath.toString(), "test-file.txt"));
		this.xmlTestFile = Files.copy(
			Paths.get(integrationFolder.toString(), "test-file.xml"),
			Paths.get(this.runningTestsPath.toString(), "test-file.xml"));
		this.ymlTestFile = Files.copy(
			Paths.get(integrationFolder.toString(), "test-file.yml"),
			Paths.get(this.runningTestsPath.toString(), "test-file.yml"));
		this.nonUtfTestFile = Files.copy(
			Paths.get(integrationFolder.toString(), "non-utf"),
			Paths.get(this.runningTestsPath.toString(), "non-utf"));
		
		this.setFieldValue(this.findAndReplaceMojo, "baseDir", this.runningTestsPath.toString());
	}
	
	@AfterEach
	void cleanup() throws IOException
	{
		this.recursiveDelete(this.runningTestsPath);
	}
	
	@Test
	void testDirectoryNames() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "-");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "_");
		this.setFieldValue(this.findAndReplaceMojo, "processDirectoryNames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "directory-names");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		final Path expectedFirstDirPath = Paths.get(this.runningTestsPath.toString(), "test_top_directory");
		final Path expectedSecondDirPath = Paths.get(expectedFirstDirPath.toString(), secondDirName);
		
		assertTrue(Files.exists(expectedFirstDirPath));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesReplaceFirst() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "-");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "_");
		this.setFieldValue(this.findAndReplaceMojo, "processDirectoryNames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "directory-names");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		final Path expectedFirstDirPath = Paths.get(this.runningTestsPath.toString(), "test_top-directory");
		final Path expectedSecondDirPath = Paths.get(expectedFirstDirPath.toString(), secondDirName);
		
		assertTrue(Files.exists(expectedFirstDirPath));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesRecursive() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "-");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "_");
		this.setFieldValue(this.findAndReplaceMojo, "processDirectoryNames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "directory-names");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		final Path expectedFirstDirPath = Paths.get(this.runningTestsPath.toString(), "test_top_directory");
		final Path expectedSecondDirPath = Paths.get(expectedFirstDirPath.toString(), "test_sub_directory");
		
		assertTrue(Files.exists(expectedFirstDirPath));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesRecursiveReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "-");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "_");
		this.setFieldValue(this.findAndReplaceMojo, "processDirectoryNames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "directory-names");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		final Path expectedFirstDirPath = Paths.get(this.runningTestsPath.toString(), "test_top-directory");
		final Path expectedSecondDirPath = Paths.get(expectedFirstDirPath.toString(), "test_sub-directory");
		
		assertTrue(Files.exists(expectedFirstDirPath));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesRecursiveExclusions()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "-");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "_");
		this.setFieldValue(this.findAndReplaceMojo, "processDirectoryNames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "directory-names");
		this.setFieldValue(this.findAndReplaceMojo, "exclusions", "-top-");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		final Path expectedSecondDirPath = Paths.get(firstDir.toString(), "test_sub_directory");
		
		assertTrue(Files.exists(firstDir));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testDirectoryNamesRecursiveExclusionsReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-top-directory"));
		final String secondDirName = "test-sub-directory";
		Files.createDirectories(Paths.get(firstDir.toString(), secondDirName));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "-");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "_");
		this.setFieldValue(this.findAndReplaceMojo, "processDirectoryNames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "directory-names");
		this.setFieldValue(this.findAndReplaceMojo, "exclusions", "-top-");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		final Path expectedSecondDirPath = Paths.get(firstDir.toString(), "test_sub-directory");
		
		assertTrue(Files.exists(firstDir));
		
		assertTrue(Files.exists(expectedSecondDirPath));
	}
	
	@Test
	void testFilenames() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path nonRecurseFile = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "_");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "-");
		this.setFieldValue(this.findAndReplaceMojo, "processFilenames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "filenames");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file-name")));
		assertTrue(Files.exists(nonRecurseFile));
	}
	
	@Test
	void testFilenamesReplaceFirst() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path nonRecurseFile = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "_");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "-");
		this.setFieldValue(this.findAndReplaceMojo, "processFilenames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "filenames");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file_name")));
		assertTrue(Files.exists(nonRecurseFile));
	}
	
	@Test
	void testFilenamesRecursive() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "_");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "-");
		this.setFieldValue(this.findAndReplaceMojo, "processFilenames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "filenames");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file-name")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-file-name")));
	}
	
	@Test
	void testFilenamesRecursiveReplaceFirst() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "_");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "-");
		this.setFieldValue(this.findAndReplaceMojo, "processFilenames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "filenames");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file_name")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-file_name")));
	}
	
	@Test
	void testFilenamesRecursiveFileMasks() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectories(Paths.get(firstDir.toString(), "test-sub-dir"));
		final Path unchangedFile1 = Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path unchangedFile2 = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		final Path unchangedFile3 = Files.createFile(Paths.get(secondDir.toString(), "some_file_name"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name.xml"));
		Files.createFile(Paths.get(firstDir.toString(), "some_file_name.txt"));
		Files.createFile(Paths.get(secondDir.toString(), "some_file_name.yml"));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "_");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "-");
		this.setFieldValue(this.findAndReplaceMojo, "processFilenames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "filenames");
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.txt,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(Files.exists(unchangedFile1));
		assertTrue(Files.exists(unchangedFile2));
		assertTrue(Files.exists(unchangedFile3));
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file-name.xml")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-file-name.txt")));
		assertTrue(Files.exists(Paths.get(secondDir.toString(), "some-file-name.yml")));
	}
	
	@Test
	void testFilenamesRecursiveFileMasksReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectories(Paths.get(firstDir.toString(), "test-sub-dir"));
		final Path unchangedFile1 = Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path unchangedFile2 = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		final Path unchangedFile3 = Files.createFile(Paths.get(secondDir.toString(), "some_file_name"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name.xml"));
		Files.createFile(Paths.get(firstDir.toString(), "some_file_name.txt"));
		Files.createFile(Paths.get(secondDir.toString(), "some_file_name.yml"));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "_");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "-");
		this.setFieldValue(this.findAndReplaceMojo, "processFilenames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "filenames");
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.txt,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(Files.exists(unchangedFile1));
		assertTrue(Files.exists(unchangedFile2));
		assertTrue(Files.exists(unchangedFile3));
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-file_name.xml")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-file_name.txt")));
		assertTrue(Files.exists(Paths.get(secondDir.toString(), "some-file_name.yml")));
	}
	
	@Test
	void testFilenamesRecursiveFileMasksWithExclusions()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectories(Paths.get(firstDir.toString(), "test-sub-dir"));
		final Path unchangedFile1 = Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path unchangedFile2 = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		final Path unchangedFile3 = Files.createFile(Paths.get(secondDir.toString(), "some_file_name"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_xml_file_name.xml"));
		Files.createFile(Paths.get(firstDir.toString(), "some_txt_file_name.txt"));
		final Path unchangedExclusion = Files.createFile(Paths.get(secondDir.toString(), "some_yml_file_name.yml"));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "_");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "-");
		this.setFieldValue(this.findAndReplaceMojo, "processFilenames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "filenames");
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.txt,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "exclusions", ".yml$");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(Files.exists(unchangedFile1));
		assertTrue(Files.exists(unchangedFile2));
		assertTrue(Files.exists(unchangedFile3));
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-xml-file-name.xml")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-txt-file-name.txt")));
		assertTrue(Files.exists(unchangedExclusion));
	}
	
	@Test
	void testFilenamesRecursiveFileMasksWithExclusionsReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectories(Paths.get(firstDir.toString(), "test-sub-dir"));
		final Path unchangedFile1 = Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_file_name"));
		final Path unchangedFile2 = Files.createFile(Paths.get(firstDir.toString(), "some_file_name"));
		final Path unchangedFile3 = Files.createFile(Paths.get(secondDir.toString(), "some_file_name"));
		Files.createFile(Paths.get(this.runningTestsPath.toString(), "some_xml_file_name.xml"));
		Files.createFile(Paths.get(firstDir.toString(), "some_txt_file_name.txt"));
		final Path unchangedExclusion = Files.createFile(Paths.get(secondDir.toString(), "some_yml_file_name.yml"));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "_");
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", "-");
		this.setFieldValue(this.findAndReplaceMojo, "processFilenames", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "filenames");
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.txt,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "exclusions", ".yml$");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(Files.exists(unchangedFile1));
		assertTrue(Files.exists(unchangedFile2));
		assertTrue(Files.exists(unchangedFile3));
		
		assertTrue(Files.exists(Paths.get(this.runningTestsPath.toString(), "some-xml_file_name.xml")));
		assertTrue(Files.exists(Paths.get(firstDir.toString(), "some-txt_file_name.txt")));
		assertTrue(Files.exists(unchangedExclusion));
	}
	
	@Test
	void testFileContents() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsReplaceFirst() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsNonStandardEncodingFindRegex()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		// Replace non-standard with standard
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "ìíîï");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "encoding", "ISO-8859-1");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertFalse(this.fileContains(this.nonUtfTestFile.toFile(), "àáâãäåæçèéêëìíîï", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardEncodingFindRegexReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		// Replace non-standard with standard
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "ìíîï");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "encoding", "ISO-8859-1");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), "àáâãäåæçèéêëìíîï", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardReplaceValue()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		// Replace standard with non-standard
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "test");
		final String replaceValue = "çåæ";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "encoding", "ISO-8859-1");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertFalse(this.fileContains(this.nonUtfTestFile.toFile(), "test", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardReplaceValueReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		// Replace standard with non-standard
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "test");
		final String replaceValue = "çåæ";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "encoding", "ISO-8859-1");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), "test", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardFindAndReplaceValue()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		// Replace non-standard with non-standard
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "àáâãäåæçèéêëìíîï");
		final String replaceValue = "çåæìíîïàáâãäå";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "encoding", "ISO-8859-1");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertFalse(this.fileContains(this.nonUtfTestFile.toFile(), "àáâãäåæçèéêëìíîï", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsNonStandardFindAndReplaceValueReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		// Replace non-standard with non-standard
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "àáâãäåæçèéêëìíîï");
		final String replaceValue = "çåæìíîïàáâãäå";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "encoding", "ISO-8859-1");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), replaceValue, StandardCharsets.ISO_8859_1));
		assertTrue(this.fileContains(this.nonUtfTestFile.toFile(), "àáâãäåæçèéêëìíîï", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void testFileContentsRecursive() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsNotRecursive() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", false);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsNotRecursiveReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", false);
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveFileMasks() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveFileMasksReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveFileMasksExclusions()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "exclusions", ".yml$");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertFalse(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testFileContentsRecursiveFileMasksExclusionsReplaceFirst()
		throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path testFileXmlMoved = Files.copy(this.xmlTestFile, Paths.get(
			firstDir.toString(),
			this.xmlTestFile.toFile().getName()));
		final Path testFileYmlMoved = Files.copy(this.ymlTestFile, Paths.get(
			firstDir.toString(),
			this.ymlTestFile.toFile().getName()));
		final Path testFileTxtMoved =
			Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "asdf");
		final String replaceValue = "value successfully replaced";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "exclusions", ".yml$");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		assertFalse(this.fileContains(this.textTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), replaceValue));
		assertFalse(this.fileContains(this.ymlTestFile.toFile(), replaceValue));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileYmlMoved.toFile(), replaceValue));
		assertFalse(this.fileContains(testFileTxtMoved.toFile(), replaceValue));
		
		assertTrue(this.fileContains(this.textTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.xmlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(this.ymlTestFile.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileXmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileYmlMoved.toFile(), "asdf"));
		assertTrue(this.fileContains(testFileTxtMoved.toFile(), "asdf"));
	}
	
	@Test
	void testEverything() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-directory"));
		final Path secondDir = Files.createDirectory(Paths.get(firstDir.toString(), "test-sub-directory"));
		Files.copy(this.xmlTestFile, Paths.get(firstDir.toString(), this.xmlTestFile.toFile().getName()));
		Files.copy(this.ymlTestFile, Paths.get(firstDir.toString(), this.ymlTestFile.toFile().getName()));
		Files.copy(this.textTestFile, Paths.get(firstDir.toString(), this.textTestFile.toFile().getName()));
		Files.copy(this.xmlTestFile, Paths.get(secondDir.toString(), this.xmlTestFile.toFile().getName()));
		Files.copy(this.ymlTestFile, Paths.get(secondDir.toString(), this.ymlTestFile.toFile().getName()));
		Files.copy(this.textTestFile, Paths.get(secondDir.toString(), this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "test-");
		final String replaceValue = "rep-";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents,filenames,directory-names");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		// Only xml files should be processed
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "exclusions", ".yml$");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", true);
		
		this.findAndReplaceMojo.execute();
		
		final Path firstDirRenamed = Paths.get(this.runningTestsPath.toString(), "rep-directory");
		final Path secondDirRenamed = Paths.get(firstDirRenamed.toString(), "rep-sub-directory");
		final Path firstXmlFile = Paths.get(this.runningTestsPath.toString(), "rep-file.xml");
		final Path firstTxtFile = Paths.get(this.runningTestsPath.toString(), "test-file.txt");
		final Path firstYmlFile = Paths.get(this.runningTestsPath.toString(), "test-file.yml");
		final Path secondXmlFile = Paths.get(firstDirRenamed.toString(), "rep-file.xml");
		final Path secondTxtFile = Paths.get(firstDirRenamed.toString(), "test-file.txt");
		final Path secondYmlFile = Paths.get(firstDirRenamed.toString(), "test-file.yml");
		final Path thirdXmlFile = Paths.get(secondDirRenamed.toString(), "rep-file.xml");
		final Path thirdTxtFile = Paths.get(secondDirRenamed.toString(), "test-file.txt");
		final Path thirdYmlFile = Paths.get(secondDirRenamed.toString(), "test-file.yml");
		
		// Assert root dir
		assertTrue(Files.exists(firstXmlFile));
		assertTrue(this.fileContains(firstXmlFile.toFile(), replaceValue));
		assertFalse(this.fileContains(firstXmlFile.toFile(), "test-"));
		assertTrue(Files.exists(firstTxtFile));
		assertFalse(this.fileContains(firstTxtFile.toFile(), replaceValue));
		assertTrue(this.fileContains(firstTxtFile.toFile(), "test-"));
		assertTrue(Files.exists(firstYmlFile));
		assertFalse(this.fileContains(firstYmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(firstYmlFile.toFile(), "test-"));
		
		// Assert first dir
		assertTrue(Files.exists(firstDirRenamed));
		assertTrue(Files.exists(secondXmlFile));
		assertTrue(this.fileContains(secondXmlFile.toFile(), replaceValue));
		assertFalse(this.fileContains(secondXmlFile.toFile(), "test-"));
		assertTrue(Files.exists(secondTxtFile));
		assertFalse(this.fileContains(secondTxtFile.toFile(), replaceValue));
		assertTrue(this.fileContains(secondTxtFile.toFile(), "test-"));
		assertTrue(Files.exists(secondYmlFile));
		assertFalse(this.fileContains(secondYmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(secondYmlFile.toFile(), "test-"));
		
		// Assert second dir
		assertTrue(Files.exists(secondDirRenamed));
		assertTrue(Files.exists(thirdXmlFile));
		assertTrue(this.fileContains(thirdXmlFile.toFile(), replaceValue));
		assertFalse(this.fileContains(thirdXmlFile.toFile(), "test-"));
		assertTrue(Files.exists(thirdTxtFile));
		assertFalse(this.fileContains(thirdTxtFile.toFile(), replaceValue));
		assertTrue(this.fileContains(thirdTxtFile.toFile(), "test-"));
		assertTrue(Files.exists(thirdYmlFile));
		assertFalse(this.fileContains(thirdYmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(thirdYmlFile.toFile(), "test-"));
	}
	
	@Test
	void testEverythingReplaceFirst() throws IOException, NoSuchFieldException, IllegalAccessException,
		MojoExecutionException, MojoFailureException
	{
		final Path firstDir = Files.createDirectory(Paths.get(this.runningTestsPath.toString(), "test-test-directory"
		));
		final Path secondDir = Files.createDirectory(Paths.get(firstDir.toString(), "test-test-sub-directory"));
		Files.copy(this.xmlTestFile, Paths.get(firstDir.toString(), "test-" + this.xmlTestFile.toFile().getName()));
		Files.copy(this.ymlTestFile, Paths.get(firstDir.toString(), "test-" + this.ymlTestFile.toFile().getName()));
		Files.copy(this.textTestFile, Paths.get(firstDir.toString(), "test-" + this.textTestFile.toFile().getName()));
		Files.copy(this.xmlTestFile, Paths.get(secondDir.toString(), "test-" + this.xmlTestFile.toFile().getName()));
		Files.copy(this.ymlTestFile, Paths.get(secondDir.toString(), "test-" + this.ymlTestFile.toFile().getName()));
		Files.copy(this.textTestFile, Paths.get(secondDir.toString(), "test-" + this.textTestFile.toFile().getName()));
		
		this.setFieldValue(this.findAndReplaceMojo, "findRegex", "test-");
		final String replaceValue = "rep-";
		this.setFieldValue(this.findAndReplaceMojo, "replaceValue", replaceValue);
		this.setFieldValue(this.findAndReplaceMojo, "processFileContents", true);
		this.setFieldValue(this.findAndReplaceMojo, "replacementType", "file-contents,filenames,directory-names");
		this.setFieldValue(this.findAndReplaceMojo, "recursive", true);
		// Only xml files should be processed
		this.setFieldValue(this.findAndReplaceMojo, "fileMask", ".xml,.yml");
		this.setFieldValue(this.findAndReplaceMojo, "exclusions", ".yml$");
		this.setFieldValue(this.findAndReplaceMojo, "replaceAll", false);
		
		this.findAndReplaceMojo.execute();
		
		final Path firstDirRenamed = Paths.get(this.runningTestsPath.toString(), "rep-test-directory");
		final Path secondDirRenamed = Paths.get(firstDirRenamed.toString(), "rep-test-sub-directory");
		final Path firstXmlFile = Paths.get(this.runningTestsPath.toString(), "rep-file.xml");
		final Path firstTxtFile = Paths.get(this.runningTestsPath.toString(), "test-file.txt");
		final Path firstYmlFile = Paths.get(this.runningTestsPath.toString(), "test-file.yml");
		final Path secondXmlFile = Paths.get(firstDirRenamed.toString(), "rep-test-file.xml");
		final Path secondTxtFile = Paths.get(firstDirRenamed.toString(), "test-test-file.txt");
		final Path secondYmlFile = Paths.get(firstDirRenamed.toString(), "test-test-file.yml");
		final Path thirdXmlFile = Paths.get(secondDirRenamed.toString(), "rep-test-file.xml");
		final Path thirdTxtFile = Paths.get(secondDirRenamed.toString(), "test-test-file.txt");
		final Path thirdYmlFile = Paths.get(secondDirRenamed.toString(), "test-test-file.yml");
		
		// Assert root dir
		assertTrue(Files.exists(firstXmlFile));
		assertTrue(this.fileContains(firstXmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(firstXmlFile.toFile(), "test-"));
		assertTrue(Files.exists(firstTxtFile));
		assertFalse(this.fileContains(firstTxtFile.toFile(), replaceValue));
		assertTrue(this.fileContains(firstXmlFile.toFile(), "test-"));
		assertTrue(Files.exists(firstYmlFile));
		assertFalse(this.fileContains(firstYmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(firstXmlFile.toFile(), "test-"));
		
		// Assert first dir
		assertTrue(Files.exists(firstDirRenamed));
		assertTrue(Files.exists(secondXmlFile));
		assertTrue(this.fileContains(secondXmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(secondXmlFile.toFile(), "test-"));
		assertTrue(Files.exists(secondTxtFile));
		assertFalse(this.fileContains(secondTxtFile.toFile(), replaceValue));
		assertTrue(this.fileContains(secondTxtFile.toFile(), "test-"));
		assertTrue(Files.exists(secondYmlFile));
		assertFalse(this.fileContains(secondYmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(secondYmlFile.toFile(), "test-"));
		
		// Assert second dir
		assertTrue(Files.exists(secondDirRenamed));
		assertTrue(Files.exists(thirdXmlFile));
		assertTrue(this.fileContains(thirdXmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(thirdXmlFile.toFile(), "test-"));
		assertTrue(Files.exists(thirdTxtFile));
		assertFalse(this.fileContains(thirdTxtFile.toFile(), replaceValue));
		assertTrue(this.fileContains(thirdTxtFile.toFile(), "test-"));
		assertTrue(Files.exists(thirdYmlFile));
		assertFalse(this.fileContains(thirdYmlFile.toFile(), replaceValue));
		assertTrue(this.fileContains(thirdYmlFile.toFile(), "test-"));
	}
	
	private void setFieldValue(final Object obj, final String fieldName, final Object val)
		throws NoSuchFieldException, IllegalAccessException
	{
		final Field field = obj.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(obj, val);
	}
	
	private void recursiveDelete(final Path pathToBeDeleted) throws IOException
	{
		Files.walk(pathToBeDeleted)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
	}
	
	private boolean fileContains(final File file, final String findValue) throws IOException
	{
		return this.fileContains(file, findValue, Charset.defaultCharset());
	}
	
	private boolean fileContains(final File file, final String findValue, final Charset charset) throws IOException
	{
		try(final FileInputStream fis = new FileInputStream(file);
			final InputStreamReader isr = new InputStreamReader(fis, charset);
			final BufferedReader fileReader = new BufferedReader(isr))
		{
			final Stream<String> lines = fileReader.lines();
			
			return lines.anyMatch(line -> line.contains(findValue));
		}
	}
}
