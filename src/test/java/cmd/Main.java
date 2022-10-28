package cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author huangzhenyu
 * @date 2022/10/27
 */
public class Main {
    public static void main(String[] args) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(new String[]{
                "ssh",
                "root@localhost"
        });
        // 重定向错误流到标准输出流
        builder.redirectErrorStream(true);
        // stdout
        Process process = builder.start();
        new Thread(new ProcessHandleRunnable(process)).start();
    }

    static class ProcessHandleRunnable implements Runnable {
        private Process process;

        public ProcessHandleRunnable(Process process) {
            this.process = process;
        }

        public void run() {
            BufferedReader br = null;
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(process.getInputStream(), StandardCharsets.US_ASCII);
                br = new BufferedReader(reader);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
