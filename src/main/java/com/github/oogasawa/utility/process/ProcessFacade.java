package com.github.oogasawa.utility.process;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * The {@code ProcessFacade} class provides a simplified interface for executing external processes.
 * It abstracts the complexity of dealing with the {@link ProcessBuilder} and {@link Process} classes
 * in Java, offering a more straightforward method to execute commands and retrieve their outputs.
 * <p>
 * This facade handles the creation and execution of processes, and encapsulates the process's
 * standard output, standard error output, and exit code within a {@link StdioData} object.
 * The {@code exec} method is used to execute a command and returns the execution result as {@code StdioData}.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * ProcessFacade facade = new ProcessFacade();
 * StdioData data = facade.exec("ls -l");
 * System.out.println("Standard Output: " + data.getStdout());
 * System.out.println("Error Output: " + data.getStderr());
 * System.out.println("Exit Code: " + data.getExitCode());
 * </pre>
 * </p>
 *
 * @see ProcessBuilder
 * @see Process
 * @see StdioData
 */
public class ProcessFacade {

    /*
     * This class is composed of the following categories:
     *
     * - Fields and Constructors
     * - High level APIs
     * - Primitive APIs
     * - Setter/Getter methods
     */


    // ========================================================================
    // Fields and Constructors
    // ========================================================================

    /**
     * The {@code StdioMode} enum defines the possible modes for handling standard
     * input/output (stdio)
     * streams in the execution of an external process.
     * <p>
     * The modes are as follows:
     * <ul>
     * <li>{@code DISCARD}: Discards the stdio streams. Any output from the process
     * is ignored.</li>
     * <li>{@code INHERIT}: Inherits the stdio streams from the current Java
     * process. The external process's output
     * and error streams are displayed in the current Java process's console.</li>
     * <li>{@code STORE}: Stores the stdio streams. The output and error streams of
     * the process are captured and can be
     * retrieved later.</li>
     * <li>{@code INHERIT_AND_STORE}: A combination of inheriting and storing. The
     * stdio streams are displayed in the
     * current Java process's console and also stored for later retrieval.</li>
     * </ul>
     * </p>
     * This enumeration is used to specify how the {@link ProcessFacade} handles the
     * stdio streams of the
     * processes it executes.
     */
    public enum StdioMode {
        DISCARD,
        INHERIT,
        STORE,
        INHERIT_AND_STORE
    }

    StdioMode stdioMode = StdioMode.INHERIT;

    
    /**
     * The {@code StdioData} class is used to represent the result of executing an
     * external process.
     * It holds the standard output, standard error output, and exit code produced
     * by the executed process.
     * <p>
     * {@code StdioData} is an immutable data structure; once created, its content
     * cannot be changed.
     * Instances of this class are created by {@link ProcessFacade} and returned as
     * the result of the process execution.
     * </p>
     * 
     * @see ProcessFacade
     */    
    public class StdioData {

        String stdout;
        String stderr;
        int exitValue;

        // --- setters and getters ---

        public String getStdout() {
            return stdout;
        }


        public String getStderr() {
            return stderr;
        }


        public int getExitValue() {
            return exitValue;
        }

        //
        // Setter methods are private to ensure that
        // those values are only set internally by the ProcessFacade class and not
        // exposed for modification outside, maintaining the immutability of the state
        // of the process execution result.
        //

        private void setExitValue(int exitValue) {
            this.exitValue = exitValue;
        }

        
        private void setStderr(String stderr) {
            this.stderr = stderr;
        }

        
        private void setStdout(String stdout) {
            this.stdout = stdout;
        }

        
    }


    ProcessBuilder pb;

    
    public ProcessFacade() {
        pb = new ProcessBuilder();
    }


    // ========================================================================
    // High level APIs
    // ========================================================================


    /**
     * Executes the specified command and returns the result as a {@link StdioData} object.
     * <p>
     * The command is executed based on the current {@code stdioMode} setting.
     * The {@code stdioMode} determines how the standard input/output streams of the
     * process are handled.
     * </p>
     * <p>
     * The {@code exec} method supports four modes of operation:
     * <ul>
     * <li>{@code DISCARD}: Discards the stdio streams. Any output from the process is ignored.</li>
     * <li>{@code INHERIT}: Inherits the stdio streams from the current Java process. The external process's output
     * and error streams are displayed in the current Java process's console.</li>
     * <li>{@code STORE}: Stores the stdio streams. The output and error streams of the process are captured and can be
     * retrieved later.</li>
     * <li>{@code INHERIT_AND_STORE}: A combination of inheriting and storing. The stdio streams are displayed in the
     * current Java process's console and also stored for later retrieval.</li>
     * </ul>
     * </p>
     * <p>
     * The default mode is {@code INHERIT}, which displays the output and error streams in the current Java process's console.
     * </p>
     *
     * @param command The command to execute.
     * @return A {@link StdioData} object containing the standard output, standard error output, and exit code of the executed process.
     */
    public StdioData exec(String... command)  {
        if (stdioMode == StdioMode.DISCARD) {
            return execWithIgnoringOutput(command);
        }
        else if (stdioMode == StdioMode.INHERIT) {
            return execWithInheritingOutput(command);
        }
        else if (stdioMode == StdioMode.STORE) {
            return execWithSavingOutput(command);
        }
        else if (stdioMode == StdioMode.INHERIT_AND_STORE) {
            return execWithSavingAndInheritingOutput(command);
        }
        else {// Default
            return execWithInheritingOutput(command);
        }
        
    }


    
    // ========================================================================
    // Primitive APIs
    // ========================================================================


    StdioData execWithIgnoringOutput(String... command)  {
        StdioData result = new StdioData();
        pb = pb.command(command);
        try {
            Process p = pb.start();

            p.waitFor();
            result.setExitValue(p.exitValue());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return result;
    }



    StdioData execWithInheritingOutput(String... command)  {

        StdioData result = new StdioData();

        pb = pb.command(command);
        try {
            Process p = pb.start();

            // Set up a thread pool to handle stdout and stderr asynchronously
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(() -> {
                try (BufferedInputStream buffer = new BufferedInputStream(p.getInputStream())) {
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = buffer.read(bytes)) != -1) {
                        System.out.write(bytes, 0, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executor.submit(() -> {
                try (BufferedInputStream buffer = new BufferedInputStream(p.getErrorStream())) {
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = buffer.read(bytes)) != -1) {
                        System.err.write(bytes, 0, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            // Wait for the process to finish
            p.waitFor();
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            e.printStackTrace();
        }
 

        return result;
    }
    

    StdioData execWithSavingOutput(String ... command) {
        StdioData result = new StdioData();
        pb = pb.command(command);
        try {
            Process p = pb.start();

            // Set up a thread pool to handle stdout and stderr asynchronously
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(() -> {
                try (InputStream inputStream = p.getInputStream()) {
                    result.setStdout(new String(inputStream.readAllBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executor.submit(() -> {
                try (InputStream errorStream = p.getErrorStream()) {
                    result.setStderr(new String(errorStream.readAllBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
 
            p.waitFor();
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            e.printStackTrace();
        }

        return result;
    }


    

    StdioData execWithSavingAndInheritingOutput(String... command)  {

        StdioData result = new StdioData();

        pb = pb.command(command);
        try {
            Process p = pb.start();

            // Set up a thread pool to handle stdout and stderr asynchronously
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(() -> {
                try (BufferedInputStream buffer = new BufferedInputStream(p.getInputStream())) {

                    // Byte array output stream to temporarily hold the byte data
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = buffer.read(bytes)) != -1) {
                        System.out.write(bytes, 0, length);
                        byteArrayOutputStream.write(bytes, 0, length);
                    }

                    // Decode the byte data into a string
                    String output = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
                    result.setStdout(output);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executor.submit(() -> {
                try (BufferedInputStream buffer = new BufferedInputStream(p.getErrorStream())) {

                    // Byte array output stream to temporarily hold the byte data
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = buffer.read(bytes)) != -1) {
                        System.out.write(bytes, 0, length);
                        byteArrayOutputStream.write(bytes, 0, length);
                    }

                    // Decode the byte data into a string
                    String output = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
                    result.setStderr(output);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            // Wait for the process to finish
            p.waitFor();
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            e.printStackTrace();
        }
 

        return result;
    }


    


    
    
    // ========================================================================
    // Setter/Getter methods
    // ========================================================================

    /**
     * Gets the working directory for the process to be executed.
     * 
     * @return The working directory as a {@link File} object. If the directory is
     *         not explicitly set,
     *         returns {@code null} indicating the current working directory of the
     *         Java process will be used.
     */
    public File directory() {
        return pb.directory();
    }



    /**
     * Sets the working directory for the process to be executed.
     * 
     * @param path The path to the working directory.
     * @return The {@link ProcessFacade} object for method chaining.
     */
    public ProcessFacade directory(Path path) {
        pb.directory(path.toFile());
        return this;
    }


    /**
     * Gets the command to be executed.
     * 
     * @return The command as a list of strings.
     */
    public Map<String, String> environment() {
        return pb.environment();
    }


    /**
     * Sets the environment variables for the process to be executed.
     * 
     * @param key The environment variable key.
     * @param value The environment variable value.
     * @return The {@link ProcessFacade} object for method chaining.
     */
    public ProcessFacade environment(String key, String value) {
        pb.environment().put(key, value);
        return this;
    }


    /**
     * Gets the process's standard input/output (stdio) mode.
     *
     * @return The stdio mode as a {@link StdioMode} enum.
     */
    public StdioMode stdioMode() {
        return this.stdioMode;
    }


    /**
     * Sets the process's standard input/output (stdio) mode.
     *
     * @param mode The stdio mode as a {@link StdioMode} enum.
     * @return The {@link ProcessFacade} object for method chaining.
     */
    public ProcessFacade stdioMode(StdioMode mode) {
        this.stdioMode = mode;
        return this;
    }


    

}
