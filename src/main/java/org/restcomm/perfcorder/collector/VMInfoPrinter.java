package org.restcomm.perfcorder.collector;

import java.util.Arrays;
import java.util.Locale;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.restcomm.perfcorder.collector.jmx.LocalVirtualMachine;
import org.restcomm.perfcorder.collector.jmx.ProxyClient;

/**
 * PerfCorder collects info about an external process through JMX beans
 *
 *
 */
public class VMInfoPrinter {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(VMInfoPrinter.class.getName());

    private static OptionParser createOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList(new String[]{"help", "?", "h"}),
                "shows this help").forHelp();

        parser
                .acceptsAll(Arrays.asList(new String[]{"p", "pid"}),
                        "PID to connect to").withRequiredArg().ofType(Integer.class);

        return parser;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%c %-5p %m%n"), "System.err"));
        logger.setLevel(org.apache.log4j.Level.INFO);

        OptionParser parser = createOptionParser();
        OptionSet a = parser.parse(args);

        if (a.has("help")) {
            System.out.println("perfcorder - java monitoring for the command-line");
            System.out.println("Usage: perfcorder.sh [PID]");
            System.out.println("");
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        String targetJVM = null;

        //to support PID as non option argument
        if (a.nonOptionArguments().size() > 0) {
            targetJVM = (String) a.nonOptionArguments().get(0);
        }

        if (a.hasArgument("pid")) {
            targetJVM = (String) a.valueOf("pid");
        }

        VMInfo vmInfo_ = null;
        try {
            Integer pid = Integer.valueOf(targetJVM);
            LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                    .getLocalVirtualMachine(pid);
            vmInfo_ = VMInfo.processNewVM(localVirtualMachine, pid);
        } catch (Exception e) {
            ProxyClient proxyClient = ProxyClient.getProxyClient(targetJVM,
                    System.getenv("PERF_USER"),
                    System.getenv("PERF_PSW"));
            proxyClient.connect();
            vmInfo_ = new VMInfo(proxyClient, null, null);
        }
        System.out.println(vmInfo_.getRuntimeMXBean().getBootClassPath());
        System.out.println(vmInfo_.getRuntimeMXBean().getClassPath());
        System.out.println(vmInfo_.getRuntimeMXBean().getInputArguments());
        System.out.println(vmInfo_.getRuntimeMXBean().getLibraryPath());
        System.out.println(vmInfo_.getRuntimeMXBean().getManagementSpecVersion());
        System.out.println(vmInfo_.getRuntimeMXBean().getName());
        System.out.println(vmInfo_.getRuntimeMXBean().getSpecName());
        System.out.println(vmInfo_.getRuntimeMXBean().getSpecVersion());
        System.out.println(vmInfo_.getRuntimeMXBean().getStartTime());
        System.out.println(vmInfo_.getRuntimeMXBean().getUptime());
        System.out.println(vmInfo_.getRuntimeMXBean().getVmName());
        System.out.println(vmInfo_.getRuntimeMXBean().getVmVendor());
        System.out.println(vmInfo_.getRuntimeMXBean().getVmVersion());
        
        System.exit(0);
    }

    public VMInfoPrinter() {
    }

}
