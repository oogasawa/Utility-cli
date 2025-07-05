package com.github.oogasawa.utility.stats;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.github.oogasawa.utility.cli.CommandRepository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


public class StatsCommands {

    /**
     * The command repository used to register commands.
     */
    CommandRepository cmdRepos = null;
    

    public void setupCommands(CommandRepository cmds) {
        this.cmdRepos = cmds;
        
        statsRunCommand();
    }


    public void statsRunCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("i")
                .longOpt("interval")
                .hasArg(true)
                .argName("SECONDS") // to make the CLI help output clearer (e.g., -i <SECONDS>)
                .desc("Interval in seconds between each statistics output.")
                .required(false)
                .build());

        opts.addOption(Option.builder("n")
                .longOpt("basename")
                .hasArg(true)
                .argName("FILENAME") 
                .desc("Base name for statistics output files")
                .required(false)
                .build());

        opts.addOption(Option.builder("g")
                .longOpt("gpu")
                .hasArg(false)
                //.argName("true or false") 
                .desc("Enable GPU monitoring (default: disabled)")
                .required(false)
                .build());

        
        
        this.cmdRepos.addCommand("stats commands", "stats:run", opts,
                "Execute an arbitrary command and collect statistics while it is running.",
                (CommandLine cl) -> {
                    int interval = Integer.valueOf(cl.getOptionValue("interval", "10"));
                    String basename = cl.getOptionValue("basename", "stats");
                    List<String> commands = cl.getArgList();
                    commands = commands.subList(1, commands.size()); // remove the 0th element (which is always "stats:run") 
                    boolean gpuFlg = cl.hasOption("gpu");

                    RunWithStats stats = new RunWithStats();
                    stats.monitorCommand(commands, interval, basename, gpuFlg);
                });
    }

}
