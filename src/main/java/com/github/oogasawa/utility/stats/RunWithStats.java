package com.github.oogasawa.utility.stats;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RunWithStats {

    public void monitorCommand(List<String> commandAndArgs, int interval, String basename, boolean gpuFlg) {
        try {
            // Start mpstat
            Process mpstat = new ProcessBuilder("bash", "-c",
                    String.format("exec mpstat -P ALL %d > %s.mpstat.out", interval, basename)).start();

            Process nvidiaSmi = null;
            if (gpuFlg) {
                // Start nvidia-smi
                nvidiaSmi = new ProcessBuilder("bash", "-c", String.format(
                        "exec nvidia-smi --query-gpu=timestamp,index,utilization.gpu,utilization.memory,memory.used,memory.total "
                                + "--format=csv,nounits --loop=%d > %s.nvidia-smi.out",
                        interval, basename)).start();
            }

            // Start main command
            Process command = new ProcessBuilder(commandAndArgs).inheritIO().start();
            long pid = command.pid();

            // Start pidstat
            Process pidstat = new ProcessBuilder("bash", "-c",
                    String.format("exec pidstat -urdh -t -p %d %d > %s.pidstat.out", pid, interval, basename)).start();

            // Wait for main command
            int exitCode = command.waitFor();

            // Wait a bit for final samples
            Thread.sleep(interval * 1000L);

            // Stop monitoring processes safely
            stopProcess(mpstat, "mpstat");
            stopProcess(pidstat, "pidstat");
            if (gpuFlg && nvidiaSmi != null) {
                stopProcess(nvidiaSmi, "nvidia-smi");
            }

            System.out.println("Main process exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stopProcess(Process process, String name) {
        if (process == null) return;

        process.destroy();  // Send SIGTERM
        try {
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                System.err.println(name + " did not terminate in time, forcing shutdown...");
                process.destroyForcibly();  // Send SIGKILL
                process.waitFor();
            }
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for " + name + " to terminate.");
            process.destroyForcibly();
        }
    }
}
