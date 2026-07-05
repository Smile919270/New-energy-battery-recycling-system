package cn.zyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(Application.class, args);
        System.out.println(" \n" +
                "                   _ooOoo_ \n" +
                "                  o8888888o \n" +
                "                  88\" . \"88 \n" +
                "                  (| -_- |) \n" +
                "                  O\\  =  /O \n" +
                "               ____/`---'\\____ \n" +
                "             .'  \\\\|     |//  `. \n" +
                "            /  \\\\|||  :  |||//  \\ \n" +
                "           /  _||||| -:- |||||-  \\ \n" +
                "           |   | \\\\\\  -  /// |   | \n" +
                "           | \\_|  ''\\---/''  |   | \n" +
                "           \\  .-\\__  `-`  ___/-. / \n" +
                "         ___`. .'  /--.--\\  `. . __ \n" +
                "      .\"\" '<  `.___\\_<|>_/___.'  >'\"\". \n" +
                "     | | :  `- \\`.;`\\ _ /`;.`/ - ` : | | \n" +
                "     \\  \\ `-.   \\_ __\\ /__ _/   .-` /  / \n" +
                "======`-.____`-.___\\_____/___.-`____.-'====== \n" +
                "                   `=---=' \n" +
                " \n" +
                "   .........................................  \n" +
                "         佛祖保佑             永无BUG \n" +
                "         电池稳定             续航千里 \n" +
                "   .........................................  \n");

        System.out.println(" \n" +
                " .-.     .-.     .-.     .-.     .-.     .-. \n" +
                " | |     | |     | |     | |     | |     | | \n" +
                " | |     | |     | |     | |     | |     | | \n" +
                " | `-----' `-----' `-----' `-----' `-----' | \n" +
                " | .-------------------------------------. | \n" +
                " | |   [+]   New Energy Battery   [-]    | | \n" +
                " | |  _  _  _  _  _  _  _  _  _  _  _  _ | | \n" +
                " | | [_][_][_][_][_][_][_][_][_][_][_][_]| | \n" +
                " | |                                     | | \n" +
                " | `-------------------------------------' | \n" +
                " |  _       _       _       _       _      | \n" +
                " `-' `-----' `-----' `-----' `-----' `-----' \n" +
                "     新能源动力电池回收系统启动成功！        \n");
    }
}
