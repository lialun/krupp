package ai.bailian.system;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 系统指令工具类
 *
 * @author lialun
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class CommandUtils {

    /**
     * 执行系统指令
     * 例如执行"ls -la"："ls" "-la"
     *
     * @param cmd 要执行的命令
     * @return 命令的输出结果
     */
    public static CommandExecuteResult executeCommand(String... cmd) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(cmd);
        List<String> inputLines = IOUtils.readLines(proc.getInputStream(), "UTF-8");
        List<String> errorLines = IOUtils.readLines(proc.getErrorStream(), "UTF-8");
        proc.waitFor();
        return new CommandExecuteResult(inputLines, errorLines, proc.exitValue());
    }

    /**
     * 执行shell命令
     *
     * @param cmd 要执行的命令
     * @return 命令的输出结果
     */
    public static CommandExecuteResult executeShell(String... cmd) throws IOException, InterruptedException {
        String[] shell = Arrays.copyOf(new String[]{"/bin/bash", "-c"}, cmd.length + 2);
        System.arraycopy(cmd, 0, shell, 2, cmd.length);
        return executeCommand(shell);
    }

    /**
     * SCP从远端传输到本地
     *
     * @param localFile  本地文件路径和文件名
     * @param user       用户名
     * @param remote     远端地址
     * @param port       远端端口
     * @param remoteFile 远端文件路径和文件名
     * @return 是否执行成功
     */
    public static boolean scpToRemote(String localFile, String remote, String user, int port, String remoteFile) throws IOException, InterruptedException {
        String cmd = String.format("scp -P %s %s %s@%s:%s", port, localFile, user, remote, remoteFile);
        return executeShell(cmd).getExitValue() == 0;
    }

    public static boolean scpFromRemote(String localFile, String remote, String user, int port, String remoteFile) throws IOException, InterruptedException {
        String cmd = String.format("scp -P %s %s@%s:%s %s", port, user, remote, remoteFile, localFile);
        return executeShell(cmd).getExitValue() == 0;
    }

    final public static class CommandExecuteResult {
        private List<String> inputLines;
        private List<String> errorLines;
        int exitValue;

        public CommandExecuteResult(List<String> inputLines, List<String> errorLines, int exitValue) {
            this.inputLines = inputLines;
            this.errorLines = errorLines;
            this.exitValue = exitValue;
        }

        public List<String> getInputLines() {
            return inputLines;
        }

        public List<String> getErrorLines() {
            return errorLines;
        }

        public int getExitValue() {
            return exitValue;
        }

        @Override
        public String toString() {
            return "CommandExecuteResult{" +
                    "inputLines=" + inputLines +
                    ", errorLines=" + errorLines +
                    ", exitValue=" + exitValue +
                    '}';
        }
    }
}
